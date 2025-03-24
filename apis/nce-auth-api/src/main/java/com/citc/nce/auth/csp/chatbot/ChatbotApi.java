package com.citc.nce.auth.csp.chatbot;

import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTreeResp;
import com.citc.nce.auth.csp.chatbot.vo.*;
import com.citc.nce.auth.csp.contract.vo.ContractStatusResp;
import com.citc.nce.auth.csp.recharge.vo.TariffOptions;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:47
 */
@FeignClient(value = "auth-service", contextId = "CSPChatbot", url = "${auth:}")
public interface ChatbotApi {
    /**
     * 列表查询
     *
     * @return PageResult
     */
    @PostMapping("/csp/chatbot/queryList")
    PageResult<ChatbotResp> queryList(@RequestBody @Valid ChatbotReq req);
    @PostMapping("/csp/chatbot/queryBychatbotAccounts")
    List<AccountManagementTreeResp> queryBychatbotAccounts(@RequestBody @Valid List<String> chatbotAccounts);
    @PostMapping("/csp/chatbot/status")
    ChatbotGetStatusResp getChatbotStatus(@RequestBody @Valid ChatbotGetReq req);

    @PostMapping("/csp/chatbot/saveCMCC")
    void saveCMCC(@RequestBody @Valid ChatbotSaveReq req);

    @PostMapping("/csp/chatbot/updateCMCCById")
    void updateCMCCById(@RequestBody @Valid ChatbotUpdateCMCCReq req);

    @PostMapping("/csp/chatbot/queryByAccountManagementId")
    ChatbotGetResp queryByAccountManagementId(@RequestBody @Valid ChatbotGetReq req);

    @PostMapping("/csp/chatbot/activeOn")
    int activeOn(@RequestBody @Valid ChatbotActiveOnReq req);

    @PostMapping("/csp/chatbot/setWhiteList")
    void setWhiteList(@RequestBody @Valid ChatbotSetWhiteListReq req);

    @PostMapping("/csp/chatbot/queryWhiteList")
    List<ChatbotSetWhiteListResp> queryWhiteList(@RequestBody @Valid ChatbotSetWhiteListReq req);

    @PostMapping("/csp/chatbot/updateChatbotStatusByAccountManagementId")
    int updateChatbotStatusByAccountManagementId(@RequestBody @Valid ChatbotStatusUpdateReq req);

    @PostMapping("/csp/chatbot/getOtherByAccountManagementId")
    AccountManagementResp getOtherByAccountManagementId(@RequestBody @Valid ChatbotGetReq req);

    @PostMapping("/csp/chatbot/saveOther")
    void saveLocalChatbot(@RequestBody @Valid ChatbotOtherSaveReq req);

    @PostMapping("/csp/chatbot/checkForCanCreate")
    boolean checkForCanCreate(@RequestBody @Valid checkForCanCreateChatbotReq req);

    @PostMapping("/csp/chatbot/updateOther")
    void updateLocalRobot(@RequestBody @Valid ChatbotOtherUpdateReq req);

    @PostMapping("/csp/chatbot/logOffLocal")
    void logOffLocalRobot(@RequestBody @Valid ChatbotLocalLogOffReq req);

    @PostMapping("/csp/chatbot/logOffCmcc")
    void logOffCmccRobot(@RequestBody @Valid ChatbotCmccLogOffReq req);

    @PostMapping("/csp/chatbot/delete")
    void deleteLocalRobot(@RequestBody @Valid ChatbotLocalDeleteReq req);

    @PostMapping("/csp/chatbot/supplier/save")
    void saveSupplierChatbot(@RequestBody @Valid ChatbotSupplierAdd req);

    @PostMapping("/csp/chatbot/supplier/update")
    void updateSupplierChatbot(@RequestBody @Valid ChatbotSupplierUpdate req);

    @GetMapping("/csp/chatbot/supplier/{id}")
    ChatbotSupplierInfo getSupplierChatbotInfo(@PathVariable("id") Long id);

    @PostMapping("/csp/chatbot/supplier/{id}/delete")
    void deleteSupplierChatbot(@PathVariable("id") Long id);

    @PostMapping("/csp/chatbot/supplier/{id}/submit")
    void submitSupplierChatbot(@PathVariable("id") Long id);

    @GetMapping("/csp/chatbot/status/options")
    ChatbotStatusResp getChatbotStatusOptions();

    @PostMapping("/csp/chatbot/channel")
    ChatbotChannelResp getChatbotChannel(@RequestBody @Valid ChatbotChannelReq req);

    @PostMapping("/csp/chatbot/new/permission")
    Boolean getNewChatbotPermission(@RequestBody @Valid ChatbotNewPermissionReq req);

    @PostMapping("/csp/chatbot/supplier/{id}/online")
    void onlineSupplierChatbot(@PathVariable("id") Long id);

    @PostMapping("/csp/chatbot/supplier/{id}/offline")
    void offlineSupplierChatbot(@PathVariable("id") Long id);
}
