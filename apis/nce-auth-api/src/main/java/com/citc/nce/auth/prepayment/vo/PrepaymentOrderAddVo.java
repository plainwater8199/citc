package com.citc.nce.auth.prepayment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class PrepaymentOrderAddVo {

    @ApiModelProperty("消息类型 {1:5g消息,2:视频短信,3:短信}")
    @NotNull
    private Integer msgType;

    @ApiModelProperty("5g消息订单->机器人账号，短信订单->短信账号，视频短信订单->视频短信账号")
    @NotNull
    private String accountId;

    @ApiModelProperty("套餐ID, mp+17位时间戳+随机数")
    @NotNull
    private String planId;

}
