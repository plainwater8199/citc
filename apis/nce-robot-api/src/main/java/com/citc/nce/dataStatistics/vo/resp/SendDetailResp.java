package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class SendDetailResp {

    private String days;

    private Long total;

//    private Integer sendResult;
    private Integer finalResult;

    private Integer messageResource;

    private String callerAccount;
}
