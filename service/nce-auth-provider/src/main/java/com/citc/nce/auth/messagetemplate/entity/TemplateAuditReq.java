package com.citc.nce.auth.messagetemplate.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author yangyang
 * chatbot 发送消息时的消息体
 */
@Data
public class TemplateAuditReq implements Serializable {
    /**
     * 蜂动平台模板id
     */
    String id;
    /**
     * 本平台模板id
     */
    Long templateId;
    /**
     * 直客服务提供商   默认为fontdo 蜂动
     */
    String supplier;
    /**
     * 模板名称
     */
    @NotNull
    @Length(min = 1, max = 200)
    String name;
    /**
     * 模板消息类型: RCS(5G消息)，AIM(智能短信)，
     * MMS(视频短信)
     */
    String type;
    /**
     * BUSINESS：商用
     * TEST：试商用
     * 默认BUSINESS
     */
    String useType;
    /**
     * 模板内容
     */
    @NotNull
    Object templateContent;
    /**
     * 主题，视频短信模板必填
     */
    String subject;
    /**
     * 短信内容，智能短信模板必填
     */
    String sms;

    /**
     * 模板中的参数列表
     */
    List<TemplateParameterReq> parameters;
    /**
     * 模版中的样式文件
     */
    TemplateStyleReq style;

}
