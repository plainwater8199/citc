package com.citc.nce.auth.messagetemplate.vo.shortcutbutton;

import lombok.Data;

import java.util.List;

@Data
public class ShortCutButtonInfo {
    private InputInfo input;

    private Integer type;

    private String uuid;

    private List<Integer> option;

    private ButtonDetailInfo buttonDetail;
}
