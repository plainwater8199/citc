package com.citc.nce.authcenter.largeModel.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PromptDetailReq {
    /**
     * promptId
     */
    @ApiModelProperty(value = "promptId", required = true)
    @NotNull(message = "id不能为空")
    private Long id;
}
