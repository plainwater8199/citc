package com.citc.nce.authcenter.auth.vo.resp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * @Author: zhujy
 * @Date: 2024/3/17 14:34
 * @Version: 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class SupplierChatbotConfigurationResp implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * chatbot账号
     */
    private String chatbotAccountId;

    /**
     * 代理商id
     */
    private String agentId;
    /**
     * chatbot服务提供商租户id
     */
    private String ecId;

    /**
     * appid
     */
    private String appId;

    /**
     * appkey
     */
    private String appKey;

    /**
     * chatbot账号
     */
    private String chatbotAccount;

}
