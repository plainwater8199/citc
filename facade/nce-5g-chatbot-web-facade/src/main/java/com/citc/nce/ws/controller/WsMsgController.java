package com.citc.nce.ws.controller;

import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.ws.ChatbotWs;
import com.citc.nce.ws.vo.WsResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @authoer:ldy
 * @createDate:2022/7/17 20:52
 * @description:
 */
@Api(tags = "webSocket服务")
@RestController()
@RequestMapping("/ws")
public class WsMsgController {

    @SkipToken
    @ApiOperation("推送消息")
    @PostMapping("/pushMsgToClient")
    public void pushMsgToClient(@RequestBody WsResp wsResp) {
        ChatbotWs.sendMsg(wsResp.getConversationId(), JsonUtils.obj2String(wsResp));
    }
    @SkipToken
    @ApiOperation("通过token清除消息")
    @PostMapping("/cleanByToken")
    public void cleanByToken(@RequestBody String token) {
        ChatbotWs.cleanByToken(token);
    }

}
