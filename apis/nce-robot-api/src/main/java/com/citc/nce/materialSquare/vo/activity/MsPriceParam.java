package com.citc.nce.materialSquare.vo.activity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author bydud
 * @since 2024/6/24 11:47
 */
@Getter
@Setter
@Accessors(chain = true)
public class MsPriceParam {
    @NotNull(message = "购买价格对应的方案不能为空，当前价格-1")
    private Long msActivityContentId;
    @NotNull(message = "作品不能为空")
    private Long mssId;
    @ApiModelProperty("原价")
    private BigDecimal originalPrice;
    @ApiModelProperty("价格展示：当前价格：original，活动价格：discount")
    private String pType;

}
