package com.citc.nce.auth.csp.contract.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * bydud
 *
 * @{DATE}
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class ContractCmccUpgrade extends ContractCmccAdd implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "数据库主键", dataType = "Long", required = true)
    @NotNull(message = "修改id不能为空")
    private Long id;
}
