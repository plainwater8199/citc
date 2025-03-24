package com.citc.nce.authcenter.tenantdata.user.dao;

import com.citc.nce.authcenter.tenantdata.user.entity.MessageTemplateDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 16:57
 * @Version: 1.0
 * @Description:
 */
public interface MessageTemplate1Dao extends BaseMapperX<MessageTemplateDo> {
    @Update("UPDATE message_template  SET delete_time=#{deleteTime},deleted=#{deleted} WHERE id=#{id} ")
    int delMessageTemplateById(HashMap<String, Object> map);
}
