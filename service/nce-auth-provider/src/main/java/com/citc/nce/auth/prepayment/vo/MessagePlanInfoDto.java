package com.citc.nce.auth.prepayment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class MessagePlanInfoDto {
    @ApiModelProperty("订单金额")
    private String amount;

    @ApiModelProperty("套餐详情,记录生成订单时使用的套餐快照")
    private String planDetail;

    @ApiModelProperty("套餐明细列表")
    private List<MessagePlanDetailDto> detailList;
}
