package com.citc.nce.module;

import com.citc.nce.module.vo.req.ImportContactGroupReq;
import com.citc.nce.module.vo.resp.ImportContactGroupResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@Api(value = "ModuleController", tags = "组件")
public class ModuleController {

    @Resource
    private ModuleApi moduleApi;


    @PostMapping("/module/importContactGroup")
    @ApiOperation(value = "组件导入联系人", notes = "组件导入联系人")
    public ImportContactGroupResp importContactGroup(@RequestBody  ImportContactGroupReq req) {
        return moduleApi.importContactGroup(req);
    }

}
