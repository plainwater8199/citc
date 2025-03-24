package com.citc.nce.robot.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsMessageResponse {
    private List<SmsResponse> data;
    private boolean success;
    /**
     * 模板替换变量后的内容
     */
    private String templateReplaceModuleInformation;
}
