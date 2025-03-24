package com.citc.nce.mall.ws;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.util.JsonUtils;
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
@RequestMapping("/mallWs")
public class MallChatbotWsMsgController {
    @SkipToken
    @ApiOperation("推送消息")
    @PostMapping("/pushMsgToClient")
    public void pushMsgToClient(@RequestBody WsResp wsResp) {
        MallChatbotWs.sendMsg(wsResp.getConversationId(), JsonUtils.obj2String(wsResp));
    }
    @ApiOperation("通过token清除消息")
    @PostMapping("/cleanByToken")
    public void pushMsgToClient(@RequestBody String token) {
        MallChatbotWs.cleanByToken(token);
    }

}
