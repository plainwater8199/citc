package com.citc.nce.misc.email.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @authoer:ldy
 * @createDate:2022/7/4 0:40
 * @description:
 */
@Data
public class EmailSendReq {

    /**
     * 邮件模板code
     */
    @NotNull
    @ApiModelProperty(value = "邮件模板code", dataType = "String", required = true)
    private String templateCode;

    /**
     * 邮件模板需要替换的参数map，可以为null
     * 模板内容中提替换的参数用{user}形式表示，
     * 例如：
     * 模板内容 你请求的验证码是{code}
     * 则参数出传递
     * "code" --> 123123
     * templateParam=new HashMap();
     * templateParam.put("code",123123)
     */
    @NotEmpty
    @ApiModelProperty(value = "邮件模板参数", dataType = "Map", required = true)
    private Map<String, Object> templateParam;

    @NotEmpty
    @ApiModelProperty(value = "邮件账户", dataType = "Array", required = true)
    private String[] targetMailAccounts;
}
