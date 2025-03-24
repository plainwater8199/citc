package com.citc.nce.auth.csp.videoSms.account;

import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDeductResidueReq;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDeleteReq;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDetailResp;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountQueryAccountIdReq;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountQueryDetailReq;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountReq;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountResp;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountSaveReq;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountEditReq;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountUpdateStatusReq;
import com.citc.nce.auth.csp.recharge.vo.TariffOptions;
import com.citc.nce.auth.csp.videoSms.account.vo.*;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.auth.prepayment.vo.VideoSmsMessageAccountListVo;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/8/12 9:17
 */
@FeignClient(value = "auth-service", contextId = "CspVideoSmsAccountApi", url = "${auth:}")
public interface CspVideoSmsAccountApi {
    /**
     * 列表查询
     *
     * @param req
     * @return list
     */
    @PostMapping("/csp/videoSms/account/queryList")
    @NotNull
    PageResult<CspVideoSmsAccountResp> queryList(@RequestBody CspVideoSmsAccountReq req);

    @PostMapping("/videoSms/account/queryListByLoginUser")
    @NotNull
    PageResult<CspVideoSmsAccountResp> queryListByLoginUser();

    @PostMapping("/videoSms/account/queryAccountIdListByUserList")
    List<String> queryAccountIdListByUserList(@RequestBody CspVideoSmsAccountQueryAccountIdReq req);

    @PostMapping("/videoSms/account/queryAccountIdListByCspUserId")
    List<String> queryAccountIdListByCspUserId(@RequestBody CspVideoSmsAccountQueryAccountIdReq req);

    @PostMapping("/csp/videoSms/account/save")
    int save(@RequestBody @Valid CspVideoSmsAccountSaveReq req);

    @PostMapping("/csp/videoSms/account/edit")
    int edit(@RequestBody @Valid CspVideoSmsAccountEditReq req);

    @PostMapping("/csp/videoSms/account/updateStatus")
    int updateStatus(@RequestBody @Valid CspVideoSmsAccountUpdateStatusReq req);

    @PostMapping("/csp/videoSms/account/delete")
    int delete(@RequestBody @Valid CspVideoSmsAccountDeleteReq req);

    @PostMapping("/csp/videoSms/account/queryDetail")
    CspVideoSmsAccountDetailResp queryDetail(@RequestBody CspVideoSmsAccountQueryDetailReq req);

    @PostMapping("/csp/videoSms/account/queryListByAccountIds")
    List<CspVideoSmsAccountResp> queryListByAccountIds(@RequestParam("accountIds") List<String> accountIds);

    @PostMapping("/csp/videoSms/account/deductResidue")
    void deductResidue(@RequestBody CspVideoSmsAccountDeductResidueReq req);

    @GetMapping("/csp/videoSms/account/queryDetail/inner")
    CspVideoSmsAccountDetailResp queryDetailInner(@RequestParam("accountId") String accountId);

    @PostMapping("/csp/videoSms/account/selectVideoSmsMessageAccountByCustomer")
    PageResult<VideoSmsMessageAccountListVo> selectVideoSmsMessageAccountByCustomer(@RequestBody MessageAccountSearchVo searchVo);

    @PostMapping("/csp/videoSms/account/queryAccountINameMapByCustomerId")
    Map<String, String> queryAccountIdNameMapByCustomerId(@RequestParam("customerId") String customerId);

    @GetMapping("/csp/videoSms/account/queryDetail/queryDetailInnerByAccountIds")
    List<CspVideoSmsAccountDetailResp> queryDetailInnerByAccountIds(@RequestParam("mediaSmsAccountIds")List<String> mediaSmsAccountIds);
}
