package com.citc.nce.auth.messagetemplate.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 16:23
 * @Version: 1.0
 * @Description:
 */
@Data
public class MessageTemplatePageReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 样式名称
     */
    @ApiModelProperty("样式名称")
    private String templateName;
    /**
     * 消息类型
     */
    @ApiModelProperty("消息类型")
    private Integer messageType;

    @ApiModelProperty("模板类型(普通模板:1,个性模板:2)")
    private Integer templateType;
    @ApiModelProperty("模板来源(群发计划（模板管理）:1,机器人流程:2 兜底模板:3)")
    private Integer templateSource;
    @ApiModelProperty("chatbot名称")
    private String chatbotAccount;
    @ApiModelProperty("模板状态")
    private Integer status;
    @ApiModelProperty("分页插件")
    private PageParam pageParam;
}
