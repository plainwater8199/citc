package com.citc.nce.robot.vo;

import lombok.Data;

import java.util.Map;

/**
 * 悬浮按钮信息
 */
@Data
public class Suggestions {
    //按钮类型
    private String type;
    //按钮里面的内容
    private String displayText;
    //可自带的信息
    private String postbackData;
    //按钮带的别的信息
    private Map<String,String> actionParams;
}
