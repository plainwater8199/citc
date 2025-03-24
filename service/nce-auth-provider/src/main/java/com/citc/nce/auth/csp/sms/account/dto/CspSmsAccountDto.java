package com.citc.nce.auth.csp.sms.account.dto;

import com.citc.nce.common.core.pojo.PageParam;
import lombok.Data;

import java.util.List;

@Data
public class CspSmsAccountDto extends PageParam {

    private String name;

    private String dictCode;

    private Integer status;

    private Integer currentPage;

    private String customerId;

    private List<String> customerIdList;

    private String cspId;
}
