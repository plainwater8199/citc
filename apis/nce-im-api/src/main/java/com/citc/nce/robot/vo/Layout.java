package com.citc.nce.robot.vo;

import lombok.Data;

/**
 * 卡片样式
 */
@Data
public class Layout {

    //对齐方式HORIZONTAL，VERTICAL
    private String cardOrientation;

    //卡片样式文件 ,公网可访问
    private String style;

    //"LEFT", "RIGHT"
    private String imageAlignment;

    //卡片宽度，取值SMALL_WIDTH,MEDIUM_WIDTH
    private String cardWidth;
}
