package com.citc.nce.auth.csp.readingLetter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CustomerReadingLetterAccountListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("账户id")
    private String accountId;

    @ApiModelProperty("资费")
    private String tariffContent;

    @ApiModelProperty("运营商")
    private Integer operator;

    @ApiModelProperty("状态 0:禁用 1:启用")
    private Integer status;

    @ApiModelProperty("账号类型 1:5G阅信 2:阅信+")
    private Integer smsType;

    @ApiModelProperty("域名")
    private String domain;

    @ApiModelProperty("创建时间")
    private Date createTime;
}
