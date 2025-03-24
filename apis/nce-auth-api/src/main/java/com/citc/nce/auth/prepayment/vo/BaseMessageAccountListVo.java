package com.citc.nce.auth.prepayment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author jiancheng
 */
@Data
@ApiModel("消息账号列表基础对象")
public class BaseMessageAccountListVo {
    @ApiModelProperty("账号名称")
    private String accountName;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("总订单金额")
    private String totalOrderAmount;

    @ApiModelProperty("总退款金额")
    private String totalOrderRefund;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
