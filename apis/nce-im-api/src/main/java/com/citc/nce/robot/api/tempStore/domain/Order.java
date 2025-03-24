package com.citc.nce.robot.api.tempStore.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.tempStore.enums.PayStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 模板商城 订单表
 * </p>
 *
 * @author bydud
 * @since 2023-11-17 02:11:15
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ts_order")
@ApiModel(value = "Order对象", description = "模板商城 订单表")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(value = "order_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    @ApiModelProperty("作品名称id--购买作品使用作品表主键id  ms_summary.mss_id")
    @TableField("goods_id")
    private Long goodsId;

    @ApiModelProperty("订单编号")
    @TableField("order_num")
    private String orderNum;

    @ApiModelProperty("作品所属cspId")
    @TableField("csp_id")
    private String cspId;

    @ApiModelProperty("模板类型")
    @TableField("temp_type")
    private MsType tempType;

    @ApiModelProperty("作品名称")
    @TableField("goods_name")
    private String goodsName;

    @ApiModelProperty("作品描述")
    @TableField("goods_desc")
    private String goodsDesc;

    //大字段不加入查询，查询时需要特殊指定
    @ApiModelProperty("作品快照")
    @TableField(value = "goods_snapshot", select = false)
    private String goodsSnapshot;
    @ApiModelProperty("活动内容---购买时参加的活动方案")
    @TableField(value = "ms_activity_content_id", select = false)
    private Long msActivityContentId;
    //大字段不加入查询，查询时需要特殊指定
    @ApiModelProperty("作品5g消息卡片样式快照")
    @TableField(value = "card_style_content", select = false)
    private String cardStyleContent;

    @ApiModelProperty("创建者（购买人）")
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("更新者")
    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("创建时间（购买时间）")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty("支付时间")
    @TableField("pay_time")
    private Date payTime;

    @ApiModelProperty("支付状态")
    @TableField("pay_status")
    private PayStatus payStatus;

    @ApiModelProperty("作品价格")
    @TableField("order_money")
    private String orderMoney;

    @ApiModelProperty("作品原价")
    @TableField("original_price")
    private String originalPrice;

    @ApiModelProperty("逻辑删除")
    @TableField("deleted")
    private Integer deleted;

    @ApiModelProperty("订单备注")
    @TableField("remake")
    private String remake;

    private Long getMssId() {
        return this.goodsId;
    }

    //是否免费的判断
    private boolean isFree() {
        return Objects.isNull(orderMoney);
    }
}
