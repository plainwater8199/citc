package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Time;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/31 15:09
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ProcessLineChartYesterdayResp {

    @ApiModelProperty(value = "流程触发数量", dataType = "Long")
    private Long processTriggersNum;

    @ApiModelProperty(value = "流程完成数量", dataType = "Long")
    private Long processCompletedNum;

    @ApiModelProperty(value = "兜底回复数量", dataType = "Long")
    private Long bottomReturnNum;

    @ApiModelProperty(value = "时间(小时)", dataType = "Time")
    private Time hours;
}
