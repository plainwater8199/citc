package com.citc.nce.authcenter.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.authcenter.captch.dto.CaptchaMailDto;
import com.citc.nce.authcenter.captcha.vo.req.SmsSendRequest;
import com.citc.nce.authcenter.captcha.vo.resp.CaptchaResp;
import com.citc.nce.authcenter.captcha.vo.resp.DyzResultResp;
import com.citc.nce.authcenter.config.DyzConfigure;
import com.citc.nce.authcenter.config.EmailCheckConfigure;
import com.citc.nce.authcenter.config.RestTemplateConfiguration;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.ErrorCode;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.misc.msg.MsgApi;
import com.citc.nce.misc.msg.req.MsgTemplateReq;
import com.citc.nce.misc.msg.resp.MsgTemplateResp;
import com.cuca.bouncycastle.util.encoders.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author luohaihang
 * @version 1.0
 * @description * 工具类分为系统短信发送验证、多因子短信发送验证.
 * * 系统短信发送验证type = SMS
 * * 多因子短信发送验证type = DYZ_SMS
 * * dyzConfigure.getState() 开关
 * * -- true开启系统和多因子短信发送验证
 * * -- false关闭 默认进行123456验证
 * @date 2023/3/1 10:30
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DyzSystemUtils {
    public static final String SMS_CODE = "system:auth:dyz:sms_code:";
    public static final String EMAIL_CODE = "system:auth:email_code:";
    public static final String DYZ_CALLBACK_SMS_CODE = "system:auth:sms_code:call_back:";

    private final DyzConfigure dyzConfigure;
    @Resource
    private  RedisService redisService;
    private final MsgApi msgApi;
    private final EmailCheckConfigure emailCheckConfigure;

    public String getUserCheckRedisKey(String bizSn) {
        Assert.notBlank(bizSn, "bizSn不能为空");
        return DYZ_CALLBACK_SMS_CODE + bizSn;
    }

    /**
     * 发送普通短信验证码
     *
     * @param phone 手机号
     * @return 返回
     */
    public CaptchaResp sendSystemCaptcha(String phone) {
        //流水号，调用方生成，保持唯一
        String bizSn = getUUID();
        //生成验证码
        String smsCode = getCaptchaCode();
        //发送短信
        sendSystemSms(phone, smsCode);
        // 保存redis 后续验证
        redisService.setCacheObject(SMS_CODE + phone + bizSn, smsCode, 5L, TimeUnit.MINUTES);
        return makeUpCaptchaResp(bizSn);
    }

    /**
     * 发送邮箱验证码（自己平台）
     *
     * @param email 邮箱
     * @return 验证码信息
     */
    public CaptchaResp sendEmailCaptcha(String email) {
        //流水号，调用方生成，保持唯一
        String bizSn = getUUID();
        //生成验证码
        String smsCode = getCaptchaCode();

        RestTemplate restTemplate = new RestTemplate();
        HashMap<String, Object> map = new HashMap<>();
        map.put("app_id", emailCheckConfigure.getAppId());
        map.put("app_secret", emailCheckConfigure.getAppSecret());
        map.put("email_type", emailCheckConfigure.getEmailType());
        List<CaptchaMailDto> emails = new ArrayList<>();
        CaptchaMailDto mailDto = new CaptchaMailDto();
        mailDto.setTitle("邮箱激活");
        StringBuffer message = new StringBuffer();
        message.append("您好！").append("\n")
                .append("欢迎来到硬核桃社区。").append("\n")
                .append("您的邮箱为：").append(email).append("，验证码如下，请回填").append("\n")
                .append(smsCode).append("\n")
                .append("验证码在30分钟内有效").append("\n")
                .append("硬核桃社区");
        mailDto.setMessage(message.toString());
        mailDto.setReceivers(new String[]{email});
        mailDto.setSender_name("硬核桃");

        emails.add(mailDto);
        map.put("emails", emails);
        String s = JsonUtils.obj2String(map);
        JSONObject jo = JSONObject.parseObject(s);
        log.info("------------中讯邮件【请求】--------------： {}", jo);
        ResponseEntity<String> response = restTemplate.postForEntity(emailCheckConfigure.getUrl(), jo, String.class);
        log.info("------------中讯邮件【响应】--------------： {}", response);
        String body = response.getBody();
        JSONObject bodyObject = JSON.parseObject(body);
        Integer code = bodyObject.getInteger("code");
        if (code != 0) {//发送验证码状态不为0   代表异常
            throw new BizException(AuthCenterError.MAIL_SERVICE_ABNORMAL);
        }
        // 保存redis 后续验证
        redisService.setCacheObject(EMAIL_CODE + email + bizSn, smsCode, 30L, TimeUnit.MINUTES);
        return makeUpCaptchaResp(bizSn);
    }

    /**
     * 发送dyz短信
     *
     * @param phone   手机号
     * @param isAdmin 是否是管理员
     * @return
     */
    public CaptchaResp sendDYZSms(String phone, Boolean isAdmin) {
        //流水号，调用方生成，保持唯一
        String bizSn = getUUID();
        //生成验证码
        String smsCode = getCaptchaCode();
        SmsSendRequest sendRequest = getSmsCodeSendRequest(phone, smsCode, bizSn, isAdmin);
        log.info("--------------------【发送】多因子验证码短信--------------------");
        DyzResultResp dyzResultResp = getDyzResultResp(SmsSendRequest.class, sendRequest, dyzConfigure.getSendVerifyCode(), AuthCenterError.CAPTCHA_GENERATOR_FAILED);
        if (dyzResultResp != null) {
            if (2052 == dyzResultResp.getCode()) {
                throw new BizException(AuthCenterError.DYZ_NO_FIND_USER);
            }
            if (2016 == dyzResultResp.getCode()) {
                throw new BizException(AuthCenterError.DYZ_SEND_LIMIT);
            }
            if (2056 == dyzResultResp.getCode()) {
                throw new BizException("短信验证码已达到最大发送次数,请稍后再试");
            }
            if (9999 == dyzResultResp.getCode()) {
                throw new BizException("短信平台系统错误,请稍后再试");
            }
            if (0 != dyzResultResp.getCode()) {//发送验证码状态不为0   代表异常
                throw new BizException(AuthCenterError.CAPTCHA_GENERATOR_FAILED);
            }
        }

        return makeUpCaptchaResp(bizSn);
    }

    /**
     * 验证dyz短信验证码
     *
     * @param account     账号
     * @param code        验证码
     * @param captchaKey  验证码key---bizSn
     * @param isAdminAuth 是否是管理平台
     */
    public Boolean checkDyzSms(String account, String code, String captchaKey, Boolean isAdminAuth) {
        SmsSendRequest sendRequest;
        if (isAdminAuth != null && isAdminAuth) {
            sendRequest = getSmsCodeSendRequest(account, code, captchaKey, true);
        } else {
            sendRequest = getSmsCodeSendRequest(account, code, captchaKey, false);
        }
        log.info("--------------------【验证】多因子验证码短信--------------------");
        DyzResultResp dyzResultResp = getDyzResultResp(SmsSendRequest.class, sendRequest, dyzConfigure.getCheckVerifyCode(), AuthCenterError.CAPTCHA_FAILED);
        return Objects.nonNull(dyzResultResp) && (dyzResultResp.getCode() == 0);
    }


    /**
     * 发送普通短信验证码
     *
     * @param phone   手机号
     * @param smsCode 验证码
     */
    private void sendSystemSms(String phone, String smsCode) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("app_id", dyzConfigure.getAppId());
        map.put("app_secret", dyzConfigure.getAppSecret());
        map.put("sms_type", 0);
        map.put("mobile", phone);
        String smsTemplateCode = dyzConfigure.getSmsTemplateCode();
        StringBuilder content = new StringBuilder();
        if (StrUtil.isNotBlank(smsTemplateCode)) {
            //模板获取
            MsgTemplateReq templateReq = new MsgTemplateReq();
            templateReq.setTempldateCode(smsTemplateCode);
            MsgTemplateResp template = msgApi.getMsgTemplateByCode(templateReq);
            content.append(template.getTempldateContent().replace("{{code}}", smsCode));
        } else {
            content.append("您的验证码为:" + smsCode + ",有效期5分钟，请勿泄露。如非本人操作，请忽略此短信，谢谢");
        }
        map.put("content", content);
        log.warn("------------统一短信【请求】--------------：{}", JsonUtils.obj2String(map));
        RestTemplate restTemplate = new RestTemplate();
        ////测试地址
        StringBuffer sb = new StringBuffer();
        sb.append(dyzConfigure.getSendSms());
        ResponseEntity<String> response = restTemplate.postForEntity(sb.toString(), map, String.class);
        log.warn("------------统一短信【响应】--------------：{}", JsonUtils.obj2String(response));
        String body = response.getBody();
        JSONObject bodyObject = JSON.parseObject(body);
        Integer code = bodyObject.getInteger("code");
        if (code != 0) {
            //发送验证码状态不为0   代表异常
            throw new BizException(AuthCenterError.CAPTCHA_GENERATOR_FAILED);
        }
    }


    /**
     * 多因子短信发送参数封装
     *
     * @param phone 手机号
     * @param code  验证码
     * @param bizSn 验证码因子
     * @return 短信对象
     */

    private SmsSendRequest getSmsCodeSendRequest(String phone, String code, String bizSn, Boolean isAdminAuth) {
        try {
            //设置缓存用于验证用户身份是否正确（本平台发出）
            String priKey = dyzConfigure.getPriKey1();//isAdminAuth ? dyzConfigure.getPriKeyAdmin() : dyzConfigure.getPriKey1();
            String appCode = dyzConfigure.getAppCode1();//isAdminAuth ? dyzConfigure.getAppCodeAdmin() : dyzConfigure.getAppCode1();
            redisService.setCacheObject(getUserCheckRedisKey(bizSn), phone, 6L, TimeUnit.MINUTES);
            String callBackUrl = URLEncoder.encode(dyzConfigure.getCallBackUrl(), StandardCharsets.UTF_8.name());
            //生成签名
            byte[] signByte = SignUtil.sign(priKey, bizSn.getBytes(StandardCharsets.UTF_8));
            String sign = Base64.toBase64String(signByte);
            //组装参数
            return SmsSendRequest.builder()
                    .authCode(code)
                    .appCode(appCode)
                    .mobile(phone)
                    .bizSn(bizSn)
                    .sign(sign)
                    .callBackUrl(callBackUrl)
                    .build();
        } catch (Exception e) {
            log.error("获取短信验证码参数构建失败", e);
            throw new BizException("获取短信验证码参数构建失败");
        }

    }

    /**
     * 多因子http调用
     *
     * @return 调用结果
     */
    @Nullable
    private <T> DyzResultResp getDyzResultResp(Class<T> clazz, T cla, String url, ErrorCode errorCodeEnum) {
        T t = clazz.cast(cla);
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate = new RestTemplate(RestTemplateConfiguration.generateHttpRequestFactory());
        } catch (Exception e) {
            log.error("初始化异常：" + e.getMessage());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<Object> entity = new HttpEntity<Object>(t, headers);
        ResponseEntity<String> response = null;
        try {
            log.info("------多因子系统交互【请求】---------：{}", JsonUtils.obj2String(entity));
            response = restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
            log.error("多因子请求失败", e);
            throw BizException.build(errorCodeEnum);
        }
        log.info("------多因子系统交互【响应】---------：{}", JsonUtils.obj2String(response));
        return JSONObject.parseObject(response.getBody(), DyzResultResp.class);
    }


    /**
     * 获取uuid
     *
     * @return uuid
     */
    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 判断是否使用多因子系统
     *
     * @return 是否使用
     */

    public Boolean isUseDYZ() {
        return dyzConfigure.getState();
    }

    /**
     * 生成验证码
     *
     * @return 验证码
     */
    private String getCaptchaCode() {
        // 生成验证码
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int random = (int) (Math.random() * 10);
            builder.append(random);
        }
        return builder.toString();
    }


    /**
     * 组装生成校验码的返回报文
     *
     * @param bizSn 验证因子
     * @return 返回结果
     */
    private CaptchaResp makeUpCaptchaResp(String bizSn) {
        CaptchaResp resp = new CaptchaResp();
        resp.setCode("******");
        resp.setCaptchaKey(bizSn);
        resp.setTimestamp(System.currentTimeMillis());
        return resp;
    }
}
