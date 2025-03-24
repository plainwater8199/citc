package com.citc.nce.robot.api.tempStore.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 商品操作日志
 * </p>
 *
 * @author bydud
 * @since 2023-11-27 10:11:42
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ts_goods_manage_log")
@ApiModel(value = "GoodsManageLog对象", description = "商品操作日志")
public class GoodsManageLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "log_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long logId;

    @ApiModelProperty("商品产品id")
    @TableField("goods_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long goodsId;

    @ApiModelProperty("商品uuid")
    @TableField("goods_uuid")
    private String goodsUuid;

    @ApiModelProperty("操作类型")
    @TableField("operate_type")
    private String operateType;

    @ApiModelProperty("操作人")
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("操作人名称")
    @TableField(exist = false)
    private String creatorName;

    @ApiModelProperty("操作时间")
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("备注信息")
    @TableField("remark")
    private String remark;


}
