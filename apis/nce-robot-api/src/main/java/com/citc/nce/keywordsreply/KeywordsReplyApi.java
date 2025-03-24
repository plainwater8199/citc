package com.citc.nce.keywordsreply;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.keywordsreply.req.KeywordsReplyAddReq;
import com.citc.nce.keywordsreply.req.KeywordsReplyEditReq;
import com.citc.nce.keywordsreply.req.KeywordsReplySearchReq;
import com.citc.nce.keywordsreply.resp.KeywordsReplyListResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign API for Keywords Reply Service
 *
 * @since 2024/5/29 16:11
 */
@FeignClient(value = "rebot-service", contextId = "KeywordsReplyApi", url = "${robot:}")
public interface KeywordsReplyApi {

    @PostMapping("/keywordsReply/add")
    void add(@RequestBody KeywordsReplyAddReq addReq);

    @PostMapping("/keywordsReply/delete")
    void delete(@RequestParam("id") Long id);

    @PostMapping("/keywordsReply/edit")
    void edit(@RequestBody KeywordsReplyEditReq editReq);

    @PostMapping("/keywordsReply/search")
    PageResult<KeywordsReplyListResp> search(@RequestBody KeywordsReplySearchReq searchReq);

    @GetMapping("/keywordsReply/query")
    Long queryMatchedTemplateId(
            @RequestParam("keywords") String keywords,
            @RequestParam("chatbotAccount") String chatbotAccount);
}
