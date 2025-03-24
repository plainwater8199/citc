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
public class PrivateNumberOrderQueryReq {

    @ApiModelProperty(value = "id", dataType = "long")
    private long id;
    @ApiModelProperty(value = "项目id", dataType = "String")
    private String projectId;
}
