package com.citc.nce.auth.csp.sms.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CspSmsAccountResp implements Serializable {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("账户id")
    private String accountId;

    @ApiModelProperty("归属客户id")
    private String enterpriseId;

    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @ApiModelProperty("企业账户名")
    private String enterpriseAccountName;

    @ApiModelProperty("通道code")
    private String dictCode;

    @ApiModelProperty("通道value")
    private String dictValue;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("状态 0:禁用 1:启用")
    private Integer status;

    @ApiModelProperty("累计充值金额")
    private String accumulatedRechargeAmount;

    @ApiModelProperty("累计退款总额")
    private String accumulatedRefundAmount;

    @ApiModelProperty("充值总条数")
    private String totalRechargeCount;

    @ApiModelProperty("扣除总条数")
    private String totalDifferCount;

    @ApiModelProperty("视频短信剩余量")
    private Long residualCount;

    @ApiModelProperty("充值单价")
    private Long chargePrice;

    private String customerId;
}
