package com.citc.nce.robot.vo;

import lombok.Data;

import java.util.List;

@Data
public class TextObject {
    //文本信息
    private String text;

    //按钮信息的集合
    private List<Suggestions> suggestions;
}
