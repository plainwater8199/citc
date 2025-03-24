package com.citc.nce.developer;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.developer.vo.DeveloperCustomerInfoVo;
import com.citc.nce.developer.vo.DeveloperCustomerVo;
import com.citc.nce.developer.vo.DeveloperSaveCallbackUrlVo;
import com.citc.nce.developer.vo.SmsDeveloperSendSearchVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author ping chen
 */
@FeignClient(value = "rebot-service", contextId = "DeveloperCustomer5g", url = "${robot:}")
public interface DeveloperCustomer5gApi {

    @PostMapping("/developer/customer/5g/queryInfo")
    DeveloperCustomerInfoVo queryInfo();

    @PostMapping("/developer/customer/5g/saveCallbackUrl")
    void saveCallbackUrl(@RequestBody @Valid DeveloperSaveCallbackUrlVo developerSaveCallbackUrlVo);

    @PostMapping("/developer/customer/5g/search/callbacks")
    PageResult<DeveloperCustomerVo> searchDeveloperSend(@RequestBody @Valid SmsDeveloperSendSearchVo smsDeveloperSendSearchVo);
}
