package com.citc.nce.auth.adminUser.service;

import com.citc.nce.auth.adminUser.vo.req.*;
import com.citc.nce.auth.adminUser.vo.resp.*;
import com.citc.nce.auth.user.vo.req.*;
import com.citc.nce.auth.user.vo.resp.UserIdAndNameResp;
import com.citc.nce.auth.user.vo.resp.UserInfoResp;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 后台管理用户
 *
 * @author huangchong
 */
public interface AdminUserService {

    /**
     * 后台管理用户登陆
     *
     * @param loginReq
     * @return
     */
    AdminUserLoginResp adminUserWebLogin(AdminLoginReq loginReq);

    /**
     * 通过账号查找用户信息，账号可能为用户名  or  手机号
     *
     * @param account
     * @return
     */
    AdminUserInfoResp getAdminUserInfoByAccount(String account);

    /**
     * 管理端dashboard用户统计
     *
     * @return
     */
    DashboardUserStatisticsResp getDashboardUserStatistics();

    /**
     * admin 端修改client 用户信息
     *
     * @param req
     */
    void updateClientUserInfo(UpdateClientUserReq req);

    /**
     * Admin端 查看client端 userId 用户认证信息
     */
    ClientUserIdentificationResp getClientUserIdentifications(ClientUserIdentificationReq req);

    /**
     * Admin端 审核client端 userId 用户认证
     */
    void auditIdentification(AuditIdentificationReq req);

    Boolean checkLoadNameExist(String checkValue);

    PageResult getManageUser(ManageUserReq req);

    PageResult getPlatformApplicationReview(PlatformApplicationReviewReq req);

    void reviewPlatformApplication(ReviewReq req);

    void reviewPlatformForCsp(ReviewCspReq req);

    List<ApprovalLogResp> getReviewLog(UuidReq req);

    void enableOrDisableUser(EnableOrDisableReq req);

    List<UserIdAndNameResp> getUserInfo(UserIdReq req);

    List<UserInfoResp> getUserInfoList(KeyWordReq req);

    Map<String, UserInfoResp> getUserInfoByUserId(CodeListReq req);

    AdminUserInfoResp getAdminUserInfoByUserId(String adminUserId);

    List<AdminRoleResp> getUserRoleList();

    PageResult getOperatorList(OperatorReq req);

    void enableOrDisableAdminUser(DisableOrEnableReq req);

    List<AdminUserResp> getMerberByRoleId(CodeReq req);

    List<RoleAndMenuResp> getMenuByRoleId(CodeReq req);

    void roleConfigurationMenu(RoleAndMenuReq req);

    List<AdminUserResp> findWorkOrderPermissionUsers();

    AdminUserSumResp getPlatformApplicationReviewSum();

    AdminUserResp getAdminUserByUserId(UserIdReq req);

    boolean checkIsAdmin(String userId);
}
