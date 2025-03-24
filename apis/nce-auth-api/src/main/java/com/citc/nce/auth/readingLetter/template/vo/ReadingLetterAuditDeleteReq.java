package com.citc.nce.auth.readingLetter.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author zjy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadingLetterAuditDeleteReq {

    @NotNull(message = "accountId不能为空")
    @ApiModelProperty("accountId")
    private String accountId;

    @NotNull(message = "阅信类型不能为空")
    @ApiModelProperty("smsType")
    private Integer smsType;

    @NotNull(message = "appId不能为空")
    @ApiModelProperty("appId")
    private String appId;

    @NotNull(message = "appKey不能为空")
    @ApiModelProperty("appKey")
    private String appKey;

    @NotNull(message = "agentId不能为空")
    @ApiModelProperty("agentId")
    private String agentId;

}
