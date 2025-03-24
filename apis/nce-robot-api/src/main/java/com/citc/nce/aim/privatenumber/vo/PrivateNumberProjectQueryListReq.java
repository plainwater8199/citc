package com.citc.nce.aim.privatenumber.vo;

import com.citc.nce.common.core.pojo.PageParam;
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
public class PrivateNumberProjectQueryListReq extends PageParam {

    @ApiModelProperty(value = "名称/号码", dataType = "String")
    private String queryString;

    @ApiModelProperty(value = "客户appKey", dataType = "String")
    private String appKey;

    @ApiModelProperty(value = "项目状态 -1:全部 0:禁用 1:启用", dataType = "int")
    @NotNull
    private int projectStatus;
}
