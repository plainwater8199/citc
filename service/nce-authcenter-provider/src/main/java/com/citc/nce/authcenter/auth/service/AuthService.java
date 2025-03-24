package com.citc.nce.authcenter.auth.service;

import com.citc.nce.authcenter.auth.vo.req.*;
import com.citc.nce.authcenter.auth.vo.resp.*;
import com.citc.nce.authcenter.user.entity.UserDo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AuthService {
    String USER = "user";
    String CSP_CTU_ID = "csp-ctuId";
    String CSP_CUSTOMER = "csp-customer";


    /**
     * 用户注册
     *
     * @param req 请求信息
     * @return 响应信息
     */
    RegisterResp register(RegisterReq req);

    /**
     * 用户登录
     *
     * @param req 请求信息
     * @return 响应信息
     */
    LoginResp login(LoginReq req);

    /**
     * 用户登出
     */
    void logout(LogoutReq req);


    /**
     * 用户信息查询
     * @param req 请求信息
     * @return 响应信息
     */
    QueryUserInfoDetailResp userInfoDetail(QueryUserInfoDetailReq req);

    /**
     * 更新用户手机或邮箱
     * @param req 请求信息
     * @return 响应信息
     */
    ModifyUserPhoneOrEmailResp modifyUserPhoneOrEmail(ModifyUserPhoneOrEmailReq req);


    /**
     * 设置用户图像、账号名
     * @param req 请求信息
     * @return 响应结果
     */
    SetUseImgOrNameResp setUserImgOrName(SetUseImgOrNameVReq req);

    /**
     * 校验用户名/手机号等是否被注册
     * @param req 请求信息
     * @return 响应结果
     */
    CheckRegisteredResp checkRegistered(CheckRegisteredReq req);

    /**
     * 校验用户是否绑定邮箱
     * @return 响应结果
     */
    CheckBindEmailResp checkBindEmail();

    /**
     * 校验账号登录时 用户名/手机号 是否存在
     * @param req 请求信息
     * @return 响应结果
     */
    CheckLoadNameResp checkLoadNameExist(CheckLoadNameReq req);

    /**
     * 邮箱激活
     * @param req 请求信息
     * @return 响应结果
     */
    ActivateEmailResp activateEmail(ActivateEmailReq req);

    /**
     * 校验是否本人手机或者邮箱
     * @param req 请求信息
     * @return 响应结果
     */
    CheckUserInfoResp checkUserInfo(CheckUserInfoReq req);

    /**
     * 商户信息查询接口
     * @param req 请求信息
     * @return 响应结果
     */
    QueryMerchantInfoResp queryMerchantInfo(QueryMerchantInfoReq req);

    /**
     * 更新商户的基本信息
     * @param req 请求信息
     * @return 响应信息
     */
    UpdateMerchantInfoResp updateMerchantInfo(UpdateMerchantInfoReq req);

    /**
     * 用户平台使用权限申请
     * @param req 请求信息
     * @return 响应信息
     */
    ApplyPlatformPermissionResp applyPlatformPermission(ApplyPlatformPermissionReq req);

    /**
     * 校验用户是否唯一
     * @param name 用户账户名
     * @param phone 用户手机号码
     * @return false：用户信息已存在，ture：用户信息不存在
     */
    Boolean checkUserInfoIsUnique(String name, String phone, String email);

    /**
     * 注册用户保存
     * @param req 注册请求信息
     * @return 注册用户
     */
    UserDo saveForRegister(RegisterReq req);

    boolean checkCspUserPWIsUpdate(String userId);

    void updateCspLoginStatus(String userId);

    ModifyUserPhoneOrEmailResp modifyUserPhoneOrEmailV(ModifyUserPhoneOrEmailVReq req);



    UserInfoDetailResp userInfoDetailByUserId(String userId);

    void recordUnRuleNum(UserDetailReq req);

    UserInfo getUserBaseInfoByUserId(String userId);

    List<UserInfo> getUserBaseInfoByUserIds(GetUsersInfoReq req);

    List<UserInfo> queryUserInfoByKey(String key);

    List<UserResp> getUserByName(String name);

    List<UserInfo> getEnterpriseInfoByUserIds(GetUsersInfoReq req);

    int checkIsAgreement(UserDo userDo);

    Boolean inUserDB(String userId);
}
