package com.citc.nce.module.controller;

import com.citc.nce.module.ModuleApi;
import com.citc.nce.module.service.ModuleService;
import com.citc.nce.module.vo.req.ImportContactGroupReq;
import com.citc.nce.module.vo.resp.ImportContactGroupResp;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@Api(value = "ModuleController", tags = "CSP--组件应用")
public class ModuleController implements ModuleApi {

    @Resource
    private ModuleService moduleService;

    @Override
    @PostMapping("/module/saveModuleButUuidRelation")
    public Boolean saveModuleButUuidRelation(@RequestParam("moduleType")Integer moduleType, @RequestParam("moduleId")String moduleId, @RequestParam("butUuid")String butUuid) {
        return moduleService.saveModuleButUuidRelation(moduleType,moduleId,butUuid);
    }

    @Override
    @PostMapping("/module/getModuleIdByButUuid")
    public String getModuleIdByButUuid(@RequestParam("butUuid")String butUuid) {
        return moduleService.getModuleIdByButUuid(butUuid);
    }

    @Override
    @PostMapping("/module/updateModuleHandle")
    public void updateModuleHandle(int btnType, String butUuid, String sender, String chatbotId) {
        moduleService.updateModuleHandle(btnType,butUuid,sender,chatbotId);
    }

    @Override
    @PostMapping("/module/importContactGroup")
    public ImportContactGroupResp importContactGroup(ImportContactGroupReq req) {
        return moduleService.importContactGroup(req);
    }
}
