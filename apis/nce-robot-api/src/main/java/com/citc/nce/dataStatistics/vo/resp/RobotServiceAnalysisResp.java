package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/30 19:11
 * @Version 1.0
 * @Description:
 */
@Data
@ApiModel
@Accessors(chain = true)
public class RobotServiceAnalysisResp {
    @ApiModelProperty(value = "场景数量")
    private Long scenariosNum;

    @ApiModelProperty(value = "流程数量")
    private Long processesNum;

    @ApiModelProperty(value = "关联5G消息账号数量")
    private Long associatedMessageAccounts;

    @ApiModelProperty(value = "终端用户量")
    private Long terminalUsersNum;

}
