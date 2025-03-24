package com.citc.nce.module;

import com.citc.nce.annotation.BossAuth;
import com.citc.nce.modulemanagement.ModuleManagementApi;
import com.citc.nce.modulemanagement.vo.ModuleManagementItem;
import com.citc.nce.modulemanagement.vo.ModuleManagementResp;
import com.citc.nce.modulemanagement.vo.req.QueryForMSReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@BossAuth({"/chatbot-view/component", "/chatbot-view/shop/storeroom-add","/chatbot-view/shop/storeroom-edit"})
@RestController
@AllArgsConstructor
@Slf4j
@Api(tags = "组件管理")
public class ModuleManagementController {
    private final ModuleManagementApi moduleManagementApi;

    @GetMapping("/moduleManagement/query")
    @ApiOperation("组件查询")
    public ModuleManagementResp moduleQuery() {
        return moduleManagementApi.moduleQuery();
    }

    @PostMapping("/moduleManagement/queryForMS")
    @ApiOperation("模版制作-组件查询")
    public ModuleManagementResp queryForMS(@RequestBody QueryForMSReq req){
        return moduleManagementApi.queryForMS(req);
    }

    @GetMapping("/moduleManagement/queryById/{id}")
    @ApiOperation("组件查询--根据id")
    public ModuleManagementItem queryById(@PathVariable("id") Long id) {
        return moduleManagementApi.queryById(id);
    }


}
