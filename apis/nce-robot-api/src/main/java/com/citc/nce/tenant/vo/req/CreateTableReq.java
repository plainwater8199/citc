package com.citc.nce.tenant.vo.req;

import com.citc.nce.authcenter.userDataSyn.vo.TenantUserUpdateInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;


@Data
@Accessors(chain = true)
public class CreateTableReq {
    @ApiModelProperty(value = "tenantUserUpdateInfos")
    private List<TenantUserUpdateInfo> tenantUserUpdateInfos;


}
