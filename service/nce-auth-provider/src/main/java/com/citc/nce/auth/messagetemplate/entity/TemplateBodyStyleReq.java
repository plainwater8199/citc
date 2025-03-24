package com.citc.nce.auth.messagetemplate.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 内容样式表
 */
@Data
public class TemplateBodyStyleReq implements Serializable {
    /**
     * 标题样式
     */
    TemplateStyleCellReq titleStyle;
    /**
     * 描述样式
     */
    TemplateStyleCellReq descriptionStyle;
    /**
     * 按钮样式
     */
    TemplateStyleCellReq suggestionsStyle;
}
