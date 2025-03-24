package com.citc.nce.auth.csp.readingLetter.service;

import com.citc.nce.auth.csp.readingLetter.vo.*;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountDeleteReq;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;

public interface CspReadingLetterAccountService {
    PageResult<CspReadingLetterAccountListResp> queryList(MessageAccountSearchVo req);
    //获取所有账户(status为null时, 不对状态进行过滤)
    List<CspReadingLetterDetailResp> queryAllAccountsOfCustomer(Integer status);

    Boolean save(CspReadingLetterAccountSaveReq req);

    Boolean edit(CspReadingLetterAccountEditReq req);

    int updateStatus(CspReadingLetterAccountUpdateStatusReq req);

    int delete(CspReadingLetterIdReq req);

    CspReadingLetterDetailResp queryDetail(CspReadingLetterIdReq req);

    CspReadingLetterMsgResp customerCheck(CspReadingLetterCustomerCheckReq req);

    CspReadingLetterMsgResp accountNameCheck(CspReadingLetterAccountNameCheckReq req);


    CspReadingLetterDetailResp selectOne(String accountId, String userId);

    List<CustomerReadingLetterAccountVo> available();

    List<CustomerReadingLetterAccountListVo> queryCustomerReadingLetterAccountList(CustomerReadingLetterAccountSearchReq searchVo);
}
