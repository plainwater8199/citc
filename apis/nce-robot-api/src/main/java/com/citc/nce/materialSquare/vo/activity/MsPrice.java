package com.citc.nce.materialSquare.vo.activity;

import com.citc.nce.materialSquare.entity.MsManageActivityContent;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author bydud
 * @since 2024/6/24 11:47
 */
@Data
@NoArgsConstructor
public class MsPrice {

    @ApiModelProperty("价格")
    private BigDecimal price;
    @ApiModelProperty("折扣")
    private Double discountRate;
    @ApiModelProperty("活动方案--打折的活动方案")
    private MsManageActivityContent activityContent;
    @ApiModelProperty("价格查询失败")
    private Boolean queryFlag = false;

    @ApiModelProperty("失败原因")
    private Exception exception;

    public MsPrice(BigDecimal price, Exception e) {
        this.price = price;
        this.queryFlag = true;
        this.exception = e;
    }
}
