package com.citc.nce.authcenter.systemmsg.vo.req;

import lombok.Data;

import java.util.Map;
@Data
public class SendSystemUserMessageReq {
    private String messageCode;
    private String userUuid;
    private Long sourceId;
    private Integer businessType;
    private Map<String,String> paraMap;
}
