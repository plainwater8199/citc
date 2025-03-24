package com.citc.nce.aim.privatenumber.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>挂短-项目 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class PrivateNumberProjectUpdateStatusReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id", dataType = "Int")
    private long id;

    @ApiModelProperty(value = "项目Id", dataType = "String", required = true)
    @NotBlank(message = "项目Id不能为空")
    private String projectId;

    @ApiModelProperty(value = "项目状态 0:禁用 1:启用", dataType = "int")
    @NotNull(message = "项目状态不能为空")
    private int projectStatus;
}
