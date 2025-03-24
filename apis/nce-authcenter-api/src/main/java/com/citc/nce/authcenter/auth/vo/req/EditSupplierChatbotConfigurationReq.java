package com.citc.nce.authcenter.auth.vo.req;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * @Author: zhujy
 * @Date: 2024/3/17 14:34
 * @Version: 1.0
 * @Description: 编辑
 */
@Data
@Accessors(chain = true)
public class EditSupplierChatbotConfigurationReq extends CompleteSupplierChatbotConfigurationReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return  "代理商ID='" + getAgentId() +";"+ '\'' +
                "租户ID='" + getEcId() +";" + '\'' +
                "应用ID='" + getAppId() +";"+ '\'' +
                "接入秘钥='" + getAppKey() +";"+ '\'' +
                "ChatbotID='" + getChatbotAccount()+";";
    }
}
