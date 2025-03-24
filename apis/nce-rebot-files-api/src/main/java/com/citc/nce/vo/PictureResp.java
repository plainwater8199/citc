package com.citc.nce.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: IPictureService
 */
@Data
public class PictureResp implements Serializable {
    private static final long serialVersionUID = 3058020845681842334L;

    @ApiModelProperty(value = "图片id",example = "2")
    private Long id;

    @ApiModelProperty(value = "图片名称",example = "a.jpg")
    private String pictureName;

    @ApiModelProperty(value = "分组id",example = "2")
    private Long groupId;

    @ApiModelProperty(value = "图片urlId",example = "3")
    private String pictureUrlId;

    @ApiModelProperty(value = "图片上传时间",example = "2022-07-05")
    private Date pictureUploadTime;

    @ApiModelProperty(value = "图片格式",example = "png")
    private String pictureFormat;

    @ApiModelProperty(value = "图片大小",example = "1M")
    private String pictureSize;

    @ApiModelProperty(value = "账户集合")
    private List<AccountResp> accounts;

    @ApiModelProperty(value = "是否被引用")
    private Integer used;

    @ApiModelProperty(value = "缩略图",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String thumbnailTid;
    @ApiModelProperty(value = "缩略图base64")
    private String autoThumbnail;
}
