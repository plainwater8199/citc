package com.citc.nce.authcenter.largeModel.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LargeModelResp implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 模型id
     */
    @ApiModelProperty(value = "模型id", dataType = "Long")
    private Long id;

    /**
     * 大模型名称
     */
    @ApiModelProperty(value = "大模型名称", dataType = "String")
    private String modelName;

    /**
     * 大模型产品
     */
    @ApiModelProperty(value = "大模型产品", dataType = "Integer")
    private Integer modelProduct;

    /**
     * 大模型编码
     */
    @ApiModelProperty(value = "大模型编码", dataType = "Integer")
    private Integer modelCode;

    /**
     * 大模型api-key
     */
    @ApiModelProperty(value = "大模型api-key", dataType = "String")
    private String apiKey;

    /**
     * 大模型secret-key
     */
    @ApiModelProperty(value = "大模型secret-key", dataType = "String")
    private String secretKey;

    /**
     * 大模型api接口
     */
    @ApiModelProperty(value = "大模型api接口", dataType = "String")
    private String apiUrl;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
