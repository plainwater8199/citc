package com.citc.nce.authcenter.sysmsg;

import com.citc.nce.authcenter.sysmsg.service.UserSysMsgService;
import com.citc.nce.authcenter.systemmsg.UserSysMsgApi;
import com.citc.nce.authcenter.systemmsg.vo.req.*;
import com.citc.nce.authcenter.systemmsg.vo.resp.QueryMsgDetailsResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.SendSystemUserMessageResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.UnreadSysMsgQueryResp;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController()
@Slf4j
public class UserSysMsgController implements UserSysMsgApi {
    @Resource
    private UserSysMsgService userSysMsgService;

    @Override
    @ApiOperation("用户站内信列表")
    @PostMapping("/user/sysMsg/querySysMsgList")
    public Object querySysMsgList(@RequestBody @Valid QuerySysMsgReq req) {
        return userSysMsgService.querySysMsgList(req);
    }

    @Override
    @ApiOperation("站内信详情查询")
    @PostMapping("/user/sysMsg/querySysMsgDetails")
    public QueryMsgDetailsResp querySysMsgDetails(@RequestBody @Valid QueryMsgDetailsReq req) {
        return userSysMsgService.querySysMsgDetails(req);
    }

    @Override
    @ApiOperation("未读站内信查询")
    @PostMapping("/user/sysMsg/unreadSysMsgQuery")
    public UnreadSysMsgQueryResp unreadSysMsgQuery() {
        return userSysMsgService.unreadSysMsgQuery();
    }

    @Override
    @PostMapping("/user/msg/save")
    public void saveMsg(UserMsgReq req) {
        userSysMsgService.saveMsg(req);
    }

    @Override
    @PostMapping("/user/msg/sendSystemMessage")
    public SendSystemUserMessageResp sendSystemMessage(SendSystemUserMessageReq sendSystemMessageReq) {
        return userSysMsgService.sendSystemMessage(sendSystemMessageReq);
    }

    @Override
    @ApiOperation("管理平台--发送系统消息")
    @PostMapping("/addMsgIntoUser")
    public void addMsgIntoUser(MsgReq req) {
        com.citc.nce.authcenter.sysmsg.dto.MsgReq dto = new com.citc.nce.authcenter.sysmsg.dto.MsgReq();
        BeanUtils.copyProperties(req,dto);
        userSysMsgService.addMsgIntoUser(dto);
    }

    @Override
    @PostMapping("/user/msg/batchSaveMsg")
    public void batchSaveMsg(List<UserMsgReq> req) {
        userSysMsgService.batchSaveMsg(req);
    }


}
