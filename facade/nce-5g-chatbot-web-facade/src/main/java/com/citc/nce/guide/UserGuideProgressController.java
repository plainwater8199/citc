package com.citc.nce.guide;

import com.citc.nce.misc.guide.UserGuideProgressApi;
import com.citc.nce.misc.guide.req.UserGuideProgressUpdateReq;
import com.citc.nce.misc.guide.resp.UserGuideProgressResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author jcrenc
 * @since 2024/11/7 9:26
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Api(value = "userGuideProgressController", tags = "用户引导进度")
public class UserGuideProgressController {
    private final UserGuideProgressApi userGuideProgressApi;

    @PostMapping("/user/guide/progress")
    @ApiOperation("更新进度")
    public void updateProgress(@RequestBody @Valid UserGuideProgressUpdateReq updateReq) {
        userGuideProgressApi.updateProgress(updateReq);
    }

    @GetMapping("/user/guide")
    @ApiOperation("获取全部引导进度")
    public UserGuideProgressResp getProgresses() {
        return userGuideProgressApi.getProgresses();
    }
}
