package com.citc.nce.auth.certificate;


import com.citc.nce.auth.certificate.service.CertificateOptionsService;
import com.citc.nce.auth.constant.CountNum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/07/14
 * @Version 1.0
 * @Description:
 */
@RestController
@Slf4j
public class CertificateController implements CertificateApi {

    @Resource
    private CertificateOptionsService certificateOptionsService;

    @Override
    @PostMapping("/certificate/getPendingReviewNum")
    public CountNum getPendingReviewNum() {
        return certificateOptionsService.getPendingReviewNum();
    }
}
