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
public class MsBannerChangeOrder {
    @ApiModelProperty("bannerId")
    @NotNull
    private Long msBannerId;
    @ApiModelProperty("排序")
    private Long orderNum;
}
