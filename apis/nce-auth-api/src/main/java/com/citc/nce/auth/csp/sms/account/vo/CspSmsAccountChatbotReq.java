package com.citc.nce.auth.csp.sms.account.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
public class CspSmsAccountChatbotReq extends PageParam implements Serializable {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("状态 -1:全部 0:禁用 1:启用")
    private Integer status;
}
