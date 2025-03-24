package com.citc.nce.modulemanagement.controller;

import com.citc.nce.modulemanagement.ModuleManagementApi;
import com.citc.nce.modulemanagement.service.IModuleUsedService;
import com.citc.nce.modulemanagement.service.ModuleManagementService;
import com.citc.nce.modulemanagement.vo.ModuleManagementItem;
import com.citc.nce.modulemanagement.vo.ModuleManagementResp;
import com.citc.nce.modulemanagement.vo.req.QueryForMSReq;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ModuleManagementController implements ModuleManagementApi {


    private final ModuleManagementService moduleManagementService;
    private final IModuleUsedService moduleUsedService;


    @Override
    @GetMapping("/moduleManagement/query")
    public ModuleManagementResp moduleQuery() {
        return moduleManagementService.moduleQuery();
    }

    @Override
    @PostMapping("/moduleManagement/queryForMS")
    public ModuleManagementResp queryForMS(@RequestBody QueryForMSReq req) {
        return moduleManagementService.queryForMS(req);
    }

    @Override
    @GetMapping("/moduleManagement/queryById/{id}")
    public ModuleManagementItem queryById(@PathVariable("id") Long id) {
        return moduleManagementService.queryById(id);
    }

    @Override
    @GetMapping("/moduleManagement/queryUsedPermissionsById/{moduleId}")
    public Boolean queryUsedPermissionsById(@PathVariable("moduleId") Long moduleId) {
        return moduleUsedService.queryUsedPermissionsById(moduleId);
    }

    @Override
    @GetMapping("/moduleManagement/queryUsedPermissionsByName/{moduleName}")
    @ApiOperation("组件查询--根据组件名称-获取登录用户是否已经使用过该组件")
    public Boolean queryUsedPermissionsByName(@PathVariable("moduleName")String moduleName) {
        return moduleUsedService.queryUsedPermissionsByName(moduleName);
    }

    @Override
    @PostMapping("/moduleManagement/useModuleManagement")
    public void useModuleManagement(@RequestBody ModuleManagementItem item) {
        moduleUsedService.useModuleManagement(item);
    }

    @Override
    @PostMapping("/moduleManagement/updateMssID/{id}/{mssId}")
    @ApiOperation("组件查询--客户使用组件")
    public void updateMssID(@PathVariable("id")Long id, @PathVariable("mssId")Long mssId) {
        moduleManagementService.updateMssID(id, mssId);
    }

    @Override
    @PostMapping("/moduleManagement/deleteMssIDForIds")
    @ApiOperation("组件更新--清空组件的mssId")
    public void deleteMssIDForIds(@RequestBody List<String> ids) {
        moduleManagementService.deleteMssIDForIds(ids);
    }
}
