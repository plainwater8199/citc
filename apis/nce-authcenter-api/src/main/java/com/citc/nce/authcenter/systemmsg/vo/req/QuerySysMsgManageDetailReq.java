package com.citc.nce.authcenter.systemmsg.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QuerySysMsgManageDetailReq {
    @NotNull
    @ApiModelProperty(value = "站内信id", example = "1",required = true)
    private Long id;
}
