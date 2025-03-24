package com.citc.nce.authcenter.tenantdata.user.dao;

import com.citc.nce.authcenter.tenantdata.user.entity.ContactBackListDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;

/**
 * @Author: yangchuang
 * @Date: 2022/8/12 15:04
 * @Version: 1.0
 * @Description:联系人组联系人
 */
public interface ContactBackList1Dao extends BaseMapperX<ContactBackListDo> {

    @Update("UPDATE contact_blacklist SET delete_time=#{deleteTime},deleted=#{deleted} WHERE id=#{id}")
    int delContactBackListById(HashMap<String, Object> map);
}
