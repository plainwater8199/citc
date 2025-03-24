package com.citc.nce.auth.messagetemplate.dao;

import com.citc.nce.auth.messagetemplate.entity.MessageTemplateAuditDo;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 16:57
 * @Version: 1.0
 * @Description:
 */
public interface MessageTemplateAuditDao extends BaseMapperX<MessageTemplateAuditDo> {
    @Update("UPDATE message_template_audit  SET delete_time=#{deleteTime},deleted=#{deleted} WHERE template_id=#{id} ")
    int delMessageTemplateAuditByTemplateId(HashMap<String, Object> map);


    @Update({
            "<script> ",
            "update message_template_audit set" +
                    "  delete_time=#{deleteTime}," +
                    "  deleted=#{deleted}" +
                    "  where  process_id=#{processId}" +
                    "  <if test=\"templateIds!=null\">" +
                    "      and template_id not in #{templateIds}" +
                    "  </if>" +
            "</script>"
    })
    int deleteTemplateAuditForInvalidOfProcess( @Param("deleteTime") Date deleteTime,
                                                @Param("deleted") Integer deleted,
                                                @Param("processId") Long processId,
                                                @Param("templateIds") List<Long> templateIds);

    @Update("UPDATE message_template_audit  SET delete_time=#{deleteTime},deleted=1 WHERE chatbot_account=#{chatbotAccount} ")
    int delMessageTemplateAuditByChatbotAccount(HashMap<String, Object> map);

    @Update("UPDATE message_template_audit  SET chatbot_account=#{newChatbotAccount} WHERE chatbot_account=#{oldChatbotAccount} ")
    int replaceChatbotAccount(HashMap<String, Object> map);
}
