package com.citc.nce.authcenter.userDataSyn.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class TenantUserUpdateInfo {
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

    @ApiModelProperty(value = "customerEnterpriseIdMap")
    private Map<Long,String> customerEnterpriseIdMap ;

    @ApiModelProperty(value = "chatbotAccountIdMap")
    private Map<String,String> chatbotAccountIdMap ;


    @ApiModelProperty(value = "idMap")
    private Map<String,String> idMap ;
    @ApiModelProperty(value = "cspIdSet")
    private Set<String> cspIdSet;
}
