package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryUserViolationReq {
    @NotNull(message = "用户ID不能为空！")
    @ApiModelProperty(value = "用户ID",dataType = "String",required = true)
    private String userId;

    @NotNull(message = "平台不能为空！")
    @ApiModelProperty(value = "平台：1-核能商城，2-硬核桃社区",dataType = "Integer",required = true)
    private Integer plate;
    @NotNull(message = "违规类型不能为空！")
    @ApiModelProperty(value = "违规类型;-1：全部",dataType = "Integer",required = true)
    private Integer violationType;
}
