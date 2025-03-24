package com.citc.nce.misc.legal.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @BelongsPackage: com.citc.nce.misc.legal.req
 * @Author: litao
 * @CreateTime: 2023-02-09  16:41
 
 * @Version: 1.0
 */
@Data
public class PageReq {
    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    @NotNull(message = "pageNo不能为空")
    @ApiModelProperty(value = "页数", dataType = "Integer", required = true)
    private Integer pageNo;

    @NotNull(message = "pageSize不能为空")
    @ApiModelProperty(value = "每页条数", dataType = "Integer", required = true)
    private Integer pageSize;
}
