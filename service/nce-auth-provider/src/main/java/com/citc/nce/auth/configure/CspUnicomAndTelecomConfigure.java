package com.citc.nce.auth.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "unicomandtelecom")
public class CspUnicomAndTelecomConfigure {
    private String contractScheduleCron;
    private String baseUnicomUrl;
    private String baseTelecomUrl;
    private String accessTokenUrl;
    private String addCspCustomerUrl;
    private String editCspCustomerUrl;
    private String deleteCspCustomerUrl;
    private String addChatBotUrl;
    private String updateChatBotUrl;
    private String deleteChatBotUrl;
    private String isOnlineUpdateUrl;
    private String updateDeveloperUrl;
    private String whiteListPhoneUrl;
    private String uploadContractFileUrl;
    private String uploadChatBotFileUrl;
    private String quitTestStatusUrl;
    private String getFileUrl;
    private String ipWhiteList;
    private String gatewayCallbackUrl;

    private String serverRootMessageUrl;
    private String serverRootMediaUrl;
}
