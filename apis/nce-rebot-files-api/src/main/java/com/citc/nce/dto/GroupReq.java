package com.citc.nce.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: GroupDto
 */
@Data
public class GroupReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "分组id",example = "1")
    private Long id;

    @NotNull
    @ApiModelProperty(value = "分组名称",example = "动物类")
    private String groupName;

    @NotNull
    @ApiModelProperty(value = "是否删除",example = "1")
    private Integer deleted;
}
