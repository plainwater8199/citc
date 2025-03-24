package com.citc.nce.authcenter.csp.multitenant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.authcenter.csp.customerLoginRecord.vo.CspCustomerLoginRecordReq;
import com.citc.nce.authcenter.csp.customerLoginRecord.vo.CspCustomerLoginRecordResp;
import com.citc.nce.authcenter.csp.multitenant.entity.CspCustomerLoginRecord;

import java.util.List;

/**
 * @author jiancheng
 */
public interface CspCustomerLoginRecordService extends IService<CspCustomerLoginRecord> {
    List<CspCustomerLoginRecordResp> queryList(CspCustomerLoginRecordReq req);
}
