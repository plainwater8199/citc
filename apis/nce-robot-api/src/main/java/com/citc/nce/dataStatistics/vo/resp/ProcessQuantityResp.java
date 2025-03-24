package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

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
public class ProcessQuantityResp  implements Serializable {
    @ApiModelProperty(value = "流程触发总数量", dataType = "Long")
    private Long processTriggersSumNum;

    @ApiModelProperty(value = "流程完成总数量", dataType = "Long")
    private Long processCompletedSumNum;

    @ApiModelProperty(value = "兜底回复总数量", dataType = "Long")
    private Long bottomReturnSumNum;

    @ApiModelProperty(value = "折现图数据", dataType = "list")
    private List<?> list;
}
