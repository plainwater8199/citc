package com.citc.nce.dataStatistics.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/30 19:22
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class OperatorTypePageReq extends StartAndEndTimeReq {
    @ApiModelProperty(value = "运营商类型", dataType = "Integer")
    private Integer operatorType;

    @NotNull(message = "pageNo不能为空")
    @ApiModelProperty(value = "pageNo", dataType = "Integer", required = true)
    private Integer pageNo;

    @NotNull(message = "pageSize不能为空")
    @ApiModelProperty(value = "pageSize", dataType = "Integer", required = true)
    private Integer pageSize;
}
