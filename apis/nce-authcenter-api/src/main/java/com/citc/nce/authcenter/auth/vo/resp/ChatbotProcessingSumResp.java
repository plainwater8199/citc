package com.citc.nce.authcenter.auth.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChatbotProcessingSumResp {
    @ApiModelProperty(value = "待处理chatbot申请", dataType = "Long")
    private Long chatbotSum;
}
