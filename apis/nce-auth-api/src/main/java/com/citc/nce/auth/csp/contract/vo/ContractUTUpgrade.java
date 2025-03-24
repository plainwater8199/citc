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
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractUTUpgrade extends ContractUTAdd implements Serializable {

    @ApiModelProperty(value = "数据库主键", dataType = "Long", required = true)
    @NotNull(message = "修改id不能为空")
    private Long id;
}
