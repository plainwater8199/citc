package com.citc.nce.auth.contactgroup.dao;

import com.citc.nce.auth.contactgroup.entity.ContactGroupDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;

/**
 * @Author: yangchuang
 * @Date: 2022/8/10 17:22
 * @Version: 1.0
 * @Description:联系人组
 */
public interface ContactGroupDao extends BaseMapperX<ContactGroupDo> {
    @Update("UPDATE contact_group SET delete_time=#{deleteTime},deleted=#{deleted} WHERE id=#{id}")
    int delContactGroupById(HashMap<String, Object> map);
}
