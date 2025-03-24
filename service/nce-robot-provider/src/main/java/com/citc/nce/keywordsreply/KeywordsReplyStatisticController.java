package com.citc.nce.keywordsreply;

import com.citc.nce.keywordsreply.req.KeywordsReplyStatisticsInfo;
import com.citc.nce.keywordsreply.service.KeywordsReplyStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class KeywordsReplyStatisticController implements KeywordsReplyStatisticApi {

    private final KeywordsReplyStatisticsService keywordsReplyStatisticsService;


    @Override
    public void statisticInsertBatch(List<KeywordsReplyStatisticsInfo> data) {
        keywordsReplyStatisticsService.statisticInsertBatch(data);
    }

    @Override
    public void updateMessageId(String oldMessageId, String messageId) {
        keywordsReplyStatisticsService.updateMessageId(oldMessageId, messageId);
    }
}
