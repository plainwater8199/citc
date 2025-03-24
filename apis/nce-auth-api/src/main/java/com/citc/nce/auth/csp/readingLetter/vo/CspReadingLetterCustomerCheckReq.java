package com.citc.nce.auth.csp.readingLetter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CspReadingLetterCustomerCheckReq {
    @ApiModelProperty("账户ID")
    private String accountId;

    @ApiModelProperty("客户ID")
    @NotBlank(message = "客户ID不能为空")
    private String customerId;

    @ApiModelProperty("运营商")
    @NotNull(message = "运营商")
    private Integer operator;
}
