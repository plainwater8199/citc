package com.citc.nce.auth.csp.videoSms.signature.service;

import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureReq;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureResp;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureSaveReq;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureSubmitReq;

import java.util.List;

/**
 * <p></p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:32
 */
public interface CspVideoSmsSignatureService {

    List<CspVideoSmsSignatureResp> getSignatureByAccountId(CspVideoSmsSignatureReq req);

    List<CspVideoSmsSignatureResp> getSignatureByAccountIdsAndType(List<String> accountIds, Integer type);

    int submit(CspVideoSmsSignatureSubmitReq req);

    List<CspVideoSmsSignatureResp> getSignatureByIds(List<Long> ids);
}
