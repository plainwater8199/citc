package com.citc.nce.auth.messagetemplate.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 不同具体单元样式设计 如title ，description的设计
 */
@Data
public class TemplateStyleCellReq implements Serializable {
    /**
     * 前景色
     */
    String color;
    /**
     * 字体
     */
    String font;
    /**
     * 对齐方式 center, left, right
     */
    String textAlign;
    /**
     * 粗体
     */
    boolean bold;
    /**
     * 斜体
     */
    boolean italic;
    /**
     * 字体大小
     */
    String fontSize;
}
