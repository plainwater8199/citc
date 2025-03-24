package com.citc.nce.im.robot.node;

import lombok.Data;

@Data
public class TransferParam {
    //需要识别的文字
    private String content;
    //变量id(系统中存在的英文变量)
    private String variableId;
    //汉字变量名
    private String variableName;
    //完整变量名
    private String variableNameValue;
}