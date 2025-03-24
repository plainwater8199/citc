package com.citc.nce.auth.postpay;

import com.citc.nce.auth.postpay.config.vo.CustomerPostpayConfigVo;
import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @author jcrenc
 * @since 2024/3/6 16:45
 */
@Api(tags = "客户后付费配置api")
@FeignClient(value = "auth-service", contextId = "CustomerPostpayConfigApi", url = "${auth:}")
public interface CustomerPostpayConfigApi {

    @GetMapping("/postpay/config")
    CustomerPostpayConfigVo queryCustomerPostpayConfig(@RequestParam("customerId") String customerId);

    @PostMapping("/postpay/config/config")
    void config(@RequestBody @Valid CustomerPostpayConfigVo configVo);
}
