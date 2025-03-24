package com.citc.nce.common.vo.tempStore;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * bydud
 * 2024/2/19
 **/
@Data
@Accessors(chain = true)
public class Csp4CustomerImg {

    @JsonSerialize(using = ToStringSerializer.class)
    //保存素材到客户侧后产生的id
    private Long newId;

    //@ApiModelProperty("图片分组id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imgId;

    //@ApiModelProperty("图片分组id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imgGroupId;

    //@ApiModelProperty("图片格式")
    private String pictureFormat;

    //@ApiModelProperty("图片名称")
    private String pictureName;

    //@ApiModelProperty("图片大小")
    private String pictureSize;

    //@ApiModelProperty("图片fileid")
    private String pictureUrlid;

    //@ApiModelProperty("图片缩略图id")
    private String thumbnailTid;

}
