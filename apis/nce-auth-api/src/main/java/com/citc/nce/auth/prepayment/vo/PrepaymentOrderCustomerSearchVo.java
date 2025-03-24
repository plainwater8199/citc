package com.citc.nce.auth.prepayment.vo;

import com.citc.nce.auth.csp.recharge.Const.PrepaymentStatus;
import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class PrepaymentOrderCustomerSearchVo extends PageParam {

    @ApiModelProperty("订单编号")
    private String orderId;

    @ApiModelProperty("消息类型")
    private Integer type;
    @ApiModelProperty("订单类型 1充值 2预购")
    private Integer consumeCategory;
    @ApiModelProperty("订单状态 0待支付 1已取消 2支付完成")
    private PrepaymentStatus status;

    @ApiModelProperty("开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;

}
