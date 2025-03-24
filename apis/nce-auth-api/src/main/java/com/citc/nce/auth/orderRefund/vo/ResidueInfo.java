package com.citc.nce.auth.orderRefund.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author bydud
 * @since 2024/3/12
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "订单退款 列表剩余信息")
public class ResidueInfo {

    @ApiModelProperty("订单ID, 17位时间戳+3位随机数")
    private String orderId;

    @ApiModelProperty("消息类型 {1:5g消息,2:视频短信,3:短信}")
    private MsgTypeEnum msgType;

    @ApiModelProperty("消息子类型 {0:文本消息,1:富媒体消息,2:会话消息}")
    private MsgSubTypeEnum msgSubType;

    @ApiModelProperty("额度")
    private Long limit;

    @ApiModelProperty("使用量")
    @TableField("`usage`")
    private Long usage;

    @ApiModelProperty("失效量")
    private Long invalid;

    @ApiModelProperty("剩余量")
    private Long usable;

    @ApiModelProperty("单价")
    private BigDecimal unitPrice;
}
