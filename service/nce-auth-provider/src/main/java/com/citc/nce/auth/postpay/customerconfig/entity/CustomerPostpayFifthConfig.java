package com.citc.nce.auth.postpay.customerconfig.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/2/28 17:08
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("customer_postpay_fifth_config")
@ApiModel(value = "CustomerPostpayFifthConfig", description = "后付费客户5G消息结算单价配置表")
public class CustomerPostpayFifthConfig implements Serializable {
    private static final long serialVersionUID = 9104138785264587763L;
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("客户端id")
    private String customerId;

    @ApiModelProperty("运营商")
    private CSPOperatorCodeEnum operator;

    @ApiModelProperty("文本消息单价")
    private BigDecimal textMessagePrice;

    @ApiModelProperty("富媒体消息单价")
    private BigDecimal richMessagePrice;

    @ApiModelProperty("会话单价")
    private BigDecimal conversionPrice;

    @ApiModelProperty("回落消息单价")
    private BigDecimal fallbackPrice;

    @ApiModelProperty("创建者")
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新者")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
