package com.citc.nce.auth.onlineservice;

import com.citc.nce.auth.onlineservice.service.McSessionMessageRecordService;
import com.citc.nce.auth.onlineservice.vo.req.MessageRecordReq;
import com.citc.nce.auth.onlineservice.vo.resp.MessageRecordResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @BelongsPackage: com.citc.nce.auth.onlineservice
 * @Author: litao
 * @CreateTime: 2023-01-06  10:12
 
 * @Version: 1.0
 */
@RestController()
@Slf4j
public class McSessionSessionMessageRecordController implements McSessionMessageRecordApi {
    @Resource
    private McSessionMessageRecordService mcSessionMessageRecordService;

    @Override
    public MessageRecordResp findUnReadCountByUserId(@RequestBody @Valid MessageRecordReq req) {
        return mcSessionMessageRecordService.findUnReadCountByUserId(req);
    }
}
