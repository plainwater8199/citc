package com.citc.nce.materialSquare.vo.activity;

import com.citc.nce.materialSquare.PromotionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author bydud
 * @since 2024/5/14 15:31
 */
@Data
@Accessors(chain = true)
public class ContentAdd implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty("方案名称")
    @NotBlank(message = "方案名称 不能为空")
    @Length(min = 1, max = 25, message = "方案名称长度必须在1~25字符")
    private String name;

    @ApiModelProperty("开始时间 yyyy-MM-dd HH:mm:ss 包括")
    @JsonFormat(locale = "zh", pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @NotNull(message = "有效期不正确")
    private Date startTime;

    @ApiModelProperty("结束时间 yyyy-MM-dd HH:mm:ss 不包含")
    @JsonFormat(locale = "zh", pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @NotNull(message = "有效期不正确")
    private Date endTime;

    @ApiModelProperty("促销形式")
    @NotNull(message = "促销形式不能为空")
    private PromotionType promotionType;

    @DecimalMin(value = "0.00", inclusive = true, message = "折扣系数必须大于等于0")
    @DecimalMax(value = "0.99", inclusive = true, message = "折扣系数必须小于1")
    @Digits(integer = 1, fraction = 2, message = "折扣系数必须保证有最多两位小数")
    @ApiModelProperty("折扣")
    private Double discountRate;
}
