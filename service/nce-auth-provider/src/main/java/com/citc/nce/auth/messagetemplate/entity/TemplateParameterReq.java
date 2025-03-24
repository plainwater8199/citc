package com.citc.nce.auth.messagetemplate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 模板参数传输类  key最大长度为20b  key的最大数量为20
 */
@Data
@AllArgsConstructor
public class TemplateParameterReq implements Serializable {
    /**
     * 参数名
     */
    String key;
    /**
     * 参数类型: STRING, LONG, DOUBLE，
     * BOOLEAN，FLOAT和INTEGER
     */
    String type;
    /**
     * 参数值
     */
    String value;
}
