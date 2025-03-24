package com.citc.nce.auth.meal.vo.contract;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * bydud
 * 2024/1/22
 **/
@Data
@ApiModel(value = "CspMealContractEditVo对象", description = "csp用户套餐表")
public class CspMealContractEditVo extends CspMealContractAddVo {
    @ApiModelProperty("主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractId;

}
