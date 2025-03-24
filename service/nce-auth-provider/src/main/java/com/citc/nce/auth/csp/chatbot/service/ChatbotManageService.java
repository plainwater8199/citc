package com.citc.nce.auth.csp.chatbot.service;

import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTreeResp;
import com.citc.nce.auth.csp.chatbot.entity.ChatbotManageDo;
import com.citc.nce.auth.csp.chatbot.vo.*;
import com.citc.nce.auth.csp.recharge.vo.TariffOptions;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>csp-chatbot管理</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:32
 */
public interface ChatbotManageService {

    /**
     * 编辑联通、硬核桃、电信
     *
     * @param req
     * @author zy.qiu
     * @createdTime 2023/3/2 10:29
     */
    void updateLocalRobot(ChatbotOtherUpdateReq req);

    /**
     * 保存联通、硬核桃、电信
     *
     * @param req
     * @author zy.qiu
     * @createdTime 2023/3/2 10:29
     */
    void saveLocalChatbot(ChatbotOtherSaveReq req);

    /**
     * 列表查询
     *
     * @param req
     * @return
     * @author zy.qiu
     * @createdTime 2023/3/2 10:29
     */
    PageResult<ChatbotResp> queryList(ChatbotReq req);

    /**
     * 根据chatbotAccount集合查询chatbot信息
     *
     * @param chatbotAccounts
     * @return
     * @author zy.qiu
     * @createdTime 2023/3/2 10:29
     */
    List<AccountManagementTreeResp> queryBychatbotAccounts(List<String> chatbotAccounts);

    /**
     * 机器人状态查询
     */
    ChatbotGetStatusResp getChatbotStatus(ChatbotGetReq req);

    /**
     * 查看详情(移动)
     *
     * @param req
     * @return
     * @author zy.qiu
     * @createdTime 2023/3/2 10:29
     */
    ChatbotGetResp queryByAccountManagementId(ChatbotGetReq req);

    /**
     * 新增chatbot（线上流程机器人）
     */
    void saveChatbot(ChatbotSaveReq req);

    /**
     * 设置白名单
     *
     * @param req
     * @return
     * @author zy.qiu
     * @createdTime 2023/3/2 10:29
     */
    void setWhiteList(ChatbotSetWhiteListReq req);

    /**
     * 查看白名单
     *
     * @param req
     * @return
     * @author zy.qiu
     * @createdTime 2023/3/2 10:29
     */
    List<ChatbotSetWhiteListResp> queryWhiteList(ChatbotSetWhiteListReq req);

    /**
     * 上架
     *
     * @param req
     * @return
     * @author zy.qiu
     * @createdTime 2023/3/2 10:29
     */
    int activeOn(ChatbotActiveOnReq req);

    /**
     * 编辑移动
     *
     * @param req
     * @return
     * @author zy.qiu
     * @createdTime 2023/3/2 10:29
     */
    void updateCMCCById(ChatbotUpdateCMCCReq req);

    /**
     * 更新机器人状态(非移动)
     */
    int updateChatbotStatusByAccountManagementId(ChatbotStatusUpdateReq req);


    /**
     * 注销本地电联机器人
     */
    void logOffLocalRobot(ChatbotLocalLogOffReq req);

    /**
     * 查看详情(移动以外)
     *
     * @param req
     * @return
     * @author zy.qiu
     * @createdTime 2023/3/2 10:29
     */
    AccountManagementResp getOtherByAccountManagementId(ChatbotGetReq req);

    void sendChatbot2ServerWhenAudit(ChatbotManageDo chatbotManageDo, AccountManagementDo accountManagementDo);


    /**
     * 注销移动chatbot(其实这里是删除)
     *
     * @param chatbotAccount chatbot账号
     */
    void cancelCMCCChatbot(String chatbotAccount);

    /**
     * 接受回调:注销移动chatbot
     *
     * @param chatbotAccount chatbot账号
     */
    void realCancelCMCCChatbot(String chatbotAccount);

    /**
     * 新增供应商chatbot（蜂动）
     */
    void saveSupplierChatbot(ChatbotSupplierAdd req);

    /**
     * 更新供应商chatbot（蜂动）
     */
    void updateSupplierChatbot(ChatbotSupplierUpdate req);

    /**
     * 查询供应商chatbot（蜂动）
     */
    ChatbotSupplierInfo getSupplierChatbotInfo(Long id);

    /**
     * 删除供应商chatbot（蜂动）
     */
    void deleteSupplierChatbot(Long id);

    /**
     * 提交供应商chatbot（蜂动）
     */
    void submitSupplierChatbot(Long id);

    /**
     * 查询chatbot状态配置（蜂动）
     */
    ChatbotStatusResp getChatbotStatusOptions();

    /**
     * 查询chatbot通道
     */
    ChatbotChannelResp getChatbotChannel(ChatbotChannelReq req);

    /**
     * 查询是否可以新建chatbot
     */
    Boolean getNewChatbotPermission(ChatbotNewPermissionReq req);

    /**
     * 删除添加的chatbot
     */
    void deleteLocalRobot(ChatbotLocalDeleteReq req);

    /**
     * 查询是否可以新建chatbot
     */
    boolean checkForCanCreate(checkForCanCreateChatbotReq req);

    void logOffCmccRobot(ChatbotCmccLogOffReq req);

    void onlineSupplierChatbot(Long id);

    void offlineSupplierChatbot(Long id);
}
