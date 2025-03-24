package com.citc.nce.authcenter.auth;

import com.citc.nce.authcenter.admin.dto.UserAndRoleDto;
import com.citc.nce.authcenter.auth.service.AdminAuthService;
import com.citc.nce.authcenter.auth.vo.AdminUserInfo;
import com.citc.nce.authcenter.auth.vo.CspCustomerChatbotAccountVo;
import com.citc.nce.authcenter.auth.vo.UserExcel;
import com.citc.nce.authcenter.auth.vo.UserInfo;
import com.citc.nce.authcenter.auth.vo.req.*;
import com.citc.nce.authcenter.auth.vo.resp.*;
import com.citc.nce.authcenter.csp.multitenant.service.CspCustomerService;
import com.citc.nce.authcenter.largeModel.service.LargeModelService;
import com.citc.nce.authcenter.largeModel.vo.LargeModelReq;
import com.citc.nce.authcenter.largeModel.vo.LargeModelResp;
import com.citc.nce.authcenter.tempStorePerm.bean.ChangePrem;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.core.pojo.RestResult;
import com.citc.nce.common.util.SessionContextUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController()
@Slf4j
public class AdminAuthController implements AdminAuthApi {

    @Resource
    private AdminAuthService adminAuthService;
    @Resource
    private CspCustomerService cspCustomerService;

    /**
     * 管理端 获取客户端用户详情
     *
     * @return 用户信息
     */
    @ApiOperation("管理端-获取客户端用户详情")
    @PostMapping("/admin/user/getClientUserDetails")
    public ClientUserDetailsResp getClientUserDetails(@RequestBody @Valid ClientUserDetailsReq req) {
        return adminAuthService.getClientUserDetails(req);
    }

    /**
     * 管理端 编辑客户端用户信息
     *
     * @return 编辑结果
     */
    @ApiOperation("管理端-编辑客户端用户信息")
    @PostMapping("/admin/user/updateClientUserInfo")
    public UpdateClientUserResp updateClientUserInfo(@RequestBody @Valid UpdateClientUserReq req) {
        return adminAuthService.updateClientUserInfo(req);
    }

    /**
     * 管理端 删除客户端用户信息
     *
     * @return 编辑结果
     */
    @ApiOperation("管理端-删除客户端用户信息")
    @PostMapping("/admin/user/deleteClientUserInfo")
    public DeleteClientUserResp deleteClientUserInfo(@RequestBody @Valid DeleteClientUserReq req) {
        return adminAuthService.deleteClientUserInfo(req);
    }

    /**
     * 管理端 用户名/手机号 是否存在
     *
     * @return 查询结果结果
     */
    @ApiOperation("管理端-检查用户名/手机号是否存在")
    @PostMapping("/admin/user/checkLoadNameExist")
    public CheckLoadNameResp checkLoadNameExist(@RequestBody @Valid CheckLoadNameReq req) {
        return adminAuthService.checkLoadNameExist(req);
    }


    /**
     * 管理端用户登录
     *
     * @param req 请求信息
     * @return 响应消息
     */
    @ApiOperation("管理端-用户登录")
    @PostMapping("/admin/user/login")
    public AdminUserLoginResp adminUserWebLogin(@RequestBody @Valid AdminLoginReq req) {
        return adminAuthService.adminUserWebLogin(req);
    }

    @ApiOperation("管理端-用户退出登录")
    @PostMapping("/admin/user/logout")
    public void logout() {
        adminAuthService.logout();
    }

    /**
     * 核能商城，硬核桃，chatbot和统一用户管理查询列表
     *
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("核能商城，硬核桃，chatbot和统一用户管理员查询列表")
    @PostMapping("/admin/user/getManageUser")
    public PageResult getManageUser(@RequestBody @Valid QueryManageUserReq req) {
        return adminAuthService.getManageUser(req);
    }

    /**
     * 平台使用申请审核列表
     *
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("平台使用申请审核列表")
    @PostMapping("/admin/user/getPlatformApplicationReview")
    public PageResult<UserInfo> getPlatformApplicationReview(@RequestBody @Valid PlatformApplicationReviewReq req) {
        return adminAuthService.getPlatformApplicationReview(req);
    }

    /**
     * 审核用户平台使用申请
     *
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("管理平台--审核用户平台使用申请")
    @PostMapping("/admin/user/reviewPlatformApplication")
    public ReviewPlatAppResp reviewPlatformApplication(@RequestBody ReviewPlatAppReq req) {
        return adminAuthService.reviewPlatformApplication(req);
    }

    /**
     * 查看平台申请审核日志
     *
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("管理平台--查看平台申请审核日志")
    @PostMapping("/admin/user/getReviewLogList")
    public GetReviewLogListResp getReviewLogList(@RequestBody GetReviewLogListReq req) {
        return adminAuthService.getReviewLogList(req);
    }

    /**
     * 启用禁用平台用户
     *
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("启用禁用平台用户")
    @PostMapping("/admin/user/enableOrDisableUser")
    public EnableOrDisableResp enableOrDisableUser(@RequestBody @Valid EnableOrDisableReq req) {
        return adminAuthService.enableOrDisableUser(req);
    }


    /**
     * 级联查看用户信息
     *
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("级联查看用户信息")
    @PostMapping("/admin/user/getUserInfo")
    public GetUserInfoResp getUserInfo(@RequestBody @Valid GetUserInfoReq req) {
        return adminAuthService.getUserInfo(req);
    }

    @ApiOperation("获取企业用户列表")
    @PostMapping("/admin/user/getEnterpriseUserList")
    public GetEnterpriseUserListResp getEnterpriseUserList() {
        return adminAuthService.getEnterpriseUserList();
    }


    @ApiOperation("查询用户角色列表")
    @PostMapping("/admin/user/getUserRoleList")
    public GetUserRoleListResp getUserRoleList() {
        return adminAuthService.getUserRoleList();
    }

    @ApiOperation("查询运营管理人员列表")
    @PostMapping("/admin/user/getOperatorList")
    public PageResult<UserAndRoleDto> getOperatorList(@RequestBody @Valid GetOperatorListReq req) {
        return adminAuthService.getOperatorList(req);
    }

    @ApiOperation("启用或禁用运营管理人员")
    @PostMapping("/admin/user/enableOrDisableAdminUser")
    public EnableOrDisableAdminUserResp enableOrDisableAdminUser(@RequestBody @Valid EnableOrDisableAdminUserReq req) {
        return adminAuthService.enableOrDisableAdminUser(req);
    }

    @ApiOperation("新增或编辑运营管理人员")
    @PostMapping("/admin/user/editOperator")
    public EditOperatorResp editOperator(@RequestBody @Valid EditOperatorReq req) {
        return adminAuthService.editOperator(req);
    }

    @ApiOperation("查询角色管理列表")
    @PostMapping("/admin/user/getRolePage")
    public PageResult getRolePage(@RequestBody @Valid GetRolePage req) {
        return adminAuthService.getRolePage(req);
    }

    @ApiOperation("启用禁用角色管理")
    @PostMapping("/admin/user/enableOrDisableRole")
    public EnableOrDisableRoleResp enableOrDisableRole(@RequestBody @Valid EnableOrDisableRoleReq req) {
        return adminAuthService.enableOrDisableRole(req);
    }

    @ApiOperation("新增或编辑角色")
    @PostMapping("/admin/user/editRole")
    public EditRoleResp editRole(@RequestBody @Valid EditRoleReq req) {
        return adminAuthService.editRole(req);
    }

    @ApiOperation("删除角色")
    @PostMapping("/admin/user/removeRole")
    public RemoveRoleResp removeRole(@RequestBody @Valid RemoveRoleReq req) {
        return adminAuthService.removeRole(req);
    }

    @ApiOperation("根据角色查询成员")
    @PostMapping("/admin/user/getMemberByRoleId")
    public GetMemberByRoleIdResp getMemberByRoleId(@RequestBody @Valid GetMemberByRoleIdReq req) {
        return adminAuthService.getMemberByRoleId(req);
    }

    @ApiOperation("角色配置成员")
    @PostMapping("/admin/user/roleConfigurationMember")
    public RoleConfigurationMemberResp roleConfigurationMember(@RequestBody @Valid RoleConfigurationMemberReq req) {
        return adminAuthService.roleConfigurationMember(req);
    }

    @ApiOperation("根据角色查询菜单资源")
    @PostMapping("/admin/user/getMenuByRoleId")
    public GetMenuByRoleIdResp getMenuByRoleId(@RequestBody @Valid GetMenuByRoleIdReq req) {
        return adminAuthService.getMenuByRoleId(req);
    }

    @ApiOperation("角色添加菜单资源")
    @PostMapping("/admin/user/roleConfigurationMenu")
    public RoleConfigurationMenuResp roleConfigurationMenu(@RequestBody @Valid RoleConfigurationMenuReq req) {
        return adminAuthService.roleConfigurationMenu(req);
    }

    @Override
    @ApiOperation("管理端-用户信息导出为excel")
    @RequestMapping(value = "/admin/user/exportUserExcel")
    public List<UserExcel> exportUserExcel(@RequestBody @Valid QueryManageUserReq req) {
        return adminAuthService.exportUserExcel(req);
    }

    @Override
    @ApiOperation("更新用户违规记录")
    @PostMapping("/admin/user/updateUserViolation")
    public UpdateUserViolationResp updateUserViolation(@RequestBody @Valid UpdateUserViolationReq req) {
        return adminAuthService.updateUserViolation(req);
    }

    @Override
    @ApiOperation("查看用户违规记录")
    @PostMapping("/admin/user/queryUserViolation")
    public QueryUserViolationResp queryUserViolation(@RequestBody @Valid QueryUserViolationReq req) {
        return adminAuthService.queryUserViolation(req);
    }

    @Override
    @ApiOperation("管理员菜单权限查询")
    @GetMapping("/admin/user/queryAdminAuthList")
    public QueryAdminAuthListResp queryAdminAuthList() {
        return adminAuthService.queryAdminAuthList();
    }

    @ApiOperation("社区用户列表查询列表")
    @PostMapping("/admin/user/queryCommunityUserList")
    @Override
    public PageResult<UserInfo> queryCommunityUserList(@RequestBody @Valid QueryCommunityUserListReq req) {
        return adminAuthService.queryCommunityUserList(req);
    }

    @ApiOperation("社区用户列表Excel导出")
    @PostMapping("/admin/user/exportCommunityUserExcel")
    @Override
    public List<UserExcel> exportCommunityUserExcel(@RequestBody @Valid QueryCommunityUserListReq req) {
        return adminAuthService.exportCommunityUserExcel(req);
    }

    @Override
    @ApiOperation("社区用户列表查询列表")
    @PostMapping("/queryCommunityUserBaseInfoList")
    public QueryCommunityUserBaseInfoListResp queryCommunityUserBaseInfoList(@RequestBody @Valid QueryCommunityUserBaseInfoListReq req) {
        return adminAuthService.queryCommunityUserBaseInfoList(req);
    }

    @Override
    @ApiOperation("社区管理员列表查询列表")
    @PostMapping("/queryCommunityAdminBaseInfoList")
    public QueryCommunityUserBaseInfoListResp queryCommunityAdminBaseInfoList(QueryCommunityUserBaseInfoListReq req) {
        return adminAuthService.queryCommunityAdminBaseInfoList(req);
    }

    @Override
    @ApiOperation("获取当前登陆社区管理员信息")
    @PostMapping("/queryCurrentCommunityAdminBaseInfo")
    public AdminUserInfo queryCurrentCommunityAdminBaseInfo() {
        return adminAuthService.queryCurrentCommunityAdminBaseInfo();
    }

    @Override
    @ApiOperation("获取管理园信息")
    @PostMapping("/admin/user/getAdminUserInfoByUserId")
    public AdminUserInfoResp getAdminUserInfoByUserId(String adminUserId) {
        return adminAuthService.getAdminUserInfoByUserId(adminUserId);
    }

    @Override
    @ApiOperation("根据userId查询管理员用户信息")
    @PostMapping("/admin/user/getAdminUserByUserId")
    public AdminUserResp getAdminUserByUserId(UserIdReq req) {
        return adminAuthService.getAdminUserByUserId(req);
    }

    @Override
    @PostMapping("/admin/user/getAdminUserByUserIds")
    public List<AdminUserResp> getAdminUserByUserId(@RequestBody List<String> userIds) {
        return adminAuthService.getAdminUserByUserId(userIds);
    }

    @Override
    @ApiOperation("支撑其他服务通过用户id查询用户信息")
    @PostMapping("/admin/user/getUserInfoByUserId")
    public Map<String, UserInfoResp> getUserInfoByUserId(CodeListReq req) {
        return adminAuthService.getUserInfoByUserId(req);
    }

    @PostMapping("/admin/user/changeTempStorePermission")
    @ApiOperation("修改模版发布权限")
    @Override
    public void changeTempStorePermission(@RequestBody @Validated ChangePrem changePrem) {
        adminAuthService.changeTempStorePermission(changePrem);
        if (Integer.valueOf(0).equals(changePrem.getPermission())) {
            adminAuthService.tempStorePermissionActiveOff(changePrem.getUserId());
        }
    }

    @Override
    public boolean haseTempStorePerm() {
        try {
            String userId = SessionContextUtil.getLoginUser().getUserId();
            return adminAuthService.haseTempStorePerm(userId);
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/admin/user/disableCustomerMealCsp")
    @ApiOperation("csp异常时禁用csp客户")
    @Override
    public void disableCustomerMealCsp(@RequestParam("cspId") String cspId) {
        cspCustomerService.disableCustomerMealCsp(cspId);
    }

    @Override
    @ApiOperation("查询待审核用户数量")
    @PostMapping("/admin/user/getPlatformApplicationReviewSum")//WATER
    public AdminUserSumResp getPlatformApplicationReviewSum() {
        return adminAuthService.getPlatformApplicationReviewSum();
    }

    @Override
    @ApiOperation("待处理Chatbot申请")
    @GetMapping("/admin/user/getSupplierChatbotProcessingSum")//WATER
    public ChatbotProcessingSumResp getSupplierChatbotProcessingSum() {
        return adminAuthService.getSupplierChatbotProcessingSum();
    }

    @Override
    @ApiOperation("csp通道详情")
    @PostMapping("/admin/user/getCspUserChannel")
    public ChannelInfoResp getCspUserChannel(@RequestBody @Valid GetUserInfoReq req) {
        return adminAuthService.getCspUserChannelByUserId(req.getUserId());
    }

    @Override
    @ApiOperation("csp通道管理")
    @PostMapping("/admin/user/updateCspUserChannel")
    public UpdateClientUserResp updateCspUserChannel(@RequestBody @Valid UpdateCspChannelReq req) {
        return adminAuthService.updateCspUserChannelByUserId(req);
    }

    @Override
    @ApiOperation("待处理的CSP的Chatbot(supplier)申请")
    @PostMapping("/admin/user/getSupplierChatbot")
    public PageResult<CspCustomerChatbotAccountVo> getSupplierChatbot(@RequestBody @Valid QuerySupplierChatbotReq req) {
        return adminAuthService.getSupplierChatbot(req);
    }

    @Override
    @ApiOperation("通过聊天机器人Id获取合同")
    @PostMapping("/admin/user/getSupplerContractByChatbotId/{chatbotAccountId}")
    public ContractSupplierInfo getSupplerContractByChatbotId(@PathVariable(value = "chatbotAccountId") String chatbotAccountId) {
        return adminAuthService.getSupplerContractByChatbotId(chatbotAccountId);
    }

    @Override
    @ApiOperation("管理平台---根据 ChatbotId找到对应的Chatbot")
    @PostMapping("/admin/user/getChatbotById/{chatbotAccountId}")
    public SupplierChatbotInfoResp getChatbotById(@PathVariable(value = "chatbotAccountId") String chatbotAccountId) {
        return adminAuthService.getChatbotById(chatbotAccountId);
    }

    @Override
    @ApiOperation("管理平台---根据 ChatbotId下载对应的合同、chatbot信息压缩包")
    @GetMapping("/admin/user/download/{chatbotAccountId}")
    public ResponseEntity<byte[]> download(@PathVariable(value = "chatbotAccountId") String chatbotAccountId) throws IOException {
        return adminAuthService.download(chatbotAccountId);
    }

    @Override
    @ApiOperation("管理平台---供应商返回的信息填入,完成supplier chatbot配置")
    @PostMapping("/admin/user/completeConfiguration")
    public RestResult<Boolean> completeConfiguration(@RequestBody @Valid CompleteSupplierChatbotConfigurationReq req) {
        return adminAuthService.completeConfiguration(req);
    }

    @Override
    @ApiOperation("管理平台---驳回supplier chatbot配置")
    @PostMapping("/admin/user/rejectConfiguration")
    public RestResult<Boolean> rejectConfiguration(@RequestBody @Valid RejectSupplierChatbotConfigurationReq req) {
        return adminAuthService.rejectConfiguration(req);
    }

    @Override
    @ApiOperation("管理平台---更改chatbot在线状态(上下线及注销)")
    @PostMapping("/admin/user/changeSupplierChatbotStatus")
    public RestResult<Boolean> changeSupplierChatbotStatus(@RequestBody @Valid changeSupplierChatbotStatusReq req) {
        return adminAuthService.changeSupplierChatbotStatus(req);
    }

    @Override
    @ApiOperation("管理平台---编辑supplier chatbot配置信息")
    @PostMapping("/admin/user/editChatbotConfiguration")
    public RestResult<Boolean> editChatbotConfiguration(@RequestBody @Valid EditSupplierChatbotConfigurationReq req) {
        return adminAuthService.editChatbotConfiguration(req);
    }

    @Override
    @PostMapping("/admin/user/supplier/update")
    @ApiOperation(value = "管理平台--更新供应商chatbot")
    public RestResult<Boolean> updateSupplierChatbot(@RequestBody @Valid UpdateChatbotSupplierReq req) {
        return adminAuthService.updateSupplierChatbot(req);
    }

    @Override
    @PostMapping("/admin/user/supplier/updateContract")
    @ApiOperation(value = "管理平台--更新供应商相关的合同信息")
    public RestResult<Boolean> updateSupplierContract(@RequestBody @Valid UpdateSupplierContractReq req) {
        return adminAuthService.updateSupplierContract(req);
    }

    @Override
    @PostMapping("/admin/user/supplier/menu/submitMenu")
    @ApiOperation(value = "管理平台--提交固定菜单", notes = "管理平台提交固定菜单")
    public RestResult<Boolean> submitMenu(@RequestBody @Valid MenuSaveReq req) {
        return adminAuthService.submitMenu(req);
    }

    @Override
    @PostMapping("/admin/user/supplier/queryMenu/{chatbotAccountId}")
    @ApiOperation(value = "管理平台--查询固定菜单", notes = "管理平台查询固定菜单")
    public MenuResp queryMenu(@PathVariable(value = "chatbotAccountId") String chatbotAccountId) {
        return adminAuthService.queryMenu(chatbotAccountId);
    }

    @Override
    @PostMapping("/admin/user/getOperationLog/{chatbotAccountId}")
    public List<OperationLogResp> getOperationLog(@PathVariable(value = "chatbotAccountId") String chatbotAccountId) {
        return adminAuthService.getOperationLog(chatbotAccountId);
    }

    @Override
    @ApiOperation("管理平台---供应商返回的信息填入,完成supplier chatbot配置")
    @PostMapping("/findConfiguration/{chatbotAccountId}")
    public SupplierChatbotConfigurationResp findConfiguration(@PathVariable(value = "chatbotAccountId") String chatbotAccountId) {
        return adminAuthService.findConfiguration(chatbotAccountId);
    }

    @Override
    @PostMapping("/supplier/chatbot/setWhiteList")
    @ApiOperation(value = "设置调试白名单", notes = "设置调试白名单")
    public void setWhiteList(@RequestBody @Valid ChatbotSetWhiteListReq req) {
        adminAuthService.setWhiteList(req);
    }
    @ApiOperation("管理平台--企业&个人资质待审核数量统计")
    @GetMapping("/admin/user/StatisticsForCertificate")
    public CertStatisticsResp statisticsForCertificate(@RequestParam("status") Integer status){
        return adminAuthService.statisticsForCertificate(status);
    }
}
