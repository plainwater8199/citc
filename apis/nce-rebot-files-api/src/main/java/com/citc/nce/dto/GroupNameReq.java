package com.citc.nce.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: GroupDto
 */
@Data
public class GroupNameReq {

    @NotNull
    @ApiModelProperty(value = "分组名称",example = "动物类")
    private String groupName;
}
