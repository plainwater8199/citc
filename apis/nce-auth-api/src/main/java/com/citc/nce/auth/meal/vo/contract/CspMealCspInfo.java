package com.citc.nce.auth.meal.vo.contract;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * bydud
 * 2024/1/23
 **/
@Data
@ApiModel(value = "CspMealCspInfo对象", description = "csp用户套餐表")
public class CspMealCspInfo {
    private String userId;
    private String cspPhone;
    private String cspUserName;
    private String cspId;
    private String enterpriseName;
    private String enterpriseAccountName;
}
