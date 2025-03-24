package com.citc.nce.materialSquare.vo.banner;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 *
 * @author bydud
 * @since 2024/5/9
 */
@Data
public class MsBannerOrder {
    @ApiModelProperty("挪完后前面得id，第一个传-1")
    @NotNull(message = "prevId不能为空")
    private Long prevId;
    @ApiModelProperty("挪动对象的bannerId")
    @NotNull(message = "挪动对象的msBannerId 能为空")
    private Long msBannerId;
}
