package com.citc.nce.auth.unicomAndTelecom.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class PlatformResult {
    private Integer code;

    private String message;

    private Object data;

    private Integer errorCode;

    private String errorMessage;
}
