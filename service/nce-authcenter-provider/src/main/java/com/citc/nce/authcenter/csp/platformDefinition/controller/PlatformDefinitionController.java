package com.citc.nce.authcenter.csp.platformDefinition.controller;

import com.citc.nce.authcenter.csp.PlatformDefinitionApi;
import com.citc.nce.authcenter.csp.platformDefinition.service.ICspPlatformDefinitionService;
import com.citc.nce.authcenter.csp.vo.resp.PlatformDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * bydud
 * 2024/1/25
 **/

@RestController
@Slf4j
public class PlatformDefinitionController implements PlatformDefinitionApi {

    @Autowired
    private ICspPlatformDefinitionService definitionService;

    @Override
    public PlatformDefinition platformDefinition(String cspId) {
        return definitionService.platformDefinition(cspId);
    }

    @Override
    public void updatePlatformDefinition(PlatformDefinition platformDefinition) {
        definitionService.updatePlatformDefinition(platformDefinition);
    }
}
