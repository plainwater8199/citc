package com.citc.nce.tenant.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
public class RefreshActualNodesReq {
    @ApiModelProperty(value = "cspIdSet")
    private Set<String> cspIdSet;
}
