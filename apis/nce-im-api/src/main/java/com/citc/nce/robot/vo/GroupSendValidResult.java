package com.citc.nce.robot.vo;

import lombok.Data;

@Data
public class GroupSendValidResult {
    // 校验结果
    private Boolean validResult;

    // 提示消息
    private String noticeMsg = "";
}
