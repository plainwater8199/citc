package com.citc.nce.auth.adminUser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.adminUser.entity.AdminRoleDo;
import com.citc.nce.auth.adminUser.vo.req.DisableOrEnableReq;
import com.citc.nce.auth.adminUser.vo.req.OperatorReq;
import com.citc.nce.auth.adminUser.vo.req.RoleReq;
import com.citc.nce.auth.user.vo.req.CodeReq;
import com.citc.nce.common.core.pojo.PageResult;

/**
 * <p>
 * 运营人员角色表 服务类
 * </p>
 *
 * @author author
 * @since 2022-09-23
 */
public interface AdminRoleService extends IService<AdminRoleDo> {

    PageResult getRolePage(OperatorReq req);

    void enableOrDisableRole(DisableOrEnableReq req);

    void editRole(RoleReq req);

    void removeRole(CodeReq req);
}
