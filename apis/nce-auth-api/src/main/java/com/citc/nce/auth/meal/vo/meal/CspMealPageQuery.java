package com.citc.nce.auth.meal.vo.meal;

import com.citc.nce.auth.meal.enums.CspMealStatus;
import com.citc.nce.auth.meal.enums.CspMealType;
import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * bydud
 * 2024/1/22
 **/
@Data
@ApiModel(value = "CspMealPageQuery对象", description = "csp用户套餐表")
public class CspMealPageQuery extends PageParam {

    /**
     * 套餐ID/套餐名称模糊查询
     */
    private String queryStr;
    private CspMealStatus status;
    @NotNull(message = "套餐类型不能为空")
    private CspMealType type;
}
