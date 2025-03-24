package com.citc.nce.authcenter.captch.service.impl;

import cn.hutool.core.lang.UUID;
import com.citc.nce.authcenter.captch.entity.CodeAndTime;
import com.citc.nce.authcenter.captch.service.CaptchaService;
import com.citc.nce.authcenter.captcha.vo.req.CaptchaCheckReq;
import com.citc.nce.authcenter.captcha.vo.req.EmailCaptchaReq;
import com.citc.nce.authcenter.captcha.vo.req.SmsCaptchaReq;
import com.citc.nce.authcenter.captcha.vo.resp.CaptchaCheckResp;
import com.citc.nce.authcenter.captcha.vo.resp.CaptchaResp;
import com.citc.nce.authcenter.config.EmailCheckConfigure;
import com.citc.nce.authcenter.config.SmsLimitConfigure;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.constant.CaptchaType;
import com.citc.nce.authcenter.constant.DyzTypeEnum;
import com.citc.nce.authcenter.constant.RedisKey;
import com.citc.nce.authcenter.utils.DyzSystemUtils;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.redis.config.RedisService;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties({EmailCheckConfigure.class, SmsLimitConfigure.class})
public class CaptchaServiceImpl implements CaptchaService {

    private final String EMAIL_CHECK = "email_check:";

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RedisService redisService;
    @Resource
    private DyzSystemUtils dyzSystemUtils;

    private final EmailCheckConfigure emailCheckConfigure;

    private final SmsLimitConfigure smsLimitConfigure;


    private void updateSMSLimit(String phone) {
        if (!Strings.isNullOrEmpty(phone)) {
            String key = "SMSLimitCheck:" + phone;
            Integer count = (Integer) redisTemplate.opsForValue().get(key);
            if (null == count) {
                //新增记录
                redisTemplate.opsForValue().set(key, 1, smsLimitConfigure.getTimeSpan(), TimeUnit.MINUTES);
            } else {
                //添加次数
                redisTemplate.opsForValue().increment(key, 1);
            }
        }
    }

    /**
     * 查询短信发送次数
     *
     * @param phone 手机号
     */
    private void checkSMSLimit(String phone) {
        if (StringUtils.hasLength(phone)) {
            String key = "SMSLimitCheck:" + phone;
            Integer count = (Integer) redisTemplate.opsForValue().get(key);
            if (count != null && count > smsLimitConfigure.getTimes()) {
                throw new BizException(AuthCenterError.SMS_SEND_LIMIT);
            }
        }
    }


    /**
     * @param smsCaptchaReq 短信验证信息
     * @return 验证码信息
     */
    @Override
    public CaptchaResp getDyzSmsCaptcha(SmsCaptchaReq smsCaptchaReq) {

        //判断是否走多因子
        if (Boolean.TRUE.equals(dyzSystemUtils.isUseDYZ())) {
            //0、校验短信发送次数
            checkSMSLimit(smsCaptchaReq.getPhone());
            //1、每分钟只能发一次校验
            check1MinOnly1Time(1L, smsCaptchaReq.getPhone() + smsCaptchaReq.getUrl());
            String nameOrPhone = smsCaptchaReq.getPhone();
            CaptchaResp captchaResp;
            if (DyzTypeEnum.ADMINAUTH.getCode().equals(smsCaptchaReq.getDyzType())) {//发送管理员DYZ验证码
                log.info("--------------------【发送--管理员】多因子验证码短信--------------------");
                captchaResp = dyzSystemUtils.sendDYZSms(nameOrPhone, true);
            } else {//发送DYZ验证码
                log.info("--------------------【发送--用户】多因子验证码短信--------------------");
                captchaResp = dyzSystemUtils.sendDYZSms(nameOrPhone, false);
            }
            updateSMSLimit(smsCaptchaReq.getPhone());
            return captchaResp;
        }

        //不走多因子验证码
        return defaultCaptcha();

    }

    @Override
    public CaptchaResp getEmailCaptcha(EmailCaptchaReq emailCaptchaReq) {
        //1、每分钟只能发一次校验
        check1MinOnly1Time(1L, emailCaptchaReq.getEmail() + emailCaptchaReq.getUrl());
        //2、验证邮箱限制
        checkEmailLimit(emailCaptchaReq.getEmail());
        //3、判断是否走多因子系统
        Boolean useDYZ = dyzSystemUtils.isUseDYZ();
        if (Boolean.TRUE.equals(useDYZ)) {
            return dyzSystemUtils.sendEmailCaptcha(emailCaptchaReq.getEmail());
        } else {
            return defaultCaptcha();
        }
    }

    /**
     * 验证获取邮箱验证码限制
     *
     * @param email 邮箱
     */
    private void checkEmailLimit(String email) {
        // 为ture则是初始设置成功，false表示已有对应值
        Boolean isExists = redisTemplate.opsForValue().setIfAbsent(EMAIL_CHECK + email, emailCheckConfigure.getLimit() - 1, emailCheckConfigure.getDuration(), TimeUnit.MINUTES);
        // 如果已有对应key的值
        if (!isExists) {
            int current = (int) redisTemplate.opsForValue().get(EMAIL_CHECK + email);
            // 如果剩余发送次数大于0
            if (current > 0) {
                // 剩余发送次数-1
                redisTemplate.opsForValue().decrement(EMAIL_CHECK + email, 1L);
            } else {
                throw new BizException(AuthCenterError.CAPTCHA_EMAIL_LIMIT);
            }

        }
    }

    /**
     * 验证码校验接口
     *
     * @param captchaCheckReq 请求信息
     * @return 校验结果
     */
    @Override
    public CaptchaCheckResp checkCaptcha(CaptchaCheckReq captchaCheckReq) {
        CaptchaCheckResp resp = new CaptchaCheckResp();
        String type = captchaCheckReq.getType();
        String captchaKey = captchaCheckReq.getCaptchaKey();
        String code = captchaCheckReq.getCode();
        String account = captchaCheckReq.getAccount();

        //检出时候是未授权的用户获取的样子码
        String phone = redisService.getCacheObject(getErrorCodeRedisKey(captchaCheckReq.getCaptchaKey()));
        if (StringUtils.hasLength(phone)) {
            resp.setResult(false);
            log.warn("unauthorized user verify sms code : {}", phone);
            return resp;
        }


        // 判断是否走多因子系统
        if (Boolean.TRUE.equals(dyzSystemUtils.isUseDYZ())) {
            if (CaptchaType.EMAIL.getCode().equalsIgnoreCase(type)) {//邮箱验证码校验：在redis中校验
                return resp.setResult(checkRedisCaptcha(DyzSystemUtils.EMAIL_CODE, code, captchaKey, account));
            }
            if (CaptchaType.SMS.getCode().equalsIgnoreCase(type) || CaptchaType.DYZ_SMS.getCode().equalsIgnoreCase(type)) {
                //sms 和dyz——sms合并  取消了sms发短信功能
                if (Boolean.FALSE.equals(dyzSystemUtils.checkDyzSms(account, code, captchaKey, captchaCheckReq.getIsAdminAuth()))) {
                    throw new BizException(AuthCenterError.CAPTCHA_FAILED);
                }
                return resp.setResult(true);
            }
            throw new BizException(AuthCenterError.UNKNOWN_TYPE);
        }

        return resp.setResult(checkDefaultCaptcha(captchaKey, code, type));

    }

    /**
     * 邮箱验证码校验，通过查询redis进行校验
     *
     * @param type       验证码类型
     * @param code       验证码
     * @param captchaKey 验证码KEY
     * @param account    账号
     * @return 校验结果
     */
    private Boolean checkRedisCaptcha(String type, String code, String captchaKey, String account) {
        Object obj = redisTemplate.opsForValue().get(type + account + captchaKey);
        if (Objects.isNull(obj)) {
            throw new BizException(AuthCenterError.CAPTCHA_CODE_INVALID);
        }
        if (!obj.toString().equals(code)) {
            throw new BizException(AuthCenterError.CAPTCHA_FAILED);
        } else {
            String key = "CaptchaLimitCheck:" + type + account + captchaKey;
            Integer count = (Integer) redisTemplate.opsForValue().get(key);
            if (null == count) {
                redisTemplate.opsForValue().set(key, 1, 10, TimeUnit.MINUTES);
            } else {
                if (count < 11) {
                    redisTemplate.opsForValue().increment(key, 1);
                } else {
                    throw new BizException(AuthCenterError.CAPTCHA_LIMIT_TIME_EREEOR);
                }
            }
        }
        return true;
    }


    /**
     * 一分钟只能发一次：一个手机号一分钟只能发一次  或者 邮箱在同一个url下一分钟只能发一次
     *
     * @param expire    时间期间
     * @param checkCode 校验值：手机号、邮箱+url
     */
    private void check1MinOnly1Time(long expire, String checkCode) {
        CodeAndTime msgTemplateByCode = new CodeAndTime().setPhone(checkCode).setTime(System.currentTimeMillis());
        Boolean notExists = redisTemplate.opsForValue().setIfAbsent(RedisKey.CAPTCHAONEMINITE.build(checkCode), msgTemplateByCode, expire, TimeUnit.MINUTES);
        if (notExists != null && !notExists) {
            CodeAndTime codeAndTime = (CodeAndTime) redisTemplate.opsForValue().get(RedisKey.CAPTCHAONEMINITE.build(checkCode));
            if (null != codeAndTime) {
                long l = System.currentTimeMillis() - codeAndTime.getTime();
                if (l / 60000 > 1) {
                    redisTemplate.delete(RedisKey.CAPTCHAONEMINITE.build(checkCode));
                } else {
                    throw new BizException(AuthCenterError.CAPTCHA_SMS_ONE_TIME_ONT_MINITE);
                }
            }
        }
    }


    /**
     * 返回默认的验证码
     *
     * @return 默认验证码
     */
    private CaptchaResp defaultCaptcha() {
        CaptchaResp resp = new CaptchaResp();
        resp.setCaptchaKey("123");
        resp.setCode("******");
        resp.setTimestamp(System.currentTimeMillis());
        return resp;
    }

    /**
     * 设计一个假的验证码到redis
     *
     * @param phone 手机号
     */
    @Override
    public CaptchaResp getErrorCode(String phone) {
        //0、校验短信发送次数
        checkSMSLimit(phone);
        //1、每分钟只能发一次校验
        check1MinOnly1Time(1L, "unauthorized" + phone);
        CaptchaResp captchaResp = defaultCaptcha();
        String uuid = UUID.fastUUID().toString(true);
        captchaResp.setCaptchaKey(uuid);
        redisService.setCacheObject(getErrorCodeRedisKey(uuid), phone, 5L, TimeUnit.MINUTES);
        log.warn("unauthorized user get sms code : {}", phone);
        updateSMSLimit(phone);
        return captchaResp;
    }

    private static String getErrorCodeRedisKey(String uuid) {
        return "system:user:dyz:unauthorized:" + uuid;
    }


    /**
     * 校验默认验证码
     *
     * @param captchaKey 验证码key
     * @param code       验证码
     * @return 校验结果
     */
    private boolean checkDefaultCaptcha(String captchaKey, String code, String type) {
        if ("123456".equals(code) || "123".equals(captchaKey)) {
            return true;
        }
        //测试手机号或验证码错误
        throw new BizException(AuthCenterError.CAPTCHA_FAILED);
    }
}
