package com.citc.nce.auth.postpay.scheme.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author jcrenc
 * @since 2024/3/6 11:16
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SchemeListVo {
    @ApiModelProperty("主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("方案名称")
    private String name;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("运营商配置")
    private Map<CSPOperatorCodeEnum, Config> configMap;

    @Data
    @Accessors(chain = true)
    public static class Config {
        @ApiModelProperty("文本消息单价")
        private BigDecimal textMessagePrice;

        @ApiModelProperty("富媒体消息单价")
        private BigDecimal richMessagePrice;

        @ApiModelProperty("会话单价")
        private BigDecimal conversionPrice;

        @ApiModelProperty("回落消息单价")
        private BigDecimal fallbackPrice;
    }
}
