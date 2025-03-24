package com.citc.nce.authcenter.permission;

import com.citc.nce.authcenter.permission.enums.Permission;
import com.citc.nce.authcenter.permission.vo.GetUrlPermissionReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author jiancheng
 */
@FeignClient(value = "authcenter-service", contextId = "apiPermission", url = "${authCenter:}")
public interface ApiPermissionApi {

    @PostMapping("/api/permission/getPermissions")
    List<Permission> getUrlPermission(@RequestBody GetUrlPermissionReq req);
}
