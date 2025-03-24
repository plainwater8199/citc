package com.citc.nce.robot.vo;

import com.citc.nce.robot.res.SendMessageRes;
import lombok.Data;

@Data
public class MessageData {
    /**
     * 0 成功  -1 失败
     */
    private Integer code;

    private String message;

    private SendMessageRes data;

    /**
     * 模板替换变量后的内容
     */
    private String templateReplaceModuleInformation;
}
