package com.citc.nce.authcenter.csp.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yy
 * @date 2024-10-24 18:59:50
 */
@Data
public class ReduceBalanceResp {
    @ApiModelProperty("实际扣除金额")
    Long deductAmount;
    @ApiModelProperty("实际扣除的数量")
    Long numOfDeduct;
    Integer price;
    Long tarrifId;
}
