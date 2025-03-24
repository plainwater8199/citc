package com.citc.nce.authcenter.admin.service;

import com.citc.nce.auth.adminUser.vo.req.AuditIdentificationReq;
import com.citc.nce.auth.adminUser.vo.req.ReviewCspReq;
import com.citc.nce.auth.identification.vo.req.IdentificationPlatformPermissionReq;
import com.citc.nce.authcenter.admin.entity.AdminMenuDo;
import com.citc.nce.authcenter.admin.entity.AdminUserDo;

import java.util.List;

public interface AdminUserService {
    /**
     * 获取管理员的角色列表
     * @param adminUserId 管理员id 
     * @return 角色Id列表
     */
    List<String> findAdminRoleList(String adminUserId);

    /**
     * 根据角色列表查询权限地址
     * @param roleIdList 角色列表
     * @return 权限地址列表
     */
    List<AdminMenuDo> findAuthUrlListByRoleIds(List<String> roleIdList);

    /**
     * 通过手机号查询管理员用户
     * @param phone 手机号
     * @return 管理用户信息
     */
    AdminUserDo findByPhone(String phone);

    /**
     * Admin端 审核client端 userId 用户认证
     */
    void auditIdentification(AuditIdentificationReq req);

    void reviewPlatformForCsp(ReviewCspReq req);

    void applyPlatformPermission(IdentificationPlatformPermissionReq req);

    void superAdminMenuSyn(String adminRoleId);
}
