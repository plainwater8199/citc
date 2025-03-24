package com.citc.nce.developer.service.impl;

import com.citc.nce.developer.service.DeveloperCustomer5gApplicationService;
import com.citc.nce.developer.service.SmsDeveloperCustomerService;
import com.citc.nce.developer.service.VideoDeveloperCustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 启动完成后处理开发者应用appSecret
 * @author bydud
 * @since 2024/4/26
 */
@Component
@Slf4j
public class DeveloperPostData implements ApplicationRunner {
    @Autowired
    private DeveloperCustomer5gApplicationService nrMessage;
    @Autowired
    private SmsDeveloperCustomerService sms;
    @Autowired
    private VideoDeveloperCustomerService videoSms;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        nrMessage.appSecretEncode();
        sms.appSecretEncode();
        videoSms.appSecretEncode();
    }


}
