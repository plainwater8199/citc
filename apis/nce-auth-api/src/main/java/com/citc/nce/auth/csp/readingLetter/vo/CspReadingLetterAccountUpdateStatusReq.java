package com.citc.nce.auth.csp.readingLetter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CspReadingLetterAccountUpdateStatusReq {
    @ApiModelProperty("账户ID")
    @NotBlank(message = "账户ID不能为空")
    private String accountId;

    @ApiModelProperty(value = "状态 0:禁用 1:启用")
    private Integer status;
}
