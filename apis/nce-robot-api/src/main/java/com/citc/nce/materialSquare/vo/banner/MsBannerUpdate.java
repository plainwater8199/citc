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
public class MsBannerUpdate extends MsBannerAdd {
    @ApiModelProperty("bannerId")
    @NotNull(message = "bannerId 不能为空")
    private Long msBannerId;
}
