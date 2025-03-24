package com.citc.nce.im.mq.service;

import com.citc.nce.im.session.entity.FifthDeliveryStatusDto;
import com.citc.nce.robot.req.DeliveryInfo;
import com.citc.nce.robot.req.DeliveryStatusReq;
import com.citc.nce.robot.req.RichMediaResultArray;

public interface MsgSendResponseManage {

    void handleMessageStatusCallback(FifthDeliveryStatusDto dto);

    void msgSendResponseAsynManageForMedia(RichMediaResultArray resultArray);
}
