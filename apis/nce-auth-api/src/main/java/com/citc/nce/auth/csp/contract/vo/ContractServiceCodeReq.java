package com.citc.nce.auth.csp.contract.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class ContractServiceCodeReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "合同Id", dataType = "Integer", required = true)
    @NotNull
    private Long id;

    @ApiModelProperty(value = "服务代码", dataType = "String")
    private String contractServiceCode;

    @ApiModelProperty(value = "服务扩展码", dataType = "String")
    private String contractServiceExtraCode;
}
