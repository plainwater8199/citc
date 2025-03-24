package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/31 16:57
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ProcessQuantityScenceTopResp {

    @ApiModelProperty(value = "场景id", dataType = "Long")
    private Long robotSceneNodeId;

    @ApiModelProperty(value = "场景名称", dataType = "String")
    private String sceneName;

    @ApiModelProperty(value = "触发次数", dataType = "Long")
    private Long processTriggersSumNum;

}
