package com.citc.nce.authcenter.csp.multitenant.apiImpl;

import com.citc.nce.authcenter.csp.customerLoginRecord.CspCustomerLoginRecordApi;
import com.citc.nce.authcenter.csp.customerLoginRecord.vo.CspCustomerLoginRecordReq;
import com.citc.nce.authcenter.csp.customerLoginRecord.vo.CspCustomerLoginRecordResp;
import com.citc.nce.authcenter.csp.multitenant.service.CspCustomerLoginRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/10/25 10:41
 */
@RestController
@Slf4j
@RequestMapping("/csp/customerLoginRecord")
public class CspCustomerLoginRecordController implements CspCustomerLoginRecordApi {

    @Autowired
    CspCustomerLoginRecordService service;

    @Override
    public List<CspCustomerLoginRecordResp> queryList(CspCustomerLoginRecordReq req) {
        return service.queryList(req);
    }
}
