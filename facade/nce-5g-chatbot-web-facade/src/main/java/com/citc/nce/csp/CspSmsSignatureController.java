package com.citc.nce.csp;

import com.citc.nce.auth.csp.sms.signature.CspSmsSignatureApi;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureReq;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureResp;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureSubmitReq;
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

@RestController
@RequestMapping("/csp")
@Api(value = "CspSmsSignatureController", tags = "CSP--短信签名")
public class CspSmsSignatureController {


    @Resource
    CspSmsSignatureApi cspSmsSignatureApi;

    @PostMapping("/sms/signature/getSignatureByAccountId")
    @ApiOperation(value = "列表查询", notes = "列表查询")
//    @HasCsp
    public List<CspSmsSignatureResp> getSignatureByAccountId(@RequestBody CspSmsSignatureReq req) {
        return cspSmsSignatureApi.getSignatureByAccountId(req);
    }

    @PostMapping("/sms/signature/submit")
    @ApiOperation(value = "提交", notes = "提交")
    @HasCsp
    @Log(title = "短信签名提交")
    public int submit(@RequestBody @Valid CspSmsSignatureSubmitReq req){
        return cspSmsSignatureApi.submit(req);
    }
}
