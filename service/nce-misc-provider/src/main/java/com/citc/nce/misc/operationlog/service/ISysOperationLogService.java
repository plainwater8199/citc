package com.citc.nce.misc.operationlog.service;

import com.citc.nce.misc.operationlog.domain.SysOperationLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.misc.operationlog.domain.SysOperationLog;

/**
 * <p>
 * 操作日志记录 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-01-26 11:01:21
 */
public interface ISysOperationLogService extends IService<SysOperationLog> {

    /**
     * 按月分表存在mysql数据库
     * @param log 日志
     */
    void saveMonth(SysOperationLog log);
}
