package com.citc.nce.misc.operationlog.service.impl;

import com.alibaba.fastjson.JSON;
import com.citc.nce.common.util.EdaFileUtil;
import com.citc.nce.misc.operationlog.config.LogFileConfig;
import com.citc.nce.misc.operationlog.domain.SysOperationLog;
import com.citc.nce.misc.operationlog.service.FileLogStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 *
 * @author bydud
 * @since 2024/4/18
 */
@Component
@AllArgsConstructor
@Slf4j
public class FileLogStorageImpl implements FileLogStorage {

    private LogFileConfig logFileConfig;

    @Override
    public void save(SysOperationLog operationLog) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int value = now.getMonthValue();

        String fileName = logFileConfig.getFilePrefix() + year + "_" + value + ".txt";
        String content = JSON.toJSONString(operationLog) + "\r\n";
        if (!EdaFileUtil.writeStringToFile(logFileConfig.getPath(), fileName, content, true)) {
            log.error("日志文件记录失败 content: {}", content);
        }
    }
}
