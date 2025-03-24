package com.citc.nce.robot.api.tempStore.bean.manage;

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
@ApiModel(value = "后台订单管理查询-分页返回")
public class ManageOrderPage {

    @ApiModelProperty("主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    @ApiModelProperty("订单编号")
    private String orderNum;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品价格")
    private String orderMoney;

    @ApiModelProperty("创建者（购买人）")
    private String creator;
    @ApiModelProperty("创建者（购买人）名称")
    private String creatorName;

    @ApiModelProperty("创建者（购买人）手机号")
    private String creatorPhone;


    @ApiModelProperty("商品所属cspId")
    private String cspId;

    @ApiModelProperty("商品所属csp名称")
    private String cspName;

    @ApiModelProperty("csp手机号")
    private String cspPhone;

    @ApiModelProperty("订单备注")
    private String remake;

    @ApiModelProperty("支付状态")
    private PayStatus payStatus;

    @ApiModelProperty("创建时间（订单的提交时间）")
    private Date createTime;

}
