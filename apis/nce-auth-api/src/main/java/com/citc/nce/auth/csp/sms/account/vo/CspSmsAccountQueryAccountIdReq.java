package com.citc.nce.auth.csp.sms.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CspSmsAccountQueryAccountIdReq implements Serializable {

    @ApiModelProperty("userIds")
    private List<String> userIds;
}
