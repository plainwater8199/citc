package com.citc.nce.auth.meal.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * bydud
 * 2024/1/24
 **/

@Data
@ApiModel(value = "MealCspHome对象", description = "csp用户套餐表")
public class MealCspHome {
    @ApiModelProperty("meal 合同主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractId;

    @ApiModelProperty("cspId")
    private String cspId;

    @ApiModelProperty("套餐客户数量")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long mealNum = 0L;

    @ApiModelProperty("正式客户数量")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerNum = 0L;

    @ApiModelProperty("合同生效时长（年）")
    private Integer validTime;

    @ApiModelProperty("生效时间")
    private Date effectiveTime;

    @ApiModelProperty("到期时间")
    private Date expireTime;

    @ApiModelProperty("基础套餐数量")
    private Integer basicMeal = 0;

    @ApiModelProperty("扩容套餐数量")
    private Integer expansionMeal = 0;
}
