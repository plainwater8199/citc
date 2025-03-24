package com.citc.nce.dataStatistics.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/30 10:10
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class StartAndEndTimeReq {

    @NotBlank(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间", dataType = "String", required = true)
    private String startTime;

    @NotBlank(message = "结束时间不能为空")
    @ApiModelProperty(value = "结束时间", dataType = "String", required = true)
    private String endTime;
}
