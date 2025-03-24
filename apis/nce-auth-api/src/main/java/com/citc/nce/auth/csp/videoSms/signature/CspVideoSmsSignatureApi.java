package com.citc.nce.auth.csp.videoSms.signature;

import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureReq;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureResp;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureSubmitReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/8/12 9:17
 */
@FeignClient(value = "auth-service", contextId = "CspVideoSmsSignatureApi", url = "${auth:}")
public interface CspVideoSmsSignatureApi {
    /**
     * 列表查询
     *
     * @param req
     * @return list
     */
    @PostMapping("/csp/videoSms/signature/getSignatureByAccountId")
    List<CspVideoSmsSignatureResp> getSignatureByAccountId(@RequestBody CspVideoSmsSignatureReq req);

    /**
     * getSignatureByAccountIdsAndType
     * @param accountIds
     * @param type 0:视频短信 1:其他
     * @return List
     * @author zy.qiu
     * @createdTime 2023/8/18 9:28
     */
    @PostMapping("/csp/videoSms/signature/getSignatureByAccountIdsAndType")
    List<CspVideoSmsSignatureResp> getSignatureByAccountIdsAndType(@RequestParam("accountIds") List<String> accountIds, @RequestParam("type") Integer type);

    @PostMapping("/csp/videoSms/signature/submit")
    int submit(@RequestBody @Valid CspVideoSmsSignatureSubmitReq req);

    @PostMapping("/csp/videoSms/signature/getSignatureByIds")
    List<CspVideoSmsSignatureResp> getSignatureByIds(@RequestParam("Ids") List<Long> ids);

//    @PostMapping("/csp/videoSms/signature/delete")
//    int delete(@RequestBody CspVideoSmsSignatureDeleteReq req);
}
