package com.citc.nce.misc.operationlog.controller;


import com.citc.nce.misc.operationlog.domain.OperationLogApi;
import com.citc.nce.misc.operationlog.domain.SysOperationLog;
import com.citc.nce.misc.operationlog.service.FileLogStorage;
import com.citc.nce.misc.operationlog.service.ISysOperationLogService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 * 操作日志记录 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2024-01-26 11:01:21
 */
@RestController
public class SysOperationLogController implements OperationLogApi {

    @Resource
    private ISysOperationLogService logService;

    @Resource
    private FileLogStorage fileLogStorage;

    public void saveLog(@RequestBody SysOperationLog log) {
        if (Objects.isNull(log.getStorageType())|| log.getStorageType()==0){
            fileLogStorage.save(log);
        }else {
            logService.saveMonth(log);
        }
    }

}

