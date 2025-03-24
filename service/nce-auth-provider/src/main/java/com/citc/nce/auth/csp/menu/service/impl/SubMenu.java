package com.citc.nce.auth.csp.menu.service.impl;

import lombok.Data;

import java.util.List;

@Data
public class SubMenu {
    private String displayText;

    private List<Suggestions> subMenu;

}
