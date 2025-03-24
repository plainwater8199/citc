package com.citc.nce.im.broadcast.utils;

import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.csp.sms.account.CspSmsAccountApi;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountDetailResp;
import com.citc.nce.auth.csp.videoSms.account.CspVideoSmsAccountApi;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDetailResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SpringUtils;
import com.citc.nce.im.broadcast.exceptions.GroupPlanExecuteException;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author jcrenc
 * @since 2024/06/25 15:09
 */
public final class BroadcastAccountUtils {

    /**
     * 根据chatbotAccount列表获取可用的chatbot列表
     */
    public static List<AccountManagementResp> getCheckedFifthGenAccount(List<String> chatbotAccounts) {
        AccountManagementApi accountManagementApi = SpringUtils.getBean(AccountManagementApi.class);
        List<String> distinct = chatbotAccounts.stream().distinct().collect(Collectors.toList());
        List<AccountManagementResp> chatbots = accountManagementApi.getListByChatbotAccountList(distinct);
        if (CollectionUtils.isEmpty(chatbotAccounts))
            throw new GroupPlanExecuteException("无可用机器人账号");
        List<CSPChatbotStatusEnum> unavailableStatus = Arrays.asList(CSPChatbotStatusEnum.STATUS_31_OFFLINE, CSPChatbotStatusEnum.STATUS_42_OFFLINE_CASE_CSP, CSPChatbotStatusEnum.STATUS_71_OFFLINE);
        if (chatbots.stream().map(AccountManagementResp::getChatbotStatus).map(CSPChatbotStatusEnum::byCode).anyMatch(unavailableStatus::contains))
            throw new GroupPlanExecuteException("机器人账号已下线");
        boolean hasYingHeTao = false, hasOther = false;
        for (AccountManagementResp chatbot : chatbots) {
            if (Objects.equals(chatbot.getAccountTypeCode(), CSPOperatorCodeEnum.DEFAULT.getCode()))
                hasYingHeTao = true;
            else
                hasOther = true;
            if (hasYingHeTao && hasOther) {
                throw new GroupPlanExecuteException("不能同时选择硬核桃和其它通道账号");
            }
        }
        return chatbots;
    }

    /**
     * 根据视频短信账号id获取可用的视频短信账号
     */
    public static List<CspVideoSmsAccountDetailResp> getCheckedMediaSmsAccount(String mediaSmsAccountId) {
        List<CspVideoSmsAccountDetailResp> mediaSmsAccounts = new ArrayList<>();
        CspVideoSmsAccountApi videoSmsAccountApi = SpringUtils.getBean(CspVideoSmsAccountApi.class);
        CspVideoSmsAccountDetailResp videoSmsAccount = videoSmsAccountApi.queryDetailInner(mediaSmsAccountId);
        if (Objects.isNull(videoSmsAccount))
            throw new GroupPlanExecuteException("视频短信账号已被删除");
        if (Objects.equals(videoSmsAccount.getStatus(), 0))
            throw new GroupPlanExecuteException("视频短信账号已被禁用");
        mediaSmsAccounts.add(videoSmsAccount);
        return mediaSmsAccounts;
    }

    /**
     * 根据短信账号id获取可用的短信账号
     */
    public static List<CspSmsAccountDetailResp> getCheckedSmsAccount(String smsAccountId) {
        List<CspSmsAccountDetailResp> smsAccounts = new ArrayList<>();
        CspSmsAccountApi smsAccountApi = SpringUtils.getBean(CspSmsAccountApi.class);
        CspSmsAccountDetailResp smsAccount = smsAccountApi.queryDetailInner(smsAccountId);
        if (Objects.isNull(smsAccount))
            throw new GroupPlanExecuteException("短信账号已被删除");
        if (Objects.equals(smsAccount.getStatus(), 0))
            throw new GroupPlanExecuteException("短信账号已被禁用");
        smsAccounts.add(smsAccount);
        return smsAccounts;
    }


    /**
     * 根据视频短信账号id获取可用的视频短信账号
     */
    public static List<CspVideoSmsAccountDetailResp> getCheckedMediaSmsAccounts(List<String> mediaSmsAccountIds) {
        CspVideoSmsAccountApi videoSmsAccountApi = SpringUtils.getBean(CspVideoSmsAccountApi.class);
        List<CspVideoSmsAccountDetailResp> videoSmsAccounts = videoSmsAccountApi.queryDetailInnerByAccountIds(mediaSmsAccountIds);
        List<CspVideoSmsAccountDetailResp> result = new ArrayList<>();
        for (CspVideoSmsAccountDetailResp videoSmsAccount : videoSmsAccounts) {
            if(!Objects.isNull(videoSmsAccount) && !Objects.equals(videoSmsAccount.getStatus(), 0)){
                result.add(videoSmsAccount);
            }
        }
        return result;
    }

    /**
     * 根据短信账号id获取可用的短信账号
     */
    public static List<CspSmsAccountDetailResp> getCheckedSmsAccounts(List<String > smsAccountIds) {
        CspSmsAccountApi smsAccountApi = SpringUtils.getBean(CspSmsAccountApi.class);
        List<CspSmsAccountDetailResp> smsAccounts = smsAccountApi.queryDetailInnerByAccountIds(smsAccountIds);
        List<CspSmsAccountDetailResp> result = new ArrayList<>();
        for (CspSmsAccountDetailResp smsAccount : smsAccounts) {
            if(!Objects.isNull(smsAccount) && !Objects.equals(smsAccount.getStatus(), 0)){
                result.add(smsAccount);
            }
        }
        return result;
    }



}
