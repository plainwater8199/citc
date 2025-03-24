package com.citc.nce.auth.csp.readingLetter;

import com.citc.nce.auth.csp.readingLetter.vo.*;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountDeleteReq;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountUpdateStatusReq;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "auth-service", contextId = "CspReadingLetterAccountApi", url = "${auth:}")
public interface CspReadingLetterAccountApi {
    @PostMapping("/csp/readingLetter/account/list")
    PageResult<CspReadingLetterAccountListResp> queryList(@RequestBody MessageAccountSearchVo searchVo);

    @PostMapping("/csp/readingLetter/account/save")
    Boolean save(@RequestBody CspReadingLetterAccountSaveReq req);

    @PostMapping("/csp/readingLetter/account/edit")
    Boolean edit(@RequestBody CspReadingLetterAccountEditReq req);

    @PostMapping("/csp/readingLetter/account/updateStatus")
    int updateStatus(@RequestBody CspReadingLetterAccountUpdateStatusReq req);

    @PostMapping("/csp/readingLetter/account/delete")
    int delete(@RequestBody CspReadingLetterIdReq req);

    @PostMapping("/csp/readingLetter/account/queryDetail")
    CspReadingLetterDetailResp queryDetail(@RequestBody CspReadingLetterIdReq req);

    @PostMapping("/csp/readingLetter/account/customer/check")
    CspReadingLetterMsgResp customerCheck(@RequestBody CspReadingLetterCustomerCheckReq req);

    @PostMapping("/csp/readingLetter/account/name/check")
    CspReadingLetterMsgResp accountNameCheck(@RequestBody CspReadingLetterAccountNameCheckReq req);

    @PostMapping("/customer/readingLetter/account/available")
    List<CustomerReadingLetterAccountVo> available();

    @PostMapping("/customer/readingLetter/account/list")
    List<CustomerReadingLetterAccountListVo> queryCustomerReadingLetterAccountList(@RequestBody CustomerReadingLetterAccountSearchReq searchVo);
}
