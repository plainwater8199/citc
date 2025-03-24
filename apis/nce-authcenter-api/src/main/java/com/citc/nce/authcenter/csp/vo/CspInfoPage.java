package com.citc.nce.authcenter.csp.vo;

import lombok.Data;

/**
 * bydud
 * 2024/1/30
 **/
@Data
public class CspInfoPage {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private String cspUserName;
}
