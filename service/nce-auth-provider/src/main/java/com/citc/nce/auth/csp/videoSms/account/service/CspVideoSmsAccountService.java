package com.citc.nce.auth.csp.videoSms.account.service;

import com.citc.nce.auth.csp.recharge.vo.TariffOptions;
import com.citc.nce.auth.csp.videoSms.account.vo.*;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.auth.prepayment.vo.VideoSmsMessageAccountListVo;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;
import java.util.Map;

/**
 * <p></p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:32
 */
public interface CspVideoSmsAccountService {

    PageResult<CspVideoSmsAccountResp> queryList(CspVideoSmsAccountReq req);

    PageResult<CspVideoSmsAccountResp> queryListByLoginUser();

    List<String> queryAccountIdListByUserList(List<String> userIds);

    List<String> queryAccountIdListByCspUserId(List<String> userIds);

    int save(CspVideoSmsAccountSaveReq req);

    int edit(CspVideoSmsAccountEditReq req);

    int updateStatus(CspVideoSmsAccountUpdateStatusReq req);

    int delete(CspVideoSmsAccountDeleteReq req);

    CspVideoSmsAccountDetailResp queryDetail(CspVideoSmsAccountQueryDetailReq req);

    List<CspVideoSmsAccountResp> queryListByAccountIds(List<String> accountIds);

    void deductResidue(CspVideoSmsAccountDeductResidueReq req);

    /**
     * 查询csp创建的视频短信账号数量
     *
     * @param userId csp的user id
     * @return 视频短信账号数量
     */
    Long countVideoSmsAccount(String userId);

    PageResult<VideoSmsMessageAccountListVo> selectVideoSmsMessageAccountByCustomer(MessageAccountSearchVo searchVo);

    Map<String, String> queryAccountINameMapByCustomerId(String customerId);
}
