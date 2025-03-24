package com.citc.nce.auth.adminUser.dao;

import com.citc.nce.auth.adminUser.entity.AdminMenuDo;
import com.citc.nce.auth.adminUser.entity.AdminUserDo;
import com.citc.nce.auth.adminUser.vo.req.OperatorReq;
import com.citc.nce.auth.adminUser.vo.resp.UserAndRoleResp;
import com.citc.nce.auth.user.vo.req.CodeReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/9/23 11:48
 * @Version 1.0
 * @Description:
 */
@Mapper
public interface AdminUserMapper {

    List<UserAndRoleResp> selectOperatorList(OperatorReq req);

    void deleteRole(CodeReq req);

    int deleteRoleMenu(@Param("menuCodeList") List<String> removeList, @Param("roleId") String roleId);

    List<AdminMenuDo> selectMenuByUserId(@Param("userId") String userId);

    void deleteRoleByUserId(@Param("userId") String userId);

    List<String> selectUserRoleByUserId(@Param("userId") String userId);

    int deleteRoleList(@Param("roleIdList") List<String> removeRoleIdList);

    int deleteRoleByUserIdList(@Param("roleId") String roleId, @Param("userIdList") List<String> removeList);

    List<AdminUserDo> findWorkOrderPermissionUsers();
}
