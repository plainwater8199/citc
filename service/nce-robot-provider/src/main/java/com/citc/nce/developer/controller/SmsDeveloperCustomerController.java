package com.citc.nce.developer.controller;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.developer.SmsDeveloperCustomerApi;
import com.citc.nce.developer.service.SmsDeveloperCustomerService;
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
public class SmsDeveloperCustomerController implements SmsDeveloperCustomerApi {
    private final SmsDeveloperCustomerService smsDeveloperCustomerService;

    /**
     * 鉴权信息生成
     * @return
     */
    @Override
    public void generate() {
         smsDeveloperCustomerService.generate();
    }

    @Override
    public SmsDeveloperAuthVo details(String customerId) {
        return smsDeveloperCustomerService.details(customerId);
    }

    /**
     * 设置回调地址
     * @param smsDeveloperAuthCallbackUrlVo
     */
    @Override
    public void saveCallbackUrl(SmsDeveloperAuthCallbackUrlVo smsDeveloperAuthCallbackUrlVo) {
        smsDeveloperCustomerService.saveCallbackUrl(smsDeveloperAuthCallbackUrlVo);
    }

    /**
     * 回调明细列表查询
     * @param smsDeveloperSendSearchVo
     * @return
     */
    @Override
    public PageResult<DeveloperCustomerVo> searchDeveloperSend(SmsDeveloperSendSearchVo smsDeveloperSendSearchVo) {
        return smsDeveloperCustomerService.searchDeveloperSend(smsDeveloperSendSearchVo);
    }

    /**
     * 禁用/启用
     * @param smsDeveloperSwitchVo
     */
    @Override
    public void setSwitch(SmsDeveloperSwitchVo smsDeveloperSwitchVo) {
        smsDeveloperCustomerService.setSwitch(smsDeveloperSwitchVo);
    }

    @Override
    public PageResult<SmsDeveloperCustomerManagerVo> queryList(SmsDeveloperCustomerReqVo smsDeveloperCustomerReqVo) {
        return smsDeveloperCustomerService.queryList(smsDeveloperCustomerReqVo);
    }

    @Override
    public PageResult<DeveloperAccountVo> getSmsDeveloperCustomerOption() {
        return smsDeveloperCustomerService.getSmsDeveloperCustomerOption();
    }

}
