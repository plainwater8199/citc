package com.citc.nce.auth.messagetemplate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 16:17
 * @Version: 1.0
 * @Description:
 */

@Data
@TableName("message_template_proved")
public class MessageTemplateProvedDo extends BaseDo {

    /**
     * 样式名称
     */
    @TableField(value ="template_name")
    private String templateName;

    /**
     * 消息类型
     */
    @TableField(value ="message_type")
    private Integer messageType;

    /**
     * 模板类型(普通模板:1,个性模板:2)
     */
    @TableField(value ="template_type")
    private Integer templateType;

    /**
     * 数据json
     */
    @TableField(value ="module_information")
    private String moduleInformation;

    /**
     * 快捷按钮
     */
    @TableField(value="shortcut_button")
    private String shortcutButton;

    /**
     * 0未删除  1已删除
     */
    @TableField(value ="deleted")
    private Integer deleted;

    /**
     * 删除时间
     */
    @TableField(value ="delete_time")
    private Date deleteTime;

    /**
     * 送审平台返回的id
     */
    @TableField("platform_template_id")
    String platformTemplateId;
    /**
     * 系统模板id
     */
    @TableField("template_id")
    Long templateId;
    /**
     * 模板素材送审时对应的chatbot
     */
    @TableField("chatbot_account")
    String chatbotAccount;
    /**
     * 归属运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信
     */
    Integer operator;
    /**
     * 服务提供商 蜂动 fontdo   csp自有：owner
     */
    @TableField("supplier_tag")
    String supplierTag;
    /**
     * 整体style信息
     */
    @TableField("style_information")
    private String styleInformation;
    /**
     * 模板在哪里定义的
     * 1  群发计划（模板管理模块）   2  机器人流程
     */
    @TableField(value ="template_source")
    private int templateSource;
    /**
     * 如果模板来自机器人流程，则会有流程id
     */
    private Long processId;
    /**
     * 流程描述id
     */
    private Long processDescId;

    /**
     * 是否支持消息回落
     */
    private Boolean smsSupport;

    /**
     * 消息回落内容
     */
    private String fallbackText;
}
