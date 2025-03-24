package com.citc.nce.auth.csp.videoSms.signature;

import com.citc.nce.auth.csp.videoSms.signature.service.CspVideoSmsSignatureService;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureReq;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureResp;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureSubmitReq;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/8/15 14:51
 */
@RestController
public class CspVideoSmsSignatureController implements CspVideoSmsSignatureApi{

    @Autowired
    CspVideoSmsSignatureService service;

    @Override
    public List<CspVideoSmsSignatureResp> getSignatureByAccountId(CspVideoSmsSignatureReq req) {
        return service.getSignatureByAccountId(req);
    }

    @Override
    public List<CspVideoSmsSignatureResp> getSignatureByAccountIdsAndType(List<String> accountIds, Integer type) {
        return service.getSignatureByAccountIdsAndType(accountIds, type);
    }

    @Override
    public int submit(CspVideoSmsSignatureSubmitReq req) {
        return service.submit(req);
    }

    @Override
    public List<CspVideoSmsSignatureResp> getSignatureByIds(List<Long> ids) {
        return service.getSignatureByIds(ids);
    }
}
