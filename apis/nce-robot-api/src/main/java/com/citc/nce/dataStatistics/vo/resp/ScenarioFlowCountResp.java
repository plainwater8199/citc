package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/28 10:42
 * @Version 1.0
 * @Description:
 */
@Data
@ApiModel
@Accessors(chain = true)
public class ScenarioFlowCountResp {

    @ApiModelProperty(value = "场景数量", dataType = "Long")
    private Long scenariosNum;

    @ApiModelProperty(value = "指令数量", dataType = "Long")
    private Long orderNum;

    @ApiModelProperty(value = "变量数量", dataType = "Long")
    private Long variablesNum;

    @ApiModelProperty(value = "流程数量", dataType = "Long")
    private Long processesNum;

    @ApiModelProperty(value = "提问数量", dataType = "Long")
    private Long questionsNum;

    @ApiModelProperty(value = "分支数量", dataType = "Long")
    private Long branchNum;

    @ApiModelProperty(value = "发送消息数量", dataType = "Long")
    private Long sentMessagesNum;

    @ApiModelProperty(value = "指令节点数量", dataType = "Long")
    private Long instructionNodesNum;

    @ApiModelProperty(value = "变量操作数量", dataType = "Long")
    private Long variableOperationsNum;

    @ApiModelProperty(value = "子流程数量", dataType = "Long")
    private Long subprocessesNum;

    @ApiModelProperty(value = "联系人操作数量", dataType = "Long")
    private Long contactOperationsNum;

}
