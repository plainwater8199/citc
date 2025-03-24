package com.citc.nce.auth.csp.menu.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class SecMenu {

    private String displayText;

    private List<Object> entries;
}
