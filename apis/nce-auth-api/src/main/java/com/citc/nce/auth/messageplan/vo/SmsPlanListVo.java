package com.citc.nce.auth.messageplan.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class SmsPlanListVo {

    @ApiModelProperty("主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("套餐ID, mp+17位时间戳+随机数")
    private String planId;

    @ApiModelProperty("套餐名称，25个字符，不能重复")
    private String name;

    @ApiModelProperty("通道： 0:默认通道")
    private Integer channel;

    @ApiModelProperty("短信数量")
    private Long number;

    @ApiModelProperty("单价")
    private String price;

    @ApiModelProperty("总价格")
    private String amount;

    @ApiModelProperty("状态 0已下架 1已上架")
    private Integer status;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("订购次数")
    private Long orderTimes;

}
