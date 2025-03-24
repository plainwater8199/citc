package com.citc.nce.auth.csp.smsTemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class SmsTemplateCheckVo {
    @ApiModelProperty("模板ID")
    private Long id;

    @ApiModelProperty("校验通过：0 有执行计划正在使用：1")
    private Integer result;
}
