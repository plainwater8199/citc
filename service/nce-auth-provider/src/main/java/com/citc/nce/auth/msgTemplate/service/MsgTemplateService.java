package com.citc.nce.auth.msgTemplate.service;

import com.citc.nce.common.core.enums.MsgTypeEnum;

public interface MsgTemplateService {
    String templateContentQuery(MsgTypeEnum msgType, Long templateId, String customerId);


}
