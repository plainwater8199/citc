package com.citc.nce.robot.api.tempStore.bean.customer;

import com.citc.nce.robot.api.tempStore.enums.PayStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author bydud
 * @since 8:56
 */
@Data
@ApiModel(value = "客户-订单管理查询信息")
public class ClientOrderPage {

    @ApiModelProperty("主键id")
    private Long orderId;

    @ApiModelProperty("订单编号")
    private String orderNum;

    @ApiModelProperty("商品所属cspId")
    private String cspId;

    @ApiModelProperty("模板类型")
    private Integer tempType;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品名称id")
    private Long goodsId;

    @ApiModelProperty("创建者（购买人）")
    private String creator;

    @ApiModelProperty("创建时间（订单的提交时间）")
    private Date createTime;

    @ApiModelProperty("支付时间/订购时间csp设置“支付完成”的时间")
    private Date payTime;

    @ApiModelProperty("支付状态")
    private PayStatus payStatus;

    @ApiModelProperty("商品价格")
    private String orderMoney;
}
