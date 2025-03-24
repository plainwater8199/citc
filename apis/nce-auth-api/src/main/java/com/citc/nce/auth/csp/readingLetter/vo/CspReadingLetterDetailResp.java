package com.citc.nce.auth.csp.readingLetter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class CspReadingLetterDetailResp {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("账户id")
    private String accountId;

    @ApiModelProperty("客户id")
    private String customerId;

    @ApiModelProperty("状态 0:禁用 1:启用")
    private Integer status;

    @ApiModelProperty("运营商 1：联通，2：移动，3：电信")
    private Integer operator;

    @ApiModelProperty("自定义域名")
    private String customDomains;

    @ApiModelProperty(value = "appId")
    private String appId;

    @ApiModelProperty(value = "appKey")
    private String appKey;

    @ApiModelProperty(value = "agentId")
    private String agentId;

    @ApiModelProperty(value = "ecId")
    private String ecId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty("资费批次")
    private String tariffBatch;
}
