package com.citc.nce.auth.messagetemplate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 16:17
 * @Version: 1.0
 * @Description:
 */

@Data
@TableName("message_template")
@Accessors(chain = true)
public class MessageTemplateDo extends BaseDo<MessageTemplateDo> {


    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

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
