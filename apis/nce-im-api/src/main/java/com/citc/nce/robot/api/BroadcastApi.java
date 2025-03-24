package com.citc.nce.robot.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author jcrenc
 * @since 2024/4/22 9:16
 */
@FeignClient(value = "im-service", contextId = "broadcastApi", url = "${im:}")
public interface BroadcastApi {
    @RequestMapping("/broadcast/start")
    String start(@RequestParam("id") Long id);

    @RequestMapping("/broadcast/stop")
    String stop(@RequestParam("id") Long id);
}
