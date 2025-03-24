package com.citc.nce.robot.domain.massSegment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author bydud
 * @since 2024/5/6
 */
@Data
public class MassSegmentDetail extends MassSegmentVo {
    @ApiModelProperty("主键")
    private Long id;
    @ApiModelProperty("号段类型 system custom")
    private String msType;
}
