package com.citc.nce.auth.adminUser;

import com.citc.nce.auth.adminUser.vo.req.*;
import com.citc.nce.auth.adminUser.vo.resp.*;
import com.citc.nce.auth.certificate.vo.req.UserTagLogByCertificateOptionsIdReq;
import com.citc.nce.auth.certificate.vo.resp.UserCertificateResp;
import com.citc.nce.auth.certificate.vo.resp.UserTagLogResp;
import com.citc.nce.auth.identification.vo.req.ViewRemarkReq;
import com.citc.nce.auth.identification.vo.resp.IdentificationAuditResp;
import com.citc.nce.auth.user.vo.req.*;
import com.citc.nce.auth.user.vo.resp.UserIdAndNameResp;
import com.citc.nce.auth.user.vo.resp.UserInfoResp;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 */
@Api(tags = "管理平台用户模块")
@FeignClient(value = "auth-service", contextId = "AdminUserApi", url = "${auth:}")
public interface AdminUserApi {

    @ApiOperation("管理平台用户登录")
    @PostMapping("/user/adminUserWebLogin")
    AdminUserLoginResp adminUserWebLogin(@RequestBody @Valid AdminLoginReq loginReq);

    @ApiOperation("通过账号查找用户信息，账号可能为用户名  or  手机号")
    @PostMapping("/user/getAdminUserInfoByAccount")
    AdminUserInfoResp getAdminUserInfoByAccount(String account);

    @ApiOperation("管理端dashboard用户统计")
    @PostMapping("/admin/user/getDashboardUserStatistics")
    DashboardUserStatisticsResp getDashboardUserStatistics();

    @ApiOperation("管理后台查询 客户端用户详情")
    @PostMapping("/admin/user/getClientUserDetails")
    ClientUserDetailsResp getClientUserDetails(@RequestBody ClientUserDetailsReq req);

    @ApiOperation("admin端 编辑client端用户信息")
    @PostMapping("/admin/user/updateClientUserInfo")
    void updateClientUserInfo(@RequestBody @Valid UpdateClientUserReq req);

    @ApiOperation("管理端 获取客户端用户认证信息")
    @PostMapping("/admin/user/getClientUserIdentifications")
    ClientUserIdentificationResp getClientUserIdentifications(@RequestBody @Valid ClientUserIdentificationReq req);

    @ApiOperation("管理端  审核用户认证")
    @PostMapping("/admin/user/auditIdentification")
    void auditIdentification(@RequestBody AuditIdentificationReq req);

    @ApiOperation("管理端 查看审核备注list")
    @PostMapping("/admin/user/viewRemarkHistory")
    List<IdentificationAuditResp> viewRemarkHistory(@RequestBody ViewRemarkReq req);

    @ApiOperation("admin 端查看 client 用户标签")
    @PostMapping("/admin/user/getClientUserCertificate")
    List<UserCertificateResp> getClientUserCertificate(@RequestBody ClientUserCertificateReq req);

    @ApiOperation("启停 用户标签")
    @PostMapping("/admin/user/onOrOffClientUserCertificate")
    void onOrOffClientUserCertificate(@RequestBody @Valid OnOrOffClientUserCertificateReq req);

    @ApiOperation("查询用户标签开启关闭的日志操作信息")
    @PostMapping("/admin/user/getUserTagLogByCertificateOptionsId")
    List<UserTagLogResp> getUserTagLogByCertificateOptionsId(@RequestBody UserTagLogByCertificateOptionsIdReq req);

    @ApiOperation("删除门户用户信息")
    @PostMapping("/admin/user/deleteClientUserInfo")
    void deleteClientUserInfo(@RequestBody @Valid DeleteClientUserReq req);

    @ApiOperation("用户名/手机号 是否存在")
    @PostMapping("/admin/user/checkLoadNameExist")
    Boolean checkLoadNameExist(@RequestBody CheckLoadNameReq req);

    @ApiOperation("核能商城，硬核桃，chatbot和统一用户管理查询列表")
    @PostMapping("/admin/user/getManageUser")
    PageResult getManageUser(@RequestBody @Valid ManageUserReq req);

    @ApiOperation("启用禁用平台用户")
    @PostMapping("/admin/user/enableOrDisableUser")
    void enableOrDisableUser(@RequestBody @Valid EnableOrDisableReq req);

    @ApiOperation("平台使用申请审核列表")
    @PostMapping("/admin/user/getPlatformApplicationReview")
    PageResult getPlatformApplicationReview(@RequestBody @Valid PlatformApplicationReviewReq req);

    @ApiOperation("审核用户平台使用申请")
    @PostMapping("/admin/user/reviewPlatformApplication")
    void reviewPlatformApplication(@RequestBody @Valid ReviewReq req);

    @ApiOperation("查看平台申请审核日志")
    @PostMapping("/admin/user/getReviewLog")
    List<ApprovalLogResp> getReviewLog(@RequestBody @Valid UuidReq req);

    @ApiOperation("级联查看用户信息")
    @PostMapping("/admin/user/getUserInfo")
    List<UserIdAndNameResp> getUserInfo(@RequestBody UserIdReq req);

    @ApiOperation("支撑通过用户名模糊查询用户信息")
    @PostMapping("/admin/user/getUserInfoList")
    List<UserInfoResp> getUserInfoList(@RequestBody KeyWordReq req);

    @ApiOperation("支撑其他服务通过用户id查询用户信息")
    @PostMapping("/admin/user/getUserInfoByUserId")
    Map<String, UserInfoResp> getUserInfoByUserId(@RequestBody CodeListReq req);

    @ApiOperation("获取管理园信息")
    @PostMapping("/admin/user/getAdminUserInfoByUserId")
    AdminUserInfoResp getAdminUserInfoByUserId(@RequestBody String adminUserId);

    @ApiOperation("查询角色下拉列表")
    @PostMapping("/admin/user/getUserRoleList")
    List<AdminRoleResp> getUserRoleList();

    @ApiOperation("查询运营管理人员列表")
    @PostMapping("/admin/user/getOperatorList")
    PageResult getOperatorList(@RequestBody @Valid OperatorReq req);

    @ApiOperation("启用或禁用运营管理人员")
    @PostMapping("/admin/user/enableOrDisableAdminUser")
    void enableOrDisableAdminUser(@RequestBody @Valid DisableOrEnableReq req);

    @ApiOperation("新增或编辑运营管理人员")
    @PostMapping("/admin/user/editOperator")
    void editOperator(@RequestBody @Valid EditOperatorReq req);

    @ApiOperation("查询角色管理列表")
    @PostMapping("/admin/user/getRolePage")
    PageResult getRolePage(@RequestBody @Valid OperatorReq req);

    @ApiOperation("启用禁用角色管理")
    @PostMapping("/admin/user/enableOrDisableRole")
    void enableOrDisableRole(@RequestBody @Valid DisableOrEnableReq req);

    @ApiOperation("新增或编辑角色")
    @PostMapping("/admin/user/editRole")
    void editRole(@RequestBody @Valid RoleReq req);

    @ApiOperation("删除角色")
    @PostMapping("/admin/user/removeRole")
    void removeRole(@RequestBody CodeReq req);

    @ApiOperation("根据角色查询成员(如果code为空查询所有用户,双接口)")
    @PostMapping("/admin/user/getMerberByRoleId")
    List<AdminUserResp> getMerberByRoleId(@RequestBody CodeReq req);

    @ApiOperation("角色配置成员")
    @PostMapping("/admin/user/roleConfigurationMember")
    void roleConfigurationMember(@RequestBody @Valid UserAndRoleReq req);

    @ApiOperation("根据角色查询菜单资源")
    @PostMapping("/admin/user/getMenuByRoleId")
    List<RoleAndMenuResp> getMenuByRoleId(@RequestBody CodeReq req);

    @ApiOperation("角色添加菜单资源")
    @PostMapping("/admin/user/roleConfigurationMenu")
    void roleConfigurationMenu(@RequestBody @Valid RoleAndMenuReq req);

    @ApiOperation("查询拥有工单菜单权限的所有管理员用户")
    @PostMapping("/admin/user/findWorkOrderPermissionUsers")
    List<AdminUserResp> findWorkOrderPermissionUsers();

    /**
     * 平台使用申请待审核数量
     *
     * @param
     * @return
     */
    @PostMapping("/admin/user/getPlatformApplicationReviewSum")
     AdminUserSumResp getPlatformApplicationReviewSum();

    @ApiOperation("根据userId查询管理员用户信息")
    @PostMapping("/admin/user/getAdminUserByUserId")
    AdminUserResp getAdminUserByUserId(@RequestBody @Valid UserIdReq req);
}
