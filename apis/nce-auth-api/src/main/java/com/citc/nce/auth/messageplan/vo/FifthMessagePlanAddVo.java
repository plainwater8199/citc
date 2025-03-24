package com.citc.nce.auth.messageplan.vo;

import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class FifthMessagePlanAddVo {

    @ApiModelProperty("套餐名称，25个字符，不能重复")
    @NotNull
    @Length(max = 25)
    private String name;

    @ApiModelProperty("运营商编码： 0:硬核桃，1：联通，2：移动，3：电信")
    @NotNull
    private CSPOperatorCodeEnum operator;

    @ApiModelProperty("文本消息数量")
    @Range(max = 999999,min = 0)
    @NotNull
    private Long textMessageNumber;

    @ApiModelProperty("文本消息单价")
    @DecimalMax("99.99999")
    @NotNull
    @PositiveOrZero
    private BigDecimal textMessagePrice;

    @ApiModelProperty("富媒体消息数量")
    @Range(max = 999999,min = 0)
    @NotNull
    private Long richMessageNumber;

    @ApiModelProperty("富媒体消息单价")
    @DecimalMax("99.99999")
    @NotNull
    @PositiveOrZero
    private BigDecimal richMessagePrice;

    @ApiModelProperty("会话数")
    @Range(max = 999999,min = 0)
    @NotNull
    private Long conversionNumber;

    @ApiModelProperty("会话单价")
    @DecimalMax("99.99999")
    @NotNull
    @PositiveOrZero
    private BigDecimal conversionPrice;
}
