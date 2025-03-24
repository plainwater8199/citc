package com.citc.nce.misc.guide;

import com.citc.nce.misc.guide.req.SysGuideAddReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author jcrenc
 * @since 2024/11/7 9:11
 */
@FeignClient(value = "misc-service", contextId = "sysGuideApi", url = "${miscServer:}")
public interface SysGuideApi {
    @PostMapping("/sys/guide")
    void addGuide(@RequestBody @Valid SysGuideAddReq addReq);
}
