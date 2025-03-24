package com.citc.nce.misc.operationlog.service.impl;

import com.citc.nce.misc.operationlog.domain.SysOperationLog;
import com.citc.nce.misc.operationlog.mapper.SysOperationLogMapper;
import com.citc.nce.misc.operationlog.service.ISysOperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.misc.operationlog.domain.SysOperationLog;
import com.citc.nce.misc.operationlog.mapper.SysOperationLogMapper;
import com.citc.nce.misc.operationlog.service.ISysOperationLogService;
import com.citc.nce.misc.tableInfo.entity.CreateTableInfo;
import com.citc.nce.misc.tableInfo.service.TableInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 操作日志记录 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-01-26 11:01:21
 */
@Service
@Slf4j
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements ISysOperationLogService {

    private final static ConcurrentHashMap<String, Boolean> TABLE_MAP = new ConcurrentHashMap<>();

    private final static String tableFix = "sys_operation_log";

    @Autowired
    private TableInfoService tableInfoService;


    @Override
    public void saveMonth(SysOperationLog log) {
        String tableName = getTableName(new Date());
        log.setOperId(IdWorker.getId());
        getBaseMapper().insertByTabName(tableName, log);
    }


    /**
     * 根据时间获取日志表名称
     *
     * @param date
     * @return
     */
    private String getTableName(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String tableName = tableFix + "_" + sdf.format(date);

        if (TABLE_MAP.getOrDefault(tableName, false)) {
            return tableName;
        }

        if (tableInfoService.existTable(tableName)) {
            TABLE_MAP.put(tableName, true);
            return tableName;
        }

        CreateTableInfo tempTable = tableInfoService.getCreateTableDDl(tableFix);
        String ddl = tempTable.getSql().replace("CREATE TABLE `" + tempTable.getTableName(), "CREATE TABLE `" + tableName) + ";";
        tableInfoService.executeDDlSql(ddl);
        log.info("创建表 {}", tableName);
        return tableName;
    }
}
