package com.citc.nce.auth.prepayment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * @author jcrenc
 * @since 2024/3/12 9:43
 */
@Data
@ApiModel
@Accessors(chain = true)
public class CustomerPrepaymentConfigVo {
    @DecimalMax("99.99999")
    @NotNull
    @PositiveOrZero
    @ApiModelProperty("回落消息单价")
    private BigDecimal fallbackPrice;

    @NotNull
    private String customerId;

}
