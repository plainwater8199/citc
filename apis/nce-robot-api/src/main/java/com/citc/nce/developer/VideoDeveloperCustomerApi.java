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
@FeignClient(value = "rebot-service", contextId = "VideoDeveloperCustomer", url = "${robot:}")
public interface VideoDeveloperCustomerApi {

    @PostMapping("/video/developer/customer/generate")
    void generate();

    @PostMapping("/video/developer/customer/details/{customerId}")
    VideoDeveloperAuthVo details(@PathVariable("customerId") String customerId);

    @PostMapping("/video/developer/customer/saveCallbackUrl")
    void saveCallbackUrl(@RequestBody @Valid VideoDeveloperAuthCallbackUrlVo videoDeveloperAuthCallbackUrlVo);

    @PostMapping("/video/developer/customer/search/callback/list")
    PageResult<DeveloperCustomerVo> searchDeveloperSend(@RequestBody @Valid VideoDeveloperSendSearchVo videoDeveloperSendSearchVo);

    @PostMapping("/video/developer/customer/setSwitch")
    void setSwitch(@RequestBody @Valid VideoDeveloperSwitchVo videoDeveloperSwitchVo);

    @PostMapping("/video/developer/customer/list")
    PageResult<VideoDeveloperCustomerManagerVo> queryList(@RequestBody @Valid VideoDeveloperCustomerReqVo videoDeveloperCustomerReqVo);

    @PostMapping("/video/developer/customer/customerOption")
    PageResult<DeveloperAccountVo> getVideoDeveloperCustomerOption();

}
