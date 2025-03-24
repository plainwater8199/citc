package com.citc.nce.im.robot.node;

import lombok.Data;

import java.util.List;

@Data
public class TextRecognitionSetting {
    //文字识别内容
    private String textValue;
    //是否开启prompt优化
    private Boolean promptOptimize;
    //设定
    private String promptSetting;
    //示例
    private String promptExample;
    //需要被替换的变量
    private List<TransferParam> recognitions;
    /**
     * 规则和示例
     */
    private String ruleAndFormats;

}