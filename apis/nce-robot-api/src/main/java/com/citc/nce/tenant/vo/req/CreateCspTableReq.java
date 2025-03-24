package com.citc.nce.tenant.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCspTableReq {
    @ApiModelProperty(value = "cspId")
    private String cspId;
}
