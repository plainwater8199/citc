package com.citc.nce.authcenter.largeModel.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PromptDetailResp implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * promptId
     */
    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    /**
     * 模型Id
     */
    @ApiModelProperty(value = "模型Id", dataType = "Long")
    private Long modelId;

    /**
     * 模型Id
     */
    @ApiModelProperty(value = "模型名称", dataType = "String")
    private String modelName;

    /**
     * prompt设定
     */
    @ApiModelProperty(value = "prompt设定", dataType = "String")
    private String promptSetting;

    /**
     * prompt规则&格式
     */
    @ApiModelProperty(value = "prompt规则&格式", dataType = "String")
    private String promptRule;

    /**
     * prompt示例
     */
    @ApiModelProperty(value = "prompt示例", dataType = "String")
    private String promptExample;
}
