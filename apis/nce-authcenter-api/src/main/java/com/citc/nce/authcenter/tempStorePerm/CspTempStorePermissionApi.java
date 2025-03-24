package com.citc.nce.authcenter.tempStorePerm;

import com.citc.nce.authcenter.tempStorePerm.bean.ChangePrem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author bydud
 * @since 14:55
 */
@Api(tags = "用户中心--站内信模块")
@FeignClient(value = "authcenter-service", contextId = "cspTempStorePermissionApi", url = "${authCenter:}")
public interface CspTempStorePermissionApi {
    @PostMapping("/cspTempStorePermission/changePermission")
    @ApiOperation("修改模板商城权限")
    public void enableOrDisableUser(@Validated @RequestBody ChangePrem changePrem);
}
