package com.citc.nce.misc.record;

import com.citc.nce.misc.record.req.BusinessIdReq;
import com.citc.nce.misc.record.req.BusinessIdsReq;
import com.citc.nce.misc.record.req.ProcessingRecordReq;
import com.citc.nce.misc.record.resp.ProcessingRecordResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "misc-service", contextId = "RecordApi", url = "${miscServer:}")
public interface ProcessingRecordApi {
    @PostMapping("/record/addRecord")
    void addRecord(@RequestBody @Valid ProcessingRecordReq req);

    @PostMapping("/record/findProcessingRecordList")
    List<ProcessingRecordResp> findProcessingRecordList(@RequestBody @Valid BusinessIdReq req);

    @PostMapping("/record/findProcessingRecordListByIds")
    List<ProcessingRecordResp> findProcessingRecordListByIds(@RequestBody @Valid BusinessIdsReq req);
    @PostMapping("/record/addBatchRecord")
    void addBatchRecord(@RequestBody @Valid List<ProcessingRecordReq> reqList);
}
