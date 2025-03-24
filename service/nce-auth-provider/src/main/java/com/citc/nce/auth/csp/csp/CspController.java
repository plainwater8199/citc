package com.citc.nce.auth.csp.csp;

import com.citc.nce.auth.csp.csp.service.CspService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class CspController implements CspApi {
    @Resource
    private CspService cspService;

    @Override
    public String obtainCspId(String userId) {
        return cspService.obtainCspId(userId);
    }
}
