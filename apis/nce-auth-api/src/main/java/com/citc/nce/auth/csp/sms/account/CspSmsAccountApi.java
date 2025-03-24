package com.citc.nce.auth.csp.sms.account;

import com.citc.nce.auth.csp.sms.account.vo.*;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDeductResidueReq;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountQueryAccountIdReq;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountResp;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.auth.prepayment.vo.SmsMessageAccountListVo;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@FeignClient(value = "auth-service", contextId = "CspSmsAccountApi", url = "${auth:}")
public interface CspSmsAccountApi {

    @PostMapping("/csp/sms/account/save")
    int save(@RequestBody @Valid CspSmsAccountSaveReq req);

    @PostMapping("/csp/sms/account/edit")
    int edit(@RequestBody @Valid CspSmsAccountEditReq req);

    @PostMapping("/csp/sms/account/updateStatus")
    int updateStatus(@RequestBody @Valid CspSmsAccountUpdateStatusReq req);

    @PostMapping("/csp/sms/account/delete")
    int delete(@RequestBody @Valid CspSmsAccountDeleteReq req);

    @PostMapping("/csp/sms/account/queryDetail")
    CspSmsAccountDetailResp queryDetail(@RequestBody CspSmsAccountQueryDetailReq req);

    @PostMapping("/sms/account/queryListByLoginUser")
    @NotNull
    PageResult<CspSmsAccountResp> queryListByLoginUser();

    @PostMapping("/csp/sms/account/queryList")
    @NotNull
    PageResult<CspSmsAccountResp> queryList(@RequestBody CspSmsAccountReq req);

    @PostMapping("/csp/sms/account/queryAccountIdList")
    List<String> queryAccountIdList();

    @PostMapping("/sms/account/queryListChatbot")
    @NotNull
    PageResult<CspSmsAccountChatbotResp> queryListChatbot(@RequestBody CspSmsAccountChatbotReq req);


    @PostMapping("/sms/account/queryAccountIdListByUserList")
    List<String> queryAccountIdListByUserList(@RequestBody CspSmsAccountQueryAccountIdReq req);

    @PostMapping("/sms/account/queryAccountIdListByCspUserId")
    List<String> queryAccountIdListByCspUserId(@RequestBody CspSmsAccountQueryAccountIdReq req);


    @PostMapping("/csp/sms/account/queryListByAccountIds")
    List<CspSmsAccountResp> queryListByAccountIds(@RequestParam("accountIds") List<String> accountIds);

    @PostMapping("/csp/sms/account/deductResidue")
    void deductResidue(@RequestBody CspSmsAccountDeductResidueReq req);

    @PostMapping("/sms/account/queryListChatbotSelectOption")
    PageResult<CspSmsAccountChatbotResp> queryListChatbotSelectOption(@RequestBody CspSmsAccountChatbotReq req);


    @GetMapping("/csp/sms/account/queryDetail/inner")
    CspSmsAccountDetailResp queryDetailInner(@RequestParam("accountId") String accountId);

    @PostMapping("/csp/sms/account/selectSmsMessageAccountByCustomer")
    PageResult<SmsMessageAccountListVo> selectSmsMessageAccountByCustomer(@RequestBody MessageAccountSearchVo searchVo);

    @PostMapping("/csp/sms/account/queryAccountIdNameMapByCustomerId")
    Map<String, String> queryAccountIdNameMapByCustomerId(@RequestParam("customerId")String customerId);

    @GetMapping("/csp/sms/account/queryDetail/queryDetailInnerByAccountIds")
    List<CspSmsAccountDetailResp> queryDetailInnerByAccountIds(@RequestParam("smsAccountIds") List<String> smsAccountIds);
}
