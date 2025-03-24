package com.citc.nce.authcenter.tempStorePerm.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 14:56
 */
@Data
public class ChangePrem {
    @NotEmpty(message = "userId 不能为空")
    private String userId;

    @ApiModelProperty("是否存在权限（0无，1有）")
    @NotNull(message = "permission不能为空切只能是0/1")
    @Max(value = 1, message = "permission最大为1")
    @Min(value = 0, message = "permission最小为0")
    private Integer permission;
    private String remark;
}
