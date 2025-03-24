package com.citc.nce.csp;

import com.citc.nce.auth.csp.videoSms.signature.CspVideoSmsSignatureApi;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureReq;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureResp;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureSubmitReq;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.security.annotation.HasCsp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:35
 */
@RestController
@RequestMapping("/csp")
@Api(value = "CspVideoSmsSignatureController", tags = "CSP--视频短信签名")
public class CspVideoSmsSignatureController {

    @Resource
    private CspVideoSmsSignatureApi cspVideoSmsSignatureApi;

    @PostMapping("/videoSms/signature/getSignatureByAccountId")
    @ApiOperation(value = "列表查询", notes = "列表查询")
//    @HasCsp
    public List<CspVideoSmsSignatureResp> getSignatureByAccountId(@RequestBody CspVideoSmsSignatureReq req) {
        return cspVideoSmsSignatureApi.getSignatureByAccountId(req);
    }

    @PostMapping("/videoSms/signature/submit")
    @ApiOperation(value = "提交", notes = "提交")
    @HasCsp
    @Log(title = "视频短信签名提交")
    public int submit(@RequestBody @Valid CspVideoSmsSignatureSubmitReq req) {
        return cspVideoSmsSignatureApi.submit(req);
    }

}
