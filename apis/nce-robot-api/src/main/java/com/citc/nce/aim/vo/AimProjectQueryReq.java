package com.citc.nce.aim.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>挂短-项目 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class AimProjectQueryReq {

    @ApiModelProperty(value = "id", dataType = "int")
    private long id;

    @ApiModelProperty(value = "项目Id", dataType = "String")
    private String projectId;

    @ApiModelProperty(value = "客户号码", dataType = "String")
    private String calling;

    @ApiModelProperty(value = "项目状态", dataType = "int")
    private int projectStatus;
}
