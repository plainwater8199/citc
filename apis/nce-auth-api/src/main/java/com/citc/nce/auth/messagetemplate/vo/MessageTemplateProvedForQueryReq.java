package com.citc.nce.auth.messagetemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yy
 * @Date: 2024/3/19 16:23
 * @Version: 1.0
 * @Description:
 */
@Data
public class MessageTemplateProvedForQueryReq implements Serializable {

    /**
     * 模板类型(普通模板:1,个性模板:2)
     */
    @ApiModelProperty("模板类型(普通模板:1,个性模板:2)")

    private Integer templateType;


    /**
     * 模板素材送审时对应的chatbot
     */
    @ApiModelProperty("模板所属chatbotAccount，逗号隔开")
    String accounts;
    /**
     * 归属运营商 硬核桃，联通，移动，电信
     */
    @ApiModelProperty("送审的运营商,逗号隔开")
    String accountType;
    /**
     * 模板在哪里定义的
     * 1  群发计划（模板管理模块）   2  机器人流程
     */
    @ApiModelProperty(value ="模板在哪里定义的 1  群发计划（模板管理模块）   2  机器人流程")
    private Integer templateSource;
}
