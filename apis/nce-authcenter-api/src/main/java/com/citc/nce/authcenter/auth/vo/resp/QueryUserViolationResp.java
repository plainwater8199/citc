package com.citc.nce.authcenter.auth.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryUserViolationResp {
    @ApiModelProperty(value = "违规次数",dataType = "Integer")
    private Integer violationTotal;
}
