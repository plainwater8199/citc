package com.citc.nce.module;

import com.citc.nce.module.vo.req.ImportContactGroupReq;
import com.citc.nce.module.vo.resp.ImportContactGroupResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "rebot-service", contextId = "ModuleApi", url = "${robot:}")
public interface ModuleApi {

    //保存关联关系
    @PostMapping("/module/saveModuleButUuidRelation")
    Boolean saveModuleButUuidRelation(@RequestParam("moduleType") Integer moduleType, @RequestParam("moduleId")String moduleId, @RequestParam("butUuid")String butUuid);

    //获取组件ID
    @PostMapping("/module/getModuleIdByButUuid")
    String getModuleIdByButUuid(@RequestParam("butUuid")String butUuid);


    //更改组件的用户关联关系
    @PostMapping("/module/updateModuleHandle")
    void updateModuleHandle(@RequestParam("btnType")int btnType, @RequestParam("butUuid")String butUuid, @RequestParam("sender")String sender,@RequestParam("chatbotId")String chatbotId);

    //导入联系人组
    @PostMapping("/module/importContactGroup")
    ImportContactGroupResp importContactGroup(@RequestBody ImportContactGroupReq req);


}
