package com.citc.nce.auth.messagetemplate.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateDo;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateDoQueryParam;
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
public interface MessageTemplateDao extends BaseMapperX<MessageTemplateDo> {
    @Update("UPDATE message_template  SET delete_time=#{deleteTime},deleted=#{deleted} WHERE id=#{id} ")
    int delMessageTemplateById(HashMap<String, Object> map);

    @Select("select distinct t.* from message_template t left join message_template_audit a on t.id=a.template_id where a.deleted=0 and t.deleted=0 and a.platform_template_id = #{platform_id}")
    MessageTemplateDo getTemplateByPlatformTemplateId(String platformTemplateId);
    List<MessageTemplateDo> getTemplates(MessageTemplateDoQueryParam queryParam);
    @Update({
            "<script> ",
            "update message_template set" +
                    "    delete_time=#{deleteTime}," +
                    "    deleted=#{deleted}" +
                    "    where  process_id=#{processId}" +
                    "    <if test=\"templateIds!=null\">" +
                    "       and id not in #{templateIds}" +
                    "    </if>" ,
            "</script>"
    })
    int deleteTemplateForInvalidOfProcess(  @Param("deleteTime") Date deleteTime,
                                            @Param("deleted") Integer deleted,
                                            @Param("processId") Long processId,
                                            @Param("templateIds") List<Long> templateIds);

    Page<MessageTemplateDo> getTemplates(
            @Param("templateName") String templateName,
            @Param("templateType") Integer templateType,
            @Param("messageType") Integer messageType,
            @Param("status") Integer status,
            @Param("templateSource") Integer templateSource,
            @Param("accounts") List<String> chatbotAccounts,
            @Param("creator") String creator,
            Page<MessageTemplateDo> page
    );
    List<MessageTemplateDo> getProveTemplates(
            @Param("templateType") Integer templateType,
            @Param("templateSource") Integer templateSource,
            @Param("operators") List<Integer> operators,
            @Param("creator") String creator
    );
}
