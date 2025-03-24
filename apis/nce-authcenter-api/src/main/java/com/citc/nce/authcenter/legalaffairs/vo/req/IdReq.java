package com.citc.nce.authcenter.legalaffairs.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @BelongsPackage: com.citc.nce.misc.legal.req
 * @Author: litao
 * @CreateTime: 2023-02-10  09:39
 
 * @Version: 1.0
 */
@Data
public class IdReq {
    @NotNull(message = "id不能为空")
    @ApiModelProperty(value = "id", dataType = "Long", required = true)
    private Long id;
}
