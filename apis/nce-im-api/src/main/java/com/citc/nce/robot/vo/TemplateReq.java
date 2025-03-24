package com.citc.nce.robot.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class TemplateReq extends RichMediaTemplateReq {
    private String title;
    private Long effectiveTime;
    private List<String> reportOperator;
    private String templateSign;
    private List<TemplatePage> pages;

    @Data
    public static class TemplatePage {
        private Integer pageNumber;
        private List<TemplateItem> items;
    }

    @Data
    public static class TemplateItem {
        private String ext;
        private String content;
        private Integer serialNumber;
    }
}
