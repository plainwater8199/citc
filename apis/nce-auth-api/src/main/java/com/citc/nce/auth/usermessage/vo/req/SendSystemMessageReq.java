package com.citc.nce.auth.usermessage.vo.req;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SendSystemMessageReq {
    private String messageCode;
    private String userUuid;
    private Long sourceId;
    private Integer businessType;
    private Map<String,String> paraMap;
}
