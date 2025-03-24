package com.citc.nce.auth.csp.chatbot;

import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTreeResp;
import com.citc.nce.auth.csp.chatbot.service.ChatbotManageService;
import com.citc.nce.auth.csp.chatbot.vo.*;
import com.citc.nce.auth.csp.recharge.vo.TariffOptions;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>csp-chatbot管理</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:27
 */
@RestController
public class ChatbotManageController implements ChatbotApi {

    @Autowired
    private ChatbotManageService chatbotManageService;


    @Override
    @NotNull
    public PageResult<ChatbotResp> queryList(@RequestBody ChatbotReq req) {
        return chatbotManageService.queryList(req);
    }
    @Override
    public List<AccountManagementTreeResp> queryBychatbotAccounts(@RequestBody @Valid List<String> chatbotAccounts){
        return chatbotManageService.queryBychatbotAccounts(chatbotAccounts);
    }

    @Override
    public ChatbotGetStatusResp getChatbotStatus(@RequestBody ChatbotGetReq req) {
        return chatbotManageService.getChatbotStatus(req);
    }

    @Override
    public void saveCMCC(ChatbotSaveReq req) {
         chatbotManageService.saveChatbot(req);
    }

    @Override
    public void updateCMCCById(ChatbotUpdateCMCCReq req) {
        chatbotManageService.updateCMCCById(req);
    }

    @Override
    public AccountManagementResp getOtherByAccountManagementId(ChatbotGetReq req) {
        return chatbotManageService.getOtherByAccountManagementId(req);
    }

    @Override
    public ChatbotGetResp queryByAccountManagementId(ChatbotGetReq req) {
        return chatbotManageService.queryByAccountManagementId(req);
    }

    @Override
    public int activeOn(ChatbotActiveOnReq req) {
        return chatbotManageService.activeOn(req);
    }

    @Override
    public void setWhiteList(ChatbotSetWhiteListReq req) {
         chatbotManageService.setWhiteList(req);
    }

    @Override
    public List<ChatbotSetWhiteListResp> queryWhiteList(ChatbotSetWhiteListReq req) {
        return chatbotManageService.queryWhiteList(req);
    }

    @Override
    public int updateChatbotStatusByAccountManagementId(ChatbotStatusUpdateReq req) {
        return chatbotManageService.updateChatbotStatusByAccountManagementId(req);
    }

    @Override
    public void saveLocalChatbot(@RequestBody @Valid ChatbotOtherSaveReq req) {
        chatbotManageService.saveLocalChatbot(req);
    }

    @Override
    public boolean checkForCanCreate(@RequestBody @Valid checkForCanCreateChatbotReq req) {
        return chatbotManageService.checkForCanCreate(req);
    }

    @Override
    public void updateLocalRobot(@RequestBody @Valid ChatbotOtherUpdateReq req) {
         chatbotManageService.updateLocalRobot(req);
    }

    @Override
    public void logOffLocalRobot(@RequestBody @Valid ChatbotLocalLogOffReq req) {
        chatbotManageService.logOffLocalRobot(req);
    }

    @Override
    public void logOffCmccRobot(@RequestBody @Valid ChatbotCmccLogOffReq req) {
        chatbotManageService.logOffCmccRobot(req);
    }

    @Override
    public void deleteLocalRobot(@RequestBody @Valid ChatbotLocalDeleteReq req) {
        chatbotManageService.deleteLocalRobot(req);
    }

    @Override
    public void saveSupplierChatbot(ChatbotSupplierAdd req) {
        chatbotManageService.saveSupplierChatbot(req);
    }

    @Override
    public void updateSupplierChatbot(ChatbotSupplierUpdate req) {
        chatbotManageService.updateSupplierChatbot(req);
    }

    @Override
    public ChatbotSupplierInfo getSupplierChatbotInfo(@PathVariable("id") Long id) {
        return chatbotManageService.getSupplierChatbotInfo(id);
    }

    @Override
    public void deleteSupplierChatbot(@PathVariable("id") Long id) {
        chatbotManageService.deleteSupplierChatbot(id);
    }

    @Override
    public void submitSupplierChatbot(@PathVariable("id") Long id) {
        chatbotManageService.submitSupplierChatbot(id);
    }

    @Override
    public ChatbotStatusResp getChatbotStatusOptions() {
        return chatbotManageService.getChatbotStatusOptions();
    }

    @Override
    public ChatbotChannelResp getChatbotChannel(ChatbotChannelReq req) {
        return chatbotManageService.getChatbotChannel(req);
    }

    @Override
    public Boolean getNewChatbotPermission(ChatbotNewPermissionReq req) {
        return chatbotManageService.getNewChatbotPermission(req);
    }

    @Override
    public void onlineSupplierChatbot(Long id) {
        chatbotManageService.onlineSupplierChatbot(id);
    }

    @Override
    public void offlineSupplierChatbot(Long id) {
        chatbotManageService.offlineSupplierChatbot(id);
    }

}
