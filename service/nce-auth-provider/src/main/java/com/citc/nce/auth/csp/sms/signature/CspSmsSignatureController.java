package com.citc.nce.auth.csp.sms.signature;


import com.citc.nce.auth.csp.sms.signature.service.CspSmsSignatureService;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureReq;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureResp;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureSubmitReq;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class CspSmsSignatureController implements CspSmsSignatureApi {

    @Resource
    private CspSmsSignatureService cspSmsSignatureService;

    @Override
    public List<CspSmsSignatureResp> getSignatureByAccountId(CspSmsSignatureReq req) {
        return cspSmsSignatureService.getSignatureByAccountId(req);
    }

    @Override
    public List<CspSmsSignatureResp> getSignatureByAccountIdsAndType(List<String> accountIds, Integer type) {
        return cspSmsSignatureService.getSignatureByAccountIdsAndType(accountIds, type);
    }

    @Override
    public int submit(CspSmsSignatureSubmitReq req) {
        return cspSmsSignatureService.submit(req);
    }

    @Override
    public List<CspSmsSignatureResp> getSignatureByIds(List<Long> ids) {
        return cspSmsSignatureService.getSignatureByIds(ids);
    }
}
