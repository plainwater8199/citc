package com.citc.nce.dataStatistics.vo;

import lombok.Data;

@Data
public class ProcessBasicInfo {
    private Long processId;
    private Long sceneId;
    private String processName;
    private String sceneName;
    private Integer derail;
}
