package com.citc.nce.auth.onlineservice.service;

import com.citc.nce.auth.onlineservice.vo.req.MessageRecordReq;
import com.citc.nce.auth.onlineservice.vo.resp.MessageRecordResp;

/**
 * @BelongsPackage: com.citc.nce.auth.onlineservice.service
 * @Author: litao
 * @CreateTime: 2023-01-06  10:06
 
 * @Version: 1.0
 */
public interface McSessionMessageRecordService {
    MessageRecordResp findUnReadCountByUserId(MessageRecordReq req);
}
