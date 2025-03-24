package com.citc.nce.dataStatistics.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/30 19:22
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class OperatorTypeReq extends StartAndEndTimeReq {
    @ApiModelProperty(value = "运营商类型", dataType = "Integer")
    private Integer operatorType;
}
