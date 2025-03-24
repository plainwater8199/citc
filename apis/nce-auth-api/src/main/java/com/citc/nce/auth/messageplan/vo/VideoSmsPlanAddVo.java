package com.citc.nce.auth.messageplan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class VideoSmsPlanAddVo {

    @ApiModelProperty("套餐名称，25个字符，不能重复")
    @NotEmpty
    private String name;

    @ApiModelProperty("通道： 0:默认通道")
    @NotNull
    private Integer channel;

    @ApiModelProperty("视频短信数量")
    @Positive
    @NotNull
    private Long number;

    @ApiModelProperty("视频短信单价")
    @DecimalMax("99.99999")
    @NotNull
    @PositiveOrZero
    private BigDecimal price;
}
