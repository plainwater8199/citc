package com.citc.nce.auth.postpay.customerconfig.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("customer_postpay_config")
@ApiModel(value = "CustomerPostpayConfig", description = "后付费客户消息结算单价配置")
public class CustomerPostpayConfig implements Serializable {
    private static final long serialVersionUID = -5158222282433385727L;
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("客户id")
    private String customerId;

    @ApiModelProperty("短信单价")
    private BigDecimal smsPrice;

    @ApiModelProperty("视频短信单价")
    private BigDecimal videoPrice;

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
