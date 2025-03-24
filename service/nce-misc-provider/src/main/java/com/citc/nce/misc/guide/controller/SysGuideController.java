package com.citc.nce.misc.guide.controller;

import com.citc.nce.misc.guide.SysGuideApi;
import com.citc.nce.misc.guide.req.SysGuideAddReq;
import com.citc.nce.misc.guide.service.SysGuideService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author jcrenc
 * @since 2024/11/6 17:39
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class SysGuideController implements SysGuideApi {
    private final SysGuideService sysGuideService;

    @PostMapping("/sys/guide")
    @Override
    public void addGuide(@RequestBody @Valid SysGuideAddReq addReq) {
        sysGuideService.add(addReq);
    }
}
