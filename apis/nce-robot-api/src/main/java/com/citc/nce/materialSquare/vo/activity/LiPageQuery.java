package com.citc.nce.materialSquare.vo.activity;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 2024/5/15 9:32
 */
@Data
public class LiPageQuery extends PageParam {
    @ApiModelProperty("活动方案id")
    @NotNull
    private Long msActivityContentId;
}
