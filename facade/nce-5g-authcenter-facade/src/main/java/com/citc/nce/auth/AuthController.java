package com.citc.nce.auth;


import com.citc.nce.auth.vo.SetUserAvatar;
import com.citc.nce.auth.vo.SetUserName;
import com.citc.nce.authcenter.Utils.UtilsApi;
import com.citc.nce.authcenter.auth.AuthApi;
import com.citc.nce.authcenter.auth.vo.req.*;
import com.citc.nce.authcenter.auth.vo.resp.*;
import com.citc.nce.authcenter.auth.vo.req.ApplyPlatformPermissionReq;
import com.citc.nce.authcenter.auth.vo.resp.ApplyPlatformPermissionResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.facadeserver.verifyCaptcha.VerifyCaptchaV2;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.common.log.enums.OperatorType;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.web.utils.dh.ECDHService;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.web.utils.GetIpAddr;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "用户认证-基础服务")
@Slf4j
@RestController
@RefreshScope
public class AuthController {

    @Resource
    private AuthApi authApi;
    @Resource
    private UtilsApi utilsApi;
    @Resource
    private RedisService redisService;
    @Resource
    private ECDHService ecdhService;


    /**
     * 用户注册
     *
     * @param req 请求信息
     * @return 响应信息
     */
    @ApiOperation("用户注册")
    @PostMapping("/user/register")
    @Log(title = "CSP侧-用户注册", operatorType = OperatorType.CSP, isSaveRequestData = false, isSaveResponseData = false)
    @SkipToken
    public RegisterResp register(@RequestBody RegisterReq req) {
        return authApi.register(req);
    }


    /**
     * 用户登录
     *
     * @param req 请求信息
     * @return 响应信息
     */
    @ApiOperation("csp用户登录")
    @PostMapping("/user/login")
    @Log(title = "CSP侧-用户登录", operatorType = OperatorType.CSP, isSaveRequestData = false, isSaveResponseData = false)
    public LoginResp login(@RequestBody LoginReq req) {
        return authApi.login(req);
    }

    /**
     * 用户 csp 登出
     */
    @ApiOperation("用户登出")
    @PostMapping("/user/logout")
    @Log(title = "CSP侧-用户登出", operatorType = OperatorType.CSP, isSaveRequestData = false, isSaveResponseData = false)
    public void logout(@RequestBody @Valid LogoutReq req) {
        authApi.logout(req);
    }


    /**
     * 获取用户基本信息
     */
    @ApiOperation("获取用户基本信息")
    @PostMapping("/user/userInfoDetail")
    public QueryUserInfoDetailResp userInfoDetail(@RequestBody @Valid QueryUserInfoDetailReq req) {
        QueryUserInfoDetailResp body = authApi.userInfoDetail(req);
        body.setPhone(ecdhService.encode(body.getPhone()));
        return body;
    }


    /**
     * 用户手机号修改
     *
     * @return 执行结果
     */
    @ApiOperation("更新用户手机或邮箱")
    @PostMapping("/user/modifyUserPhoneOrEmail")
    @Log(title = "CSP侧-更新用户手机或邮箱", operatorType = OperatorType.CSP)
    public ModifyUserPhoneOrEmailResp modifyUserPhoneOrEmail(@RequestBody ModifyUserPhoneOrEmailVReq req) {
        String key = "myVerifyCaptchaV2:" + req.getMyVerifyCaptchaV2();
        if (redisService.hasKey(key)) {
            ModifyUserPhoneOrEmailResp resp = authApi.modifyUserPhoneOrEmailV(req);
            if (resp.getExecResult()) {
                //修改成功在删除redis值
                redisService.deleteObject(key);
            }
            return resp;
        }
        throw new BizException("用户身份失效,请重新获取原验证码");
    }


    /**
     * 设置用户图像 or  用户账号名
     */
    @ApiOperation("设置用户图像用户账号名")
    @PostMapping("/user/setUserImgOrName")
    @Log(title = "CSP侧-设置用户图像用户账号名", operatorType = OperatorType.CSP)
    public SetUseImgOrNameResp setUserImgOrName(@RequestBody SetUseImgOrNameVReq req) {
        return authApi.setUserImgOrName(req);

    }

    @ApiOperation("设置用户账号名")
    @PostMapping("/user/setUserName")
    @VerifyCaptchaV2
    public SetUseImgOrNameResp setUserImgOrName(@RequestBody @Valid SetUserName req) {
        BaseUser loginUser = SessionContextUtil.getLoginUser();
        SetUseImgOrNameVReq vReq = new SetUseImgOrNameVReq();
        vReq.setUserId(loginUser.getUserId());
        vReq.setName(req.getName());
        vReq.setCsp(!loginUser.getIsCustomer());
        return authApi.setUserImgOrName(vReq);
    }

    /**
     * 设置用户图像
     */
    @ApiOperation("设置用户头像")
    @PostMapping("/user/setUserAvatar")
    public SetUseImgOrNameResp setUserImgOrName(@RequestBody @Valid SetUserAvatar req) {
        BaseUser loginUser = SessionContextUtil.getLoginUser();
        SetUseImgOrNameVReq vReq = new SetUseImgOrNameVReq();
        vReq.setUserId(loginUser.getUserId());
        vReq.setUserImg(req.getUserImg());
        vReq.setCsp(!loginUser.getIsCustomer());
        return authApi.setUserImgOrName(vReq);
    }

    //    /**
//     * 校验用户名/手机号等是否被注册
//     * EMAIL(1,"mail"),
//     * NAME(2,"name"),
//     * PHONE(3,"phone");
//     * @return true 已注册   false 未注册
//     */
    @ApiOperation("校验用户名/手机号等是否被注册")
    @PostMapping("/user/checkRegistered")
    public CheckRegisteredResp checkRegistered(HttpServletRequest request, @RequestBody CheckRegisteredReq req) {
        String ip = GetIpAddr.getIpAddr(request);
        utilsApi.checkIPVisitCount(ip);
        return authApi.checkRegistered(req);
    }

    @ApiOperation("校验用户是否绑定邮箱")
    @PostMapping("/user/checkBindEmail")
    public CheckBindEmailResp checkBindEmail() {
        return authApi.checkBindEmail();
    }

    /**
     * 校验账号登录时 用户名/手机号 是否存在
     * EMAIL(1,"mail"),
     * NAME(2,"name"),
     * PHONE(3,"phone");
     *
     * @return true 已注册   false 未注册
     */
    @ApiOperation("校验账号登录时 用户名/手机号 是否存在")
    @PostMapping("/user/checkLoadNameExist")
    public CheckLoadNameResp checkLoadNameExist(@RequestBody CheckLoadNameReq req) {
        return authApi.checkLoadNameExist(req);
    }


    /**
     * 邮箱激活
     *
     * @param req 请求信息
     * @return 响应信息
     */
    @ApiOperation("邮箱激活")
    @PostMapping("/user/activateEmail")
    @Log(title = "CSP侧-邮箱激活", operatorType = OperatorType.CSP)
    public ActivateEmailResp activateEmail(@RequestBody ActivateEmailReq req) {
        return authApi.activateEmail(req);
    }

    /**
     * 校验是否本人手机或者邮箱
     *
     * @param req 请求信息
     * @return {@link Boolean}
     */
    @ApiOperation("校验是否本人手机或者邮箱")
    @PostMapping("/user/checkUserInfo")
    public CheckUserInfoResp checkUserInfo(@RequestBody CheckUserInfoReq req) {
        return authApi.checkUserInfo(req);
    }

    @ApiOperation("商户信息查询接口")
    @PostMapping("/merchant/queryMerchantInfo")
    public QueryMerchantInfoResp queryMerchantInfo(@RequestBody @Valid QueryMerchantInfoReq req) {
        return authApi.queryMerchantInfo(req);
    }

    @ApiOperation("商户信息更新接口")
    @PostMapping("/merchant/updateMerchantInfo")
    public UpdateMerchantInfoResp updateMerchantInfo(@RequestBody @Valid UpdateMerchantInfoReq req) {
        return authApi.updateMerchantInfo(req);
    }

    /**
     * 用户平台使用权限申请
     */
    @ApiOperation("用户端--用户平台使用权限申请")
    @PostMapping("/user/applyPlatformPermission")
    public ApplyPlatformPermissionResp applyPlatformPermission(@RequestBody ApplyPlatformPermissionReq req) {
        req.setUserId(SessionContextUtil.getLoginUser().getUserId());
        return authApi.applyPlatformPermission(req);
    }

    /**
     * 用户代登录开关开启/关闭
     */
    @ApiOperation("用户端--用户代登录开关开启/关闭")
    @PostMapping("/user/changeEnableAgentLogin")
    public void changeEnableAgentLogin(@RequestBody EnableAgentLoginReq req) {
        authApi.changeEnableAgentLogin(req);
    }


}
