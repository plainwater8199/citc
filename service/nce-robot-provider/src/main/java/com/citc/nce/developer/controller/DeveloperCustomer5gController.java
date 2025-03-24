package com.citc.nce.developer.controller;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.developer.DeveloperCustomer5gApi;
import com.citc.nce.developer.service.DeveloperCustomer5gService;
import com.citc.nce.developer.vo.DeveloperCustomerInfoVo;
import com.citc.nce.developer.vo.DeveloperCustomerVo;
import com.citc.nce.developer.vo.DeveloperSaveCallbackUrlVo;
import com.citc.nce.developer.vo.SmsDeveloperSendSearchVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ping chen
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class DeveloperCustomer5gController implements DeveloperCustomer5gApi {
    private final DeveloperCustomer5gService developerCustomer5gService;

    /**
     * 获取详情
     * @return
     */
    @Override
    public DeveloperCustomerInfoVo queryInfo() {
        return developerCustomer5gService.queryInfo();
    }

    /**
     * 设置回调地址
     * @param developerSaveCallbackUrlVo
     */
    @Override
    public void saveCallbackUrl(DeveloperSaveCallbackUrlVo developerSaveCallbackUrlVo) {
        developerCustomer5gService.saveCallbackUrl(developerSaveCallbackUrlVo);
    }

    @Override
    public PageResult<DeveloperCustomerVo> searchDeveloperSend(SmsDeveloperSendSearchVo smsDeveloperSendSearchVo) {
        return developerCustomer5gService.searchDeveloperSend(smsDeveloperSendSearchVo);
    }
}
