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
 * 扩展商城-资源管理-图片-分组管理
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 11:11:28
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ts_resources_img")
@ApiModel(value = "ResourcesImgGroup对象", description = "扩展商城-资源管理-图片-分组管理")
public class ResourcesImg implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("图片分组id")
    @TableId(value = "img_id",type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imgId;

    @ApiModelProperty("图片分组id")
    @TableField("img_group_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imgGroupId;

    @ApiModelProperty("图片格式")
    @TableField("picture_format")
    private String pictureFormat;

    @ApiModelProperty("图片名称")
    @TableField("picture_name")
    private String pictureName;

    @ApiModelProperty("图片大小")
    @TableField("picture_size")
    private String pictureSize;

    @ApiModelProperty("图片fileid")
    @TableField("picture_urlId")
    private String pictureUrlid;

    @ApiModelProperty("图片缩略图id")
    @TableField("thumbnail_tid")
    private String thumbnailTid;

    @ApiModelProperty("上传人")
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("上传时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("是否删除")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    @ApiModelProperty(value = "缩略图base64")
    private String autoThumbnail;


}
