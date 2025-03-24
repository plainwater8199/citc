package com.citc.nce.auth.prepayment.vo;

import com.citc.nce.auth.csp.recharge.Const.PrepaymentStatus;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class PrepaymentOrderManageListVo {
    @ApiModelProperty("订单主键ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("订单编号")
    private String orderId;

    @ApiModelProperty("消息类型 1:5g消息,2:视频短信,3:短信")
    private MsgTypeEnum msgType;

    @ApiModelProperty("套餐ID")
    private String planId;

    @ApiModelProperty("账号名称")
    private String accountName;

    @ApiModelProperty("客户企业账号名称")
    private String enterpriseAccountName;

    @ApiModelProperty("客户手机号")
    private String phone;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("订单金额")
    private String amount;

    @ApiModelProperty("套餐详情,记录生成订单时使用的套餐快照")
    private String planDetail;

    @ApiModelProperty("订单状态 0待支付 1已取消 2支付完成")
    private PrepaymentStatus status;

    @ApiModelProperty("支付时间")
    private LocalDateTime payTime;

    @ApiModelProperty("交易流水号")
    private String serialNumber;

    @ApiModelProperty("订单备注")
    private String note;

    @JsonIgnore
    private String customerId;

    @ApiModelProperty("消费种类 1:充值  2:预购套餐")
    private Integer consumeCategory;
    @ApiModelProperty("支付金额")
    private Long payAmount;
    @ApiModelProperty("充值金额")
    private Long chargeAmount;
}
