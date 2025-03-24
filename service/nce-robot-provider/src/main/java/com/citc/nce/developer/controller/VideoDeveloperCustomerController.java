package com.citc.nce.developer.controller;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.developer.VideoDeveloperCustomerApi;
import com.citc.nce.developer.service.VideoDeveloperCustomerService;
import com.citc.nce.developer.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ping chen
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class VideoDeveloperCustomerController implements VideoDeveloperCustomerApi {
    private final VideoDeveloperCustomerService videoDeveloperCustomerService;


    @Override
    public void generate() {
        videoDeveloperCustomerService.generate();
    }

    @Override
    public VideoDeveloperAuthVo details(String customerId) {
        return videoDeveloperCustomerService.details(customerId);
    }

    @Override
    public void saveCallbackUrl(VideoDeveloperAuthCallbackUrlVo videoDeveloperAuthCallbackUrlVo) {
        videoDeveloperCustomerService.saveCallbackUrl(videoDeveloperAuthCallbackUrlVo);
    }

    @Override
    public PageResult<DeveloperCustomerVo> searchDeveloperSend(VideoDeveloperSendSearchVo videoDeveloperSendSearchVo) {
        return videoDeveloperCustomerService.searchDeveloperSend(videoDeveloperSendSearchVo);
    }

    @Override
    public void setSwitch(VideoDeveloperSwitchVo videoDeveloperSwitchVo) {
        videoDeveloperCustomerService.setSwitch(videoDeveloperSwitchVo);
    }

    @Override
    public PageResult<VideoDeveloperCustomerManagerVo> queryList(VideoDeveloperCustomerReqVo videoDeveloperCustomerReqVo) {
        return videoDeveloperCustomerService.queryList(videoDeveloperCustomerReqVo);
    }

    @Override
    public PageResult<DeveloperAccountVo> getVideoDeveloperCustomerOption() {
        return videoDeveloperCustomerService.getVideoDeveloperCustomerOption();
    }
}
