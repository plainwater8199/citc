package com.citc.nce.misc.record.service;

import com.citc.nce.misc.record.req.BusinessIdReq;
import com.citc.nce.misc.record.req.BusinessIdsReq;
import com.citc.nce.misc.record.req.ProcessingRecordReq;
import com.citc.nce.misc.record.resp.ProcessingRecordResp;

import java.util.List;

public interface ProcessingRecordService {
    void addRecord(ProcessingRecordReq req);

    List<ProcessingRecordResp> findProcessingRecordList(BusinessIdReq req);

    List<ProcessingRecordResp> findProcessingRecordListByIds(BusinessIdsReq req);

    void addBatchRecord(List<ProcessingRecordReq> reqList);
}
