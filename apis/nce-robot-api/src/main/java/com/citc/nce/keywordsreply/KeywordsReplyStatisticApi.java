package com.citc.nce.keywordsreply;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.keywordsreply.req.KeywordsReplyAddReq;
import com.citc.nce.keywordsreply.req.KeywordsReplyEditReq;
import com.citc.nce.keywordsreply.req.KeywordsReplySearchReq;
import com.citc.nce.keywordsreply.req.KeywordsReplyStatisticsInfo;
import com.citc.nce.keywordsreply.resp.KeywordsReplyListResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign API for Keywords Reply Service
 *
 * @since 2024/5/29 16:11
 */
@FeignClient(value = "rebot-service", contextId = "KeywordsReplyStatisticApi", url = "${robot:}")
public interface KeywordsReplyStatisticApi {

    @PostMapping("statistic/insertBatch")
    void statisticInsertBatch(@RequestBody List<KeywordsReplyStatisticsInfo> data);
    @PostMapping("statistic/updateMessageId")
    void updateMessageId(@RequestParam("oldMessageId")String oldMessageId, @RequestParam("messageId")String messageId);
}
