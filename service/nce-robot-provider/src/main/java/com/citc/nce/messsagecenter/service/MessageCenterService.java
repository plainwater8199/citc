package com.citc.nce.messsagecenter.service;

import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.robot.vo.MessageData;
import com.citc.nce.tenant.robot.entity.MsgRecordDo;

public interface MessageCenterService {

    boolean withdraw(MsgRecordDo msgRecordDo, AccountManagementResp account);

    MessageData sendMessage(Object obj, AccountManagementResp account);
}
