package com.citc.nce.authcenter.auth.service;

import com.citc.nce.authcenter.admin.dto.UserAndRoleDto;
import com.citc.nce.authcenter.auth.vo.AdminUserInfo;
import com.citc.nce.authcenter.auth.vo.CspCustomerChatbotAccountVo;
import com.citc.nce.authcenter.auth.vo.UserExcel;
import com.citc.nce.authcenter.auth.vo.UserInfo;
import com.citc.nce.authcenter.auth.vo.req.*;
import com.citc.nce.authcenter.auth.vo.resp.*;
import com.citc.nce.authcenter.tempStorePerm.bean.ChangePrem;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.core.pojo.RestResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AdminAuthService {
    /**
     * 管理端-获取客户端用户详情
     * @param req 请求信息
     * @return 响应结果
     */
    ClientUserDetailsResp getClientUserDetails(ClientUserDetailsReq req);

    /**
     * 管理端-编辑客户端用户信息
     * @param req 请求信息
     * @return 响应结果
     */
    UpdateClientUserResp updateClientUserInfo(UpdateClientUserReq req);

    /**
     * 管理端-删除客户端用户信息
     * @param req 删除用户信息
     * @return 响应结果
     */
    DeleteClientUserResp deleteClientUserInfo(DeleteClientUserReq req);

    /**
     * 管理端-检查用户名/手机号是否存在
     * @param req 请求信息
     * @return 响应信息
     */
    CheckLoadNameResp checkLoadNameExist(CheckLoadNameReq req);

    /**
     * 管理端-用户登录
     * @param req 请求信息
     * @return 响应结果
     */
    AdminUserLoginResp adminUserWebLogin(AdminLoginReq req);

    /**
     * 管理端-用户退出登录
     */
    void logout();

    /**
     * 核能商城，硬核桃，chatbot和统一用户管理员查询列表
     * @param req 请求信息
     * @return 响应结果
     */
    PageResult getManageUser(QueryManageUserReq req);

    /**
     * 平台使用申请审核列表
     * @param req 请求消息
     * @return  响应消息
     */
    PageResult<UserInfo> getPlatformApplicationReview(PlatformApplicationReviewReq req);


    /**
     * 管理平台--审核用户平台使用申请
     * @param req 请求信息
     * @return 响应结果
     */
    ReviewPlatAppResp reviewPlatformApplication(ReviewPlatAppReq req);

    /**
     * 管理平台--查看平台申请审核日志
     * @param req 请求信息
     * @return 响应结果
     */
    GetReviewLogListResp getReviewLogList(GetReviewLogListReq req);

    /**
     * 启用禁用平台用户
     * @param req 请求消息
     * @return 响应消息
     */
    EnableOrDisableResp enableOrDisableUser(EnableOrDisableReq req);

    /**
     * 级联查看用户信息
     * @param req 请求消息
     * @return 响应消息
     */
    GetUserInfoResp getUserInfo(GetUserInfoReq req);

    /**
     * 获取企业用户信息
     * @return 返回信息
     */
    GetEnterpriseUserListResp getEnterpriseUserList();

    /**
     * 查询用户角色列表
     * @return 返回信息
     */
    GetUserRoleListResp getUserRoleList();

    /**
     * 查询运营管理人员列表
     * @param req 请求信息
     * @return 响应信息
     */
    PageResult<UserAndRoleDto> getOperatorList(GetOperatorListReq req);

    /**
     * 启用或禁用运营管理人员
     * @param req 请求信息
     * @return 响应结果
     */
    EnableOrDisableAdminUserResp enableOrDisableAdminUser(EnableOrDisableAdminUserReq req);

    /**
     * 新增或编辑运营管理人员
     * @param req 请求信息
     * @return 响应结果
     */
    EditOperatorResp editOperator(EditOperatorReq req);

    /**
     * 查询角色管理列表
     * @param req 请求信息
     * @return 响应信息
     */
    PageResult getRolePage(GetRolePage req);

    /**
     * 启用禁用角色管理
     * @param req 请求信息
     * @return 响应结果
     */
    EnableOrDisableRoleResp enableOrDisableRole(EnableOrDisableRoleReq req);

    /**
     * 新增或编辑角色
     * @param req 请求信息
     * @return 响应信息
     */
    EditRoleResp editRole(EditRoleReq req);

    /**
     * 删除角色
     * @param req  请求信息
     * @return 响应信息
     */
    RemoveRoleResp removeRole(RemoveRoleReq req);

    /**
     * 根据角色查询成员
     * @param req 请求信息
     * @return 响应结果
     */
    GetMemberByRoleIdResp getMemberByRoleId(GetMemberByRoleIdReq req);

    /**
     * 角色配置成员
     * @param req 请求信息
     * @return 响应信息
     */
    RoleConfigurationMemberResp roleConfigurationMember(RoleConfigurationMemberReq req);

    /**
     * 根据角色查询菜单资源
     * @param req 请求信息
     * @return 响应结果
     */
    GetMenuByRoleIdResp getMenuByRoleId(GetMenuByRoleIdReq req);

    /**
     * 角色添加菜单资源
     * @param req 请求信息
     * @return 响应结果
     */
    RoleConfigurationMenuResp roleConfigurationMenu(RoleConfigurationMenuReq req);

    /**
     * 获取导出数据
     * @param req 请求信息
     * @return 响应信息
     */
    List<UserExcel> exportUserExcel(QueryManageUserReq req);
    /**
     * 更新用户违规信息
     * @param req 请求信息
     * @return 响应信息
     */
    UpdateUserViolationResp updateUserViolation(UpdateUserViolationReq req);

    /**
     * 查看用户违规记录
     * @param req 请求信息
     * @return 响应信息
     */
    QueryUserViolationResp queryUserViolation(QueryUserViolationReq req);
    /**
     * queryAdminAuthList
     * @return 响应信息
     */
    QueryAdminAuthListResp queryAdminAuthList();
    /**
     * 社区用户查询
     * @param req 请求信息
     * @return 响应信息
     */
    PageResult<UserInfo> queryCommunityUserList(QueryCommunityUserListReq req);
    /**
     * 社区用户导出
     * @param req 请求信息
     * @return 响应信息
     */
    List<UserExcel> exportCommunityUserExcel(QueryCommunityUserListReq req);
    /**
     * 用户基本信息查询
     * @param req 请求信息
     * @return 响应信息
     */
    QueryCommunityUserBaseInfoListResp queryCommunityUserBaseInfoList(QueryCommunityUserBaseInfoListReq req);

    /**
     * 社区管理员列表查询列表
     * @param req
     * @return
     */
    QueryCommunityUserBaseInfoListResp queryCommunityAdminBaseInfoList(QueryCommunityUserBaseInfoListReq req);

    /**
     * 获取当前登陆社区管理员信息
     * @return
     */
    AdminUserInfo queryCurrentCommunityAdminBaseInfo();

    AdminUserInfoResp getAdminUserInfoByUserId(String adminUserId);

    AdminUserResp getAdminUserByUserId(UserIdReq req);
    List<AdminUserResp> getAdminUserByUserId(Collection<String> userIds);

    Map<String, UserInfoResp> getUserInfoByUserId(CodeListReq req);

    /**
     * 修改模版发布权限
     * @param changePrem 修改数据
     */
    void changeTempStorePermission(ChangePrem changePrem);

    /**
     * 查询模版发布
     * @param userId 用户id
     */
    boolean haseTempStorePerm(String userId);


    void tempStorePermissionActiveOff(String userId);

    AdminUserSumResp getPlatformApplicationReviewSum();

    ChatbotProcessingSumResp getSupplierChatbotProcessingSum();

    ChannelInfoResp getCspUserChannelByUserId(String userId);

    UpdateClientUserResp updateCspUserChannelByUserId(UpdateCspChannelReq req);

    PageResult<CspCustomerChatbotAccountVo> getSupplierChatbot(QuerySupplierChatbotReq req);

    ContractSupplierInfo getSupplerContractByChatbotId(String chatbotId);

    SupplierChatbotInfoResp getChatbotById(String chatbotId);

    ResponseEntity<byte[]> download(String chatbotId) throws IOException;

    RestResult<Boolean> completeConfiguration(CompleteSupplierChatbotConfigurationReq req);

    RestResult<Boolean> rejectConfiguration(RejectSupplierChatbotConfigurationReq req);

    RestResult<Boolean> changeSupplierChatbotStatus(changeSupplierChatbotStatusReq req);

    RestResult<Boolean> editChatbotConfiguration(EditSupplierChatbotConfigurationReq req);

    RestResult<Boolean> updateSupplierChatbot(UpdateChatbotSupplierReq req);

    RestResult<Boolean> updateSupplierContract(UpdateSupplierContractReq req);

    MenuResp queryMenu(String chatbotAccountId);

    RestResult<Boolean> submitMenu(MenuSaveReq req);

    List<OperationLogResp> getOperationLog(String chatbotAccountId);

    SupplierChatbotConfigurationResp findConfiguration(String chatbotAccountId);

    void setWhiteList(ChatbotSetWhiteListReq req);
    CertStatisticsResp statisticsForCertificate(@RequestParam("status") Integer status);

}
