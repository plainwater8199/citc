package com.citc.nce.auth.csp.sms.account.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CspSmsAccountReq extends PageParam implements Serializable {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("通道")
    private String dictCode;

    @ApiModelProperty("状态 -1:全部 0:禁用 1:启用")
    private Integer status;

    @ApiModelProperty("客户ID")
    private String customerId;
}
