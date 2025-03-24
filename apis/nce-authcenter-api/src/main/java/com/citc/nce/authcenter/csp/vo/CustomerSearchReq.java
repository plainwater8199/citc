package com.citc.nce.authcenter.csp.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CustomerSearchReq extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("企业账户名称")
    private String enterpriseAccountName;

    @ApiModelProperty("CSP启用状态 0:未启用 1：已启用 2试用中 3试用结束")
    private Integer customerActive;

    @ApiModelProperty("客户手机号")
    private String phone;
}
