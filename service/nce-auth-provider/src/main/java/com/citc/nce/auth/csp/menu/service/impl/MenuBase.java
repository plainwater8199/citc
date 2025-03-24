package com.citc.nce.auth.csp.menu.service.impl;

import lombok.Data;

import java.util.Map;

@Data
public class MenuBase {
    private String displayText;

    private Map<String,String> postback;
}
