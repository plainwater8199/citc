package com.citc.nce.auth.contactlist.dao;

import com.citc.nce.auth.contactlist.entity.ContactListDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yangchuang
 * @Date: 2022/8/12 15:04
 * @Version: 1.0
 * @Description:联系人组联系人
 */
public interface ContactListDao extends BaseMapperX<ContactListDo> {
    @Update("UPDATE contact_list SET delete_time=#{deleteTime},deleted=#{deleted} WHERE id=#{id}")
    int delContactListById(HashMap<String, Object> map);

    @Update("UPDATE contact_list SET blacklist=#{blacklist} WHERE id=#{id}")
    int saveBlack(HashMap<String, Object> map);
    @Select("SELECT MAX(update_time) FROM contact_list WHERE group_id=#{groupId} and deleted=0")
    Date getContactListUpdateTimeByGroupId(Long groupId);
    @Select("SELECT group_id AS groupId,COUNT(*) AS nums FROM contact_list WHERE deleted=0 and creator=#{userId} GROUP BY group_id")
    List<Map<String,Long>> selectCountByGroupId(String userId);

    @Update("UPDATE contact_list SET delete_time=#{deleteTime},deleted=#{deleted} WHERE group_id=#{groupId} and phone_num=#{phoneNum}")
    int delContactListByPhone(HashMap<String, Object> map);
    @Select("SELECT group_id AS groupId,COUNT(*) AS nums FROM contact_list WHERE deleted=0 GROUP BY group_id")
    List<Map<String, Long>> selectCountByGroupIdAdmin();
}
