package com.citc.nce.robot.req;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class RichMediaResult {
    private Boolean success;
    private String code;
    private String message;
    private JSONObject result;
}
