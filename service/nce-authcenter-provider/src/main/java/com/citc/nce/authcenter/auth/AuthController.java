package com.citc.nce.authcenter.auth;

import com.citc.nce.authcenter.auth.service.AuthService;
import com.citc.nce.authcenter.auth.vo.req.*;
import com.citc.nce.authcenter.auth.vo.resp.*;
import com.citc.nce.authcenter.csp.multitenant.service.CspCustomerService;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.google.common.base.Strings;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController()
@Slf4j
public class AuthController implements AuthApi {
    @Resource
    private AuthService authService;
    @Autowired
    private CspCustomerService cspCustomerService;


    @Override
    @ApiOperation("用户注册")
    @PostMapping("/user/register")
    public RegisterResp register(@RequestBody @Valid RegisterReq req) {
        return authService.register(req);
    }

    @Override
    @ApiOperation("用户登录")
    @PostMapping("/user/login")
    public LoginResp login(@RequestBody @Valid LoginReq req) {
        return authService.login(req);
    }

    @Override
    @ApiOperation("用户登出")
    @PostMapping("/user/logout")
    public void logout(@RequestBody @Valid LogoutReq req) {
        authService.logout(req);
    }

    @Override
    @ApiOperation("获取用户基本信息")
    @PostMapping("/user/userInfoDetail")
    public QueryUserInfoDetailResp userInfoDetail(@RequestBody @Valid QueryUserInfoDetailReq req) {
        BaseUser user = SessionContextUtil.getLoginUser();
        if (Objects.equals(Boolean.TRUE, user.getIsCustomer())) {
            return cspCustomerService.getUserInfoDetail(req);
        } else {
            QueryUserInfoDetailResp resp = authService.userInfoDetail(req);
            resp.setCtuId(user.getCtuId());
            return resp;
        }
    }

    @Override
    @ApiOperation("更新用户手机或邮箱")
    @PostMapping("/user/modifyUserPhoneOrEmail")
    public ModifyUserPhoneOrEmailResp modifyUserPhoneOrEmail(@RequestBody @Valid ModifyUserPhoneOrEmailReq req) {
        return authService.modifyUserPhoneOrEmail(req);
    }


    @Override
    @ApiOperation("设置用户图像,用户名")
    @PostMapping("/user/setUserImgOrName")
    public SetUseImgOrNameResp setUserImgOrName(@RequestBody @Valid SetUseImgOrNameVReq req) {
        if (req.isCsp()) {
            return authService.setUserImgOrName(req);
        } else {
            return cspCustomerService.setCusImgOrName(req);
        }

    }

    @Override
    @ApiOperation("校验用户名/手机号等是否被注册")
    @PostMapping("/user/checkRegistered")
    public CheckRegisteredResp checkRegistered(@RequestBody @Valid CheckRegisteredReq req) {
        return authService.checkRegistered(req);
    }

    @Override
    @ApiOperation("校验用户是否绑定邮箱")
    @PostMapping("/user/checkBindEmail")
    public CheckBindEmailResp checkBindEmail() {
        return authService.checkBindEmail();
    }

    @Override
    @ApiOperation("校验账号登录时 用户名/手机号 是否存在")
    @PostMapping("/user/checkLoadNameExist")
    public CheckLoadNameResp checkLoadNameExist(@RequestBody @Valid CheckLoadNameReq req) {
        return authService.checkLoadNameExist(req);
    }

    @Override
    @ApiOperation("邮箱激活")
    @PostMapping("/user/activateEmail")
    public ActivateEmailResp activateEmail(@RequestBody @Valid ActivateEmailReq req) {
        BaseUser user = SessionContextUtil.getUser();
        if (user != null) {
            Boolean isCustomer = user.getIsCustomer();
            if (Boolean.FALSE.equals(isCustomer))
                return authService.activateEmail(req);
            else
                return cspCustomerService.activateEmail(req);
        } else {
            String userId = req.getUserId();
            if (!Strings.isNullOrEmpty(userId) && userId.length() == 15)
                return cspCustomerService.activateEmail(req);
            else
                return authService.activateEmail(req);
        }
    }

    @Override
    @ApiOperation("校验是否本人手机或者邮箱")
    @PostMapping("/user/checkUserInfo")
    public CheckUserInfoResp checkUserInfo(@RequestBody @Valid CheckUserInfoReq req) {
        return authService.checkUserInfo(req);
    }

    @Override
    @ApiOperation("商户信息查询接口")
    @PostMapping("/merchant/queryMerchantInfo")
    public QueryMerchantInfoResp queryMerchantInfo(@RequestBody @Valid QueryMerchantInfoReq req) {
        return authService.queryMerchantInfo(req);
    }

    @Override
    @ApiOperation("商户信息更新接口")
    @PostMapping("/merchant/updateMerchantInfo")
    public UpdateMerchantInfoResp updateMerchantInfo(@RequestBody @Valid UpdateMerchantInfoReq req) {
        return authService.updateMerchantInfo(req);
    }

    /**
     * 用户平台使用权限申请
     */
    @ApiOperation("用户平台使用权限申请")
    @PostMapping("/user/applyPlatformPermission")
    public ApplyPlatformPermissionResp applyPlatformPermission(@RequestBody ApplyPlatformPermissionReq req) {
        return authService.applyPlatformPermission(req);
    }

    @ApiOperation("校验用户是否唯一")
    @PostMapping("/user/checkUserInfoIsUnique")
    @Override
    public Boolean checkUserInfoIsUnique(CheckUserInfoIsUniqueReq req) {
        return authService.checkUserInfoIsUnique(req.getName(), req.getPhone(), null);
    }

    @ApiOperation("注册用户保存")
    @PostMapping("/user/saveForRegister")
    @Override
    public String saveForRegister(SaveForRegisterReq req) {
        RegisterReq registerReq = new RegisterReq();
        BeanUtils.copyProperties(req, registerReq);
        return authService.saveForRegister(registerReq).getUserId();
    }

    @Override
    @PostMapping("/csp/customer/checkCspUserPWIsUpdate")
    public boolean checkCspUserPWIsUpdate(@RequestParam("userId") String userId) {
        return authService.checkCspUserPWIsUpdate(userId);
    }

    @Override
    @PostMapping("/csp/customer/updateCspLoginStatus")
    public void updateCspLoginStatus(@RequestParam("userId") String userId) {
        authService.updateCspLoginStatus(userId);
    }

    @Override
    @ApiOperation("安全--更新用户手机或邮箱")
    @PostMapping("/user/safe/modifyUserPhoneOrEmail")
    public ModifyUserPhoneOrEmailResp modifyUserPhoneOrEmailV(@RequestBody ModifyUserPhoneOrEmailVReq req) {
        Boolean isCustomer = SessionContextUtil.getLoginUser().getIsCustomer();
        if (isCustomer) {
            return cspCustomerService.updatePhoneOrEmail(req);
        }
        return authService.modifyUserPhoneOrEmailV(req);
    }


    @Override
    @ApiOperation("安全--更新用户手机或邮箱")
    @PostMapping("/user/addDyzUser")
    public void addDyzUser(@RequestBody DyzUserReq dyzUserReq) {
        //多因素平台不存用户数据，此处什么都不做
    }

    @Override
    @ApiOperation("根据用户id查询用户信息")
    @GetMapping("/user/userInfoDetailByUserId")
    public UserInfoDetailResp userInfoDetailByUserId(String userId) {
        return authService.userInfoDetailByUserId(userId);
    }

    @Override
    @ApiOperation("记录用户违规数量")
    @PostMapping("/user/recordUnRuleNum")
    public void recordUnRuleNum(UserDetailReq req) {
        authService.recordUnRuleNum(req);
    }

    @Override
    @ApiOperation("根据用户id查询用户信息")
    @PostMapping("/user/getUserBaseInfoByUserId")
    public UserInfo getUserBaseInfoByUserId(String userId) {
        return authService.getUserBaseInfoByUserId(userId);
    }

    @Override
    @ApiOperation("根据用户id集合查询用户信息集合")
    @PostMapping("/user/getUserBaseInfoByUserIds")
    public List<UserInfo> getUserBaseInfoByUserIds(GetUsersInfoReq req) {
        return authService.getUserBaseInfoByUserIds(req);
    }

    @Override
    @ApiOperation("根据名字模糊查询用户信息")
    @PostMapping("/user/queryUserInfoByKey")
    public List<UserInfo> queryUserInfoByKey(String key) {
        return authService.queryUserInfoByKey(key);
    }

    @Override
    @ApiOperation("根据名称查询userId")
    @PostMapping("/user/getUserByName")
    public List<UserResp> getUserByName(String name) {
        return authService.getUserByName(name);
    }

    @Override
    @ApiOperation("根据userId列表查询用户企业信息")
    @PostMapping("/user/getEnterpriseInfoByUserIds")
    public List<UserInfo> getEnterpriseInfoByUserIds(GetUsersInfoReq req) {
        return authService.getEnterpriseInfoByUserIds(req);
    }

    @Override
    public void changeEnableAgentLogin(EnableAgentLoginReq req) {
        cspCustomerService.changeEnableAgentLogin(req);
    }


    @Override
    @PostMapping("/user/inUserDB")
    public Boolean inUserDB(@RequestParam("userId") String userId) {
        return authService.inUserDB(userId);
    }
}
