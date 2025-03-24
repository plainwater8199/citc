package com.citc.nce.authcenter.admin.dao;

import com.citc.nce.authcenter.admin.dto.CodeDto;
import com.citc.nce.authcenter.admin.dto.OperatorDto;
import com.citc.nce.authcenter.admin.dto.UserAndRoleDto;
import com.citc.nce.authcenter.admin.entity.AdminMenuDo;
import com.citc.nce.authcenter.admin.entity.AdminUserDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminUserMapper {

    List<UserAndRoleDto> selectOperatorList(OperatorDto req);

    void deleteRole(CodeDto req);

    int deleteRoleMenu(@Param("menuCodeList") List<String> removeList, @Param("roleId") String roleId);

    List<AdminMenuDo> selectMenuByUserId(@Param("userId") String userId);

    void deleteRoleByUserId(@Param("userId") String userId);

    List<String> selectUserRoleByUserId(@Param("userId") String userId);

    int deleteRoleList(@Param("roleIdList") List<String> removeRoleIdList, @Param("userId") String userId);

    int deleteRoleByUserIdList(@Param("roleId") String roleId, @Param("userIdList") List<String> removeList);

    List<AdminUserDo> findWorkOrderPermissionUsers();
}
