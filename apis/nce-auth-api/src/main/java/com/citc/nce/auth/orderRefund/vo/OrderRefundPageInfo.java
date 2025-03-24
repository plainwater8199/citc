package com.citc.nce.auth.orderRefund.vo;

import com.citc.nce.common.core.enums.MsgTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author bydud
 * @since 2024/3/12
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "订单退款 分页")
public class OrderRefundPageInfo {
    @ApiModelProperty("主键")
    private Long orId;
    @ApiModelProperty("订单表id")
    private Long tOrderId;
    @ApiModelProperty("订单编号")
    private String orderId;

    @ApiModelProperty("套餐类型 1:5g消息,2:视频短信,3:短信")
    private MsgTypeEnum msgType;

    @ApiModelProperty("申请人")
    private String creator;
    @ApiModelProperty("客户名称（企业账号名称）")
    private String enterpriseAccountName;
    @ApiModelProperty("5g消息订单->机器人账号，短信订单->短信账号，视频短信订单->视频短信账号")
    private String accountId;
    @ApiModelProperty("5g消息订单->机器人账号，短信订单->短信账号，视频短信订单->视频短信账号")
    private String accountName;


    @ApiModelProperty("退款金额")
    private BigDecimal refundAmount;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("剩余信息")
    private List<ResidueInfo> residueInfo;
}
