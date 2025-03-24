package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

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
public class     ProcessQuantityTableResp {
    @ApiModelProperty(value = "场景id")
    private Long robotSceneNodeId;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    @ApiModelProperty(value = "流程id")
    private Long robotProcessSettingNodeId;

    @ApiModelProperty(value = "流程名称")
    private String processName;

    @ApiModelProperty(value = "开关")
    private int derail;

    @ApiModelProperty(value = "流程触发总数量", dataType = "Long")
    private Long processTriggersSumNum;

    @ApiModelProperty(value = "流程完成总数量", dataType = "Long")
    private Long processCompletedSumNum;

    @ApiModelProperty(value = "兜底回复总数量", dataType = "Long")
    private Long bottomReturnSumNum;


}
