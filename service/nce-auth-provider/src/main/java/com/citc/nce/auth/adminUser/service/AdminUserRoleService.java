package com.citc.nce.auth.adminUser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.adminUser.entity.AdminUserRoleDo;
import com.citc.nce.auth.adminUser.vo.req.EditOperatorReq;
import com.citc.nce.auth.adminUser.vo.req.UserAndRoleReq;


/**
 * 管理用户角色服务
 *
 * @author ylzouf
 * @date 2022/09/23
 */
public interface AdminUserRoleService extends IService<AdminUserRoleDo> {

    void editOperator(EditOperatorReq req);

    void roleConfigurationMember(UserAndRoleReq req);
}
