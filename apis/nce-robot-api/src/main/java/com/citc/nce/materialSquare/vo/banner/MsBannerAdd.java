package com.citc.nce.materialSquare.vo.banner;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author bydud
 * @since 2024/5/9
 */
@Data
public class MsBannerAdd {
    @ApiModelProperty("banner名称")
    @Length(min = 1, max = 25, message = "banner名称，必填，最大25字")
    private String name;

    @ApiModelProperty("关联活动页面id")
    @NotNull(message = "关联活动页面不能为空")
    private Long msActivityId;

    @ApiModelProperty("banner图文件id")
    @NotBlank(message = "封面图必传")
    private String imgFileId;

    @ApiModelProperty("banner图的格式")
    @NotBlank(message = "封面格式必传")
    private String imgFormat;

    @ApiModelProperty("banner图的名称")
    private String imgName;

    @ApiModelProperty("banner图的大小以字节(byte)为单位")
    private Long imgLength;
}
