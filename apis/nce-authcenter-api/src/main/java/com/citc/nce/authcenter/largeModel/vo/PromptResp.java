package com.citc.nce.authcenter.largeModel.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PromptResp implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * promptId
     */
    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    /**
     * 节点类型
     */
    @ApiModelProperty(value = "type", dataType = "Long")
    private Integer type;

    /**
     * 模型Id
     */
    @ApiModelProperty(value = "模型Id", dataType = "Long")
    private Long modelId;

    /**
     * 模型名称
     */
    @ApiModelProperty(value = "模型名称", dataType = "String")
    private String modelName;

    /**
     * 模型产品
     */
    @ApiModelProperty(value = "模型产品", dataType = "Integer")
    private Integer modelProduct;

    /**
     * 模型编码
     */
    @ApiModelProperty(value = "模型编码", dataType = "Integer")
    private Integer modelCode;

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

    /**
     * prompt状态
     */
    @ApiModelProperty(value = "prompt状态", dataType = "Integer")
    private Integer status;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
