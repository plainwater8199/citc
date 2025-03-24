package com.citc.nce.auth.csp.readingLetter;

import com.citc.nce.auth.csp.readingLetter.service.CspReadingLetterAccountService;
import com.citc.nce.auth.csp.readingLetter.vo.*;
import com.citc.nce.auth.csp.sms.account.service.CspSmsAccountService;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountDeleteReq;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountReq;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountResp;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountSaveReq;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class CspReadingLetterAccountController implements CspReadingLetterAccountApi {

    @Resource
    private CspReadingLetterAccountService cspReadingLetterAccountService;

    @Override
    public PageResult<CspReadingLetterAccountListResp> queryList(MessageAccountSearchVo req) {
        return cspReadingLetterAccountService.queryList(req);
    }

    @Override
    public Boolean save(CspReadingLetterAccountSaveReq req) {
        return cspReadingLetterAccountService.save(req);
    }

    @Override
    public Boolean edit(CspReadingLetterAccountEditReq req) {
        return cspReadingLetterAccountService.edit(req);
    }

    @Override
    public int updateStatus(CspReadingLetterAccountUpdateStatusReq req) {
        return cspReadingLetterAccountService.updateStatus(req);
    }

    @Override
    public int delete(CspReadingLetterIdReq req) {
        return cspReadingLetterAccountService.delete(req);
    }

    @Override
    public CspReadingLetterDetailResp queryDetail(CspReadingLetterIdReq req) {
        return cspReadingLetterAccountService.queryDetail(req);
    }

    @Override
    public CspReadingLetterMsgResp customerCheck(CspReadingLetterCustomerCheckReq req) {
        return cspReadingLetterAccountService.customerCheck(req);
    }

    @Override
    public CspReadingLetterMsgResp accountNameCheck(CspReadingLetterAccountNameCheckReq req) {
        return cspReadingLetterAccountService.accountNameCheck(req);
    }

    @Override
    public List<CustomerReadingLetterAccountVo> available() {
        return cspReadingLetterAccountService.available();
    }

    @Override
    public List<CustomerReadingLetterAccountListVo> queryCustomerReadingLetterAccountList(CustomerReadingLetterAccountSearchReq searchVo) {
        return cspReadingLetterAccountService.queryCustomerReadingLetterAccountList(searchVo);
    }
}
