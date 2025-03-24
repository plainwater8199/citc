package com.citc.nce.record;

import com.citc.nce.authcenter.processrecord.ProcessRecordApi;
import com.citc.nce.misc.record.ProcessingRecordApi;
import com.citc.nce.misc.record.req.BusinessIdReq;
import com.citc.nce.misc.record.req.ProcessingRecordReq;
import com.citc.nce.misc.record.resp.ProcessingRecordResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = "处理记录")
@Slf4j
public class ProcessingRecordController {
    @Resource
    private ProcessingRecordApi processingRecordApi;
    @Resource
    private ProcessRecordApi processRecordApi;

    @ApiOperation("添加处理记录")
    @PostMapping("/record/addRecord")
    public void addRecord(@RequestBody @Valid ProcessingRecordReq req){
        processingRecordApi.addRecord(req);
    }

    @ApiOperation("查询处理记录列表")
    @PostMapping("/record/findProcessingRecordList")
    public List<ProcessingRecordResp> findProcessingRecordList(@RequestBody @Valid BusinessIdReq req){
        return processRecordApi.findProcessingRecordList(req);
    }
}
