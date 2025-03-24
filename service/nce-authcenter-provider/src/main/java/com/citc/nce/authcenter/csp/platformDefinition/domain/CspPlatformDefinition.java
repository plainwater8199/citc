package com.citc.nce.authcenter.csp.platformDefinition.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * csp平台自定义数据
 * </p>
 *
 * @author bydud
 * @since 2024-01-25 11:01:01
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("csp_platform_definition")
@ApiModel(value = "CspPlatformDefinition对象", description = "csp平台自定义数据")
public class CspPlatformDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "cpd_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cpdId;

    @TableField(value = "csp_id", updateStrategy = FieldStrategy.NEVER)
    private String cspId;

    @ApiModelProperty("平台名称")
    @TableField(value = "name", updateStrategy = FieldStrategy.ALWAYS)
    private String name;

    @ApiModelProperty("平台自定义log")
    @TableField(value = "logo", updateStrategy = FieldStrategy.ALWAYS)
    private String logo;

    @ApiModelProperty("轮播图json")
    @TableField(value = "carousel_chart", updateStrategy = FieldStrategy.ALWAYS)
    private String carouselChart;

    @ApiModelProperty("主题颜色")
    @TableField(value = "theme_color", updateStrategy = FieldStrategy.ALWAYS)
    private String themeColor;


}
