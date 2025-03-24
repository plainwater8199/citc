package com.citc.nce.auth.prepayment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 预付费订单详情表
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-25 10:01:03
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("prepayment_order_detail")
@ApiModel(value = "PrepaymentOrderDetail对象", description = "预付费订单详情表")
public class PrepaymentOrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "`id`", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("订单ID, 17位时间戳+3位随机数")
    private String orderId;

    @ApiModelProperty("消息类型 {1:5g消息,2:视频短信,3:短信}")
    private MsgTypeEnum msgType;

    @ApiModelProperty("消息子类型 {0:文本消息,1:富媒体消息,2:会话消息}")
    private MsgSubTypeEnum msgSubType;

    @ApiModelProperty("总额度")
    private Long totalAmount;

    @ApiModelProperty("已用量")
    private Long usedAmount;

    @ApiModelProperty("失效量")
    private Long expiredAmount;

    @ApiModelProperty("可用量")
    private Long availableAmount;

    @ApiModelProperty("是否用完")
    private Boolean isDepleted;


}
