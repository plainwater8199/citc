package com.citc.nce.aim.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * <p>挂短-项目 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class AimOrderQueryReq {

    @ApiModelProperty(value = "id", dataType = "int")
    private long id;
    @ApiModelProperty(value = "项目id", dataType = "int")
    private String projectId;
}
