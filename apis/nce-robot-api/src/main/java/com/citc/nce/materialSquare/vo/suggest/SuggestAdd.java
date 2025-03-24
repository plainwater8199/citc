package com.citc.nce.materialSquare.vo.suggest;

import com.citc.nce.robot.api.materialSquare.emums.MsType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 2024/5/15 16:10
 */
@Data
public class SuggestAdd {
    @ApiModelProperty("作品库id")
    @NotNull(message = "作品不能为空")
    private Long mssId;

    @ApiModelProperty("作品库id")
    @NotNull(message = "作品类型不能为空")
    private MsType suggestType;
}
