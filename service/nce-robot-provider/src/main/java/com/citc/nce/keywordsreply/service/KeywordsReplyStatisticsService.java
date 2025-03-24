package com.citc.nce.keywordsreply.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.dataStatistics.vo.KeywordReplyItem;
import com.citc.nce.dataStatistics.vo.KeywordReplyTimeItem;
import com.citc.nce.dataStatistics.vo.req.StartAndEndTimeReq;
import com.citc.nce.dataStatistics.vo.resp.KeywordReplyTimeTrendResp;
import com.citc.nce.dataStatistics.vo.resp.KeywordReplyTop10Resp;
import com.citc.nce.keywordsreply.dao.KeywordsReplyStatisticsMapper;
import com.citc.nce.keywordsreply.entity.KeyWordsReplyStatistics;
import com.citc.nce.keywordsreply.req.KeywordsReplyStatisticsInfo;
import com.citc.nce.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.citc.nce.dataStatistics.constant.StatisticsConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeywordsReplyStatisticsService extends ServiceImpl<KeywordsReplyStatisticsMapper, KeyWordsReplyStatistics> implements IService<KeyWordsReplyStatistics> {


    /**
     * 批量关键字数据插入数据
     * @param data 数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void statisticInsertBatch(List<KeywordsReplyStatisticsInfo> data) {
        if(!CollectionUtils.isEmpty(data)){
            String creator = SessionContextUtil.getUser() == null ? "system" : SessionContextUtil.getUser().getUserId();
            List<KeyWordsReplyStatistics> saveList = data.parallelStream()
                    .map(item -> {
                        KeyWordsReplyStatistics statistics = new KeyWordsReplyStatistics();
                        statistics.setKeywords(item.getKeywords());
                        statistics.setMessageId(item.getMessageId());
                        statistics.setCustomerId(item.getCustomerId());
                        statistics.setCreator(creator);
                        statistics.setCreateTime(LocalDateTime.now());
                        return statistics;
                    })
                    .collect(Collectors.toList());

            if (!saveList.isEmpty()) {
                this.saveOrUpdateBatch(saveList); // 确保 saveOrUpdateBatch 已针对批量操作进行了优化
            }
        }
    }


    /**
     * 更改消息ID
     * @param oldMessageId 旧消息ID
     * @param messageId   新消息ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateMessageId(String oldMessageId, String messageId) {
        List<KeyWordsReplyStatistics> records = this.lambdaQuery().eq(KeyWordsReplyStatistics::getMessageId, oldMessageId).list();
        if(!CollectionUtils.isEmpty(records)){
            records.forEach(item -> {
                item.setMessageId(messageId);
            });
            this.saveOrUpdateBatch(records);
        }
    }

    public KeywordReplyTop10Resp keywordReplyTop10(StartAndEndTimeReq req) {
        KeywordReplyTop10Resp resp = new KeywordReplyTop10Resp();
        String customerId = SessionContextUtil.getUser().getUserId();
        Date startTime = DateUtils.obtainDayTime(START,DateUtils.obtainDate(req.getStartTime()));
        Date endTime = DateUtils.obtainDayTime(END,DateUtils.obtainDate(req.getEndTime()));

        // Step 1: 查询数据
        List<KeyWordsReplyStatistics> records = this.lambdaQuery().eq(KeyWordsReplyStatistics::getCustomerId, customerId)
                .between(KeyWordsReplyStatistics::getCreateTime, startTime, endTime).list();

        // Step 2: 统计关键字出现次数
        Map<String, Long> keywordCountMap = records.stream()
                .collect(Collectors.groupingBy(KeyWordsReplyStatistics::getKeywords, Collectors.counting()));

        // Step 3: 对分组结果进行排序，获取前 10 项
        List<Map.Entry<String, Long>> top10Keywords = keywordCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());

        List<KeywordReplyItem> keywordReplyItems = top10Keywords.stream().map(item -> {
            KeywordReplyItem keywordReplyItem = new KeywordReplyItem();
            keywordReplyItem.setKeyword(item.getKey());
            keywordReplyItem.setCount(item.getValue());
            return keywordReplyItem;
        }).collect(Collectors.toList());

        resp.setKeywordItems(keywordReplyItems);
        return resp;
    }

    public KeywordReplyTimeTrendResp keywordReplyTimeTrend(StartAndEndTimeReq req) {
        KeywordReplyTimeTrendResp resp = new KeywordReplyTimeTrendResp();
        Date startTime = DateUtils.obtainDayTime(START,DateUtils.obtainDate(req.getStartTime()));
        Date endTime = DateUtils.obtainDayTime(END,DateUtils.obtainDate(req.getEndTime()));
        String timeType = DateUtils.obtainTimeType(startTime, endTime);
        List<String> times = HOUR.equals(timeType) ?  DateUtils.obtainAllHourList() : DateUtils.obtainDayList(startTime, endTime);
        Map<String, KeywordReplyTimeItem> keywordReplyTimeTrendMap = initKeywordReplyTimeTrend(times);
        //统计数据
        List<KeywordReplyTimeItem> keywordReplyTimeItems = cllTrend(timeType,startTime,endTime);

        if (!CollectionUtils.isEmpty(keywordReplyTimeItems)) {
            for (KeywordReplyTimeItem item : keywordReplyTimeItems) {
                if (keywordReplyTimeTrendMap.containsKey(item.getTime())) {
                    keywordReplyTimeTrendMap.put(item.getTime(), item);
                }
            }
        }
        resp.setTimeItems(new ArrayList<>(keywordReplyTimeTrendMap.values()));
        return resp;
    }

    private List<KeywordReplyTimeItem> cllTrend(String timeType, Date startTime, Date endTime) {
        // Step 1: 查询满足条件的数据
        List<KeyWordsReplyStatistics> records = this.lambdaQuery()
                .eq(KeyWordsReplyStatistics::getCustomerId, SessionContextUtil.getUserId())
                .between(KeyWordsReplyStatistics::getCreateTime, startTime, endTime)
                .list();

        // Step 2: 将查询结果按关键词和小时进行分组统计
        DateTimeFormatter hourFormatter = HOUR.equals(timeType) ? DateTimeFormatter.ofPattern("HH:00") : DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, Long> keywordHourlyStats = records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getCreateTime().format(hourFormatter),
                        Collectors.counting()
                ));

        List<KeywordReplyTimeItem> keywordReplyTimeItems = new ArrayList<>();
        for (Map.Entry<String, Long> entry : keywordHourlyStats.entrySet()) {
            KeywordReplyTimeItem item = new KeywordReplyTimeItem();
            item.setTime(entry.getKey());
            item.setCount(entry.getValue());
            keywordReplyTimeItems.add(item);
        }
        return keywordReplyTimeItems;
    }


    private Map<String, KeywordReplyTimeItem> initKeywordReplyTimeTrend(List<String> times) {
        return times.stream()
                .collect(Collectors.toMap(
                        time -> time,
                        time -> {
                            KeywordReplyTimeItem item = new KeywordReplyTimeItem();
                            item.setTime(time);
                            item.setCount(0L);
                            return item;
                        },
                        (existing, replacement) -> existing,
                        TreeMap::new
                ));
    }
}
