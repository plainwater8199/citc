package com.citc.nce.authcenter.largeModel.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class LargeModelCreateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 大模型名称
     */
    @ApiModelProperty(value = "大模型名称", dataType = "String")
    @NotBlank(message = "名称不能为空")
    @Length(max = 25, message = "大模型名称长度超过限制(最大25位)")
    private String modelName;

    /**
     * 大模型产品
     */
    @ApiModelProperty(value = "大模型产品", dataType = "Integer")
    @NotNull(message = "产品不能为空")
    private Integer modelProduct;

    /**
     * 大模型代码
     */
    @ApiModelProperty(value = "大模型代码", dataType = "Integer")
    @NotNull(message = "大模型不能为空")
    private Integer modelCode;

    /**
     * 大模型api-key
     */
    @ApiModelProperty(value = "大模型api-key", dataType = "String")
    @NotBlank(message = "API Key不能为空")
    @Length(max = 50, message = "API Key长度超过限制(最大50位)")
    private String apiKey;

    /**
     * 大模型secret-key
     */
    @ApiModelProperty(value = "大模型secret-key", dataType = "String")
    @NotBlank(message = "Secret Key不能为空")
    @Length(max = 50, message = "Secret Key长度超过限制(最大50位)")
    private String secretKey;

    /**
     * 大模型api地址
     */
    @ApiModelProperty(value = "大模型api地址", dataType = "String")
    @NotBlank(message = "API地址不能为空")
    @Length(max = 500, message = "API地址长度超过限制(最大500位)")
    private String apiUrl;
}
