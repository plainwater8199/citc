package com.citc.nce.auth.accountmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTreeResp;
import com.citc.nce.auth.csp.chatbot.vo.ChatbotPackageAvailableAmount;
import com.citc.nce.auth.csp.chatbot.vo.ChatbotResp;
import com.citc.nce.auth.prepayment.vo.FifthMessageAccountListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:00
 * @Version: 1.0
 * @Description:
 */
@Mapper
public interface AccountManagementMapper extends BaseMapper<AccountManagementDo> {
    Page<ChatbotResp> queryChatbot(
            @Param("cspId") String cspId,
            @Param("chatbotString") String chatbotString,
            @Param("customerId") String customerId,
            @Param("operatorCode") Integer operatorCode,
            @Param("chatbotStatus") Integer chatbotStatus,
            Page<ChatbotResp> page
    );

    List<AccountManagementTreeResp> getAllChatbot(@Param("customerId") String customerId);

    List<AccountManagementTreeResp> getAllChatbotIncludeDeleted(@Param("customerId") String customerId);

    Page<FifthMessageAccountListVo> selectAccountByCustomerId(@Param("customerId") String customerId, Page<FifthMessageAccountListVo> page);

    List<AccountManagementTreeResp> getProvedChatbot(@Param("customerId") String customerId, @Param("chatbotAccounts") List<String> chatbotAccounts);

    List<AccountManagementTreeResp> getChatbotByChatbotAccounts(@Param("customerId") String customerId, @Param("chatbotAccounts") List<String> chatbotAccounts);

    List<ChatbotPackageAvailableAmount> selectChatbotAvaliableAmountByChatbotIds(@Param("chatbotIds") List<String> chatbotIds);

    List<FifthMessageAccountListVo> selectPlanAccountByCustomerId(@Param("customerId") String customerId, @Param("chatbotAccountIds") String[] chatbotAccountIds);

}
