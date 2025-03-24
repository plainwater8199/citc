package com.citc.nce.auth.csp.sms.signature;

import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureReq;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureResp;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureSubmitReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "auth-service", contextId = "CspSmsSignatureApi", url = "${auth:}")
public interface CspSmsSignatureApi {

    /**
     * 列表查询
     *
     * @param req
     * @return list
     */
    @PostMapping("/csp/sms/signature/getSignatureByAccountId")
    List<CspSmsSignatureResp> getSignatureByAccountId(@RequestBody CspSmsSignatureReq req);

    /**
     * getSignatureByAccountIdsAndType
     * @param accountIds
     * @param type 0:视频短信 1:其他
     * @return List
     * @author zy.qiu
     * @createdTime 2023/8/18 9:28
     */
    @PostMapping("/csp/sms/signature/getSignatureByAccountIdsAndType")
    List<CspSmsSignatureResp> getSignatureByAccountIdsAndType(@RequestParam("accountIds") List<String> accountIds, @RequestParam("type") Integer type);


    @PostMapping("/csp/sms/signature/submit")
    int submit(@RequestBody @Valid CspSmsSignatureSubmitReq req);

    @PostMapping("/csp/sms/signature/getSignatureByIds")
    List<CspSmsSignatureResp> getSignatureByIds(@RequestParam("Ids") List<Long> ids);
}
