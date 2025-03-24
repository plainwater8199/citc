package com.citc.nce.aim.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>挂短-项目 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class AimOrderQueryListReq extends PageParam {

    @ApiModelProperty(value = "项目Id", dataType = "String", required = true)
    @NotBlank(message = "项目Id不能为空")
    private String projectId;
}
