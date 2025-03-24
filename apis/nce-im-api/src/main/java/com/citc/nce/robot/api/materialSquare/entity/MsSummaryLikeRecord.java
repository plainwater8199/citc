package com.citc.nce.robot.api.materialSquare.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.robot.api.materialSquare.emums.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 点赞记录表
 * </p>
 *
 * @author bydud
 * @since 2024-05-31 09:05:58
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ms_summary_like_record")
@ApiModel(value = "MsSummaryLikeRecord对象", description = "点赞记录表")
public class MsSummaryLikeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("素材id")
    @TableField(value = "mss_id")
    private Long mssId;

    @ApiModelProperty(value = "客户端用户user_id", required = true)
    private String userId;

    @ApiModelProperty("是否删除")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("更新者")
    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
