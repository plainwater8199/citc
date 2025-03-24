package com.citc.nce.auth.meal.vo.contract;

import com.citc.nce.auth.meal.domain.CspMealContractAssociation;
import com.citc.nce.auth.meal.enums.CspMealType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * bydud
 * 2024/1/23
 **/
@Data
@ApiModel(value = "CspContractMeal对象", description = "csp用户套餐表")
public class CspContractMeal {

    public CspContractMeal() {
    }

    public CspContractMeal(CspMealContractAssociation o) {
        this.id = o.getId();
        this.mealId = o.getMealId();
        this.name = o.getName();
        this.type = o.getType();
        this.customerNumber = o.getCustomerNumber();
        this.effectiveTime = o.getEffectiveTime();
    }

    @ApiModelProperty("主键 csp_meal_contract_association.id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("套餐ID")
    private String mealId;

    @ApiModelProperty("套餐名称")
    private String name;

    @ApiModelProperty("套餐类型，1普通套餐，2扩容套餐")
    private CspMealType type;

    @ApiModelProperty("客户个数")
    private Integer customerNumber;


    @ApiModelProperty("生效时间")
    private Date effectiveTime;
}
