package com.citc.nce.auth.csp.customer;

import com.citc.nce.auth.csp.customer.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:47
 */
@FeignClient(value = "auth-service", contextId = "CSPCustomer", url = "${auth:}")
public interface CustomerApi {
    /**
     * 列表查询
     *
     * @param req
     * @return list
     */
    @PostMapping("/csp/customer/queryList")
    @NotNull
    PageResult<CustomerResp> queryList(@RequestBody CustomerReq req);

    @PostMapping("/csp/customer/save")
    @NotNull
    int save(@RequestBody @Valid CustomerSaveReq req);

    @NotNull
    @PostMapping("/csp/customer/getDetailByUserId")
    CustomerGetDetailResp getDetailByUserId(@RequestBody CustomerGetDetailReq req);


    @NotNull
    @PostMapping("/csp/customer/updateCspActive")
    int updateCspActive(@RequestBody CustomerActiveUpdateReq req);
    @PostMapping("/csp/customer/queryUserProvince")
    List<CustomerProvinceResp> queryUserProvince();


    @PostMapping("/csp/customer/queryCspId")
    String queryCspId(@RequestBody String customerId);

}
