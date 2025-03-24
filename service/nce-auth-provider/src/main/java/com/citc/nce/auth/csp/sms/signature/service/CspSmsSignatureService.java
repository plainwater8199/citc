package com.citc.nce.auth.csp.sms.signature.service;

import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureReq;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureResp;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureSubmitReq;

import java.util.List;

public interface CspSmsSignatureService {
    
    List<CspSmsSignatureResp> getSignatureByIds(List<Long> ids);

    List<CspSmsSignatureResp> getSignatureByIdsDelete(List<Long> ids);

    int submit(CspSmsSignatureSubmitReq req);

    List<CspSmsSignatureResp> getSignatureByAccountIdsAndType(List<String> accountIds, Integer type);

    List<CspSmsSignatureResp> getSignatureByAccountId(CspSmsSignatureReq req);
}
