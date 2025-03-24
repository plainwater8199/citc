package com.citc.nce.authcenter.csp.vo;

import lombok.Data;

@Data
public class CspAgentLoginReq {
    private String cspId;
    private String customerId;
    private Integer platformType;
    private String deviceType;
}
