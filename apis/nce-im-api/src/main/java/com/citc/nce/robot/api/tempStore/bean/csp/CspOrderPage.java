package com.citc.nce.robot.api.tempStore.bean.csp;

import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.tempStore.enums.PayStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author bydud
 * @since 8:56
 */
@Data
@ApiModel(value = "csp-order-分页查询信息")
public class CspOrderPage {

    @ApiModelProperty("主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    @ApiModelProperty("订单编号")
    private String orderNum;

    @ApiModelProperty("商品所属cspId")
    private String cspId;

    @ApiModelProperty("模板类型")
    private MsType tempType;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品名称id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long goodsId;

    @ApiModelProperty("创建者（购买人）")
    private String creator;

    @ApiModelProperty("创建者（购买人）名称")
    private String creatorName;

    @ApiModelProperty("创建者（购买人）手机号")
    private String creatorPhone;

    @ApiModelProperty("创建时间（订单的提交时间）")
    private Date createTime;

    @ApiModelProperty("支付时间/订购时间csp设置“支付完成”的时间")
    private Date payTime;

    @ApiModelProperty("支付状态")
    private PayStatus payStatus;

    @ApiModelProperty("商品价格")
    private String orderMoney;

    @ApiModelProperty("商品原价")
    private String originalPrice;
}
