package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/30 19:14
 * @Version 1.0
 * @Description:
 */
@Data
@ApiModel
@Accessors(chain = true)
public class ConversationalChartResp {
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "上行量总数", dataType = "Long")
    private Long upsideSumNum;

    @ApiModelProperty(value = "供应商类型", dataType = "Integer")
    private int operatorType;

    @ApiModelProperty(value = "展示时间")
    private String showTime;
}
