package com.citc.nce.robot.api.materialSquare.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.robot.api.materialSquare.emums.MsLikeStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
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
@TableName("ms_summary_view_record")
@ApiModel(value = "MsSummaryViewRecord对象", description = "点赞记录表")
public class MsSummaryViewRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("素材id")
    @TableField(value = "mss_id")
    private Long mssId;

    @ApiModelProperty(value = "客户端用户user_id", required = true)
    private String userId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

}
