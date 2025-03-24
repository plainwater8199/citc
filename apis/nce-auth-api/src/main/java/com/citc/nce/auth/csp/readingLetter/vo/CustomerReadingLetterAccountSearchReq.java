package com.citc.nce.auth.csp.readingLetter.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class CustomerReadingLetterAccountSearchReq {
    @ApiModelProperty("账号名称")
    private String accountName;

    @ApiModelProperty("运营商")
    private Integer operator;

    @ApiModelProperty("账号类型 1:5G阅信 2:阅信+")
    private Integer smsType;
}
