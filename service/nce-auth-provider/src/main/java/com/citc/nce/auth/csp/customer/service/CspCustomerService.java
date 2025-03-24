package com.citc.nce.auth.csp.customer.service;

import com.citc.nce.auth.csp.customer.vo.*;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;

/**
 * <p>csp-客户管理</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:32
 */
public interface CspCustomerService {

    PageResult<CustomerResp> queryList(CustomerReq req);

    int save(CustomerSaveReq req);

    CustomerGetDetailResp getDetailByUserId(String userId);


    int updateCspActive(CustomerActiveUpdateReq req);

    List<CustomerProvinceResp> queryUserProvince();

    String queryCspId(String customerId);
}
