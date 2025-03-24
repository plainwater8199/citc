package com.citc.nce.fastgroupmessage;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.FastGroupMessageApi;
import com.citc.nce.robot.req.FastGroupMessageQueryReq;
import com.citc.nce.robot.req.FastGroupMessageReq;
import com.citc.nce.robot.res.FastGroupMessageItem;
import com.citc.nce.robot.res.FastGroupMessageSelectAllResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author jcrenc
 * @since 2024/7/1 11:12
 */
@RequiredArgsConstructor
@RestController
@Api(tags = "快捷群发")
@Validated
public class FastGroupMessageController {
    private final FastGroupMessageApi fastGroupMessageApi;

    @ApiOperation("新增快捷群发")
    @PostMapping("/fastGroupMessage/create")
    public void createAndStartFastGroupMessage(@RequestBody @Valid FastGroupMessageReq req) {
        fastGroupMessageApi.createAndStartFastGroupMessage(req);
    }

    @ApiOperation("编辑快捷群发")
    @PostMapping("/fastGroupMessage/edit")
    public void updateFastGroupMessage(@RequestBody @Validated(FastGroupMessageReq.Update.class) FastGroupMessageReq req) {
        fastGroupMessageApi.updateFastGroupMessage(req);
    }

    @ApiOperation("快捷群发列表查询")
    @PostMapping("/fastGroupMessage/queryList")
    public PageResult<FastGroupMessageItem> queryList(@RequestBody @Valid FastGroupMessageQueryReq req) {
        return fastGroupMessageApi.queryList(req);
    }


    @ApiOperation("快捷群发列表查询--查询所有")
    @PostMapping("/fastGroupMessage/selectAll")
    public FastGroupMessageSelectAllResp selectAll() {
        return fastGroupMessageApi.selectAll();
    }

}
