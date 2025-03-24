package com.citc.nce.auth.csp.sms.account.service;

import com.citc.nce.auth.csp.sms.account.vo.*;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.auth.prepayment.vo.SmsMessageAccountListVo;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;
import java.util.Map;

public interface CspSmsAccountService {
    
    int save(CspSmsAccountSaveReq req);

    int edit(CspSmsAccountEditReq req);

    int updateStatus(CspSmsAccountUpdateStatusReq req);

    int delete(CspSmsAccountDeleteReq req);

    CspSmsAccountDetailResp queryDetail(CspSmsAccountQueryDetailReq req);

    PageResult<CspSmsAccountResp> queryListByLoginUser();

    PageResult<CspSmsAccountResp> queryList(CspSmsAccountReq req);

    PageResult<CspSmsAccountChatbotResp> queryListChatbot(CspSmsAccountChatbotReq req);

    List<String> queryAccountIdListByUserList(List<String> userIds);

    List<String> queryAccountIdListByCspUserId(List<String> userIds);

    List<CspSmsAccountResp> queryListByAccountIds(List<String> accountIds);

    void deductResidue(CspSmsAccountDeductResidueReq req);

    void checkCustomIsCsp(String customerId);

    PageResult<SmsMessageAccountListVo> selectSmsMessageAccountByCustomer(MessageAccountSearchVo searchVo);

    Map<String, String> queryAccountIdNameMapByCustomerId(String customerId);
}
