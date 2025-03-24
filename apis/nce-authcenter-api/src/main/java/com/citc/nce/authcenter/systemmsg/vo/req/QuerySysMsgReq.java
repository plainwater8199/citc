package com.citc.nce.authcenter.systemmsg.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class QuerySysMsgReq {
    @ApiModelProperty(value = "管理员用户id", dataType = "String")
    private String adminUserId;
    @ApiModelProperty(value = "标题",dataType = "String")
    private String title;

    @NotNull
    @ApiModelProperty(value = "当前页",dataType = "Integer", example = "1")
    private Integer pageNo;

    @NotNull
    @ApiModelProperty(value = "每页展示条数",dataType = "Integer", example = "5")
    private Integer pageSize;
}
