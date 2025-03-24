package com.citc.nce.materialSquare.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 素材广场_后台管理_参与活动的素材
 * </p>
 *
 * @author bydud
 * @since 2024-05-14 02:05:36
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ms_manage_activity_li")
@ApiModel(value = "MsManageActivityLi对象", description = "素材广场_后台管理_参与活动的素材")
public class MsManageActivityLi implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("关联表id")
    @TableId(value = "ms_activity_li_id", type = IdType.ASSIGN_ID)
    private Long msActivityLiId;

    @ApiModelProperty("活动方案id")
    @TableField("ms_activity_content_id")
    private Long msActivityContentId;

    @ApiModelProperty("素材ms_summary表id")
    @TableField("mss_id")
    private Long mssId;


    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty(value = "是否删除",example = "1")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}
