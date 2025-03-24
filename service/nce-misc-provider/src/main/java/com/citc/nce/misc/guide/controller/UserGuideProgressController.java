package com.citc.nce.misc.guide.controller;

import com.citc.nce.misc.guide.UserGuideProgressApi;
import com.citc.nce.misc.guide.req.UserGuideProgressUpdateReq;
import com.citc.nce.misc.guide.resp.UserGuideProgressResp;
import com.citc.nce.misc.guide.service.UserGuideProgressService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author jcrenc
 * @since 2024/11/6 17:51
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserGuideProgressController implements UserGuideProgressApi {
    private final UserGuideProgressService userGuideProgressService;

    @PostMapping("/user/guide/progress")
    @Override
    public void updateProgress(@RequestBody @Valid UserGuideProgressUpdateReq updateReq) {
        userGuideProgressService.updateProgress(updateReq);
    }

    @Override
    @GetMapping("/user/guide")
    public UserGuideProgressResp getProgresses() {
        return userGuideProgressService.getProgress();
    }
}
