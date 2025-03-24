package com.citc.nce.developer;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.developer.vo.Developer5gApplicationNameVo;
import com.citc.nce.developer.vo.Developer5gApplicationVo;
import com.citc.nce.developer.vo.DeveloperAccountVo;
import com.citc.nce.developer.vo.DeveloperCustomer5gManagerVo;
import com.citc.nce.developer.vo.DeveloperQueryApplicationVo;
import com.citc.nce.developer.vo.DeveloperSetStateApplicationVo;
import com.citc.nce.developer.vo.SmsDeveloperCustomerReqVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author ping chen
 */
@FeignClient(value = "rebot-service", contextId = "DeveloperCustomer5gApplicationApi", url = "${robot:}")
public interface DeveloperCustomer5gApplicationApi {

    @PostMapping("/developer/customer/5g/application/save")
    void saveApplication(@RequestBody @Valid Developer5gApplicationVo developerSaveApplicationVo);

    @PostMapping("/developer/customer/5g/application/edit")
    void editApplication(@RequestBody @Valid Developer5gApplicationVo developerSaveApplicationVo);

    @PostMapping("/developer/customer/5g/application/delete/{uniqueId}")
    void deleteApplication(@PathVariable("uniqueId") String uniqueId);

    @PostMapping("/developer/customer/5g/application/setState")
    void setStateApplication(@RequestBody @Valid DeveloperSetStateApplicationVo developerSetStateApplicationVo);

    @PostMapping("/developer/customer/5g/application/queryApplicationInfo/{uniqueId}")
    Developer5gApplicationVo queryApplicationInfo(@PathVariable("uniqueId") String uniqueId);

    @PostMapping("/developer/customer/5g/application/queryApplicationList")
    PageResult<Developer5gApplicationVo> queryApplicationList(@RequestBody @Valid DeveloperQueryApplicationVo developerQueryApplicationVo);

    @PostMapping("/developer/customer/5g/application/queryList")
    PageResult<DeveloperCustomer5gManagerVo> queryList(@RequestBody @Valid SmsDeveloperCustomerReqVo smsDeveloperCustomerReqVo);

    @PostMapping("/developer/customer/5g/application/customerOption")
    PageResult<DeveloperAccountVo> get5gDeveloperCustomerOption();

    @PostMapping("/developer/customer/5g/application/queryApplicationNameWithoutLogin")
    List<Developer5gApplicationNameVo> queryApplicationNameWithoutLogin(@RequestBody List<Long> ids);
}
