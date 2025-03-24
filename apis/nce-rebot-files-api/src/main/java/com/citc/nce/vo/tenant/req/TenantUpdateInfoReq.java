package com.citc.nce.vo.tenant.req;

import com.citc.nce.authcenter.userDataSyn.vo.TenantUserUpdateInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class TenantUpdateInfoReq {

    @ApiModelProperty(value = "tenantUserUpdateInfos")
    private List<TenantUserUpdateInfo> tenantUserUpdateInfos;

    private Map<Long, String> chatbotId;

}
