package com.citc.nce.auth.messageplan.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.auth.messageplan.enums.MessagePlanStatus;
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
public class SmsPlanVo {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("套餐ID, mp+17位时间戳+随机数")
    private String planId;

    @ApiModelProperty("套餐名称，25个字符，不能重复")
    private String name;

    @ApiModelProperty("通道： 0:默认通道")
    private Integer channel;

    @ApiModelProperty("短信数量")
    private Long number;

    @ApiModelProperty("短信单价")
    private BigDecimal price;

    @ApiModelProperty("总价格")
    private String amount;

    @ApiModelProperty("状态 0已下架 1已上架")
    private MessagePlanStatus status;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("订购次数")
    private Long orderTimes;
}
