package com.citc.nce.dataStatistics.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/28 15:28
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class SceneIdReq {
    @ApiModelProperty(value = "场景id", dataType = "Long", required = true)
    private Long robotSceneNodeId;
}
