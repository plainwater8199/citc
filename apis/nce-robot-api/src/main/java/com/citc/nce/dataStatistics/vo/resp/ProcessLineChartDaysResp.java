package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Date;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/28 17:41
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ProcessLineChartDaysResp {

    @ApiModelProperty(value = "流程触发数量", dataType = "Long")
    private Long processTriggersNum;

    @ApiModelProperty(value = "流程完成数量", dataType = "Long")
    private Long processCompletedNum;

    @ApiModelProperty(value = "兜底回复数量", dataType = "Long")
    private Long bottomReturnNum;

    @ApiModelProperty(value = "时间(每天)", dataType = "Date")
    private Date days;
}
