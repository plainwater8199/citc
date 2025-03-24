package com.citc.nce.auth.csp.smsTemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

/**
 * @author ping chen
 */
@Data
public class SmsTemplateSendVo {
    @ApiModelProperty("模板Id")
    private String templateId;

    @ApiModelProperty("平台模板Id")
    private String platformTemplateId;

    @ApiModelProperty("个性模板变量，多个变量逗号分隔，最多5个")
    private String variable;

    private List<SmsDeveloperSendPhoneVo> smsDeveloperSendPhoneVoList;
}
