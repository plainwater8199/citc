package com.citc.nce.auth.csp.readingLetter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CspReadingLetterIdReq {
    @ApiModelProperty("账户ID")
    @NotBlank(message = "账户ID不能为空")
    private String accountId;
}
