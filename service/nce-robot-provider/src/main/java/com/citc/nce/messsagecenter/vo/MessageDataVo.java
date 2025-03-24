package com.citc.nce.messsagecenter.vo;

import lombok.Data;

@Data
public class MessageDataVo {
    private Integer code;

    private String message;

    private SendMessageRes data;
}
