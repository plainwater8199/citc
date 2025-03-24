package com.citc.nce.auth.csp.contract.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * bydud
 *
 * @{DATE}
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class ContractUTInfo extends ContractUTUpgrade implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("客户企业名称")
    private String enterpriseAccountName;
}
