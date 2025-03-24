package com.citc.nce.keywordsreply;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.keywordsreply.req.KeywordsReplyAddReq;
import com.citc.nce.keywordsreply.req.KeywordsReplyEditReq;
import com.citc.nce.keywordsreply.req.KeywordsReplySearchReq;
import com.citc.nce.keywordsreply.resp.KeywordsReplyListResp;
import com.citc.nce.keywordsreply.service.KeywordsReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/keywordsReply")
@RequiredArgsConstructor
public class KeywordsReplyController implements KeywordsReplyApi {

    private final KeywordsReplyService keywordsReplyService;

    @PostMapping("/add")
    public void add(@RequestBody KeywordsReplyAddReq addReq) {
        keywordsReplyService.add(addReq);
    }

    @PostMapping("/delete")
    public void delete(@RequestParam("id") Long id) {
        keywordsReplyService.delete(id);
    }

    @PostMapping("/edit")
    public void edit(@RequestBody KeywordsReplyEditReq editReq) {
        keywordsReplyService.edit(editReq);
    }

    @PostMapping("/search")
    public PageResult<KeywordsReplyListResp> search(@RequestBody KeywordsReplySearchReq searchReq) {
        return keywordsReplyService.search(searchReq);
    }

    @Override
    @GetMapping("/query")
    public Long queryMatchedTemplateId(@RequestParam("keywords")  String keywords, @RequestParam("chatbotAccount") String chatbotAccount) {
        return keywordsReplyService.queryTemplateIdByChatbotAccount(keywords, chatbotAccount);
    }
}
