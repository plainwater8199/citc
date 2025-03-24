package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/28 17:41
 * @Version 1.0
 * @Description:
 */
@Data
@ApiModel
@Accessors(chain = true)
public class ProcessQuantityWeeksResp {

    @ApiModelProperty(value = "表主键", dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "运营商类型", dataType = "Integer")
    private Integer operatorType;

    @ApiModelProperty(value = "场景id", dataType = "Long")
    private Long robotSceneNodeId;

    @ApiModelProperty(value = "场景名称", dataType = "String")
    private String robotSceneNodeName;

    @ApiModelProperty(value = "流程id", dataType = "Long")
    private Long robotProcessSettingNodeId;

    @ApiModelProperty(value = "流程名称", dataType = "String")
    private String robotProcessSettingNodeName;

    @ApiModelProperty(value = "状态", dataType = "Integer")
    private Integer status;

    @ApiModelProperty(value = "流程触发数量", dataType = "Long")
    private Long processTriggersNum;

    @ApiModelProperty(value = "流程完成数量", dataType = "Long")
    private Long processCompletedNum;

    @ApiModelProperty(value = "兜底回复数量", dataType = "Long")
    private Long bottomReturnNum;

    @ApiModelProperty(value = "流程完成率", dataType = "BigDecimal")
    private BigDecimal processCompletionRate;

    @ApiModelProperty(value = "时间(每周)", dataType = "String")
    private String weeks;

    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "是否删除 默认0 未删除  1 删除", dataType = "Integer")
    private Integer deleted;

    @ApiModelProperty(value = "删除时间戳", dataType = "Long")
    private Long deletedTime;
}
