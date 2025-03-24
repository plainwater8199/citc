package com.citc.nce.misc.record;

import com.citc.nce.misc.record.req.BusinessIdReq;
import com.citc.nce.misc.record.req.BusinessIdsReq;
import com.citc.nce.misc.record.req.ProcessingRecordReq;
import com.citc.nce.misc.record.resp.ProcessingRecordResp;
import com.citc.nce.misc.record.service.ProcessingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class ProcessingRecordController implements ProcessingRecordApi {
    @Autowired
    private ProcessingRecordService processingRecordService;

    @Override
    public void addRecord(ProcessingRecordReq req) {
        processingRecordService.addRecord(req);
    }

    @Override
    public List<ProcessingRecordResp> findProcessingRecordList(BusinessIdReq req) {
        return processingRecordService.findProcessingRecordList(req);
    }

    @Override
    public List<ProcessingRecordResp> findProcessingRecordListByIds(BusinessIdsReq req) {
        return processingRecordService.findProcessingRecordListByIds(req);
    }

    @Override
    public void addBatchRecord(List<ProcessingRecordReq> reqList) {
        processingRecordService.addBatchRecord(reqList);
    }
}
