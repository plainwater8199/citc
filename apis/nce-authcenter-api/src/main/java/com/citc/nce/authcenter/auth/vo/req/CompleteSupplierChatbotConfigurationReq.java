package com.citc.nce.authcenter.auth.vo.req;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;


/**
 * @Author: zhujy
 * @Date: 2024/3/17 14:34
 * @Version: 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class CompleteSupplierChatbotConfigurationReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * chatbot账号
     */
    private String chatbotAccountId;

    /**
     * 代理商id
     */
    @Size(max = 50, message = "代理商Id长度不能超过50")
    @NotBlank(message = "代理商Id不能为空")
    private String agentId;
    /**
     * chatbot服务提供商租户id
     */
    @Size(max = 50, message = "租户id长度不能超过50")
    @NotBlank(message = "租户id不能为空")
    private String ecId;

    /**
     * appid
     */
    @Size(max = 128, message = "应用Id长度不能超过128")
    @NotBlank(message = "应用Id不能为空")
    private String appId;

    /**
     * appkey
     */
    @Size(max = 50, message = "接入密钥长度不能超过50")
    @NotBlank(message = "接入密钥不能为空")
    private String appKey;

    /**
     * chatbot账号
     */
    @Size(max = 64, message = "chatbotID长度不能超过64")
    @NotBlank(message = "chatbotID不能为空")
    private String chatbotAccount;

    @Override
    public String toString() {
        return  "代理商ID='" + getAgentId() +";"+ '\'' +
                "租户ID='" + getEcId() +";" + '\'' +
                "应用ID='" + getAppId() +";"+ '\'' +
                "接入秘钥='" + getAppKey() +";"+ '\'' +
                "ChatbotID='" + getChatbotAccount()+";";
    }

}
