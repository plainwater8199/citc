package com.citc.nce.auth.messagetemplate.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 消息样式设置
 */
@Data
public class TemplateStyleReq implements Serializable {
    /**
     * 整体前景色
     */
    String color;
    /**
     * 整体字符
     */
    String font;
    /**
     * 整体对齐方式
     */
    String textAlign;
    /**
     * 整体背景色
     */
    String backgroundColor;
    /**
     * 整体背景图片
     */
    String backgroundImage;
    /**
     *卡片样式设计
     */
    TemplateBodyStyleReq style;
}
