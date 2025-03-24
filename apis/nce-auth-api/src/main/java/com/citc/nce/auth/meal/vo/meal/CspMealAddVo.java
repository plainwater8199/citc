package com.citc.nce.auth.meal.vo.meal;

import com.citc.nce.auth.meal.enums.CspMealType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author jiancheng
 */
@Data
@ApiModel("csp套餐添加vo")
public class CspMealAddVo {
    @ApiModelProperty("套餐类型 ")
    @NotNull(message = "套餐类型不能为空")
    private CspMealType type;

    @ApiModelProperty("套餐名称")
    @Length(max = 50, message = "套餐名称最长为50个字符")
    @NotEmpty(message = "套餐名称不能为空")
    private String name;

    @ApiModelProperty("客户数量")
    @NotNull(message = "客户数量不能为空")
    private Integer customerNumber;

    @ApiModelProperty("套餐价格")
    @NotNull(message = "套餐价格不能为空")
    @DecimalMax(value = "99999999.99", message = "最大为99999999.99")
    private BigDecimal price;
}
