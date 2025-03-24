package com.citc.nce.auth.onlineservice;

import com.citc.nce.auth.onlineservice.vo.req.MessageRecordReq;
import com.citc.nce.auth.onlineservice.vo.resp.MessageRecordResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @BelongsPackage: com.citc.nce.auth.onlineservice
 * @Author: litao
 * @CreateTime: 2023-01-06  10:13
 
 * @Version: 1.0
 */
@Api(tags = "会话消息记录")
@FeignClient(value = "auth-service", contextId = "McMessageRecordApi", url = "${auth:}")
public interface McSessionMessageRecordApi {
    @ApiOperation("根据用户id查询未读消息记录")
    @PostMapping("/messageRecord/findUnReadCountByUserId")
    MessageRecordResp findUnReadCountByUserId(MessageRecordReq req);
}
