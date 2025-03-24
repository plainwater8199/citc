package com.citc.nce.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: PictureDto
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PictureReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = 4315766730499059899L;

    @ApiModelProperty(value = "图片名称",example = "a.jpg")
    @NotNull
    private String pictureName;

    @NotNull
    @ApiModelProperty(value = "图片存储的id",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String pictureUrlId;

    @NotNull
    @ApiModelProperty(value = "图片格式",example = "png")
    private String pictureFormat;

    @NotNull
    @ApiModelProperty(value = "图片大小",example = "1M")
    private String pictureSize;

    @ApiModelProperty(value = "分组id",example = "1")
    private Long groupId;

    @ApiModelProperty(value = "缩略图",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String thumbnailTid;
}
