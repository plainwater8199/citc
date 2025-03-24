package com.citc.nce.authcenter.systemmsg.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QuerySysMsgManageListReq {
    @ApiModelProperty(value = "标题：根据标题模糊查询", dataType = "String")
    private String title;

    @NotNull(message = "pageNo不能为空")
    @ApiModelProperty(value = "pageNo", dataType = "Integer", required = true)
    private Integer pageNo;

    @NotNull(message = "pageSize不能为空")
    @ApiModelProperty(value = "pageSize", dataType = "Integer", required = true)
    private Integer pageSize;

}
