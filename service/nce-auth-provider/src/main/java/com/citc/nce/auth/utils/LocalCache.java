package com.citc.nce.auth.utils;


import com.citc.nce.auth.certificate.constant.Constant;
import com.citc.nce.auth.certificate.entity.UserCertificateDo;
import com.citc.nce.auth.certificate.service.UserCertificateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class LocalCache {

    public static final Map<String, UserCertificateDo> userCertificateCacheMap = new ConcurrentHashMap<>();

    @Autowired
    private UserCertificateService userCertificateService;

    @PostConstruct
    public void init() {
        log.info("启动启动，初始化加载userCertificate 信息");
        List<UserCertificateDo> list = userCertificateService.getUserCertificate();
        if (!CollectionUtils.isEmpty(list)) {
            for (UserCertificateDo uc : list) {
                userCertificateCacheMap.put(String.format(Constant.USER_CERTIFICATE_ID, uc.getId()), uc);
            }
        }

    }


}
