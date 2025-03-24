package com.citc.nce.auth.postpay.scheme.vo;

import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author jcrenc
 * @since 2024/3/6 9:50
 */
@Data
@ApiModel
public class SchemeAddVo {
    @ApiModelProperty("方案名称")
    @NotEmpty(message = "方案名称不能为空")
    private String name;

    @Valid
    @NotEmpty
    private List<Config> configs;

    @ApiModel
    @Data
    public static class Config {
        @ApiModelProperty("运营商")
        @NotNull
        private CSPOperatorCodeEnum operator;

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
}
