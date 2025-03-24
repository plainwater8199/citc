package com.citc.nce.auth.mobile.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementDao;
import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.auth.accountmanagement.service.AccountManagementService;
import com.citc.nce.auth.configure.ChatBotMobileConfigure;
import com.citc.nce.auth.csp.account.entity.AccountManageDo;
import com.citc.nce.auth.csp.account.service.AccountManageService;
import com.citc.nce.auth.csp.chatbot.dao.ChatbotManageChangeDao;
import com.citc.nce.auth.csp.chatbot.dao.ChatbotManageDao;
import com.citc.nce.auth.csp.chatbot.dao.ChatbotManageShelvesDao;
import com.citc.nce.auth.csp.chatbot.dao.ChatbotManageWhiteListDao;
import com.citc.nce.auth.csp.chatbot.entity.ChatbotManageChangeDo;
import com.citc.nce.auth.csp.chatbot.entity.ChatbotManageDo;
import com.citc.nce.auth.csp.chatbot.entity.ChatbotManageShelvesDo;
import com.citc.nce.auth.csp.chatbot.entity.ChatbotManageWhiteListDo;
import com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum;
import com.citc.nce.auth.csp.contract.dao.ContractManageDao;
import com.citc.nce.auth.csp.contract.entity.ContractManageDo;
import com.citc.nce.auth.csp.csp.service.CspService;
import com.citc.nce.auth.csp.menu.dao.MenuDao;
import com.citc.nce.auth.csp.menu.entity.MenuDo;
import com.citc.nce.auth.messagetemplate.service.MessageTemplateService;
import com.citc.nce.auth.mobile.exp.MobileExp;
import com.citc.nce.auth.mobile.req.*;
import com.citc.nce.auth.mobile.resp.SyncResult;
import com.citc.nce.auth.utils.BeanUtil;
import com.citc.nce.auth.utils.HttpsUtil;
import com.citc.nce.auth.utils.SHA256Utils;
import com.citc.nce.auth.utils.SimpleUuidUtil;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.util.UUIDUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class ChatBotService {

    @Resource
    private ChatBotMobileConfigure chatBotMobileConfigure;

    @Resource
    ChatbotManageWhiteListDao chatbotManageWhiteListDao;

    @Resource
    ChatbotManageDao chatbotManageDao;

    @Resource
    ChatbotManageShelvesDao chatbotManageShelvesDao;

    @Resource
    private ContractManageDao contractManageDao;

    @Resource
    MenuDao menuDao;

    @Resource
    private AccountManageService accountManageService;

    @Resource
    AccountManagementDao accountManagementDao;
    @Resource
    private CspService cspService;

    @Autowired
    private ChatbotManageChangeDao chatbotManageChangeDao;

    @Autowired
    private AccountManagementService accountManagementService;
    @Autowired
    private MessageTemplateService messageTemplateService;

    public SyncResult chatBotStatusChange(ChatBotInfo chatBotInfo) {
        String chatBotInfoString = JSONObject.toJSONString(chatBotInfo);
        CspAccount cspAccount = getCspAccount(chatBotInfo.getCreator());
        return execute(chatBotMobileConfigure.getChatBotStatusChangeUrl(), cspAccount.getAppId(), cspAccount.getPrivateKey(), cspAccount.getPassword(), chatBotInfoString);
    }

    public void chatBotCancel(ChatBotInfo chatBotInfo) {
        String chatBotInfoString = JSONObject.toJSONString(chatBotInfo);
        CspAccount cspAccount = getCspAccount(chatBotInfo.getCreator());
        execute(chatBotMobileConfigure.getChatBotCancelUrl(), cspAccount.getAppId(), cspAccount.getPrivateKey(), cspAccount.getPassword(), chatBotInfoString);
        syncReturnSuccess();
    }

    public SyncResult chatBotNew(ChatBotNew chatBotNew) {
        buildParam(chatBotNew);
        String chatBotNewString = JSONObject.toJSONString(chatBotNew);
        CspAccount cspAccount = getCspAccount(chatBotNew.getCreator());
        return execute(chatBotMobileConfigure.getChatBotNewUrl(), cspAccount.getAppId(), cspAccount.getPrivateKey(), cspAccount.getPassword(), chatBotNewString);
    }

    public SyncResult chatBotChange(ChatBotNew chatBotNew) {
        buildParam(chatBotNew);
        String chatBotChangeString = JSONObject.toJSONString(chatBotNew);
        CspAccount cspAccount = getCspAccount(chatBotNew.getCreator());
        return execute(chatBotMobileConfigure.getChatBotChangeUrl(), cspAccount.getAppId(), cspAccount.getPrivateKey(), cspAccount.getPassword(), chatBotChangeString);
    }

    private void buildParam(ChatBotNew chatBotNew) {
        chatBotNew.setMessageId(SimpleUuidUtil.generateShortUuid());
        chatBotNew.setETag("1");
        chatBotNew.setCreateTime(DateUtil.formatDate(new Date()));
        chatBotNew.setOpTime(DateUtil.formatDate(new Date()));
    }

    public SyncResult chatBotShelfApply(ChatBotShelfApply chatBotShelfApply) {
        String chatBotShelfApplyString = JSONObject.toJSONString(chatBotShelfApply);
        CspAccount cspAccount = getCspAccount(chatBotShelfApply.getCreator());
        return execute(chatBotMobileConfigure.getChatBotShelfApplyUrl(), cspAccount.getAppId(), cspAccount.getPrivateKey(), cspAccount.getPassword(), chatBotShelfApplyString);
    }

    public SyncResult resubmitDebugWhite(ChatBotSubmitWhiteList chatBotSubmitWhiteList) {
        String chatBotSubmitWhiteListString = JSONObject.toJSONString(chatBotSubmitWhiteList);
        CspAccount cspAccount = getCspAccount(chatBotSubmitWhiteList.getCreator());
        return execute(chatBotMobileConfigure.getResubmitDebugWhiteUrl(), cspAccount.getAppId(), cspAccount.getPrivateKey(), cspAccount.getPassword(), chatBotSubmitWhiteListString);
    }

    //注销移动Chatbot
    public SyncResult logOffCmccChatbot(ChatBotLogOff chatBotLogOff) {
        String chatBotLogOffString = JSONObject.toJSONString(chatBotLogOff);
        CspAccount cspAccount = getCspAccount(chatBotLogOff.getCreator());
        return execute(chatBotMobileConfigure.getCancelUrl(), cspAccount.getAppId(), cspAccount.getPrivateKey(), cspAccount.getPassword(), chatBotLogOffString);
    }

    public SyncResult syncMenu(MenuRequest menuRequest) {
        String menuRequestStr = JSONObject.toJSONString(menuRequest);
        CspAccount cspAccount = getCspAccount(menuRequest.getCreator());
        return execute(chatBotMobileConfigure.getSyncMenuUrl(), cspAccount.getAppId(), cspAccount.getPrivateKey(), cspAccount.getPassword(), menuRequestStr);
    }


    private CspAccount getCspAccount(String userId) {
        AccountManageDo accountManageDo = accountManageService.getCspAccount(2, cspService.obtainCspId(userId));
        CspAccount cspAccount = new CspAccount();
        BeanUtils.copyProperties(accountManageDo, cspAccount);
        cspAccount.setPassword(accountManageDo.getCspPassword());
        return cspAccount;
    }


    /**
     * 移动chatbot状态变更
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> chatBotStatus(ChatBotInfo chatBotInfo) {
        String chatbotId = chatBotInfo.getChatbotId().split("@")[0];
        int state = Integer.parseInt(chatBotInfo.getStatus());
        ChatbotManageDo chatbotManageDo = chatbotManageDao.selectOne(ChatbotManageDo::getChatbotId, chatbotId);
        if (chatbotManageDo == null)
            throw new BizException(MobileExp.CHATBOT_NOT_EXISTS);
        chatbotManageDo.setChatbotStatus(state);
        chatbotManageDao.updateById(chatbotManageDo);
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(AccountManagementDo::getChatbotAccountId, chatbotManageDo.getChatbotAccountId());
        accountManagementDo.setChatbotStatus(state);
        accountManagementDao.updateById(accountManagementDo);

        //当前业务 暂停==删除  !!!1删除chatbot对应的模板的审核记录等
        if (CSPChatbotStatusEnum.STATUS_40_PAUSE.equals(CSPChatbotStatusEnum.byCode(Integer.valueOf(chatBotInfo.getStatus())))) {
            messageTemplateService.cancelAudit(accountManagementDo.getChatbotAccount());
        }

        return syncReturnSuccess();
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> chatBotAudit(ChatBotExamine chatBotExamine) {
        String chatbotId = chatBotExamine.getChatbotId().split("@")[0];
        ChatbotManageDo chatbotManageDo = chatbotManageDao.selectOne(ChatbotManageDo::getChatbotId, chatbotId);
        if (chatbotManageDo == null)
            throw new BizException(MobileExp.CHATBOT_NOT_EXISTS);
        String chatbotAccountId = chatbotManageDo.getChatbotAccountId();
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(AccountManagementDo::getChatbotAccountId, chatbotAccountId);
        ChatbotManageWhiteListDo whiteListDo = chatbotManageWhiteListDao.selectOne(ChatbotManageWhiteListDo::getChatbotAccountId, chatbotAccountId);
        /*
         * 审核类型：1-新增审核 2-变更审核 3-调试白名单审核 4-上架
         */
        switch (chatBotExamine.getAuthType()) {
            case 1:
                //新增审核
                if (StringUtils.equals(chatBotExamine.getAuthStatus(), "1")) {
                    //成功变为调试状态
                    chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_26_WHITE_AND_IS_AUDITING.getCode());
                    accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_26_WHITE_AND_IS_AUDITING.getCode());
                    if (ObjectUtils.isNotEmpty(whiteListDo)) {
                        whiteListDo.setCreateAndAudit(1);
                    }
                } else {
                    //失败变为新增审核不通过
                    chatbotManageDo.setFailureReason(chatBotExamine.getComment());
                    chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_11_CRATE_AND_AUDIT_NOT_PASS.getCode());
                    accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_11_CRATE_AND_AUDIT_NOT_PASS.getCode());

                }
                accountManagementDao.updateById(accountManagementDo);
                chatbotManageDao.updateById(chatbotManageDo);
                break;
            case 2:
                // 变更审核 成功变为提交变更前的状态
                ChatbotManageChangeDo changeDo = chatbotManageChangeDao.selectOne(ChatbotManageChangeDo::getChatbotAccountId, chatbotAccountId);
                if (StringUtils.equals(chatBotExamine.getAuthStatus(), "1")) {
                    chatbotManageDo.setChatbotStatus(chatbotManageDo.getActualState());
                    accountManagementDo.setChatbotStatus(chatbotManageDo.getActualState());
                    chatbotManageDo.setActualState(null);
                    changeDo.setId(null);
                    changeDo.setChatbotStatus(null);
                    changeDo.setActualState(null);
                    BeanUtil.copyNonNullProperties(changeDo, chatbotManageDo);
                } else {
                    // 失败变为变更审核不通过
                    chatbotManageDo.setFailureReason(chatBotExamine.getComment());
                    accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_12_UPDATE_AND_AUDIT_NOT_PASS.getCode());
                    chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_12_UPDATE_AND_AUDIT_NOT_PASS.getCode());
                }
                accountManagementDao.updateById(accountManagementDo);
                chatbotManageDao.updateById(chatbotManageDo);
                chatbotManageChangeDao.deleteById(changeDo);
                break;
            case 3:
                if (ObjectUtils.isEmpty(whiteListDo)) {
                    break;
                }
                if (StringUtils.equals(chatBotExamine.getAuthStatus(), "1")) {
                    // 白名单审核通过
                    whiteListDo.setStatus(1);
                    chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_50_DEBUG.getCode());
                    accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_50_DEBUG.getCode());
                    whiteListDo.setCreateAndAudit(0);
                    accountManagementDao.updateById(accountManagementDo);
                    chatbotManageDao.updateById(chatbotManageDo);
                } else {
                    // 白名单审核不通过
                    whiteListDo.setStatus(2);
                    if (0 == whiteListDo.getCreateAndAudit()) {
                        chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_27_WHITE_AND_AUDIT_NOT_PASS.getCode());
                        accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_27_WHITE_AND_AUDIT_NOT_PASS.getCode());
                        chatbotManageDo.setFailureReason(chatBotExamine.getComment());
                        accountManagementDao.updateById(accountManagementDo);
                        chatbotManageDao.updateById(chatbotManageDo);
                    }
                }
                chatbotManageWhiteListDao.updateById(whiteListDo);
                break;
            case 4:
                //上架
                LambdaQueryWrapper<ChatbotManageShelvesDo> shelvesWrapper = new LambdaQueryWrapper<>();
                shelvesWrapper.eq(ChatbotManageShelvesDo::getChatbotId, chatbotId);
                ChatbotManageShelvesDo shelvesDo = chatbotManageShelvesDao.selectOne(shelvesWrapper);
                if (StringUtils.equals(chatBotExamine.getAuthStatus(), "1")) {
                    //上架审核通过
                    shelvesDo.setStatus(1);
                    chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_30_ONLINE.getCode());
                    accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_30_ONLINE.getCode());
                } else {
                    //上架审核失败
                    shelvesDo.setStatus(2);
                    chatbotManageDo.setFailureReason(chatBotExamine.getComment());
                    accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_25_SHELVE_AND_AUDIT_NOT_PASS.getCode());
                    chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_25_SHELVE_AND_AUDIT_NOT_PASS.getCode());
                }
                accountManagementDao.updateById(accountManagementDo);
                chatbotManageDao.updateById(chatbotManageDo);
                chatbotManageShelvesDao.updateById(shelvesDo);
                break;
        }
        return syncReturnSuccess();
    }

    /**
     * 菜单审核回调接口
     *
     * @param authInfo 回调信息
     */
    @Transactional
    public Map<String, String> authNotification(AuthInfo authInfo) {
        log.info("接收移动固定菜单审核回调：{}", authInfo);
        LambdaQueryWrapper<MenuDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MenuDo::getMessageId, authInfo.getAuthMessageId());
        wrapper.orderByDesc(MenuDo::getVersion);
        List<MenuDo> menuDos = menuDao.selectList(wrapper);
        if (1 == Integer.parseInt(authInfo.getAuthStatus())) {
            //可用
            menuDos.get(0).setMenuStatus(1);
            menuDos.get(0).setResult("审核通过");
        } else {
            //不可用
            menuDos.get(0).setMenuStatus(2);
            menuDos.get(0).setResult("【审核不通过】");
        }
        menuDao.updateById(menuDos.get(0));
        return syncReturnSuccess();
    }


    public static Map<String, String> syncReturnSuccess() {
        Map<String, String> res = new HashMap<>();
        res.put("resultCode", "00000");
        res.put("resultDesc", "成功");
        return res;
    }


    /**
     * 2.8.1	非直签客户新增(运营平台->CSP平台)
     *
     * @param signedCustomer 移动签约客户信息
     * @return 回调参数
     */
    public SyncResult clientNew(SignedCustomer signedCustomer) {
        buildParam(signedCustomer);
        String signedCustomerString = JSONObject.toJSONString(signedCustomer);
        CspAccount cspAccount = getCspAccount(signedCustomer.getCreator());
        return execute(chatBotMobileConfigure.getClientNewUrl(), cspAccount.getAppId(), cspAccount.getPrivateKey(), cspAccount.getPassword(), signedCustomerString);
    }

    public SyncResult clientChange(SignedCustomer signedCustomer) {
        buildParam(signedCustomer);
        String signedCustomerString = JSONObject.toJSONString(signedCustomer);
        CspAccount cspAccount = getCspAccount(signedCustomer.getCreator());
        return execute(chatBotMobileConfigure.getClientChangeUrl(), cspAccount.getAppId(), cspAccount.getPrivateKey(), cspAccount.getPassword(), signedCustomerString);
    }

    private void buildParam(SignedCustomer signedCustomer) {
        signedCustomer.setMessageId(SimpleUuidUtil.generateShortUuid());
        signedCustomer.setCreator(SessionContextUtil.getUser().getUserId());
        signedCustomer.setETag("1");
        signedCustomer.setAuditPerson("5G消息测试");
        signedCustomer.setAuditOpinion("审核通过");
        signedCustomer.setAuditTime(DateUtil.formatDate(new Date()));
    }

    public SyncResult syncAllotServiceCode(AgentServiceCode agentServiceCode) {
        String agentServiceCodeString = JSONObject.toJSONString(agentServiceCode);
        CspAccount cspAccount = getCspAccount(agentServiceCode.getCreator());
        return execute(chatBotMobileConfigure.getAgentServiceCodeUrl(), cspAccount.getAppId(), cspAccount.getPrivateKey(), cspAccount.getPassword(), agentServiceCodeString);
    }

    public Map<String, String> platClientNew(SignedCustomer signedCustomer) {
        return syncReturnSuccess();
    }

    public Map<String, String> platClientChange(SignedCustomer signedCustomer) {
        return syncReturnSuccess();
    }

    public Map<String, String> platClientAudit(SignedCustomerExamine signedCustomerExamine) {
        LambdaUpdateWrapper<ContractManageDo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ContractManageDo::getCustomerNum, signedCustomerExamine.getCustomerNum());
        ContractManageDo contractManageDo = new ContractManageDo();
        if (StringUtils.equals("3", signedCustomerExamine.getAuthType())) {
            //新增审核
            if (StringUtils.equals("1", signedCustomerExamine.getAuthStatus())) {
                contractManageDo.setContractStatus(30);
            } else {
                contractManageDo.setContractStatus(11);
            }
        } else {
            if (StringUtils.equals("1", signedCustomerExamine.getAuthStatus())) {
                contractManageDo.setContractStatus(30);
            } else {
                contractManageDo.setContractStatus(12);
            }
        }

        contractManageDao.update(contractManageDo, wrapper);

        return syncReturnSuccess();
    }


    public Map<String, String> platClientStatus(CustomerStatus customerStatus) {
        LambdaUpdateWrapper<ContractManageDo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ContractManageDo::getCustomerNum, customerStatus.getCustomerNum());
        ContractManageDo contractManageDo = new ContractManageDo();
        contractManageDo.setContractStatus(Integer.parseInt(customerStatus.getStatus()));
        contractManageDao.update(contractManageDo, wrapper);
        return syncReturnSuccess();
    }

    public Map<String, String> allotServiceCode(AgentServiceCode agentServiceCode) {
        return syncReturnSuccess();
    }


    /**
     * 接收移动机器人配置变更通知，获取机器人appId、appKey，并生成token，最后将机器人信息同步网关
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> syncConfigChatBot(ChatBotConfigInfo chatBotConfigInfo) {
        String chatbotId = chatBotConfigInfo.getChatbotId().split("@")[0];
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(AccountManagementDo::getChatbotAccount, chatbotId);
        if (accountManagementDo == null)
            throw new BizException(MobileExp.CHATBOT_NOT_EXISTS);
        AccountManageDo accountManageDo = accountManageService.getCspAccount(accountManagementDo.getAccountTypeCode(), accountManagementDo.getCspId());
        accountManagementDo.setAppId(chatBotConfigInfo.getCspToken()); //cspToken
        accountManagementDo.setAppKey(accountManageDo.getCspCode()); //csp编码
        accountManagementDo.setToken(UUIDUtils.generateUUID().substring(0, 16));//自己生成
        accountManagementService.updateChatbotWhenGetAuditResult(accountManagementDo);
        accountManagementDao.updateById(accountManagementDo);
        return syncReturnSuccess();
    }

    /**
     * 订购信息（服务代码）
     *
     * @param serviceCode 服务代码
     */
    public Map<String, String> syncProduct(ServiceCode serviceCode) {

        return syncReturnSuccess();
    }


    private String getAuthorization(String password, String requestID, String privateKey, String timestamp) throws
            Exception {
        String token = SHA256Utils.getSHA256(password);
        String signatureStr = token + timestamp + requestID;
        byte[] bytes = SHA256Utils.signSHA256withRSA(signatureStr, privateKey);
        return "Basic " + Base64.encodeBase64String(bytes);
    }


    @SneakyThrows
    private SyncResult execute(String url, String appId, String privateKey, String password, String paramString) {
        String requestId = IdUtil.fastSimpleUUID();
        try (CloseableHttpClient client = HttpsUtil.createSSLClientDefault()) {
            String executeUrl = chatBotMobileConfigure.getBaseUrl() + url;
            HttpPost httpPost = new HttpPost(executeUrl);
            log.info("executeUrl is {}", executeUrl);
            long timestamp = System.currentTimeMillis();
            String authorization = getAuthorization(password, requestId, privateKey, timestamp + "");
            httpPost.addHeader("Authorization", authorization);
            httpPost.addHeader("Timestamp", timestamp + "");
            httpPost.addHeader("App-ID", appId);
            httpPost.addHeader("Request-ID", requestId);
            log.info("同步请求头：{}", Arrays.toString(httpPost.getAllHeaders()));
            log.info("同步请求体数据为： {}", paramString);
            HttpEntity entity = new StringEntity(paramString, ContentType.create("application/json", "utf-8"));
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            String string = EntityUtils.toString(responseEntity);
            log.info("同步请求体结果为： {}", string);
            SyncResult syncResult = JSONObject.parseObject(string, SyncResult.class);
            if (!"0000".equals(syncResult.getResultCode())) {
                if (StringUtils.isEmpty(syncResult.getResultDesc())) {
                    syncResult.setResultDesc(syncResult.getError());
                }
            }
            return syncResult;
        } catch (Exception e) {
            log.error("execute 失败", e);
            throw new BizException(500, "访问运营商服务失败");
        }

    }

}
