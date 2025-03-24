package com.citc.nce.auth.csp.readingLetter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CspReadingLetterAccountNameCheckReq {
    @ApiModelProperty("账户ID")
    private String accountId;

    @ApiModelProperty("账号名称")
    @NotNull(message = "账号名称")
    private String accountName;
}
