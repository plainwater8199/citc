package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author ping chen
 */
@Data
public class DeveloperSend5gSaveDataVo {

    @ApiModelProperty("消息Id")
    @NotBlank(message = "消息Id不能为空")
    private String messageId;

    @ApiModelProperty("状态")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @ApiModelProperty("电话号码")
    private String phoneNum;
}
