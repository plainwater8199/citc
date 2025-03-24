package com.citc.nce.authcenter.tenantdata.user.dao;


import com.citc.nce.authcenter.tenantdata.user.entity.FormManagementDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;

/**
 * 表单管理
 */
public interface FormManagement1Dao extends BaseMapperX<FormManagementDo> {

    @Update("UPDATE form_management SET delete_time=#{deleteTime},deleted=#{deleted} WHERE id=#{id}")
    int delFormManagementById(HashMap<String, Object> map);
}
