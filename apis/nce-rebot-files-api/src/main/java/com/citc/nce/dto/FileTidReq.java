package com.citc.nce.dto;

import lombok.Data;

@Data
public class FileTidReq {
    private String fileUrlId;

    private String appId;

    private String operator;
    /**
     * 服务商tag fontdo 蜂动 owner csp自己
     */
    private String supplierTag;
}
