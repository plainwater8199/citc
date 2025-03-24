package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/28 15:22
 * @Version 1.0
 * @Description:
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SceneResp {
    @ApiModelProperty(value = "场景id", dataType = "Long")
    private Long robotSceneNodeId;
    @ApiModelProperty(value = "场景名称", dataType = "String")
    private String robotSceneNodeName;
}
