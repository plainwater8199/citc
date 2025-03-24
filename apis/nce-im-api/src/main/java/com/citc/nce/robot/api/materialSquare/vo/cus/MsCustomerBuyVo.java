package com.citc.nce.robot.api.materialSquare.vo.cus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author bydud
 * @since 2024/6/24 11:21
 */
@Data
public class MsCustomerBuyVo {
    @ApiModelProperty("作品id")
    @NotNull(message = "作品id不能为空")
    private Long mssId;
    @ApiModelProperty("页面价格")
    @NotNull(message = "购买价格不能为空")
    private BigDecimal price;
    @ApiModelProperty("活动方案id")
    @NotNull(message = "购买价格对应的方案不能为空，未参加活动-1")
    private Long msActivityContentId;

    @ApiModelProperty("价格展示：当前价格：original，活动价格：discount")
    private String pType;
}
