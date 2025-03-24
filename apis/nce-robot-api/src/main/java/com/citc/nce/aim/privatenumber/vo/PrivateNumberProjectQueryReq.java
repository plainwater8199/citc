package com.citc.nce.aim.privatenumber.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>挂短-项目 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class PrivateNumberProjectQueryReq {

    @ApiModelProperty(value = "id", dataType = "int")
    private long id;

    @ApiModelProperty(value = "项目Id", dataType = "String")
    private String projectId;

    @ApiModelProperty(value = "客户appKey", dataType = "String")
    private String appKey;

    @ApiModelProperty(value = "项目状态", dataType = "int")
    private int projectStatus;
}
