package com.citc.nce.auth.messageplan.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.citc.nce.auth.messageplan.enums.MessagePlanStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 短信套餐表
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-23 03:01:42
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sms_plan")
@ApiModel(value = "SmsPlan对象", description = "短信套餐表")
public class SmsPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
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

    @ApiModelProperty("创建者")
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新者")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("删除时间")
    @TableLogic(value = "'1000-01-01 00:00:00'", delval = "now()")
    private LocalDateTime deletedTime;


}
