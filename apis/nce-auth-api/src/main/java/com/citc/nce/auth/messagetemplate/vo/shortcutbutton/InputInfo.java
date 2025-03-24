package com.citc.nce.auth.messagetemplate.vo.shortcutbutton;

import lombok.Data;

import java.util.List;

@Data
public class InputInfo {

    private String name;

    private String value;

    private List<String> names;

    private Integer length;
}
