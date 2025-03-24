package com.citc.nce.auth.postpay.config.vo;

import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author jcrenc
 * @since 2024/3/6 16:29
 */
@Data
@ApiModel
@Accessors(chain = true)
public class CustomerPostpayConfigVo {
    @ApiModelProperty("客户id")
    @NotNull
    private String customerId;

    @ApiModelProperty("短信单价")
    @DecimalMax("99.99999")
    @NotNull(groups = CreateGroup.class)
    @PositiveOrZero
    private BigDecimal smsPrice;

    @ApiModelProperty("视频短信单价")
    @DecimalMax("99.99999")
    @NotNull(groups = CreateGroup.class)
    @PositiveOrZero
    private BigDecimal videoPrice;

    @ApiModelProperty("5G消息单价配置")
    @Valid
    private Map<CSPOperatorCodeEnum, Config> fifthConfigMap;

    @ApiModel
    @Data
    public static class Config {

        @ApiModelProperty("文本消息单价")
        @DecimalMax("99.99999")
        @NotNull
        @PositiveOrZero
        private BigDecimal textMessagePrice;

        @ApiModelProperty("富媒体消息单价")
        @DecimalMax("99.99999")
        @NotNull
        @PositiveOrZero
        private BigDecimal richMessagePrice;

        @ApiModelProperty("会话单价")
        @DecimalMax("99.99999")
        @NotNull
        @PositiveOrZero
        private BigDecimal conversionPrice;

        @ApiModelProperty("回落消息单价")
        @DecimalMax("99.99999")
        @NotNull
        @PositiveOrZero
        private BigDecimal fallbackPrice;
    }

    public interface CreateGroup{}
    public interface UpdateGroup{}
}
