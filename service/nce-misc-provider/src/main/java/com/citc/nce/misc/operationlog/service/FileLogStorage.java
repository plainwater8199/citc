package com.citc.nce.misc.operationlog.service;

import com.citc.nce.misc.operationlog.domain.SysOperationLog;

/**
 *
 * @author bydud
 * @since 2024/4/18
 */
public interface FileLogStorage {

    void save(SysOperationLog log);
}
