package com.citc.nce.auth.messagetemplate.entity;

import com.citc.nce.robot.res.SendMessageRes;
import lombok.Data;

/**
 * @author yy
 * @date 2024-03-15 14:58:16
 */
@Data
public class  TemplateDataResp<T> {
    private Integer code;

    private String message;

    private T data;
}
