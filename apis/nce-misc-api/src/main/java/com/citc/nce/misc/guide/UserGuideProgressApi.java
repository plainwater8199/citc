package com.citc.nce.misc.guide;

import com.citc.nce.misc.guide.req.UserGuideProgressUpdateReq;
import com.citc.nce.misc.guide.resp.UserGuideProgressResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author jcrenc
 * @since 2024/11/7 9:11
 */
@FeignClient(value = "misc-service", contextId = "userGuideProgressApi", url = "${miscServer:}")
public interface UserGuideProgressApi {
    @PostMapping("/user/guide/progress")
    void updateProgress(@RequestBody @Valid UserGuideProgressUpdateReq updateReq);

    @GetMapping("/user/guide")
    UserGuideProgressResp getProgresses();
}
