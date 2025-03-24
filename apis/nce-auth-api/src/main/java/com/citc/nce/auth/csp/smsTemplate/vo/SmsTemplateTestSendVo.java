package com.citc.nce.auth.csp.smsTemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author ping chen
 */
@Data
public class SmsTemplateTestSendVo {
    @ApiModelProperty("模板Id")
    @NotBlank(message = "模板Id不能为空")
    private String templateId;

    @ApiModelProperty("个性模板变量，多个变量逗号分隔，最多5个")
    private String variable;

    @ApiModelProperty("电话号码")
    @NotBlank(message = "电话号码不能为空")
    private String phone;
}
