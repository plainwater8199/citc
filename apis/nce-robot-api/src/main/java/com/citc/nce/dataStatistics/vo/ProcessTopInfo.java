package com.citc.nce.dataStatistics.vo;

import lombok.Data;
import java.util.Map;
import java.util.Set;

@Data
public class ProcessTopInfo {
    private Map<Long,Long> processMap ;
    private Map<Long,Long> sceneMap ;
    private Map<Long,Long> processToSceneMap ;
    private Set<Long> sceneIds ;
    private Set<Long> processIds;
}
