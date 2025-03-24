package com.citc.nce.authcenter.systemmsg;

import com.citc.nce.authcenter.identification.vo.resp.SendSystemMessageResp;
import com.citc.nce.authcenter.systemmsg.vo.req.*;
import com.citc.nce.authcenter.systemmsg.vo.resp.QueryMsgDetailsResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.SendSystemUserMessageResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.UnreadSysMsgQueryResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "用户中心--站内信模块")
@FeignClient(value = "authcenter-service", contextId = "system-message", url = "${authCenter:}")
public interface UserSysMsgApi {
    @ApiOperation("用户站内信列表")
    @PostMapping("/user/sysMsg/querySysMsgList")
    Object querySysMsgList(@RequestBody @Valid QuerySysMsgReq req);
    @ApiOperation("站内信详情查询")
    @PostMapping("/user/sysMsg/querySysMsgDetails")
    QueryMsgDetailsResp querySysMsgDetails(@RequestBody @Valid  QueryMsgDetailsReq req);
    @ApiOperation("未读站内信查询")
    @PostMapping("/user/sysMsg/unreadSysMsgQuery")
    UnreadSysMsgQueryResp unreadSysMsgQuery();



    @PostMapping("/user/msg/save")
    void saveMsg(@RequestBody UserMsgReq req);

    @PostMapping("/user/msg/sendSystemMessage")
    SendSystemUserMessageResp sendSystemMessage(@RequestBody @Valid SendSystemUserMessageReq sendSystemMessageReq);

    @ApiOperation("管理平台--发送系统消息")
    @PostMapping("/addMsgIntoUser")
    void addMsgIntoUser(@RequestBody @Valid MsgReq req);

    @PostMapping("/user/msg/batchSaveMsg")
    void batchSaveMsg(@RequestBody List<UserMsgReq> req);


}
