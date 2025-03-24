package com.citc.nce.authcenter.largeModel.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class PromptUpdateReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * promptId
     */
    @ApiModelProperty(value = "id", dataType = "Long")
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 模型Id
     */
    @ApiModelProperty(value = "模型Id", dataType = "Long")
    @NotNull(message = "绑定大模型不能为空")
    private Long modelId;

    /**
     * prompt设定
     */
    @ApiModelProperty(value = "prompt设定", dataType = "String")
    @Length(max = 500, message = "设定长度超过限制(最大500位)")
    private String promptSetting;

    /**
     * prompt规则&格式
     */
    @ApiModelProperty(value = "prompt规则&格式", dataType = "String")
    @Length(max = 500, message = "规则&格式长度超过限制(最大500位)")
    private String promptRule;

    /**
     * prompt示例
     */
    @ApiModelProperty(value = "prompt示例", dataType = "String")
    @Length(max = 500, message = "示例长度超过限制(最大500位)")
    private String promptExample;
}
