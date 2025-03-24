package com.citc.nce.modulemanagement;


import com.citc.nce.modulemanagement.vo.ModuleManagementItem;
import com.citc.nce.modulemanagement.vo.ModuleManagementResp;
import com.citc.nce.modulemanagement.vo.req.QueryForMSReq;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "rebot-service", contextId = "ModuleManagementApi", url = "${robot:}")
public interface ModuleManagementApi {

    @GetMapping("/moduleManagement/query")
    ModuleManagementResp moduleQuery();

    @PostMapping("/moduleManagement/queryForMS")
    ModuleManagementResp queryForMS(@RequestBody QueryForMSReq req);


    @GetMapping("/moduleManagement/queryById/{id}")
    @ApiOperation("组件查询--根据id")
    ModuleManagementItem queryById(@PathVariable("id") Long id);

    @GetMapping("/moduleManagement/queryUsedPermissionsById/{moduleId}")
    @ApiOperation("组件查询--根据id-获取登录用户是否有权限")
    Boolean queryUsedPermissionsById(@PathVariable("moduleId") Long moduleId);

    @GetMapping("/moduleManagement/queryUsedPermissionsByName/{moduleName}")
    @ApiOperation("组件查询--根据组件名称-获取登录用户是否已经使用过该组件")
    Boolean queryUsedPermissionsByName(@PathVariable("moduleName") String moduleName);

    @PostMapping("/moduleManagement/useModuleManagement")
    @ApiOperation("组件查询--客户使用组件")
    void useModuleManagement(@RequestBody ModuleManagementItem item);
    @PostMapping("/moduleManagement/updateMssID/{id}/{mssId}")
    @ApiOperation("组件查询--客户使用组件")
    void updateMssID(@PathVariable("id")Long id, @PathVariable("mssId")Long mssId);

    @PostMapping("/moduleManagement/deleteMssIDForIds")
    @ApiOperation("组件更新--清空组件的mssId")
    void deleteMssIDForIds(@RequestBody List<String> strings);
}
