package com.citc.nce.auth.accountmanagement.service;

import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.auth.accountmanagement.vo.AccountChatbotAccountQueryReq;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementEditReq;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementForProvedTreeReq;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementOptionVo;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementReq;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTypeReq;
import com.citc.nce.auth.accountmanagement.vo.PageResultAccountResp;
import com.citc.nce.auth.prepayment.vo.FifthAccountVo;
import com.citc.nce.auth.prepayment.vo.FifthMessageAccountListVo;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:37
 * @Version: 1.0
 * @Description:
 */
public interface AccountManagementService {
    PageResultAccountResp<?> getAccountManagements(PageParam pageParam);

    AccountManagementDo saveAccountManagement(AccountManagementReq accountManagementReq);

    /**
     * @param syncZx 是否需要同步中讯
     */
    void updateAccountManagement(AccountManagementEditReq accountManagementEditReq, Boolean syncZx);

    int delAccountManagementById(String chatbotAccountId);

    AccountManagementResp getAccountManagementById(String chatbotAccountId);
    AccountManagementResp getAccountManagementById(Long id);

    AccountManagementResp getAccountManagementByAccountId(String accountId);

    AccountManagementResp getAccountManagementByChatbotAccountId(String chatbotAccountId);

    List<AccountManagementResp> getAccountManagementlist(String creator);

    AccountManagementResp getAccountManagementByAccountType(AccountManagementTypeReq accountManagementTypeReq);

    List<AccountManagementResp> getAccountManagementByAccountTypes(AccountManagementTypeReq accountManagementTypeReq);

    AccountManagementResp getAccountManagementByAccountTypeAndSupplier(AccountManagementTypeReq accountManagementTypeReq);


    long selectCountAll();

    List<AccountManagementResp> getListByChatbotAccounts(AccountChatbotAccountQueryReq accountChatbotAccountQueryReq);

    List<AccountManagementResp> getListByCreators(List<String> creators);

    Boolean checkChatBotStatus(AccountManagementTypeReq accountManagementTypeReq);

    void updateChatbotWhenGetAuditResult(AccountManagementDo accountManagementDo);

    List<String> getChatbotAccountIdsByCustomerId(String customerId);

    /**
     * 获取该用户机器人账号列表
     *
     * @param channelAvailability 通道可用性
     * @return 机器人账号列表
     */
    List<AccountManagementOptionVo> getAllAccountManagement(Boolean channelAvailability);

    List<AccountManagementOptionVo> getAccountManagementByIdIncludeDeleted(Boolean channelAvailability);

    List<AccountManagementOptionVo> getProvedTreeList(AccountManagementForProvedTreeReq accountManagementForProvedTreeReq);

    PageResult<FifthMessageAccountListVo> selectFifthMessageAccountByCustomer(MessageAccountSearchVo searchVo);

    /**
     * 查询5g账号
     *
     * @param chatbotAccount chatbotAccount
     * @return 5g账号信息
     */
    FifthAccountVo queryFifthAccount(String chatbotAccount);

    /**
     * 根据用户id查询用户的chatbot列表
     *
     * @param customerId 用户id
     * @return 用户信息
     */
    List<AccountManagementResp> getChatbotAccountInfoByCustomerId(String customerId);

    List<AccountManagementResp> getListByChatbotAccountList(List<String> chatbotAccountList);

    List<AccountManagementResp> getListByChatbotAccountIdList(List<String> chatbotAccountIdList);
}
