package com.citc.nce.authcenter.auth;

import com.citc.nce.authcenter.auth.vo.req.*;
import com.citc.nce.authcenter.auth.vo.resp.*;
import com.citc.nce.authcenter.auth.vo.req.ApplyPlatformPermissionReq;
import com.citc.nce.authcenter.auth.vo.resp.ApplyPlatformPermissionResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "用户--认证模块")
@FeignClient(value = "authcenter-service", contextId = "user", url = "${authCenter:}")
public interface AuthApi {
    @ApiOperation("用户注册")
    @PostMapping("/user/register")
    RegisterResp register(@RequestBody RegisterReq req);
    @ApiOperation("用户登录")
    @PostMapping("/user/login")
    LoginResp login(@RequestBody LoginReq req);
    @ApiOperation("用户登出")
    @PostMapping("/user/logout")
    void logout(@RequestBody @Valid LogoutReq req);
    @ApiOperation("获取用户基本信息")
    @PostMapping("/user/userInfoDetail")
    QueryUserInfoDetailResp userInfoDetail(@RequestBody QueryUserInfoDetailReq req);
    @ApiOperation("更新用户手机或邮箱")
    @PostMapping("/user/modifyUserPhoneOrEmail")
    ModifyUserPhoneOrEmailResp modifyUserPhoneOrEmail(@RequestBody ModifyUserPhoneOrEmailReq req);

    @ApiOperation("设置用户图像、账号名")
    @PostMapping("/user/setUserImgOrName")
    SetUseImgOrNameResp setUserImgOrName(@RequestBody SetUseImgOrNameVReq req);
    @ApiOperation("校验用户名/手机号等是否被注册")
    @PostMapping("/user/checkRegistered")
    CheckRegisteredResp checkRegistered(@RequestBody CheckRegisteredReq req);
    @ApiOperation("校验用户是否绑定邮箱")
    @PostMapping("/user/checkBindEmail")
    CheckBindEmailResp checkBindEmail();
    @ApiOperation("校验账号登录时 用户名/手机号 是否存在")
    @PostMapping("/user/checkLoadNameExist")
    CheckLoadNameResp checkLoadNameExist(@RequestBody CheckLoadNameReq req);
    @ApiOperation("邮箱激活")
    @PostMapping("/user/activateEmail")
    ActivateEmailResp activateEmail(@RequestBody ActivateEmailReq req);

    @ApiOperation("校验是否本人手机或者邮箱")
    @PostMapping("/user/checkUserInfo")
    CheckUserInfoResp checkUserInfo(@RequestBody CheckUserInfoReq req);
    @ApiOperation("商户信息查询接口")
    @PostMapping("/merchant/queryMerchantInfo")
    QueryMerchantInfoResp queryMerchantInfo(@RequestBody QueryMerchantInfoReq req);

    @ApiOperation("商户信息更新接口")
    @PostMapping("/merchant/updateMerchantInfo")
    UpdateMerchantInfoResp updateMerchantInfo(@RequestBody UpdateMerchantInfoReq req);

    @ApiOperation("用户平台使用权限申请")
    @PostMapping("/user/applyPlatformPermission")
    ApplyPlatformPermissionResp applyPlatformPermission(@RequestBody @Valid ApplyPlatformPermissionReq req);

    @ApiOperation("校验用户是否唯一")
    @PostMapping("/user/checkUserInfoIsUnique")
    Boolean checkUserInfoIsUnique(@RequestBody CheckUserInfoIsUniqueReq req);

    @ApiOperation("注册用户保存")
    @PostMapping("/user/saveForRegister")
    String saveForRegister(@RequestBody @Valid SaveForRegisterReq req);

    @PostMapping("/csp/customer/checkCspUserPWIsUpdate")
    boolean checkCspUserPWIsUpdate(@RequestParam("userId") String userId);
    @PostMapping("/csp/customer/updateCspLoginStatus")
    void updateCspLoginStatus(@RequestParam("userId") String userId);
    @ApiOperation("安全--更新用户手机或邮箱")
    @PostMapping("/user/safe/modifyUserPhoneOrEmail")
    ModifyUserPhoneOrEmailResp modifyUserPhoneOrEmailV(@RequestBody ModifyUserPhoneOrEmailVReq req);


    @ApiOperation("安全--更新用户手机或邮箱")
    @PostMapping("/user/addDyzUser")
    void addDyzUser(@RequestBody DyzUserReq dyzUserReq);



    @ApiOperation("根据用户id查询用户信息")
    @GetMapping("/user/userInfoDetailByUserId")//WATER
    UserInfoDetailResp userInfoDetailByUserId(@RequestParam("userId") String userId);

    @ApiOperation("记录用户违规数量")
    @PostMapping("/user/recordUnRuleNum")//WATER
    void recordUnRuleNum(@RequestBody UserDetailReq req);

    @ApiOperation("根据用户id查询用户信息")
    @PostMapping("/user/getUserBaseInfoByUserId")//WATER
    UserInfo getUserBaseInfoByUserId(@RequestParam("userId") String userId);

    @ApiOperation("根据用户id集合查询用户信息集合")
    @PostMapping("/user/getUserBaseInfoByUserIds")//WATER
    List<UserInfo> getUserBaseInfoByUserIds(@RequestBody GetUsersInfoReq req);

    @ApiOperation("根据名字模糊查询用户信息")
    @PostMapping("/user/queryUserInfoByKey")//WATER
    List<UserInfo> queryUserInfoByKey(@RequestParam("key") String key);

    @ApiOperation("根据名称查询userId")
    @PostMapping("/user/getUserByName")//WATER
    List<UserResp> getUserByName(@RequestBody @Valid String name);

    @ApiOperation("根据userId列表查询用户企业信息")
    @PostMapping("/user/getEnterpriseInfoByUserIds")//WATER
    List<UserInfo> getEnterpriseInfoByUserIds(@RequestBody GetUsersInfoReq req);

    @ApiOperation("用户是否在user表中")
    @PostMapping("/user/inUserDB")
    Boolean inUserDB(@RequestParam("userId") String userId);
    @PostMapping("/user/changeEnableAgentLogin")
    void changeEnableAgentLogin(@RequestBody EnableAgentLoginReq req);
}
