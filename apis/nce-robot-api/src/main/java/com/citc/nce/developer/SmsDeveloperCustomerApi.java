package com.citc.nce.developer;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.developer.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author ping chen
 */
@FeignClient(value = "rebot-service", contextId = "SmsDeveloperCustomer", url = "${robot:}")
public interface SmsDeveloperCustomerApi {

    @PostMapping("/sms/developer/customer/generate")
    void generate();

    @PostMapping("/sms/developer/customer/details/{customerId}")
    SmsDeveloperAuthVo details(@PathVariable("customerId") String customerId);

    @PostMapping("/sms/developer/customer/saveCallbackUrl")
    void saveCallbackUrl(@RequestBody @Valid SmsDeveloperAuthCallbackUrlVo smsDeveloperAuthCallbackUrlVo);

    @PostMapping("/sms/developer/customer/search/callback/list")
    PageResult<DeveloperCustomerVo> searchDeveloperSend(@RequestBody @Valid SmsDeveloperSendSearchVo smsDeveloperSendSearchVo);

    @PostMapping("/sms/developer/customer/setSwitch")
    void setSwitch(@RequestBody @Valid SmsDeveloperSwitchVo smsDeveloperSwitchVo);

    @PostMapping("/sms/developer/customer/list")
    PageResult<SmsDeveloperCustomerManagerVo> queryList(@RequestBody @Valid SmsDeveloperCustomerReqVo smsDeveloperCustomerReqVo);

    @PostMapping("/sms/developer/customer/customerOption")
    PageResult<DeveloperAccountVo> getSmsDeveloperCustomerOption();

}
