package com.citc.nce.auth.adminUser;

import com.citc.nce.auth.adminUser.service.AdminRoleService;
import com.citc.nce.auth.adminUser.service.AdminUserRoleService;
import com.citc.nce.auth.adminUser.service.AdminUserService;
import com.citc.nce.auth.adminUser.vo.req.*;
import com.citc.nce.auth.adminUser.vo.resp.*;
import com.citc.nce.auth.certificate.service.CertificateOptionsService;
import com.citc.nce.auth.certificate.service.UserCertificateService;
import com.citc.nce.auth.certificate.vo.req.UserTagLogByCertificateOptionsIdReq;
import com.citc.nce.auth.certificate.vo.resp.UserCertificateResp;
import com.citc.nce.auth.certificate.vo.resp.UserTagLogResp;
import com.citc.nce.auth.identification.service.IdentificationAuditRecordService;
import com.citc.nce.auth.identification.vo.req.ViewRemarkReq;
import com.citc.nce.auth.identification.vo.resp.IdentificationAuditResp;
import com.citc.nce.auth.user.entity.UserDo;
import com.citc.nce.auth.user.service.UserService;
import com.citc.nce.auth.user.vo.req.*;
import com.citc.nce.auth.user.vo.resp.UserIdAndNameResp;
import com.citc.nce.auth.user.vo.resp.UserInfoResp;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.core.pojo.RestResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/21 17:11
 * @Version: 1.0
 * @Description:
 */
@RestController()
@Slf4j
public class AdminUserController implements AdminUserApi {

    @Resource
    private AdminUserService adminUserService;
    @Resource
    private UserService userService;
    @Resource
    private IdentificationAuditRecordService auditRecordService;
    @Resource
    private UserCertificateService userCertificateService;
    @Resource
    private CertificateOptionsService certificateOptionsService;
    @Resource
    private AdminUserRoleService adminUserRoleService;
    @Resource
    private AdminRoleService adminRoleService;

    /**
     * 管理后台用户登录
     *
     * @param loginReq
     * @return
     */
    @Override
    @PostMapping("/user/adminUserWebLogin")
    public AdminUserLoginResp adminUserWebLogin(@RequestBody @Valid AdminLoginReq loginReq) {
        return adminUserService.adminUserWebLogin(loginReq);
    }

    /**
     * 通过账号查找用户信息，账号可能为用户名  or  手机号
     *
     * @param account
     * @return
     */
    @Override
    @PostMapping("/user/getAdminUserInfoByAccount")
    public AdminUserInfoResp getAdminUserInfoByAccount(String account) {
        return adminUserService.getAdminUserInfoByAccount(account);
    }

    /**
     * 管理端dashboard用户统计
     *
     * @return
     */
    @Override
    @PostMapping("/admin/user/getDashboardUserStatistics")
    public DashboardUserStatisticsResp getDashboardUserStatistics() {
        DashboardUserStatisticsResp resp = adminUserService.getDashboardUserStatistics();
        return resp;
    }

    /**
     * 管理后台查询 客户端用户详情
     *
     * @param req
     * @return
     */
    @Override
    @PostMapping("/admin/user/getClientUserDetails")
    public ClientUserDetailsResp getClientUserDetails(@RequestBody ClientUserDetailsReq req) {
        ClientUserDetailsResp resp = new ClientUserDetailsResp();
        UserDo clientUserDo = userService.userInfoDetailByUserId(req.getUserId());

        BeanUtils.copyProperties(clientUserDo, resp);
        List<UserCertificateResp> certificateOptions = userCertificateService.getCertificateOptions(req.getUserId());
        StringBuilder sb = new StringBuilder();
        if (!CollectionUtils.isEmpty(certificateOptions)) {
            for (int i = 0; i < certificateOptions.size(); i++) {
                UserCertificateResp certificateResp = certificateOptions.get(i);
                String certificateName = certificateResp.getCertificateName();
                sb.append(certificateName.contains("、"));
            }
        }
        String flags = sb.toString();
        if (!StringUtils.isEmpty(flags)) {
            flags = flags.substring(0, flags.length() - 1);
            resp.setFlags(flags);
        }
        return resp;
    }

    @Override
    @PostMapping("/admin/user/updateClientUserInfo")
    public void updateClientUserInfo(@RequestBody UpdateClientUserReq req) {
        //核对 重新修改的用户账号，邮箱，手机号 不重复
        adminUserService.updateClientUserInfo(req);
    }

    @Override
    @PostMapping("/admin/user/getClientUserIdentifications")
    public ClientUserIdentificationResp getClientUserIdentifications(@RequestBody @Valid ClientUserIdentificationReq req) {
        return adminUserService.getClientUserIdentifications(req);
    }

    /**
     * 管理端  审核用户认证
     *
     * @param req
     */
    @Override
    @PostMapping("/admin/user/auditIdentification")
    public void auditIdentification(@RequestBody AuditIdentificationReq req) {
        adminUserService.auditIdentification(req);
    }


    /**
     * 查看审核历史记录
     *
     * @return
     */
    @Override
    @PostMapping("/admin/user/viewRemarkHistory")
    public List<IdentificationAuditResp> viewRemarkHistory(@RequestBody ViewRemarkReq req) {
        return auditRecordService.viewRemarkHistory(req);
    }


    /**
     * admin 端查看 client 用户标签
     *
     * @param req
     * @return
     */
    @PostMapping("/admin/user/getClientUserCertificate")
    @Override
    public List<UserCertificateResp> getClientUserCertificate(@RequestBody ClientUserCertificateReq req) {
        return userCertificateService.getCertificateOptions(req.getClientUserId());
    }


    /**
     * 启停 用户标签(如果用户没有申请此标签也帮用户添加,如果申请了就刷新此标签状态)
     *
     * @param req
     */
    @PostMapping("/admin/user/onOrOffClientUserCertificate")
    @Override
    public void onOrOffClientUserCertificate(@RequestBody @Valid OnOrOffClientUserCertificateReq req) {
        certificateOptionsService.onOrOffClientUserCertificate(req);
    }

    /**
     * 通过用户资质信息表id查询日志操作信息
     *
     * @param req
     * @return
     */
    @PostMapping("/admin/user/getUserTagLogByCertificateOptionsId")
    @Override
    public List<UserTagLogResp> getUserTagLogByCertificateOptionsId(@RequestBody UserTagLogByCertificateOptionsIdReq req) {
        return certificateOptionsService.getUserTagLogByCertificateOptionsId(req);
    }

    /**
     * 删除门户用户信息
     *
     * @param req
     */
    @PostMapping("/admin/user/deleteClientUserInfo")
    @Override
    public void deleteClientUserInfo(@RequestBody @Valid DeleteClientUserReq req) {
        userService.deleteClientUserInfo(req);
    }

    /**
     * 用户名/手机号 是否存在
     *
     * @param req
     * @return
     */
    @PostMapping("/admin/user/checkLoadNameExist")
    @Override
    public Boolean checkLoadNameExist(@RequestBody CheckLoadNameReq req) {
        return adminUserService.checkLoadNameExist(req.getCheckValue());
    }

    /**
     * 核能商城，硬核桃，chatbot和统一用户管理查询列表
     *
     * @param req
     * @return
     */
    @PostMapping("/admin/user/getManageUser")
    @Override
    public PageResult getManageUser(@RequestBody @Valid ManageUserReq req) {
        return adminUserService.getManageUser(req);
    }

    /**
     * 启用禁用平台用户
     *
     * @param req
     * @return
     */
    @PostMapping("/admin/user/enableOrDisableUser")
    @Override
    public void enableOrDisableUser(@RequestBody @Valid EnableOrDisableReq req) {
        adminUserService.enableOrDisableUser(req);
    }

    /**
     * 平台使用申请审核列表
     *
     * @param req
     * @return
     */
    @PostMapping("/admin/user/getPlatformApplicationReview")
    @Override
    public PageResult getPlatformApplicationReview(@RequestBody @Valid PlatformApplicationReviewReq req) {
        return adminUserService.getPlatformApplicationReview(req);
    }

    /**
     * 审核用户平台使用申请
     *
     * @param req
     * @return
     */
    @PostMapping("/admin/user/reviewPlatformApplication")
    @Override
    public void reviewPlatformApplication(@RequestBody @Valid ReviewReq req) {
        adminUserService.reviewPlatformApplication(req);
    }

    /**
     * 查看平台申请审核日志
     *
     * @param req
     * @return
     */
    @PostMapping("/admin/user/getReviewLog")
    @Override
    public List<ApprovalLogResp> getReviewLog(@RequestBody @Valid UuidReq req) {
        return adminUserService.getReviewLog(req);
    }

    /**
     * 级联查看用户信息
     *
     * @param req
     * @return
     */
    @PostMapping("/admin/user/getUserInfo")
    @Override
    public List<UserIdAndNameResp> getUserInfo(@RequestBody UserIdReq req) {
        return adminUserService.getUserInfo(req);
    }

    /**
     * 支撑通过用户名模糊查询用户信息
     *
     * @param req
     * @return
     */
    @PostMapping("/admin/user/getUserInfoList")
    @Override
    public List<UserInfoResp> getUserInfoList(@RequestBody KeyWordReq req) {
        return adminUserService.getUserInfoList(req);
    }

    /**
     * 支撑其他服务通过用户id查询用户信息
     *
     * @param req
     * @return
     */
    @PostMapping("/admin/user/getUserInfoByUserId")
    @Override
    public Map<String, UserInfoResp> getUserInfoByUserId(@RequestBody CodeListReq req) {
        return adminUserService.getUserInfoByUserId(req);
    }

    @Override
    @PostMapping("/admin/user/getAdminUserInfoByUserId")
    public AdminUserInfoResp getAdminUserInfoByUserId(@RequestBody String adminUserId) {
        return adminUserService.getAdminUserInfoByUserId(adminUserId);
    }

    /**
     * 查询角色下拉列表
     *
     * @return {@link List}<{@link AdminRoleResp}>
     */
    @Override
    @PostMapping("/admin/user/getUserRoleList")
    public List<AdminRoleResp> getUserRoleList() {
        return adminUserService.getUserRoleList();
    }

    /**
     * 查询运营管理人员列表
     *
     * @param req
     * @return {@link PageResult}
     */
    @Override
    @PostMapping("/admin/user/getOperatorList")
    public PageResult getOperatorList(@RequestBody @Valid OperatorReq req) {
        return adminUserService.getOperatorList(req);
    }

    /**
     * 启用或禁用运营管理人员
     *
     * @param req
     */
    @Override
    @PostMapping("/admin/user/enableOrDisableAdminUser")
    public void enableOrDisableAdminUser(@RequestBody @Valid DisableOrEnableReq req) {
        adminUserService.enableOrDisableAdminUser(req);
    }

    /**
     * 新增或编辑运营管理人员
     *
     * @param req
     */
    @Override
    @PostMapping("/admin/user/editOperator")
    public void editOperator(@RequestBody @Valid EditOperatorReq req) {
        adminUserRoleService.editOperator(req);
    }

    /**
     * 查询角色管理列表
     *
     * @param req
     * @return {@link PageResult}
     */
    @Override
    @PostMapping("/admin/user/getRolePage")
    public PageResult getRolePage(@RequestBody @Valid OperatorReq req) {
        return adminRoleService.getRolePage(req);
    }

    /**
     * 启用禁用角色管理
     *
     * @param req
     */
    @Override
    @PostMapping("/admin/user/enableOrDisableRole")
    public void enableOrDisableRole(@RequestBody @Valid DisableOrEnableReq req) {
        adminRoleService.enableOrDisableRole(req);
    }

    /**
     * 新增或编辑角色
     *
     * @param req
     */
    @Override
    @PostMapping("/admin/user/editRole")
    public void editRole(@RequestBody @Valid RoleReq req) {
        adminRoleService.editRole(req);
    }

    /**
     * 删除角色
     *
     * @param req
     */
    @Override
    @PostMapping("/admin/user/removeRole")
    public void removeRole(@RequestBody @Valid CodeReq req) {
        adminRoleService.removeRole(req);
    }

    /**
     * 根据角色查询成员
     *
     * @param req
     * @return
     */
    @Override
    @PostMapping("/admin/user/getMerberByRoleId")
    public List<AdminUserResp> getMerberByRoleId(@Valid CodeReq req) {
        return adminUserService.getMerberByRoleId(req);
    }

    /**
     * 角色配置成员
     *
     * @param req
     */
    @Override
    @PostMapping("/admin/user/roleConfigurationMember")
    public void roleConfigurationMember(@RequestBody @Valid UserAndRoleReq req) {
        adminUserRoleService.roleConfigurationMember(req);
    }

    /**
     * 根据角色查询菜单资源
     *
     * @param req
     */
    @Override
    @PostMapping("/admin/user/getMenuByRoleId")
    public List<RoleAndMenuResp> getMenuByRoleId(@RequestBody @Valid CodeReq req) {
        return adminUserService.getMenuByRoleId(req);
    }

    /**
     * 角色添加菜单资源
     *
     * @param req
     */
    @Override
    @PostMapping("/admin/user/roleConfigurationMenu")
    public void roleConfigurationMenu(@RequestBody @Valid RoleAndMenuReq req) {
        adminUserService.roleConfigurationMenu(req);
    }

    @Override
    public List<AdminUserResp> findWorkOrderPermissionUsers() {
        return adminUserService.findWorkOrderPermissionUsers();
    }

    /**
     * 平台使用申请待审核数量
     *
     * @param
     * @return
     */
    @PostMapping("/admin/user/getPlatformApplicationReviewSum")
    public AdminUserSumResp getPlatformApplicationReviewSum() {
        return adminUserService.getPlatformApplicationReviewSum();
    }

    @Override
    @PostMapping("/admin/user/getAdminUserByUserId")
    public AdminUserResp getAdminUserByUserId(@RequestBody @Valid UserIdReq req) {
        return adminUserService.getAdminUserByUserId(req);
    }
}


