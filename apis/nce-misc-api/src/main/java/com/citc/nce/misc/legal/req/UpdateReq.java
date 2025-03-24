package com.citc.nce.misc.legal.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @BelongsPackage: com.citc.nce.misc.legal.req
 * @Author: litao
 * @CreateTime: 2023-02-10  09:40
 
 * @Version: 1.0
 */
@Data
public class UpdateReq {
    @NotNull(message = "id不能为空")
    @ApiModelProperty(value = "id", dataType = "Long", required = true)
    private Long id;

    @NotEmpty(message = "文件内容不能为空")
    @ApiModelProperty(value = "文件内容", dataType = "String", required = true)
    private String fileContent;

    @NotNull(message = "是否需要用户确认不能为空")
    @ApiModelProperty(value = "是否需要用户确认", dataType = "Integer", required = true)
    private Integer isConfirm;
}
