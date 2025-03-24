package com.citc.nce.auth.csp.readingLetter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class CspReadingLetterAccountSaveReq implements Serializable {
    @ApiModelProperty("账户名称")
    @NotBlank(message = "账户名称不能为空")
    @Length(max = 25, message = "账户名称长度超过限制")
    private String accountName;

    @ApiModelProperty("客户ID")
    @NotBlank(message = "归属客户不能为空")
    private String customerId;

    @ApiModelProperty("自定义域名")
    private String customDomains;

    @ApiModelProperty("运营商")
    private Integer operator;

    @ApiModelProperty(value = "appId")
    @NotBlank(message = "应用ID不能为空")
    @Length(max = 50, message = "应用ID长度超过限制")
    private String appId;

    @ApiModelProperty(value = "appKey")
    @NotBlank(message = "接入密钥不能为空")
    @Length(max = 50, message = "接入密钥长度超过限制")
    private String appKey;

    @ApiModelProperty(value = "agentId")
    @NotBlank(message = "代理商ID不能为空")
    @Length(max = 50, message = "代理商ID长度超过限制")
    private String agentId;

    @ApiModelProperty(value = "ecId")
    @NotBlank(message = "租户ID不能为空")
    @Length(max = 50, message = "租户ID长度超过限制")
    private String ecId;
}
