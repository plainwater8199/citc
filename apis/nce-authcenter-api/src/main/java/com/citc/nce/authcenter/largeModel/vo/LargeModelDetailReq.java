package com.citc.nce.authcenter.largeModel.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class LargeModelDetailReq implements Serializable {
    /**
     * 大模型Id
     */
    @ApiModelProperty(value = "大模型id", required = true)
    @NotNull(message = "id不能为空")
    private Long id;
}
