package com.citc.nce.auth.messagetemplate.vo.shortcutbutton;

import lombok.Data;

import java.util.List;

@Data
public class ButtonDetailInfo {
    private Integer type;
    private InputInfo input;
    private String businessId;
    private List<Integer> option;
}
