package com.citc.nce.auth.postpay.customerconfig.controller;

import com.citc.nce.auth.postpay.CustomerPostpayConfigApi;
import com.citc.nce.auth.postpay.config.vo.CustomerPostpayConfigVo;
import com.citc.nce.auth.postpay.customerconfig.service.CustomerPostpayConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author jcrenc
 * @since 2024/3/6 16:47
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class CustomerPostpayConfigController implements CustomerPostpayConfigApi {
    private final CustomerPostpayConfigService postpayConfigService;

    @Override
    public CustomerPostpayConfigVo queryCustomerPostpayConfig(String customerId) {
        return postpayConfigService.queryCustomerPostpayConfig(customerId);
    }

    @Override
    public void config(CustomerPostpayConfigVo configVo) {
        postpayConfigService.config(configVo);
    }
}
