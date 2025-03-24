package com.citc.nce.largeModel;

import com.citc.nce.authcenter.largeModel.LargeModelApi;
import com.citc.nce.authcenter.largeModel.vo.PromptStatusResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/model")
@Slf4j
@Api(value = "misc", tags = "大模型管理管理")
public class LargeModelController {
    @Resource
    LargeModelApi largeModelApi;

    @ApiOperation(value = "查询prompt状态", notes = "查询prompt状态")
    @GetMapping("/prompt/status")
    public PromptStatusResp getPromptStatus() {
        return largeModelApi.getPromptStatus();
    }
}
