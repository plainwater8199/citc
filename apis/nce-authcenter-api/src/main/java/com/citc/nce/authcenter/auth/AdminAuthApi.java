package com.citc.nce.authcenter.auth;

import com.citc.nce.authcenter.auth.vo.AdminUserInfo;
import com.citc.nce.authcenter.auth.vo.CspCustomerChatbotAccountVo;
import com.citc.nce.authcenter.auth.vo.UserExcel;
import com.citc.nce.authcenter.auth.vo.UserInfo;
import com.citc.nce.authcenter.auth.vo.req.*;
import com.citc.nce.authcenter.auth.vo.resp.*;
import com.citc.nce.authcenter.largeModel.vo.LargeModelReq;
import com.citc.nce.authcenter.largeModel.vo.LargeModelResp;
import com.citc.nce.authcenter.tempStorePerm.bean.ChangePrem;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.core.pojo.RestResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Api(tags = "管理端--认证模块")
@FeignClient(value = "authcenter-service", contextId = "admin", url = "${authCenter:}")
public interface AdminAuthApi {
    @ApiOperation("管理端-获取客户端用户详情")
    @PostMapping("/admin/user/getClientUserDetails")
    ClientUserDetailsResp getClientUserDetails(@RequestBody @Valid ClientUserDetailsReq req);

    @ApiOperation(" 管理端-编辑客户端用户信息")
    @PostMapping("/admin/user/updateClientUserInfo")
    UpdateClientUserResp updateClientUserInfo(@RequestBody @Valid UpdateClientUserReq req);

    @ApiOperation("管理端-删除客户端用户信息")
    @PostMapping("/admin/user/deleteClientUserInfo")
    DeleteClientUserResp deleteClientUserInfo(@RequestBody @Valid DeleteClientUserReq req);

    @ApiOperation("管理端-检查用户名/手机号是否存在")
    @PostMapping("/admin/user/checkLoadNameExist")
    CheckLoadNameResp checkLoadNameExist(@RequestBody @Valid CheckLoadNameReq req);

    @ApiOperation("管理端-用户登录")
    @PostMapping("/admin/user/login")
    AdminUserLoginResp adminUserWebLogin(@RequestBody @Valid AdminLoginReq req);

    @ApiOperation("管理端-用户退出登录")
    @PostMapping("/admin/user/logout")
    void logout();

    @ApiOperation("核能商城，硬核桃，chatbot和统一用户管理员查询列表")
    @PostMapping("/admin/user/getManageUser")
    PageResult getManageUser(@RequestBody @Valid QueryManageUserReq req);

    @ApiOperation("平台使用申请审核列表")
    @PostMapping("/admin/user/getPlatformApplicationReview")
    PageResult getPlatformApplicationReview(@RequestBody @Valid PlatformApplicationReviewReq req);

    @ApiOperation("管理平台--审核用户平台使用申请")
    @PostMapping("/admin/user/reviewPlatformApplication")
    ReviewPlatAppResp reviewPlatformApplication(@RequestBody @Valid ReviewPlatAppReq req);

    @ApiOperation("管理平台--查看平台申请审核日志")
    @PostMapping("/admin/user/getReviewLogList")
    GetReviewLogListResp getReviewLogList(@RequestBody @Valid GetReviewLogListReq req);

    @ApiOperation("启用禁用平台用户")
    @PostMapping("/admin/user/enableOrDisableUser")
    EnableOrDisableResp enableOrDisableUser(@RequestBody @Valid EnableOrDisableReq req);

    @ApiOperation("级联查看用户信息")
    @PostMapping("/admin/user/getUserInfo")
    GetUserInfoResp getUserInfo(@RequestBody @Valid GetUserInfoReq req);

    @ApiOperation("获取企业用户列表")
    @PostMapping("/admin/user/getEnterpriseUserList")
    GetEnterpriseUserListResp getEnterpriseUserList();

    @ApiOperation("查询用户角色列表")
    @PostMapping("/admin/user/getUserRoleList")
    GetUserRoleListResp getUserRoleList();

    @ApiOperation("查询运营管理人员列表")
    @PostMapping("/admin/user/getOperatorList")
    PageResult getOperatorList(@RequestBody @Valid GetOperatorListReq req);

    @ApiOperation("启用或禁用运营管理人员")
    @PostMapping("/admin/user/enableOrDisableAdminUser")
    EnableOrDisableAdminUserResp enableOrDisableAdminUser(@RequestBody @Valid EnableOrDisableAdminUserReq req);

    @ApiOperation("新增或编辑运营管理人员")
    @PostMapping("/admin/user/editOperator")
    EditOperatorResp editOperator(@RequestBody @Valid EditOperatorReq req);

    @ApiOperation("查询角色管理列表")
    @PostMapping("/admin/user/getRolePage")
    PageResult getRolePage(@RequestBody @Valid GetRolePage req);

    @ApiOperation("启用禁用角色管理")
    @PostMapping("/admin/user/enableOrDisableRole")
    EnableOrDisableRoleResp enableOrDisableRole(@RequestBody @Valid EnableOrDisableRoleReq req);

    @ApiOperation("新增或编辑角色")
    @PostMapping("/admin/user/editRole")
    EditRoleResp editRole(@RequestBody @Valid EditRoleReq req);

    @ApiOperation("删除角色")
    @PostMapping("/admin/user/removeRole")
    RemoveRoleResp removeRole(@RequestBody @Valid RemoveRoleReq req);

    @ApiOperation("根据角色查询成员")
    @PostMapping("/admin/user/getMemberByRoleId")
    GetMemberByRoleIdResp getMemberByRoleId(@RequestBody @Valid GetMemberByRoleIdReq req);

    @ApiOperation("角色配置成员")
    @PostMapping("/admin/user/roleConfigurationMember")
    RoleConfigurationMemberResp roleConfigurationMember(@RequestBody @Valid RoleConfigurationMemberReq req);

    @ApiOperation("根据角色查询菜单资源")
    @PostMapping("/admin/user/getMenuByRoleId")
    GetMenuByRoleIdResp getMenuByRoleId(@RequestBody @Valid GetMenuByRoleIdReq req);

    @ApiOperation("角色添加菜单资源")
    @PostMapping("/admin/user/roleConfigurationMenu")
    RoleConfigurationMenuResp roleConfigurationMenu(@RequestBody @Valid RoleConfigurationMenuReq req);

    @ApiOperation("管理端-用户信息导出为excel")
    @RequestMapping(value = "/admin/user/exportUserExcel")
    List<UserExcel> exportUserExcel(@RequestBody @Valid QueryManageUserReq req);

    @ApiOperation("更新用户违规记录")
    @PostMapping("/admin/user/updateUserViolation")
    UpdateUserViolationResp updateUserViolation(@RequestBody @Valid UpdateUserViolationReq req);

    @ApiOperation("查看用户违规记录")
    @PostMapping("/admin/user/queryUserViolation")
    QueryUserViolationResp queryUserViolation(@RequestBody @Valid QueryUserViolationReq req);

    @ApiOperation("管理员菜单权限查询接口")
    @GetMapping("/admin/user/queryAdminAuthList")
    QueryAdminAuthListResp queryAdminAuthList();

    @ApiOperation("社区用户列表查询列表")
    @PostMapping("/admin/user/queryCommunityUserList")
    PageResult<UserInfo> queryCommunityUserList(@RequestBody @Valid QueryCommunityUserListReq req);

    @ApiOperation("社区用户列表Excel导出")
    @PostMapping("/admin/user/exportCommunityUserExcel")
    List<UserExcel> exportCommunityUserExcel(@RequestBody @Valid QueryCommunityUserListReq req);

    @ApiOperation("社区用户列表查询列表")
    @PostMapping("/queryCommunityUserBaseInfoList")
    QueryCommunityUserBaseInfoListResp queryCommunityUserBaseInfoList(@RequestBody @Valid QueryCommunityUserBaseInfoListReq req);

    @ApiOperation("社区管理员列表查询列表")
    @PostMapping("/queryCommunityAdminBaseInfoList")
    QueryCommunityUserBaseInfoListResp queryCommunityAdminBaseInfoList(@RequestBody @Valid QueryCommunityUserBaseInfoListReq req);

    @ApiOperation("获取当前登陆社区管理员信息")
    @PostMapping("/queryCurrentCommunityAdminBaseInfo")
    AdminUserInfo queryCurrentCommunityAdminBaseInfo();


    @ApiOperation("获取管理员信息")
    @PostMapping("/admin/user/getAdminUserInfoByUserId")//WATER
    AdminUserInfoResp getAdminUserInfoByUserId(@RequestBody String adminUserId);

    @ApiOperation("根据userId查询管理员用户信息")
    @PostMapping("/admin/user/getAdminUserByUserId")//WATER
    AdminUserResp getAdminUserByUserId(@RequestBody @Valid UserIdReq req);

    @PostMapping("/admin/user/getAdminUserByUserIds")
    public List<AdminUserResp> getAdminUserByUserId(@RequestBody List<String> userIds);
    @ApiOperation("支撑其他服务通过用户id查询用户信息")
    @PostMapping("/admin/user/getUserInfoByUserId")

    Map<String, UserInfoResp> getUserInfoByUserId(@RequestBody CodeListReq req);

    @PostMapping("/admin/user/changeTempStorePermission")
    @ApiOperation("修改模板商城权限")
    void changeTempStorePermission(@RequestBody @Validated ChangePrem changePrem);

    @GetMapping("/tempStore/perm/hasePerm")
    boolean haseTempStorePerm();

    @PostMapping("/admin/user/disableCustomerMealCsp")
    @ApiOperation("csp异常时禁用csp客户")
    public void disableCustomerMealCsp(@RequestParam("cspId") String cspId);

    @ApiOperation("查询待审核用户数量")
    @PostMapping("/admin/user/getPlatformApplicationReviewSum")//WATER
    AdminUserSumResp getPlatformApplicationReviewSum();

    @ApiOperation("待处理Chatbot申请")
    @GetMapping("/admin/user/getSupplierChatbotProcessingSum")
    ChatbotProcessingSumResp getSupplierChatbotProcessingSum();

    // 新增通道
    @ApiOperation("csp通道详情")
    @PostMapping("/admin/user/getCspUserChannel")
    ChannelInfoResp getCspUserChannel(@RequestBody @Valid GetUserInfoReq req);

    @ApiOperation("csp通道管理")
    @PostMapping("/admin/user/updateCspUserChannel")
    UpdateClientUserResp updateCspUserChannel(@RequestBody @Valid UpdateCspChannelReq req);

    @ApiOperation("待处理的CSP的Chatbot(supplier)申请")
    @PostMapping("/admin/user/getSupplierChatbot")
    PageResult<CspCustomerChatbotAccountVo> getSupplierChatbot(@RequestBody @Valid QuerySupplierChatbotReq req);

    @ApiOperation("管理平台---根据supplier Chatbot找到对应的合同")
    @PostMapping("/admin/user/getSupplerContractByChatbotId/{chatbotAccountId}")
    ContractSupplierInfo getSupplerContractByChatbotId(@PathVariable(value = "chatbotAccountId")String chatbotId);

    @ApiOperation("管理平台---根据 ChatbotId找到对应的Chatbot")
    @PostMapping("/admin/user/getChatbotById/{chatbotAccountId}")
    SupplierChatbotInfoResp getChatbotById(@PathVariable(value = "chatbotAccountId")String chatbotAccountId);


    @ApiOperation("管理平台---根据 ChatbotId下载对应的合同、chatbot信息压缩包")
    @GetMapping("/admin/user/download/{chatbotAccountId}")
    public ResponseEntity<byte[]> download(@PathVariable(value = "chatbotAccountId")String chatbotId) throws IOException;

    @ApiOperation("管理平台---供应商返回的信息填入,完成supplier chatbot配置")
    @PostMapping("/admin/user/completeConfiguration")
    RestResult<Boolean> completeConfiguration(@RequestBody @Valid CompleteSupplierChatbotConfigurationReq req);

    @ApiOperation("管理平台---驳回supplier chatbot配置")
    @PostMapping("/admin/user/rejectConfiguration")
    RestResult<Boolean> rejectConfiguration(@RequestBody @Valid RejectSupplierChatbotConfigurationReq req);

    @ApiOperation("管理平台---更改chatbot在线状态")
    @PostMapping("/admin/user/changeSupplierChatbotStatus")
    RestResult<Boolean> changeSupplierChatbotStatus(@RequestBody @Valid changeSupplierChatbotStatusReq req);

    @ApiOperation("管理平台---编辑supplier chatbot配置信息")
    @PostMapping("/admin/user/editChatbotConfiguration")
    RestResult<Boolean> editChatbotConfiguration(@RequestBody @Valid EditSupplierChatbotConfigurationReq req);

    @PostMapping("/admin/user/supplier/update")
    @ApiOperation(value = "更新供应商chatbot")
    RestResult<Boolean> updateSupplierChatbot(@RequestBody @Valid UpdateChatbotSupplierReq req);

    @PostMapping("/admin/user/supplier/updateContract")
    @ApiOperation(value = "管理平台--更新供应商相关的合同信息")
    RestResult<Boolean> updateSupplierContract(@RequestBody @Valid UpdateSupplierContractReq req);

    @PostMapping("/admin/user/supplier/menu/submitMenu")
    @ApiOperation(value = "管理平台--提交固定菜单", notes = "管理平台提交固定菜单")
    RestResult<Boolean> submitMenu(@RequestBody @Valid MenuSaveReq req);

    @PostMapping("/admin/user/supplier/queryMenu/{chatbotAccountId}")
    @ApiOperation(value = "管理平台--查询固定菜单", notes = "管理平台查询固定菜单")
    MenuResp queryMenu(@PathVariable(value = "chatbotAccountId")String chatbotAccountId);

    @ApiOperation("管理平台---获取管理员对chatbot的操作记录")
    @PostMapping("/admin/user/getOperationLog/{chatbotAccountId}")
    List<OperationLogResp> getOperationLog(@PathVariable(value = "chatbotAccountId")String chatbotAccountId);

    @ApiOperation("管理平台---供应商返回的信息填入,完成supplier chatbot配置")
    @PostMapping("/findConfiguration/{chatbotAccountId}")
    public SupplierChatbotConfigurationResp findConfiguration(@PathVariable(value = "chatbotAccountId") String chatbotAccountId);

    @PostMapping("/supplier/chatbot/setWhiteList")
    @ApiOperation(value = "设置调试白名单", notes = "设置调试白名单")
    public void setWhiteList(@RequestBody @Valid ChatbotSetWhiteListReq req) ;

    @ApiOperation("管理平台--企业&个人资质待审核数量统计")
    @GetMapping("/admin/user/StatisticsForCertificate")
    CertStatisticsResp statisticsForCertificate(@RequestParam("status") Integer status);

}
