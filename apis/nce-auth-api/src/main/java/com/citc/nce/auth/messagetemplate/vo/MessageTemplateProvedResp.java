package com.citc.nce.auth.messagetemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: zhujy
 * @Date: 2024/3/19 16:23
 * @Version: 1.0
 * @Description:
 */
@Data
public class MessageTemplateProvedResp extends MessageTemplateResp {

    /**
     * 送审平台返回的id
     */
    @ApiModelProperty("送审平台返回的id")
    private String platformTemplateId;

}
