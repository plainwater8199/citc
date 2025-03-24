package com.citc.nce.auth.csp.chatbot.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ChatbotNewPermissionReq  implements Serializable {
    // 客户编码
    @NotBlank(message = "客户编码不能为空")
    private String customerId;

    // 运营商编码
    @NotNull(message = "运营商编码不能为空")
    private Integer operatorCode;
}
