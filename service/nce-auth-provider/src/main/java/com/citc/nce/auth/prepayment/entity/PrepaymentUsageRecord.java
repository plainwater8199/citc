package com.citc.nce.auth.prepayment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 预付费订单使用记录
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-25 10:01:03
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("prepayment_usage_record")
@ApiModel(value = "PrepaymentUsageRecord对象", description = "预付费订单使用记录")
public class PrepaymentUsageRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("订单ID, 17位时间戳+3位随机数")
    private String orderId;

    @ApiModelProperty("消息类型 {1:5g消息,2:视频短信,3:短信}")
    private MsgTypeEnum msgType;

    @ApiModelProperty("消息子类型 {0:文本消息,1:富媒体消息,2:会话消息}")
    private MsgSubTypeEnum msgSubType;

    private Integer number;

    @ApiModelProperty("创建者")
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


}
