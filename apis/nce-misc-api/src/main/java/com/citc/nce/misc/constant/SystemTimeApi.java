package com.citc.nce.misc.constant;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/24 20:50
 * @Version 1.0
 * @Description:
 */
@FeignClient(value = "misc-service", contextId = "systemTimeApi", url = "${miscServer:}")
public interface SystemTimeApi {

    /**
     * 获取时间戳
     */
    @PostMapping("/getSystemTime")
    Long getSystemTime();
}
