package com.citc.nce.authcenter.permission.apiImpl;

import com.citc.nce.authcenter.permission.ApiPermissionApi;
import com.citc.nce.authcenter.permission.enums.Permission;
import com.citc.nce.authcenter.permission.service.ApiPermissionConfigService;
import com.citc.nce.authcenter.permission.vo.GetUrlPermissionReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author jiancheng
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiPermissionImpl implements ApiPermissionApi {
    private final ApiPermissionConfigService apiPermissionConfigService;
    @PostMapping("/api/permission/getPermissions")
    @Override
    public List<Permission> getUrlPermission(GetUrlPermissionReq req) {
        return apiPermissionConfigService.getUrlPermission(req.getUri());
    }
}
