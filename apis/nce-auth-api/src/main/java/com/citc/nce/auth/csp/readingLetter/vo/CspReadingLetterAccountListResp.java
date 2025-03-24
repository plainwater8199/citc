package com.citc.nce.auth.csp.readingLetter.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class CspReadingLetterAccountListResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("账户id")
    private String accountId;

    @ApiModelProperty("客户账户名")
    private String enterpriseAccountName;

    @ApiModelProperty("客户名")
    private String enterpriseName;

    @ApiModelProperty("运营商")
    private Integer operator;

    @ApiModelProperty("运营商名称")
    private String operatorName;

    @ApiModelProperty("状态 0:禁用 1:启用")
    private Integer status;

    @ApiModelProperty("自定义域名")
    private String customDomains;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("资费定价")
    private String tariffContent;

    @ApiModelProperty("客户id")
    private String customerId;
}
