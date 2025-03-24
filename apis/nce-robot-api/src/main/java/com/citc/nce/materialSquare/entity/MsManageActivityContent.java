package com.citc.nce.materialSquare.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.citc.nce.materialSquare.PromotionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 素材广场_后台管理_活动方案
 * </p>
 *
 * @author bydud
 * @since 2024-05-14 02:05:31
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ms_manage_activity_content")
@ApiModel(value = "MsManageActivityContent对象", description = "素材广场_后台管理_活动方案")
public class MsManageActivityContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("活动方案id")
    @TableId(value = "ms_activity_content_id", type = IdType.ASSIGN_ID)
    private Long msActivityContentId;

    @ApiModelProperty("活动名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("开始时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty("活动类型")
    @TableField("promotion_type")
    private PromotionType promotionType;

    @ApiModelProperty("折扣")
    @TableField("discount_rate")
    private Double discountRate;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @TableField("deleted")
    @TableLogic
    private Integer deleted;


}
