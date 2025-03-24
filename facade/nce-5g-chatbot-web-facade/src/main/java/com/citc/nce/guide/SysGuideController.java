package com.citc.nce.guide;

import com.citc.nce.misc.guide.SysGuideApi;
import com.citc.nce.misc.guide.req.SysGuideAddReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author jcrenc
 * @since 2024/11/7 9:21
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Api(value = "sysGuideController", tags = "系统引导")
public class SysGuideController {
    private final SysGuideApi sysGuideApi;

    @PostMapping("/sys/guide")
    @ApiOperation("新增引导")
    public void addGuide(@RequestBody @Valid SysGuideAddReq addReq) {
        sysGuideApi.addGuide(addReq);
    }
}
