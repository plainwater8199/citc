package com.citc.nce.misc.operationlog.domain;

import com.citc.nce.misc.operationlog.domain.SysOperationLog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * bydud
 * 2024/1/26
 **/

@FeignClient(value = "misc-service", contextId = "operationLogApi", url = "${miscServer:}")
public interface OperationLogApi {

    @PostMapping("/misc/sysOperationLog/save")
    public void saveLog(@RequestBody SysOperationLog log);
}
