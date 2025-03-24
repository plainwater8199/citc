package com.citc.nce.auth.messageplan.vo;

import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.messageplan.enums.MessagePlanStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class FifthMessagePlanVo {

    @ApiModelProperty("主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("套餐ID, mp+17位时间戳+随机数")
    private String planId;

    @ApiModelProperty("套餐名称，25个字符，不能重复")
    private String name;

    @ApiModelProperty("运营商编码： 0:硬核桃，1：联通，2：移动，3：电信")
    private CSPOperatorCodeEnum operator;

    @ApiModelProperty("文本消息数量")
    private Long textMessageNumber;

    @ApiModelProperty("文本消息单价")
    private BigDecimal textMessagePrice;

    @ApiModelProperty("富媒体消息数量")
    private Long richMessageNumber;

    @ApiModelProperty("富媒体消息单价")
    private BigDecimal richMessagePrice;

    @ApiModelProperty("会话数")
    private Long conversionNumber;

    @ApiModelProperty("会话单价")
    private BigDecimal conversionPrice;

    @ApiModelProperty("总价格")
    private String amount;

    @ApiModelProperty("状态 0已下架 1已上架")
    private MessagePlanStatus status;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("订购次数")
    private Long orderTimes;
}
