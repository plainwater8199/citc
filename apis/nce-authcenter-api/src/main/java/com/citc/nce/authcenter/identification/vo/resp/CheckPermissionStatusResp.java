package com.citc.nce.authcenter.identification.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CheckPermissionStatusResp {
    @ApiModelProperty(value = "是否为能力提供商", dataType = "Boolean")
    private Boolean isAbilitySupplier;

    @ApiModelProperty(value = "是否为解决方案商", dataType = "Boolean")
    private Boolean isSolutionProvider;
}
