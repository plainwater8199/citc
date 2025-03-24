package com.citc.nce.dataStatistics.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/28 11:24
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class SceneAndProcessReq extends StartAndEndTimeReq {
    @ApiModelProperty(value = "场景id", dataType = "Long", required = false)
    private Long robotSceneNodeId;

    @ApiModelProperty(value = "流程id", dataType = "Long", required = false)
    private Long robotProcessSettingNodeId;

    @ApiModelProperty(value = "平台类型", dataType = "Integer", required = false)
    private Integer operatorType;

    @ApiModelProperty(value = "chatBotId", dataType = "String", required = false)
    private String chatBotId;
}
