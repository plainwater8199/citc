package com.citc.nce.robot.api.tempStore.bean.images;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 10:22
 */
@Data
@ApiModel(value = "csp-模板商城-资源管理-img管理-新增")
public class ImgAdd {

    @ApiModelProperty("组id")
    @NotNull(message = "组id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imgGroupId;

    @ApiModelProperty("图片名称")
    @NotBlank(message = "图片名称不能为空")
    private String pictureName;

    @ApiModelProperty("图片格式")
    @NotBlank(message = "图片格式不能为空")
    private String pictureFormat;

    @ApiModelProperty("图片大小")
    @NotBlank(message = "图片大小不能为空")
    private String pictureSize;

    @NotBlank(message = "图片封面不能为空")
    private String thumbnailTid;

    @NotBlank(message = "图片不能为空")
    private String pictureUrlid;




}
