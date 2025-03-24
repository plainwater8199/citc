package com.citc.nce.auth.user.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @BelongsPackage: com.citc.nce.apis.api.vo.resp
 * @Author: litao
 * @CreateTime: 2022-11-21  14:52
 
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
public class PermissionStatusResp {
    @ApiModelProperty(value = "是否为能力提供商", dataType = "Boolean")
    private Boolean isAbilitySupplier;

    @ApiModelProperty(value = "是否为解决方案商", dataType = "Boolean")
    private Boolean isSolutionProvider;
}
