package com.citc.nce.filecenter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class UpdateDateReq {

    @ApiModelProperty(value = "cspUserId")
    private String cspUserId;

    @ApiModelProperty(value = "customerUserId")
    private String customerUserId;

    @ApiModelProperty(value = "cspId")
    private String cspId;

    @ApiModelProperty(value = "customerId")
    private String customerId;

    @ApiModelProperty(value = "cspAccountManageMap")
    private Map<Long,String> cspAccountManageMap ;

    @ApiModelProperty(value = "accountManagementIdMap")
    private Map<Long,String> accountManagementIdMap ;
}
