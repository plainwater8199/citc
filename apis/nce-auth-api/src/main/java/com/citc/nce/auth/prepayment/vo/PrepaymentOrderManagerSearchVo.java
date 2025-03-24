package com.citc.nce.auth.prepayment.vo;

import com.citc.nce.auth.csp.recharge.Const.PrepaymentStatus;
import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class PrepaymentOrderManagerSearchVo extends PageParam {
    @ApiModelProperty("订单ID")
    private String orderId;

    @ApiModelProperty("消息类型 1:5g消息,2:视频短信,3:短信,4:阅信+")
    private Integer type;
    @ApiModelProperty("订单类别 2 预购 1 充值 空 全部")
    private Integer consumeCategory;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("订单状态 0待支付 1已取消 2支付完成")
    private PrepaymentStatus status;
}
