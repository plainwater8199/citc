package com.citc.nce.misc.operationlog.mapper;

import com.citc.nce.misc.operationlog.domain.SysOperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.misc.operationlog.domain.SysOperationLog;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 操作日志记录 Mapper 接口
 * </p>
 *
 * @author bydud
 * @since 2024-01-26 11:01:21
 */
public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {

    void insertByTabName(@Param("tableName") String tableName, @Param("log") SysOperationLog log);
}
