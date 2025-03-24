package com.citc.nce.auth.csp.customer;

import com.citc.nce.auth.csp.customer.service.CspCustomerService;
import com.citc.nce.auth.csp.customer.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>csp-客户管理</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:27
 */
@RestController
@Slf4j
public class CspCustomerController implements CustomerApi {

    @Autowired
    private CspCustomerService cspCustomerService;


    @Override
    @NotNull
    @PostMapping("/csp/customer/queryList")
    public PageResult<CustomerResp> queryList(@RequestBody CustomerReq req) {
        return cspCustomerService.queryList(req);
    }

    @Override
    @NotNull
    @PostMapping("/csp/customer/save")
    public int save(@RequestBody @Valid CustomerSaveReq req) {
        return cspCustomerService.save(req);
    }

    @Override
    @NotNull
    @PostMapping("/csp/customer/getDetailByUserId")
    public CustomerGetDetailResp getDetailByUserId(@RequestBody @Valid CustomerGetDetailReq req) {
        return cspCustomerService.getDetailByUserId(req.getUserId());
    }


    @Override
    @NotNull
    @PostMapping("/csp/customer/updateCspActive")
    public int updateCspActive(@RequestBody @Valid CustomerActiveUpdateReq req){
        return cspCustomerService.updateCspActive(req);
    }

    @Override
    public List<CustomerProvinceResp> queryUserProvince() {
        return cspCustomerService.queryUserProvince();
    }


    @PostMapping("/csp/customer/queryCspId")
    @Override
    public String queryCspId(String customerId) {
        return cspCustomerService.queryCspId(customerId);
    }
}
