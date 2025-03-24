package com.citc.nce.tenant.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class StatisticSyncReq {

    @ApiModelProperty(value = "idMap")
    private Map<String,String> idMap ;
    @ApiModelProperty(value = "cspIdSet")
    private Set<String> cspIdSet;
    @ApiModelProperty(value = "templateSignMap")
    private Map<Integer,Map<Long,String>> templateSignMap;
    @ApiModelProperty(value = "templateMap")
    private Map<Integer,Map<String,Long>> templateMap;
    @ApiModelProperty(value = "templateByIdMap")
    private Map<Integer,Map<Long,String>> templateByIdMap;
    @ApiModelProperty(value = "chatbotMap")
    private Map<String,Map<Integer,String>> chatbotMap;
}
