package com.citc.nce.authcenter.systemmsg.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryMsgDetailsReq {
    @NotNull
    @ApiModelProperty(value = "msgId")
    private String msgId;

}
