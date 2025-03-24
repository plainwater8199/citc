package com.citc.nce.auth.mobile.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MenuRequest extends BaseRequest{

    private String chatbotId;

    /**
     * Chatbot固定菜单Json内容，采用Base64编码，编码前的内容长度不超过2048字节
     * 需按照唯一性规则填写菜单（建议操作/建议回复）中包含的postback.data，可采用UUID
     * 支持2级3*5菜单
     */
    private String menu;

}
