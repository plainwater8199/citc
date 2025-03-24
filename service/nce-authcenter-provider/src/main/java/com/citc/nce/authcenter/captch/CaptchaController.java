package com.citc.nce.authcenter.captch;

import com.citc.nce.authcenter.admin.entity.AdminUserDo;
import com.citc.nce.authcenter.admin.service.AdminUserService;
import com.citc.nce.authcenter.auth.service.AuthService;
import com.citc.nce.authcenter.captch.service.CaptchaService;
import com.citc.nce.authcenter.captcha.CaptchaApi;
import com.citc.nce.authcenter.captcha.vo.req.CaptchaCheckReq;
import com.citc.nce.authcenter.captcha.vo.req.EmailCaptchaReq;
import com.citc.nce.authcenter.captcha.vo.req.SelfEmailCaptchaReq;
import com.citc.nce.authcenter.captcha.vo.req.SmsCaptchaReq;
import com.citc.nce.authcenter.captcha.vo.resp.CaptchaCheckResp;
import com.citc.nce.authcenter.captcha.vo.resp.CaptchaResp;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.csp.domain.CspTrumpetUser;
import com.citc.nce.authcenter.csp.multitenant.entity.Csp;
import com.citc.nce.authcenter.csp.multitenant.entity.CspCustomer;
import com.citc.nce.authcenter.csp.multitenant.service.CspCustomerService;
import com.citc.nce.authcenter.csp.multitenant.service.CspService;
import com.citc.nce.authcenter.csp.multitenant.utils.CspUtils;
import com.citc.nce.authcenter.csp.trumpet.service.ICspTrumpetUserService;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.authcenter.user.entity.UserDo;
import com.citc.nce.authcenter.user.service.UserService;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@Slf4j
public class CaptchaController implements CaptchaApi {
    @Resource
    private CaptchaService captchaService;
    @Resource
    private UserService userService;
    /**
     * csp 得小号
     */
    @Resource
    private ICspTrumpetUserService trumpetUserService;

    @Resource
    private AdminUserService adminUserService;
    @Resource
    private AuthService authService;

    @Autowired
    private CspService cspService;

    @Autowired
    private CspCustomerService cspCustomerService;


    /**
     * 获取短信验证码
     *
     * @param smsCaptchaReq
     * @return
     */
    @Override
    @PostMapping("/captcha/getSmsCaptcha")
    public CaptchaResp getSmsCaptcha(@RequestBody @Valid SmsCaptchaReq smsCaptchaReq) {

        //csp表中查询用户
        UserDo userDo = userService.findByPhone(smsCaptchaReq.getPhone());
        if (Objects.nonNull(userDo)) {
            throw new BizException(AuthCenterError.USER_PHONE_REPEAT);
        }
        //csp小号表中查询用户
        CspTrumpetUser trumpetUser = trumpetUserService.getByPhone(smsCaptchaReq.getPhone());
        if (Objects.nonNull(trumpetUser)) {
            throw new BizException(AuthCenterError.USER_PHONE_REPEAT);
        }
        //返回短信验证码
        return captchaService.getDyzSmsCaptcha(smsCaptchaReq);

    }

    /**
     * 多因子发送验证码
     *
     * @param smsCaptchaReq
     * @return
     */
    @Override
    @PostMapping("/captcha/getDyzSmsCaptcha")
    public CaptchaResp getDyzSmsCaptcha(@RequestBody @Valid SmsCaptchaReq smsCaptchaReq) {
        String logInStr = smsCaptchaReq.getPhone();
        //cus登陆
        if (Boolean.TRUE.equals(smsCaptchaReq.getIsCustomer())) {
            //验证cspId
            String cspId = getCspId(smsCaptchaReq.getUrl());
            if (!StringUtils.hasLength(cspId)) {
                throw new BizException("登陆地址错误");
            }

            List<CspCustomer> customers = cspCustomerService.lambdaQuery()
                    .eq(CspCustomer::getCspId, cspId)
                    .eq(CspCustomer::getPhone, logInStr)
                    .list();
            if (CollectionUtils.isEmpty(customers)) {
                return captchaService.getErrorCode(smsCaptchaReq.getPhone());
            }
            CspCustomer customer = customers.get(0);
            smsCaptchaReq.setPhone(customer.getPhone());
            return captchaService.getDyzSmsCaptcha(smsCaptchaReq);
        }

        //管理员登陆
        if (Integer.valueOf(2).equals(smsCaptchaReq.getDyzType())) {
            //管理平台（只有手机号登陆）
            AdminUserDo adminUserDo = adminUserService.findByPhone(logInStr);
            if (Objects.isNull(adminUserDo)) {
                return captchaService.getErrorCode(smsCaptchaReq.getPhone());
            }

            smsCaptchaReq.setPhone(adminUserDo.getPhone());
            return captchaService.getDyzSmsCaptcha(smsCaptchaReq);
        }

        //csp登陆
        UserDo userDo = getUserDo(smsCaptchaReq, logInStr);
        if (Objects.isNull(userDo)) {
            return captchaService.getErrorCode(smsCaptchaReq.getPhone());
        }

        //3、校验用户信息是否需要勾选已阅读法务文件
        int isAgreement = authService.checkIsAgreement(userDo);
        smsCaptchaReq.setPhone(userDo.getPhone());
        //3、返回短信验证码
        CaptchaResp resp = captchaService.getDyzSmsCaptcha(smsCaptchaReq);
        resp.setIsAgreement(isAgreement);
        resp.setUserIdentity(Objects.nonNull(userDo.getCtuId()) ? AuthService.CSP_CTU_ID : AuthService.USER);
        return resp;

    }


    private UserDo getUserDo(SmsCaptchaReq smsCaptchaReq, String var) {
        UserDo userDo;
        if (Integer.valueOf(1).equals(smsCaptchaReq.getLoginType())) {
            //账号
            userDo = userService.findUserByNameOrPhoneOrEmail(var, var, null);
            if (Objects.isNull(userDo)) {
                CspTrumpetUser account = trumpetUserService.getByAccountName(var);
                if (Objects.isNull(account)) {
                    account = trumpetUserService.getByPhone(var);
                }
                if (Objects.isNull(account)) {
                    throw new BizException(AuthCenterError.ACCOUNT_PWD_WRONG);
                }
                userDo = convertCspTrumpetUser(account);
            }
        } else {
            //手机号
            userDo = userService.findByPhone(var);
            if (Objects.isNull(userDo)) {
                userDo = convertCspTrumpetUser(trumpetUserService.getByPhone(var));
            }
        }
        return userDo;
    }


    /**
     * 把小号的csp账号查询出来返回
     *
     * @param account 小号
     * @return csp账号
     */
    private UserDo convertCspTrumpetUser(CspTrumpetUser account) {
        if (Objects.isNull(account)) return null;
        //查询csp数据
        Csp csp = cspService.getCspByCspId(account.getCspId());
        if (Objects.isNull(csp)) {
            throw new BizException(AuthCenterError.ACCOUNT_USER_ABSENT);
        }
        //查询csp是否被禁止登陆
        if (!cspService.getCspStatus(csp.getCspId())) {
            throw new BizException(AuthCenterError.ACCOUNT_IS_FORBIDDEN);
        }
        //汪鹏说自己查询csp账号  csp账号勾过协议就算数
        UserDo userDo = userService.userInfoDetailByUserId(csp.getUserId());
        userDo.setCtuId(account.getCtuId());
        userDo.setPhone(account.getPhone()); //把csp的手机号修改为自己的手机号，用于验证码验证
        return userDo;
    }


    private String getCspId(String loginAddress) {
        String cspId = null;
        if (loginAddress.contains("/")) {
            String[] split = loginAddress.split("/");
            String addressCode = split[split.length - 1];
            if (addressCode.contains("?")) {
                addressCode = addressCode.substring(0, addressCode.indexOf("?"));
            }
            cspId = CspUtils.decodeCspId(addressCode);
        }
        return cspId;
    }

    /**
     * 获取邮箱验证码
     *
     * @param emailCaptchaReq
     * @return
     */
    @Override
    @PostMapping("/captcha/getEmailCaptcha")
    public CaptchaResp getEmailCaptcha(@RequestBody @Valid EmailCaptchaReq emailCaptchaReq) {
        //2、type=1，校验已注册邮箱，type=0,校验未注册邮箱
        UserDo userDo = userService.findByEmail(emailCaptchaReq.getEmail());
        Integer type = emailCaptchaReq.getType();
        if (type == 1) {
            if (emailCaptchaReq.getIsCustomer()) {
                if (!cspCustomerService.lambdaQuery()
                        .eq(emailCaptchaReq.getIsCustomer(), CspCustomer::getCspId, SessionContextUtil.getLoginUser().getCspId())
                        .eq(CspCustomer::getMail, emailCaptchaReq.getEmail()).exists())
                    throw new BizException(AuthCenterError.EMAIL_NOT_REGISTERED);
            } else {
                if (userDo == null) {
                    throw new BizException(AuthCenterError.EMAIL_NOT_REGISTERED);
                }
            }
        } else if (type == 0) {
            if (emailCaptchaReq.getIsCustomer()) {
                if (cspCustomerService.lambdaQuery()
                        .eq(emailCaptchaReq.getIsCustomer(), CspCustomer::getCspId, SessionContextUtil.getLoginUser().getCspId())
                        .eq(CspCustomer::getMail, emailCaptchaReq.getEmail()).exists())
                    throw new BizException(AuthCenterError.EMAIL_REGISTERED);
            } else {
                if (userDo != null) {
                    throw new BizException(AuthCenterError.EMAIL_REGISTERED);
                }
            }
        } else {
            throw new BizException(AuthCenterError.EMAIL_TYPE_ERROR);
        }
        //3、返回短信验证码
        return captchaService.getEmailCaptcha(emailCaptchaReq);

    }

    /**
     * 校验验证码
     *
     * @param captchaCheckReq
     * @return
     */
    @Override
    @PostMapping("/captcha/checkCaptcha")
    public CaptchaCheckResp checkCaptcha(@RequestBody @Valid CaptchaCheckReq captchaCheckReq) {
        return captchaService.checkCaptcha(captchaCheckReq);
    }

    @Override
    public CaptchaResp getSelfDyzSmsCaptcha(@RequestBody @Valid SmsCaptchaReq smsCaptchaReq) {
        //1、校验验证码是否正常
        //移动到facade
        BaseUser baseUser = SessionContextUtil.getUser();
        if (baseUser != null) {
            String userId = baseUser.getUserId();
            if (!Strings.isNullOrEmpty(userId)) {
                String phone = baseUser.getIsCustomer()
                        ? Optional.ofNullable(cspCustomerService.queryById(userId))
                        .map(UserInfoVo::getPhone)
                        .orElse(null)
                        : Optional.ofNullable(userService.findByUserId(userId))
                        .map(UserDo::getPhone)
                        .orElse(null);
                if (phone != null) {
                    if (smsCaptchaReq.getPhone().equals(phone)) {
                        //3、返回短信验证码
                        return captchaService.getDyzSmsCaptcha(smsCaptchaReq);
                    }
                    throw new BizException(AuthCenterError.USER_NOT_SELF);
                }
                throw new BizException(AuthCenterError.ACCOUNT_USER_ABSENT);
            }
        }
        throw new BizException(AuthCenterError.USER_NOT_LOGIN);
    }

    @Override
    public CaptchaResp getSelfEmailCaptcha(@RequestBody @Valid SelfEmailCaptchaReq emailCaptchaReq) {
        //1、校验验证码是否正常
        BaseUser baseUser = SessionContextUtil.getUser();
        if (baseUser != null) {
            String userId = baseUser.getUserId();
            if (!Strings.isNullOrEmpty(userId)) {
                String email = baseUser.getIsCustomer()
                        ? Optional.ofNullable(cspCustomerService.queryById(userId))
                        .map(UserInfoVo::getMail)
                        .orElse(null)
                        : Optional.ofNullable(userService.findByUserId(userId))
                        .map(UserDo::getMail)
                        .orElse(null);
                if (email != null) {
                    if (emailCaptchaReq.getEmail().equals(email)) {
                        //3、返回短信验证码
                        EmailCaptchaReq emailCaptcha = new EmailCaptchaReq();
                        BeanUtils.copyProperties(emailCaptchaReq, emailCaptcha);
                        return captchaService.getEmailCaptcha(emailCaptcha);
                    }
                    throw new BizException(AuthCenterError.USER_NOT_SELF);
                }
                throw new BizException(AuthCenterError.ACCOUNT_USER_ABSENT);
            }
        }
        throw new BizException(AuthCenterError.USER_NOT_LOGIN);
    }

}
