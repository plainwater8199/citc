package com.citc.nce.auth.prepayment.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/3/12 9:36
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("customer_prepayment_config")
@ApiModel(value = "CustomerPrepaymentConfig", description = "预付费客户消息结算单价（回落）配置表")
public class CustomerPrepaymentConfig {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("客户id")
    private String customerId;

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
