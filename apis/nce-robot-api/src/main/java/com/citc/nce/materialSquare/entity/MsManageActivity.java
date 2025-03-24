package com.citc.nce.materialSquare.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 素材广场_后台管理_活动封面
 * </p>
 *
 * @author bydud
 * @since 2024-05-14 02:05:31
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ms_manage_activity")
@ApiModel(value = "MsManageActivity对象", description = "素材广场_后台管理_活动封面	")
public class MsManageActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("活动id")
    @TableId(value = "ms_activity_id", type = IdType.ASSIGN_ID)
    private Long msActivityId;

    @ApiModelProperty("活动名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("封面文件id")
    @TableField("cover_file_id")
    private String coverFileId;

    @ApiModelProperty("封面格式")
    @TableField("cover_format")
    private String coverFormat;


    @ApiModelProperty("封面的名称")
    @TableField("cover_name")
    private String coverName;

    @ApiModelProperty("封面的大小以字节(byte)为单位")
    @TableField("cover_length")
    private String coverLength;

    @ApiModelProperty("h5 ID")
    @TableField("h5_id")
    private Long h5Id;


    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;
    @TableField(exist = false)
    private String creatorName;


    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty("正被使用（banner、suggest）")
    @TableField(exist = false)
    private Boolean occupant;

    @TableField(exist = false)
    private String autoThumbnail;


}
