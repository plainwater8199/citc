package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/28 15:25
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ProcessResp {
    @ApiModelProperty(value = "流程id", dataType = "Long")
    private Long robotProcessSettingNodeId;
    @ApiModelProperty(value = "流程名称", dataType = "String")
    private String robotProcessSettingNodeName;
}
