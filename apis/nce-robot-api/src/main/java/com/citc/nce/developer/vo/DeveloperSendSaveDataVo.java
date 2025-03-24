package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author ping chen
 */
@Data
public class DeveloperSendSaveDataVo {

    @ApiModelProperty("消息Id")
    @NotBlank(message = "消息Id不能为空")
    private String messageId;

    @ApiModelProperty("状态")
    @NotBlank(message = "状态不能为空")
    private String status;

    @ApiModelProperty("电话号码")
    @NotBlank(message = "电话号码不能为空")
    private String phoneNum;
}
