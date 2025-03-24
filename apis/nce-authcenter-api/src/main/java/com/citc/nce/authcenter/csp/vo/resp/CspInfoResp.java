package com.citc.nce.authcenter.csp.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CspInfoResp {
    @ApiModelProperty("cspId")
    private String cspId;

    @ApiModelProperty("是否已分表哦")
    private boolean isSplite;
}
