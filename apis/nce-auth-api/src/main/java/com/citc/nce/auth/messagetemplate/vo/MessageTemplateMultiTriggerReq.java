package com.citc.nce.auth.messagetemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 16:23
 * @Version: 1.0
 * @Description:
 */
@Data
public class MessageTemplateMultiTriggerReq implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @ApiModelProperty("id")
    private Long id;
    /**
     * 模板名称
     */
    @ApiModelProperty("模板名称")
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    /**
     * 消息类型
     */
    @ApiModelProperty("消息类型")
    @NotNull(message = "消息类型不能为空")
    private Integer messageType;

    @ApiModelProperty("模板类型(普通模板:1,个性模板:2)")
    private Integer templateType;

    /**
     * 数据json
     */
    @ApiModelProperty("消息数据JSON")
    @NotBlank(message = "消息数据JSON不能为空")
    private String moduleInformation;

    /**
     * 快捷按钮
     */
    @ApiModelProperty("快捷按钮")
    @NotBlank(message = "快捷按钮不能为空")
    private String shortcutButton;
    /**
     * 整体style信息
     */
    @ApiModelProperty("css样式JSON")
    private String styleInformation;
    /**
     * 是否需要送审，机器人保存是不需要送审为1，模板保存是需要送审为2 重新送审3
     */
    @ApiModelProperty("是否需要送审,默认为需要2  1为不需要")
    private int needAudit;
    /**
     * 模板来源 1模板管理  2机器人流程
     */
    @ApiModelProperty("模板来源 1模板管理  2机器人流程 3 兜底回复")
    private int templateSource;
    /**
     * 如果模板来自机器人流程，则会有流程id
     */
    private Long processId;

    /**
     * 素材关联运营商operator
     */
    private String operator;
    /**
     * 如果模板来自机器人流程，流程描述id
     */
    private Long processDescId;
    /**
     * 如果模板来自机器人流程，流程节点id
     */
    private String processNodeId;

    private String chatbotAccount;
}
