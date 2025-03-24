package com.citc.nce.auth.messagetemplate.dao;

import com.citc.nce.auth.messagetemplate.entity.MessageTemplateDo;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateProvedDo;
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
public interface MessageTemplateProvedDao extends BaseMapperX<MessageTemplateProvedDo> {
    @Update("UPDATE message_template_proved  SET delete_time=#{deleteTime},deleted=#{deleted} WHERE template_id=#{id} ")
    int delProvedMessageTemplateByTemplateId(HashMap<String, Object> map);
    @Update("UPDATE message_template_proved  SET delete_time=#{deleteTime},deleted=1 WHERE chatbot_account=#{chatbotAccount} ")
    int delProvedMessageTemplateByChatbotAccount(HashMap<String, Object> map);
    @Update("UPDATE message_template_proved  SET chatbot_account=#{newChatbotAccount} WHERE chatbot_account=#{oldChatbotAccount} ")
    int replaceChatbotAccount(HashMap<String, Object> map);
    @Update({
            "<script> ",
            "update message_template_proved set" +
                    "  delete_time=#{deleteTime}," +
                    "  deleted=#{deleted}" +
                    "  where  process_id=#{processId}" +
                    "  <if test=\"templateIds!=null\">" +
                    "      and template_id not in #{templateIds}" +
                    "  </if>",
            "</script>"
    })
    int deleteTemplateProvedForInvalidOfProcess(@Param("deleteTime") Date deleteTime,
                                                @Param("deleted") Integer deleted,
                                                @Param("processId") Long processId,
                                                @Param("templateIds") List<Long> templateIds);

    @Select({
            "<script> ",
            "select * from message_template_proved" +
                    "  where  process_id=#{processId}" +
                    " and deleted=0 "+
                    "  <if test=\"templateIds!=null\">" +
                    "      and template_id not in #{templateIds}" +
                    "  </if>",
            "</script>"
    })
    List<MessageTemplateProvedDo> selectTemplateProvedForInvalidOfProcess( @Param("processId") Long processId,
                                                @Param("templateIds") List<Long> templateIds);
}
