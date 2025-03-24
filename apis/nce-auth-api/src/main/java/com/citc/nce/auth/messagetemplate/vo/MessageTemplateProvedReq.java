package com.citc.nce.auth.messagetemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: zhujy
 * @Date: 2024/3/19 16:23
 * @Version: 1.0
 * @Description:
 */
@Data
public class MessageTemplateProvedReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * templateId
     */
    @ApiModelProperty("templateId")
    private Long templateId;

    /**
     * 运营商
     */
    @ApiModelProperty("运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信")
    private Integer operator;

    /**
     * 创建者
     */
    @ApiModelProperty("服务提供商 蜂动 fontdo   csp自有：owner")
    private String supplierTag;
    /**
     * 运营商
     */
    @ApiModelProperty("运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信")
    private String chatbotAccount;
    /**
     * 运营商
     */
    @ApiModelProperty("运营商 硬核桃，联通，移动，电信")
    private String accountType;

    public MessageTemplateProvedReq(Long templateId, String accountType) {
        this.templateId = templateId;
        this.accountType = accountType;
    }

    public MessageTemplateProvedReq() {
    }
}
