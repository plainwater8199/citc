package com.citc.nce.tenant.vo.req;

import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class MsgRecordDataSynReq {

    private Set<String> cspIdSet;

    private Map<Integer, Map<Long,String>> templateSignMap;
}
