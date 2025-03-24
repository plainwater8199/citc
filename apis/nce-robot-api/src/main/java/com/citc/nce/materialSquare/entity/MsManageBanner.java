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
 * 素材广场_管理端_banner管理
 * </p>
 *
 * @author bydud
 * @since 2024-05-09 10:05:26
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ms_manage_banner")
@ApiModel(value = "MsManageBanner对象", description = "素材广场_管理端_banner管理")
public class MsManageBanner implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("bannerId")
    @TableId(value = "ms_banner_id", type = IdType.ASSIGN_ID)
    private Long msBannerId;

    @ApiModelProperty("banner名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("banner管理文件id")
    @TableField("img_file_id")
    private String imgFileId;

    @ApiModelProperty("banner的格式")
    @TableField("img_format")
    private String imgFormat;

    @ApiModelProperty("banner图的名称")
    @TableField("img_name")
    private String imgName;

    @ApiModelProperty("banner图的大小以字节(byte)为单位")
    @TableField("img_length")
    private Long imgLength;

    @ApiModelProperty("关联活动id")
    @TableField("ms_activity_id")
    private Long msActivityId;
    @ApiModelProperty("活动名称")
    @TableField(exist = false)
    private String msActivityName;


    @ApiModelProperty("排序")
    @TableField("order_num")
    private Long orderNum;

    @ApiModelProperty("是否删除")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;



    @ApiModelProperty("h5 ID")
    @TableField(exist = false)
    private Long h5Id;
}
