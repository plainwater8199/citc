package com.citc.nce.aim.privatenumber.service.impl;

import com.citc.nce.aim.privatenumber.dao.PrivateNumberSentDataDao;
import com.citc.nce.aim.privatenumber.service.PrivateNumberTableManagerService;
import com.citc.nce.seata.ShardingJdbcNodesAutoConfigurator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PrivateNumberTableManagerServiceImpl implements PrivateNumberTableManagerService {
    @Resource
    PrivateNumberSentDataDao privateNumberSentDataDao;

    @Resource
    private ShardingJdbcNodesAutoConfigurator shardingJdbcNodesAutoConfigurator;

    @Override
    public void createAimPrivateNumberTable() {
        //如果不存在就创建表
        privateNumberSentDataDao.createTable(obtainTableName(0));
        privateNumberSentDataDao.createTable(obtainTableName(1));
        privateNumberSentDataDao.createTable(obtainTableName(2));

    }

    @Override
    public void refreshShardingActualNodes(boolean isCreate) {
        log.info("-----------PrivateNumber-----启动后刷新sharding的真实结点----------------");
        try {
            // 定义开始时间和结束时间
            LocalDateTime startTime = obtainQueryStartTime();
            LocalDateTime endTime = LocalDateTime.now().plusDays(2);
            List<String> aimPrivateNumberActualNodes = obtainAllTableNames(startTime, endTime);
            aimPrivateNumberActualNodes.forEach(tableName -> {
                if (isCreate) {
                    privateNumberSentDataDao.createTable(tableName.split("\\.")[1]);
                }

            });
            shardingJdbcNodesAutoConfigurator.generateActualDataNodes("aim_private_number_sent_data", aimPrivateNumberActualNodes);
            log.info("-----------PrivateNumber-----启动后刷新sharding的真实结点------------完成----");
        } catch (Exception exception) {
            log.error("PrivateNumber刷新节点异常",exception);
            throw exception;
        }
    }

    /**
     * 获取查询开始时间,如果当前时间距离2024-08-20 超过60天，则取当前时间的前60天,否则取2024-08-20
     *
     * @return 最早时间
     */
    private LocalDateTime obtainQueryStartTime() {
        LocalDateTime settingDateTime = LocalDateTime.of(2024, 8, 20, 12, 0);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earliestTime = now.minusDays(60);
        earliestTime = settingDateTime.isBefore(earliestTime) ? earliestTime : settingDateTime;
        return earliestTime;
    }

    /**
     * LocalDateTime timeField = now.withMinute((now.getMinute() / 10) * 10).withSecond(0).withNano(0);  //每10分钟创建一张表
     *
     * @param day 天数
     * @return 表名
     */
    private static String obtainTableName(int day) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        now = now.plusDays(day);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return "aim_private_number_sent_data_" + now.format(formatter);
    }

    /**
     * 获取真实表名列表
     * 取整分钟为 00、10、20、30、40、50
     * int minute = (currentTime.getMinute() / 10) * 10;
     * currentTime = currentTime.withMinute(minute);
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 表名列表
     */
    private List<String> obtainAllTableNames(LocalDateTime startTime, LocalDateTime endTime) {
        List<String> tableNames = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        // 当前时间
        LocalDateTime currentTime = startTime;
        while (currentTime.isBefore(endTime) || currentTime.isEqual(endTime)) {
            // 格式化并保存
            tableNames.add("robot.aim_private_number_sent_data_" + currentTime.format(formatter));
            // 增加 10 分钟
            currentTime = currentTime.plusDays(1);
        }
        return tableNames;
    }

    public static void main(String[] args) {
        PrivateNumberTableManagerServiceImpl test = new PrivateNumberTableManagerServiceImpl();
        test.refreshShardingActualNodes(false);
    }
}
