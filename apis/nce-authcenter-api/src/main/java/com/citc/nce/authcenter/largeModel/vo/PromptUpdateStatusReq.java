package com.citc.nce.authcenter.largeModel.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class PromptUpdateStatusReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * promptId
     */
    @ApiModelProperty(value = "id", dataType = "Long")
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * prompt状态
     */
    @ApiModelProperty(value = "prompt状态", dataType = "Boolean")
    @NotNull(message = "变更状态不能为空")
    private Integer status;
}
