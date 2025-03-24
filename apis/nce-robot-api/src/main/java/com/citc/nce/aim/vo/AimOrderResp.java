package com.citc.nce.aim.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>挂短-订单</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class AimOrderResp implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id", dataType = "int")
    private long id;

    @ApiModelProperty(value = "项目Id", dataType = "String")
    private String projectId;

    @ApiModelProperty(value = "订单名称", dataType = "String")
    private String orderName;

    @ApiModelProperty(value = "购买量", dataType = "long")
    private long orderAmount;

    @ApiModelProperty(value = "消耗量", dataType = "long")
    private long orderConsumption;

    @ApiModelProperty(value = "订单状态 0:已关闭 1:已启用 2:已完成 3:已停用", dataType = "int")
    private int orderStatus;

    @ApiModelProperty(value = "是否删除 0:未删除 1:已删除", dataType = "int")
    private int deleted;

    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;

    @ApiModelProperty(value = "更新时间", dataType = "Date")
    private Date updateTime;

    @ApiModelProperty(value = "创建者", dataType = "String")
    private String creator;

    @ApiModelProperty(value = "更新者", dataType = "String")
    private String updater;
}
