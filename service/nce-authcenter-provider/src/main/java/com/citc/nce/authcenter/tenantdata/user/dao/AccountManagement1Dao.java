package com.citc.nce.authcenter.tenantdata.user.dao;

import com.citc.nce.authcenter.tenantdata.user.entity.AccountManagementDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:00
 * @Version: 1.0
 * @Description:
 */
@Mapper
public interface AccountManagement1Dao extends BaseMapperX<AccountManagementDo> {
    @Update("UPDATE account_management SET delete_time=#{deleteTime},deleted=#{deleted} WHERE id=#{id} ")
    int delAccountManagementById(HashMap<String, Object> map);
}
