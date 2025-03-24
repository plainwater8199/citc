package com.citc.nce.auth.readingLetter.shortUrl.mqConsumer;

/**
 * @Author: zjy
 * @Date: 2023/7/26 10:46
 * @Version: 1.0
 * @Description:
 */

import com.citc.nce.auth.readingLetter.shortUrl.service.ReadingLetterShortUrlService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 接收call-back服务发送的阅信+ 解析回执记录信息
 */
@Service
@RocketMQMessageListener(consumerGroup = "${rocketmq.readingLetterPlus.group}", topic = "${rocketmq.readingLetterPlus.topic}" )
@Slf4j
public class ParseRecordListener implements RocketMQListener<String> {

    @Autowired
    private ReadingLetterShortUrlService service;
    /**
     * 接收回调
     * @param requestStr 回调的请求体
     */
    @Override
    public void onMessage(String requestStr) {
        //该短链的已解析数量+1
//        service.handleParseRecord(requestStr);
    }
}
