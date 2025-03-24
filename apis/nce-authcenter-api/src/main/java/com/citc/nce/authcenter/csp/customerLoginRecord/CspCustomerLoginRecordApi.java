package com.citc.nce.authcenter.csp.customerLoginRecord;

import com.citc.nce.authcenter.csp.customerLoginRecord.vo.CspCustomerLoginRecordReq;
import com.citc.nce.authcenter.csp.customerLoginRecord.vo.CspCustomerLoginRecordResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author jiancheng
 */
@FeignClient(value = "authcenter-service", contextId = "CspCustomerLoginRecord", url = "${authCenter:}")
public interface CspCustomerLoginRecordApi {

    @PostMapping("/csp/customerLoginRecord/queryList")
    List<CspCustomerLoginRecordResp> queryList(@RequestBody CspCustomerLoginRecordReq req);
}
