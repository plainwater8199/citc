package com.citc.nce.auth.messagetemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 16:23
 * @Version: 1.0
 * @Description:
 */
@Data
public class MessageTemplateButtonReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 按钮id
     */
    @ApiModelProperty("按钮id")
    @NotBlank(message = "按钮id不能为空")
    private String buttonId;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    @NotBlank(message = "创建者不能为空")
    private String creator;
}
