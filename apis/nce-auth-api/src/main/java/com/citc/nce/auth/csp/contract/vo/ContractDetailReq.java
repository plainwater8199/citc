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
public class ContractDetailReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "合同Id")
    @NotNull
    private Long id;

    @ApiModelProperty(value = "isChange")
    @NotNull
    private Integer changeDetails;

    private Integer operatorCode;

}
