package com.citc.nce.auth.messagetemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 16:23
 * @Version: 1.0
 * @Description:
 */
@Data
public class MessageTemplateTreeResp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 样式名称
     */
    @ApiModelProperty("样式名称")
    private String templateName;

    /**
     * 消息类型
     */
    @ApiModelProperty("消息类型")
    private int messageType;

    /**
     * 数据json
     */
    @ApiModelProperty("消息数据JSON")
    private String moduleInformation;

    /**
     * 快捷按钮
     */
    @ApiModelProperty("快捷按钮")
    private String shortcutButton;

    /**
     * 模板类型
     */
    @ApiModelProperty("模板类型 1:普通模板,2:个性模板")
    private Integer templateType;

}
