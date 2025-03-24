package com.citc.nce.robot.res;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RichMediaSendParam {
    private String templateNumber;
    private String mobile;
    private String customId;
    private String extendedCode;
    private List<DataItem> data;

    @Data
    public static class DataItem {
        private String mobile;
        /**
         * 变量名称:变量值
         */
        private Map<String, String> params;
    }
}
