package com.citc.nce.auth.meal.vo.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * bydud
 * 2024/1/22
 **/
@Data
@ApiModel(value = "CspMealContractAddVo对象", description = "csp用户套餐表")
public class CspMealContractAddVo {
    @ApiModelProperty("合同编号")
    @NotEmpty(message = "合同该编号不能为空")
    private String contractNo;

    @ApiModelProperty("cspId")
    @NotEmpty(message = "cspId不能为空")
    private String cspId;

    @ApiModelProperty("合同生效时长（年）")
    @NotNull(message = "生效时长不能为空")
    private Integer validTime;

    @ApiModelProperty("生效时间")
    @NotNull(message = "生效时间不能为空")
    private Date effectiveTime;

    @ApiModelProperty("基础套餐 套餐编号")
    private String basicMeal;

    @ApiModelProperty("扩容套餐 套餐编号")
    private List<String> expansionMeal;

}
