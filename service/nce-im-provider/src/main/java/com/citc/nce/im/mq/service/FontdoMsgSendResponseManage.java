package com.citc.nce.im.mq.service;

import com.citc.nce.robot.req.FontdoMessageStatusReq;

public interface FontdoMsgSendResponseManage {

    void msgSendResponseAsynManageFor5G(FontdoMessageStatusReq statusReq);
}
