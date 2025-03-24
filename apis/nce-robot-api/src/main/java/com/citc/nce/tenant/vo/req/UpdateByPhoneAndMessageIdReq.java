package com.citc.nce.tenant.vo.req;

import lombok.Data;

@Data
public class UpdateByPhoneAndMessageIdReq {
    private Integer msgType;

    private String phoneNum;

    private String messageId;

    private String customerId;

    private MsgRecordVo msgRecordVoUpdate;
}
