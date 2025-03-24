package com.citc.nce.keywordsreply;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.keywordsreply.req.KeywordsReplyAddReq;
import com.citc.nce.keywordsreply.req.KeywordsReplyEditReq;
import com.citc.nce.keywordsreply.req.KeywordsReplySearchReq;
import com.citc.nce.keywordsreply.resp.KeywordsReplyListResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/keywordsReply")
@RequiredArgsConstructor
@Validated
@Api(tags = "关键词回复")
public class KeywordsReplyController {

    private final KeywordsReplyApi keywordsReplyApi;

    @PostMapping("/add")
    @ApiOperation("新增关键词回复")
    public void add(@RequestBody @Valid KeywordsReplyAddReq addReq) {
        keywordsReplyApi.add(addReq);
    }

    @PostMapping("/delete")
    @ApiOperation("删除关键词回复")
    public void delete(@RequestParam("id") Long id) {
        keywordsReplyApi.delete(id);
    }

    @PostMapping("/edit")
    @ApiOperation("编辑关键词回复")
    public void edit(@RequestBody @Valid KeywordsReplyEditReq editReq) {
        keywordsReplyApi.edit(editReq);
    }

    @PostMapping("/search")
    @ApiOperation("搜索关键词回复")
    public PageResult<KeywordsReplyListResp> search(@RequestBody @Valid KeywordsReplySearchReq searchReq) {
        return keywordsReplyApi.search(searchReq);
    }
}
