package com.citc.nce.auth.csp.menu.service.impl;

import lombok.Data;

@Data
public class MessageData {
    private Integer code;

    private String message;

    private SendMessageRes data;
}
