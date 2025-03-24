package com.citc.nce.robot.vo;

import lombok.Data;

import java.util.List;

@Data
public class CardObject {
    private List<Media> media;

    private Layout layout;

    //悬浮菜单数组
    private List<Suggestions> suggestions;
}
