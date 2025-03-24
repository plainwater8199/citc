package com.citc.nce.misc.sms.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class SendSmsInfo {
    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(value = "发送目标手机号", dataType = "String", required = true)
    private String mobile;
    @NotBlank(message = "短信内容不能为空")
    @ApiModelProperty(value = "短信内容", dataType = "String", required = true)
    private String content;
}
