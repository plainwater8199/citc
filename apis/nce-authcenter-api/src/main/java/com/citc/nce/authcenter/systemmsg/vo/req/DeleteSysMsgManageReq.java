package com.citc.nce.authcenter.systemmsg.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteSysMsgManageReq {
    @NotNull
    @ApiModelProperty(value = "查询或删除id", example = "1")
    private Long id;
}
