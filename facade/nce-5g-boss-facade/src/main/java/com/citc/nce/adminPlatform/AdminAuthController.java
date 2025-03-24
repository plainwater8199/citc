package com.citc.nce.adminPlatform;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.citc.nce.annotation.BossAuth;
import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.auth.vo.UserInfo;
import com.citc.nce.authcenter.auth.vo.*;
import com.citc.nce.authcenter.auth.vo.req.*;
import com.citc.nce.authcenter.auth.vo.resp.*;
import com.citc.nce.authcenter.identification.vo.ApprovalLogItem;
import com.citc.nce.authcenter.largeModel.vo.LargeModelReq;
import com.citc.nce.authcenter.largeModel.vo.LargeModelResp;
import com.citc.nce.authcenter.tempStorePerm.bean.ChangePrem;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.core.pojo.RestResult;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.common.log.enums.OperatorType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


@Api(tags = "管理平台-认证管理")
@RestController
@RequestMapping("/admin/user")
@Slf4j
public class AdminAuthController {

    @Resource
    private AdminAuthApi adminAuthApi;

    /**
     * 管理端 获取客户端用户详情
     * @return 用户信息
     */
    @BossAuth({"/chatbot-view/audit","/unified-user-view/compile","/chatbot-view/auditDetail"})
    @ApiOperation("管理端-获取客户端用户详情")
    @PostMapping("/getClientUserDetails")
    public ClientUserDetailsResp getClientUserDetails(@RequestBody @Valid ClientUserDetailsReq req) {
        return adminAuthApi.getClientUserDetails(req);
    }

    /**
     * 管理端 编辑客户端用户信息
     *
     * @return 编辑结果
     */
    @BossAuth("/unified-user-view/compile")
    @ApiOperation(" 管理端-编辑客户端用户信息")
    @PostMapping("/updateClientUserInfo")
    @Log(title = "管理端-编辑客户端用户信息", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    public void updateClientUserInfo(@RequestBody @Valid UpdateClientUserReq req) {
        adminAuthApi.updateClientUserInfo(req);
    }

    /**
     * 管理端 删除客户端用户信息
     *
     * @return 编辑结果
     */
    @ApiOperation("管理端-删除客户端用户信息")
    @PostMapping("/deleteClientUserInfo")
    @Log(title = "管理端-删除客户端用户信息", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    public void deleteClientUserInfo(@RequestBody @Valid DeleteClientUserReq req) {
        adminAuthApi.deleteClientUserInfo(req);
    }

    /**
     * 管理端 用户名/手机号 是否存在
     *
     * @return 查询结果结果
     */
    @ApiOperation("管理端-检查用户名/手机号是否存在")
    @PostMapping("/checkLoadNameExist")
    public Boolean checkLoadNameExist(@RequestBody @Valid CheckLoadNameReq req) {
        return adminAuthApi.checkLoadNameExist(req).getResult();
    }

    /**
     * 查询用户通道详情
     *
     * @return 查询结果结果
     */
    @ApiOperation("csp通道详情")
    @PostMapping("/getCspUserChannel")
    public ChannelInfoResp getCspUserChannel(@RequestBody @Valid GetUserInfoReq req) {
        return adminAuthApi.getCspUserChannel(req);
    }

    /**
     * 更新用户通道详情
     *
     */
    @BossAuth("/chatbot-view/user-crm")
    @ApiOperation("csp通道更新")
    @PostMapping("/updateCspUserChannel")
    public UpdateClientUserResp updateCspUserChannel(@RequestBody @Valid UpdateCspChannelReq req) {
        return adminAuthApi.updateCspUserChannel(req);
    }

    /**
     * 平台使用申请待审核数量
     *
     * @param
     * @return
     */
    @BossAuth("/chatbot-view/proxyChannelManagement/chatbot")
    @ApiOperation("管理平台---CSP的Chatbot(supplier)列表")
    @PostMapping("/getSupplierChatbot")
    public PageResult<CspCustomerChatbotAccountVo> getSupplierChatbot(@RequestBody @Valid QuerySupplierChatbotReq req) {
        return adminAuthApi.getSupplierChatbot(req);
    }


    /**
     * 平台使用申请待审核数量
     *
     * @param
     * @return
     */
    @BossAuth("/chatbot-view/proxyChannelManagement/contractInfo")
    @ApiOperation("管理平台---根据supplier Chatbot找到对应的合同")
    @PostMapping("/getSupplerContractByChatbotId/{chatbotId}")
    public ContractSupplierInfo getSupplerContractByChatbotId(@PathVariable(value = "chatbotId") String chatbotId) {
        return adminAuthApi.getSupplerContractByChatbotId(chatbotId);
    }

    /**
     * 平台使用申请待审核数量
     *
     * @param
     * @return
     */
    @BossAuth("/chatbot-view/proxyChannelManagement/chatbotInfo")
    @ApiOperation("管理平台---根据 ChatbotId找到对应的Chatbot")
    @PostMapping("/getChatbotById/{chatbotAccountId}")
    public SupplierChatbotInfoResp getChatbotById(@PathVariable(value = "chatbotAccountId") String chatbotAccountId) {
        return adminAuthApi.getChatbotById(chatbotAccountId);
    }

    /**
     * 平台使用申请待审核数量
     *
     * @param
     * @return
     */
    @BossAuth("/chatbot-view/proxyChannelManagement/chatbot")
    @ApiOperation("管理平台---根据 ChatbotId下载对应的合同、chatbot信息压缩包")
    @GetMapping("/download/{chatbotAccountId}")
    public ResponseEntity<byte[]> download(@PathVariable(value = "chatbotAccountId") String chatbotAccountId) throws IOException {
        log.info("download chatbotAccountId begin:{}", chatbotAccountId);
        ResponseEntity<byte[]> download = adminAuthApi.download(chatbotAccountId);

        HttpHeaders oldHttpHeaders=download.getHeaders();
        HttpHeaders newHttpHeaders=new HttpHeaders();
        for (Map.Entry<String, List<String>> entry: oldHttpHeaders.entrySet()) {
            String  key = entry.getKey();
            if("transfer-encoding".equals(key)||"Transfer-Encoding".equals(key)){
                continue;
            }
            newHttpHeaders.addAll(key,oldHttpHeaders.get(key));
        }

        log.info("download chatbotAccountId finish:{}", chatbotAccountId);
        return ResponseEntity.
                ok().
                headers(newHttpHeaders)
                .body(download.getBody());
    }

    /**
     * 平台使用申请待审核数量
     *
     * @param
     * @return
     */
    @BossAuth("/chatbot-view/proxyChannelManagement/chatbot")
    @ApiOperation("管理平台---供应商返回的信息填入,完成supplier chatbot配置")
    @PostMapping("/completeConfiguration")
    public RestResult<Boolean> completeConfiguration(@RequestBody @Valid CompleteSupplierChatbotConfigurationReq req) {
        return adminAuthApi.completeConfiguration(req);
    }

    /**
     * 平台使用申请待审核数量
     *
     * @param
     * @return
     */
    @BossAuth("/chatbot-view/proxyChannelManagement/chatbot")
    @ApiOperation("管理平台---查询supplier chatbot配置")
    @PostMapping("/findConfiguration/{chatbotAccountId}")
    public SupplierChatbotConfigurationResp findConfiguration(@PathVariable(value = "chatbotAccountId") String chatbotAccountId) {
        return adminAuthApi.findConfiguration(chatbotAccountId);
    }

    @PostMapping("/supplier/chatbot/setWhiteList")
    @ApiOperation(value = "设置调试白名单", notes = "设置调试白名单")
    public void setWhiteList(@RequestBody @Valid ChatbotSetWhiteListReq req) {
        adminAuthApi.setWhiteList(req);
    }

    @BossAuth("/chatbot-view/proxyChannelManagement/chatbot")
    @ApiOperation(value = "获取操作日志", notes = "获取操作日志")
    @PostMapping("/getOperationLog/{chatbotAccountId}")
    public List<OperationLogResp> getOperationLog(@PathVariable(value = "chatbotAccountId")String chatbotAccountId) {
        return adminAuthApi.getOperationLog(chatbotAccountId);
    }

    /**
     * 平台使用申请待审核数量
     *
     * @param
     * @return
     */
    @ApiOperation("管理平台---驳回supplier chatbot配置")
    @PostMapping("/rejectConfiguration")
    public RestResult<Boolean> rejectConfiguration(@RequestBody @Valid RejectSupplierChatbotConfigurationReq req) {
        return adminAuthApi.rejectConfiguration(req);
    }

    /**
     * 更改chatbot在线状态(上下线及注销)
     *
     * @param
     * @return
     */
    @BossAuth("/chatbot-view/proxyChannelManagement/chatbot")
    @ApiOperation("管理平台---更改chatbot在线状态(上下线及注销)")
    @PostMapping("/changeSupplierChatbotStatus")
    public RestResult<Boolean> changeSupplierChatbotStatus(@RequestBody @Valid changeSupplierChatbotStatusReq req) {
        return adminAuthApi.changeSupplierChatbotStatus(req);
    }

    /**
     * 更改chatbot在线状态
     *
     * @param
     * @return
     */
    @BossAuth("/chatbot-view/proxyChannelManagement/chatbot")
    @ApiOperation("管理平台---编辑supplier chatbot配置信息")
    @PostMapping("/editChatbotConfiguration")
    public RestResult<Boolean> editChatbotMsg(@RequestBody @Valid EditSupplierChatbotConfigurationReq req) {
        return adminAuthApi.editChatbotConfiguration(req);
    }

    @BossAuth("/chatbot-view/proxyChannelManagement/chatbotInfo")
    @PostMapping("/supplier/update")
    @ApiOperation(value = "更新供应商的chatbot")
    public RestResult<Boolean> updateSupplierChatbot(@RequestBody @Valid UpdateChatbotSupplierReq req) {
        return adminAuthApi.updateSupplierChatbot(req);
    }

    @BossAuth("/chatbot-view/proxyChannelManagement/contractInfo")
    @PostMapping("/supplier/updateContract")
    @ApiOperation(value = "管理平台--更新供应商相关的合同信息")
    public RestResult<Boolean> updateSupplierContract(@RequestBody @Valid UpdateSupplierContractReq req) {
        return adminAuthApi.updateSupplierContract(req);
    }

    @PostMapping("/supplier/queryMenu/{chatbotAccountId}")
    @ApiOperation(value = "管理平台--查询固定菜单", notes = "管理平台查询固定菜单")
    public MenuResp queryMenu(@PathVariable(value = "chatbotAccountId" ) String chatbotAccountId){
        return adminAuthApi.queryMenu(chatbotAccountId);
    }

    @PostMapping("/supplier/menu/submitMenu")
    @ApiOperation(value = "管理平台--提交固定菜单", notes = "管理平台提交固定菜单")
    public RestResult<Boolean> submitMenu(@RequestBody @Valid MenuSaveReq req){
        return adminAuthApi.submitMenu(req);
    }

    /**
     * 管理端用户登录
     * @param req 请求信息
     * @return 响应消息
     */
    @ApiOperation("管理端-用户登录")
    @PostMapping("/login")
    @SkipToken
    @Log(title = "管理端-用户登录", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    public AdminUserLoginResp adminUserWebLogin(@RequestBody @Valid AdminLoginReq req) {
        return adminAuthApi.adminUserWebLogin(req);
    }

    @ApiOperation("管理端-用户退出登录")
    @PostMapping("/logout")
    @Log(title = "管理端-用户退出登录", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    public void logout() {
        adminAuthApi.logout();
    }


    /**
     * 下载用户信息Excel
     * @param req 请求信息
     * @param response 响应消息
     */
    @ApiOperation("管理端-用户信息导出为excel")
    @RequestMapping(value = "/exportUserExcel")
    @Log(title = "管理端-用户信息导出为excel", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    public void exportUserExcel(@RequestBody QueryManageUserReq req, HttpServletResponse response) throws Exception {
        // 设置响应头
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 设置防止中文名乱码
        String fileName = "用户信息.xlsx";
        String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileNameURL + ";" + "filename*=utf-8''" + fileNameURL);
        // 构建写入到excel文件的数据
        req.setIsNotExport(false);
        //获取数据列表
        List<UserExcel> userExcelList = adminAuthApi.exportUserExcel(req);
        // 写入数据到excel
        ServletOutputStream os = response.getOutputStream();
        EasyExcel.write(os, UserExcel.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("用户信息")
                .doWrite(userExcelList);
        if (null != os) {
            os.flush();
            os.close();
        }
    }

    /**
     * 核能商城，硬核桃，chatbot和统一用户管理查询列表
     * @param req 请求消息
     * @return 响应消息
     */
    @BossAuth({"/chatbot-view/user-crm", "/unified-user-view/user-crm"})
    @ApiOperation("核能商城，硬核桃，chatbot和统一用户管理员查询列表")
    @PostMapping("/getManageUser")
    PageResult getManageUser(@RequestBody @Valid QueryManageUserReq req) {
        return adminAuthApi.getManageUser(req);
    }

    /**
     * 社区用户列表查询列表
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("社区用户列表查询列表")
    @PostMapping("/queryCommunityUserList")
    PageResult<UserInfo> queryCommunityUserList(@RequestBody @Valid QueryCommunityUserListReq req) {
        return adminAuthApi.queryCommunityUserList(req);
    }

    /**
     * 社区用户基本信息列表查询
     * @param req 请求消息
     * @return 响应消息
     */
    @SkipToken
    @ApiOperation("社区用户列表查询列表")
    @PostMapping("/queryCommunityUserBaseInfoList")
    QueryCommunityUserBaseInfoListResp queryCommunityUserBaseInfoList(@RequestBody @Valid QueryCommunityUserBaseInfoListReq req) {
        return adminAuthApi.queryCommunityUserBaseInfoList(req);
    }

    /**
     * 获取当前登陆社区管理员信息
     * @return 响应消息
     */
    @ApiOperation("获取当前登陆社区管理员信息")
    @PostMapping("/queryCurrentCommunityAdminBaseInfo")
    public AdminUserInfo queryCurrentCommunityAdminBaseInfo() {
        return adminAuthApi.queryCurrentCommunityAdminBaseInfo();
    }


    /**
     * 社区管理员基本信息列表查询
     * @param req 请求消息
     * @return 响应消息
     */
    @SkipToken
    @ApiOperation("社区管理员信息列表查询")
    @PostMapping("/queryCommunityAdminBaseInfoList")
    QueryCommunityUserBaseInfoListResp queryCommunityAdminBaseInfoList(@RequestBody @Valid QueryCommunityUserBaseInfoListReq req) {
        return adminAuthApi.queryCommunityAdminBaseInfoList(req);
    }


    @ApiOperation("管理端-社区用户信息导出为excel")
    @RequestMapping(value = "/exportCommunityUserExcel")
    @Log(title = "管理端-社区用户信息导出为excel", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    public void exportCommunityUserExcel(@RequestBody QueryCommunityUserListReq req, HttpServletResponse response) throws Exception {
        // 设置响应头
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 设置防止中文名乱码
        String fileName = "用户信息.xlsx";
        String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileNameURL + ";" + "filename*=utf-8''" + fileNameURL);
        //获取数据列表
        List<UserExcel> userExcelList = adminAuthApi.exportCommunityUserExcel(req);
        // 写入数据到excel
        ServletOutputStream os = response.getOutputStream();
        EasyExcel.write(os, UserExcel.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("用户信息")
                .doWrite(userExcelList);
        if (null != os) {
            os.flush();
            os.close();
        }
    }

    /**
     * 平台使用申请审核列表
     * @param req 请求消息
     * @return 响应消息
     */
    @BossAuth("/chatbot-view/audit")
    @ApiOperation("平台使用申请审核列表")
    @PostMapping("/getPlatformApplicationReview")
    PageResult getPlatformApplicationReview(@RequestBody @Valid PlatformApplicationReviewReq req) {
        return adminAuthApi.getPlatformApplicationReview(req);
    }


    /**
     * 审核用户平台使用申请
     * @param req 请求消息
     * @return 响应消息
     */
    @BossAuth("/chatbot-view/auditDetail")
    @ApiOperation("管理平台--审核用户平台使用申请")
    @PostMapping("/reviewPlatformApplication")
    void reviewPlatformApplication(@RequestBody ReviewPlatAppReq req) {
        adminAuthApi.reviewPlatformApplication(req);
    }

    /**
     * 查看平台申请审核日志
     * @param req 请求消息
     * @return 响应消息
     */
    @BossAuth({"/chatbot-view/audit","/chatbot-view/auditDetail"})
    @ApiOperation("管理平台--查看平台申请审核日志")
    @PostMapping("/getReviewLogList")
    List<ApprovalLogItem> getReviewLogList(@RequestBody GetReviewLogListReq req) {
        return adminAuthApi.getReviewLogList(req).getApprovalLogItems();
    }

    @BossAuth("/unified-user-view/user-crm")
    @ApiOperation("管理平台--企业&个人资质待审核数量统计")
    @GetMapping("/StatisticsForCertificate")
    public CertStatisticsResp statisticsForCertificate(@RequestParam("status") Integer status){
        return adminAuthApi.statisticsForCertificate(status);
    }
    /**
     * 启用禁用平台用户
     * @param req 请求消息
     * @return 响应消息
     */
    @BossAuth("/chatbot-view/user-crm")
    @ApiOperation("启用禁用平台用户")
    @PostMapping("/enableOrDisableUser")
    @Log(title = "管理端-启用禁用平台用户", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    void enableOrDisableUser(@RequestBody @Valid EnableOrDisableReq req) {
        adminAuthApi.enableOrDisableUser(req);
    }


    /**
     * 级联查看用户信息
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("级联查看用户信息")
    @PostMapping("/getUserInfo")
    public List<UserIdAndNameInfo> getUserInfo(@RequestBody @Valid GetUserInfoReq req) {
        return adminAuthApi.getUserInfo(req).getUserIdAndNameInfos();
    }

    @ApiOperation("获取企业用户列表")
    @PostMapping("/getEnterpriseUserList")
    List<UserInfo> getEnterpriseUserList() {
        return adminAuthApi.getEnterpriseUserList().getUserInfos();
    }


    @BossAuth("/system-management-view/operators")
    @ApiOperation("查询用户角色列表")
    @PostMapping("/getUserRoleList")
    List<AdminRoleItem> getUserRoleList() {
        return adminAuthApi.getUserRoleList().getAdminRoleItems();
    }

    @BossAuth("/system-management-view/operators")
    @ApiOperation("查询运营管理人员列表")
    @PostMapping("/getOperatorList")
    PageResult getOperatorList(@RequestBody @Valid GetOperatorListReq req) {
        return adminAuthApi.getOperatorList(req);
    }

    @BossAuth("/system-management-view/operators")
    @ApiOperation("启用或禁用运营管理人员")
    @PostMapping("/enableOrDisableAdminUser")
    @Log(title = "管理端-启用或禁用运营管理人员", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    void enableOrDisableAdminUser(@RequestBody @Valid EnableOrDisableAdminUserReq req) {
        adminAuthApi.enableOrDisableAdminUser(req);
    }

    @BossAuth("/system-management-view/operators")
    @ApiOperation("新增或编辑运营管理人员")
    @PostMapping("/editOperator")
    @Log(title = "管理端-新增或编辑运营管理人员", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    void editOperator(@RequestBody @Valid EditOperatorReq req) {
        adminAuthApi.editOperator(req);
    }

    @BossAuth("/system-management-view/role-manager")
    @ApiOperation("查询角色管理列表")
    @PostMapping("/getRolePage")
    PageResult getRolePage(@RequestBody @Valid GetRolePage req) {
        return adminAuthApi.getRolePage(req);
    }

    @BossAuth("/system-management-view/role-manager")
    @ApiOperation("启用禁用角色管理")
    @PostMapping("/enableOrDisableRole")
    @Log(title = "管理端-启用禁用角色管理", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    void enableOrDisableRole(@RequestBody @Valid EnableOrDisableRoleReq req) {
        adminAuthApi.enableOrDisableRole(req);
    }

    @BossAuth("/system-management-view/role-manager")
    @ApiOperation("新增或编辑角色")
    @PostMapping("/editRole")
    @Log(title = "管理端-新增或编辑角色", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    void editRole(@RequestBody @Valid EditRoleReq req) {
        adminAuthApi.editRole(req);
    }

    @BossAuth("/system-management-view/role-manager")
    @ApiOperation("删除角色")
    @PostMapping("/removeRole")
    @Log(title = "管理端-删除角色", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    RemoveRoleResp removeRole(@RequestBody RemoveRoleReq req) {
        return adminAuthApi.removeRole(req);
    }

    @BossAuth("/system-management-view/role-manager")
    @ApiOperation("根据角色查询成员")
    @PostMapping("/getMemberByRoleId")
    List<AdminUserInfo> getMemberByRoleId(@RequestBody GetMemberByRoleIdReq req) {
        return adminAuthApi.getMemberByRoleId(req).getAdminUserInfos();
    }

    @BossAuth("/system-management-view/role-manager")
    @ApiOperation("角色配置成员")
    @PostMapping("/roleConfigurationMember")
    @Log(title = "管理端-角色配置成员", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    void roleConfigurationMember(@RequestBody @Valid RoleConfigurationMemberReq req) {
        adminAuthApi.roleConfigurationMember(req);
    }

    @BossAuth("/system-management-view/role-manager")
    @ApiOperation("根据角色查询菜单资源")
    @PostMapping("/getMenuByRoleId")
    List<RoleAndMenuItem> getMenuByRoleId(@RequestBody GetMenuByRoleIdReq req) {
        return adminAuthApi.getMenuByRoleId(req).getRoleAndMenuItems();
    }

    @BossAuth("/system-management-view/role-manager")
    @ApiOperation("角色添加菜单资源")
    @PostMapping("/roleConfigurationMenu")
    @Log(title = "管理端-角色添加菜单资源", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    void roleConfigurationMenu(@RequestBody @Valid RoleConfigurationMenuReq req) {
        adminAuthApi.roleConfigurationMenu(req);
    }

    @ApiOperation("更新用户违规记录")
    @PostMapping("/updateUserViolation")
    @Log(title = "管理端-更新用户违规记录", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    UpdateUserViolationResp updateUserViolation(@RequestBody @Valid UpdateUserViolationReq req) {
        return adminAuthApi.updateUserViolation(req);
    }

    @ApiOperation("查看用户违规记录")
    @PostMapping("/queryUserViolation")
    QueryUserViolationResp queryUserViolation(@RequestBody @Valid QueryUserViolationReq req) {
        return adminAuthApi.queryUserViolation(req);
    }

    @BossAuth("/unified-user-view/user-crm")
    @ApiOperation("管理员菜单权限查询接口")
    @GetMapping("/queryAdminAuthList")
    QueryAdminAuthListResp queryAdminAuthList() {
        return adminAuthApi.queryAdminAuthList();
    }
}
