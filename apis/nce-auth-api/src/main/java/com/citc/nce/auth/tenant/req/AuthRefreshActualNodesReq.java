package com.citc.nce.auth.tenant.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
public class AuthRefreshActualNodesReq {
    @ApiModelProperty(value = "cspIdSet")
    private Set<String> cspIdSet;
}
