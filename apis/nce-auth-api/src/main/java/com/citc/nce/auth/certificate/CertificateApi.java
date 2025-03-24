package com.citc.nce.auth.certificate;

import com.citc.nce.auth.constant.CountNum;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/7/14 17:05
 * @Version 1.0
 * @Description:
 */
@FeignClient(value = "auth-service", contextId = "CertificateApi", url = "${auth:}")
public interface CertificateApi {

    /**
     * 查询用户标签申请未处理数量
     *
     * @return {@link CountNum}
     */
    @PostMapping("/certificate/getPendingReviewNum")
    CountNum getPendingReviewNum();
}
