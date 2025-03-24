package com.citc.nce.authcenter.csp.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * csp客户下拉框vo
 *
 * @author jiancheng
 */
@Accessors(chain = true)
@Data
public class CustomerOptionVo {
    @ApiModelProperty("客户ID")
    private String customerId;
    private String cspId;

    @ApiModelProperty("用户名称")
    private String name;

    @ApiModelProperty("客户企业名称")
    private String enterpriseName;

    @ApiModelProperty("客户企业账号名称")
    private String enterpriseAccountName;

    @ApiModelProperty("合同绑定状态")
    private Boolean contractBanding;

    @ApiModelProperty("服务代码")
    private String contractServiceCode;

    @ApiModelProperty("服务扩展码")
    private String contractServiceExtraCode;

    @ApiModelProperty("chatbot绑定状态")
    private Boolean chatbotBanding;

    @ApiModelProperty("客户是否启用，true为启用")
    private Boolean customerActive;
}
