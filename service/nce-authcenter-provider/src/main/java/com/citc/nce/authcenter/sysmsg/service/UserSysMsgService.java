package com.citc.nce.authcenter.sysmsg.service;

import com.citc.nce.authcenter.sysmsg.dto.MsgReq;
import com.citc.nce.authcenter.systemmsg.vo.req.QueryMsgDetailsReq;
import com.citc.nce.authcenter.systemmsg.vo.req.QuerySysMsgReq;
import com.citc.nce.authcenter.systemmsg.vo.req.SendSystemUserMessageReq;
import com.citc.nce.authcenter.systemmsg.vo.req.UserMsgReq;
import com.citc.nce.authcenter.systemmsg.vo.resp.QueryMsgDetailsResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.SendSystemUserMessageResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.UnreadSysMsgQueryResp;

import java.util.List;

public interface UserSysMsgService {
    Object querySysMsgList(QuerySysMsgReq req);

    QueryMsgDetailsResp querySysMsgDetails(QueryMsgDetailsReq req);

    UnreadSysMsgQueryResp unreadSysMsgQuery();

    void addMsgIntoUser(MsgReq req);

    void saveMsg(UserMsgReq req);

    SendSystemUserMessageResp sendSystemMessage(SendSystemUserMessageReq sendSystemMessageReq);

    void batchSaveMsg(List<UserMsgReq> req);
}
