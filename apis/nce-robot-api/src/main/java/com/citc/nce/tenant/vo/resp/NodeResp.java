package com.citc.nce.tenant.vo.resp;

import lombok.Data;

@Data
public class NodeResp {

    /**
     * 运营商编码： 0：缺省(硬核桃)，1：联通，2：移动，3：电信
     */
    private Integer operatorCode;

    /**
     * 节点Id
     */
    private Long planDetailId;

    /**
     * 计划id
     */
    private Long planId;
    //创建者:customerId
    private String creator;

}