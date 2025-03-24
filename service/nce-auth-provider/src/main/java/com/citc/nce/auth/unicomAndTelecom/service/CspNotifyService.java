package com.citc.nce.auth.unicomAndTelecom.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementDao;
import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.auth.accountmanagement.service.AccountManagementService;
import com.citc.nce.auth.constant.csp.common.CSPContractStatusEnum;
import com.citc.nce.auth.csp.account.entity.AccountManageDo;
import com.citc.nce.auth.csp.account.service.AccountManageService;
import com.citc.nce.auth.csp.chatbot.dao.ChatbotManageChangeDao;
import com.citc.nce.auth.csp.chatbot.dao.ChatbotManageDao;
import com.citc.nce.auth.csp.chatbot.dao.ChatbotManageWhiteListDao;
import com.citc.nce.auth.csp.chatbot.entity.ChatbotManageChangeDo;
import com.citc.nce.auth.csp.chatbot.entity.ChatbotManageDo;
import com.citc.nce.auth.csp.chatbot.entity.ChatbotManageWhiteListDo;
import com.citc.nce.auth.csp.chatbot.service.ChatbotManageService;
import com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum;
import com.citc.nce.auth.csp.contract.dao.ContractManageChangeDao;
import com.citc.nce.auth.csp.contract.dao.ContractManageDao;
import com.citc.nce.auth.csp.contract.entity.ContractManageChangeDo;
import com.citc.nce.auth.csp.contract.entity.ContractManageDo;
import com.citc.nce.auth.messagetemplate.service.MessageTemplateService;
import com.citc.nce.auth.unicomAndTelecom.req.CspAuditReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.dto.FileExamineDeleteReq;
import com.citc.nce.fileApi.PlatformApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CspNotifyService {

    @Resource
    ChatbotManageChangeDao chatbotManageChangeDao;

    @Resource
    ChatbotManageDao chatbotManageDao;

    @Resource
    ContractManageDao contractManageDao;

    @Resource
    ContractManageChangeDao contractManageChangeDao;

    @Resource
    private AccountManageService accountManageService;

    @Resource
    CspPlatformService cspPlatformService;

    @Resource
    ChatbotManageService chatbotManageService;

    @Resource
    AccountManagementDao accountManagementDao;

    @Resource
    AccountManagementService accountManagementService;

    @Autowired
    private ChatbotManageWhiteListDao chatbotManageWhiteListDao;

    @Resource
    private MessageTemplateService messageTemplateService;

    @Resource
    private PlatformApi platformApi;



    @Transactional(rollbackFor = Exception.class)
    public void cspAudit(CspAuditReq cspAuditReq) {
        // chatbot
        if (cspAuditReq.getType() == 2) {
            ChatbotManageDo chatbotManageDo = chatbotManageDao.selectOne(ChatbotManageDo::getAccessTagNo, cspAuditReq.getAuditNo());
            Long id = chatbotManageDo.getId();
            AccountManagementDo accountManagementDo = accountManagementDao.selectOne(AccountManagementDo::getChatbotAccountId, chatbotManageDo.getChatbotAccountId());
            boolean syncZx = true;
            boolean syncWhitelist = false;
            //合同
            switch (cspAuditReq.getOpType()) {
                case 1:
                    //新增
                    if (cspAuditReq.getAuditStatus() == 1) {
                        //新增通过变为测试状态
                        chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_70_TEST.getCode());
                        // 审核通过以后，向运营商发送我们自己生成的token(最大16位)，网关回调地址，并通过返回信息获取到appID和appKey
                        chatbotManageService.sendChatbot2ServerWhenAudit(chatbotManageDo, accountManagementDo);
                        syncWhitelist = true;
                    } else {
                        //新增不通过
                        chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_64_NEW_AUDIT_NOT_PASS.getCode());
                        chatbotManageDo.setFailureReason(cspAuditReq.getDescription());
                        syncZx = false;
                    }
                    break;
                case 2:
                    //变更
                    ChatbotManageChangeDo chatbotManageChangeDo = chatbotManageChangeDao.selectOne("chatbot_account_id", chatbotManageDo.getChatbotAccountId());
                    if (cspAuditReq.getAuditStatus() == 1) {
                        //变更通过
                        //先保存审核前状态
                        Integer actualState = chatbotManageDo.getActualState();

                        //把变更的属性copy到机器人表
                        BeanUtils.copyProperties(chatbotManageChangeDo, chatbotManageDo);
                        //设置状态 修改id 删除变更历史
                        chatbotManageDo.setId(id);
                        chatbotManageDo.setChatbotStatus(actualState);
                        chatbotManageDo.setAccessTagNo(cspAuditReq.getAuditNo());
                        chatbotManageDo.setActualState(null);
                        chatbotManageChangeDao.deleteById(chatbotManageChangeDo);
                        syncWhitelist = true;
                        //调用开发者接口，同步机器人信息
                        chatbotManageService.sendChatbot2ServerWhenAudit(chatbotManageDo, accountManagementDo);
                    } else {
                        //变更不通过
                        chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_65_EDIT_AUDIT_NOT_PASS.getCode());
                        chatbotManageDo.setFailureReason(cspAuditReq.getDescription());
                    }
                    break;
                case 3:
                    //注销
                    if (cspAuditReq.getAuditStatus() == 1) {
                        //注销通过变为测试状态
                        chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_69_LOG_OFF.getCode());
                        //删除chatbot对应的模板的审核记录等
                        messageTemplateService.cancelAudit(accountManagementDo.getChatbotAccount());
                        //删除素材审核记录
                        FileExamineDeleteReq fileExamineDeleteReq = new FileExamineDeleteReq();
                        fileExamineDeleteReq.setChatbotAccount(accountManagementDo.getChatbotAccount());
                        platformApi.deleteAuditRecord(fileExamineDeleteReq);

                    } else {
                        //新增不通过
                        chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_66_LOGOFF_AUDIT_NOT_PASS.getCode());
                        chatbotManageDo.setFailureReason(cspAuditReq.getDescription());
                    }
                    break;
                case 4:
                    //测试转上线
                    if (cspAuditReq.getAuditStatus() == 1) {
                        //转为上线
                        chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_68_ONLINE.getCode());

                    } else {
                        //测试转上线不通过
                        chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_67_TEST2ONLINE_AUDIT_NOT_PASS.getCode());
                        chatbotManageDo.setFailureReason(cspAuditReq.getDescription());
                    }
                    break;
            }
            chatbotManageDo.setUpdateTime(new Date());
            chatbotManageDao.updateById(chatbotManageDo);
            accountManagementDo.setChatbotStatus(chatbotManageDo.getChatbotStatus());
            if (syncZx)
                accountManagementService.updateChatbotWhenGetAuditResult(accountManagementDo);
            if (syncWhitelist) {
                ChatbotManageWhiteListDo whiteListDo = chatbotManageWhiteListDao.selectOne(ChatbotManageWhiteListDo::getChatbotAccountId, accountManagementDo.getChatbotAccountId());
                List<String> list = Arrays.asList(whiteListDo.getWhiteList().split(","));
                AccountManageDo cspInfo = accountManageService.getCspAccount(chatbotManageDo.getOperatorCode(), accountManagementDo.getCspId());
                String accessTagNo = chatbotManageDo.getAccessTagNo();
                if (chatbotManageDo.getChatbotStatus().equals(CSPChatbotStatusEnum.STATUS_70_TEST.getCode())) {
                    cspPlatformService.whiteListPhone(list, accessTagNo, cspInfo.getCspPassword(), cspInfo.getCspAccount(), chatbotManageDo.getOperatorCode());
                    whiteListDo.setStatus(1);
                    chatbotManageWhiteListDao.updateById(whiteListDo);//修改白名单状态
                }
            }
            accountManagementDo.setUpdateTime(new Date());
            accountManagementDao.updateById(accountManagementDo);
        } else {
            //合同
            LambdaQueryWrapper<ContractManageDo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ContractManageDo::getCustomerNum, cspAuditReq.getAuditNo());
            wrapper.notIn(ContractManageDo::getContractStatus, CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_40_PAUSE.getCode());
            ContractManageDo contractManageDo = contractManageDao.selectOne(wrapper);
            ChatbotManageDo chatbotManageDo = chatbotManageDao.getByISPAndCustomerIdExcludeFd(contractManageDo.getOperatorCode(), contractManageDo.getCustomerId());

            contractManageDo.setUpdateTime(new Date());
            switch (cspAuditReq.getOpType()) {
                case 1:
                    //新增
                    if (cspAuditReq.getAuditStatus() == 1) {
                        //新增审核通过
                        contractManageDo.setContractStatus(66);
                        if (contractManageDo.getContractIsRenewal() == 0 && contractManageDo.getContractExpireDate().compareTo(new Date()) < 0) {
                            //未续签
                            contractManageDo.setContractStatus(68);
                            contractManageDo.setActualState(68);
                        }
                        if (contractManageDo.getContractIsRenewal() == 1 && contractManageDo.getContractRenewalDate().compareTo(new Date()) < 0) {
                            //已续签
                            contractManageDo.setContractStatus(68);
                            contractManageDo.setActualState(68);
                        }
                    } else {
                        //新增不通过
                        contractManageDo.setContractStatus(63);
                        contractManageDo.setFailureReason(cspAuditReq.getDescription());
                    }
                    break;
                case 2:
                    //变更
                    ContractManageChangeDo contractManageChangeDo = contractManageChangeDao.selectOne("contract_id", contractManageDo.getId());
                    if (cspAuditReq.getAuditStatus() == 1) {
                        //变更审核通过 判断时间是否
                        //1、回调通过，判断时间有效期，过期将显示状态变为已过期
                        // （1、看变更过去的数据有效期有没有变化，有就判断，如果大于当前时间，就变为正常，要将原来的数据都要进行更新，另外将实际状态和显示状态全部变为正常）
                        BeanUtils.copyProperties(contractManageChangeDo, contractManageDo);
                        contractManageDo.setContractStatus(66);
                        contractManageDo.setActualState(null);
                        contractManageDo.setCustomerNum(cspAuditReq.getAuditNo());
                        boolean updateChtBot = updateChtBot(contractManageDo, contractManageChangeDo, chatbotManageDo);
                        log.info("合同变更审核通过, contractManageDo: {}, contractManageChangeDo: {}, chatbotManageDo: {}, updateChtBot: {}", contractManageDo, contractManageChangeDo, chatbotManageDo, updateChtBot);
                        if (updateChtBot) {
                            contractManageDao.updateById(contractManageDo);
                            return;
                        }
                        ;
                        //判断历史数据有没有修改有效期
                    } else {
                        //变更不通过(将显示状态变为变更不通过，实际数据不修改)
                        contractManageDo.setContractStatus(64);
                        contractManageDo.setFailureReason(cspAuditReq.getDescription());
                    }
                    break;
                case 3:
                    //注销
                    if (cspAuditReq.getAuditStatus() == 1) {
                        //注销审核通过
                        contractManageDo.setContractStatus(67);
                        contractManageDo.setActualState(null);
                    } else {
                        //注销不通过
                        contractManageDo.setContractStatus(65);
                        contractManageDo.setFailureReason(cspAuditReq.getDescription());
                    }
                    break;
            }
            contractManageDao.updateById(contractManageDo);
        }
    }

    private boolean updateChtBot(ContractManageDo contractManageDo, ContractManageChangeDo contractManageChangeDo, ChatbotManageDo chatbotManageDo) {
        if (contractManageChangeDo.getContractIsRenewal() == 0 && contractManageChangeDo.getContractExpireDate().compareTo(new Date()) < 0) {
            //未续签
            contractManageDo.setContractStatus(68);
            contractManageDo.setActualState(68);
            //下线chatBot
            offlineChatBot(chatbotManageDo);
            return true;
        }
        if (contractManageChangeDo.getContractIsRenewal() == 1 && contractManageChangeDo.getContractRenewalDate().compareTo(new Date()) < 0) {
            //已续签
            contractManageDo.setContractStatus(68);
            contractManageDo.setActualState(68);
            contractManageDao.updateById(contractManageDo);
            //下线chatBot
            offlineChatBot(chatbotManageDo);
            return true;
        }
        return false;
    }

    private void offlineChatBot(ChatbotManageDo chatbotManageDo) {
        if (ObjectUtil.isNotEmpty(chatbotManageDo)) {
            AccountManageDo cspInfo = accountManageService.getCspAccount(chatbotManageDo.getOperatorCode(), chatbotManageDo.getCreator());
            cspPlatformService.isOnlineUpdate(chatbotManageDo.getAccessTagNo(), cspInfo.getCspPassword(), cspInfo.getCspAccount(), 2, chatbotManageDo.getOperatorCode());
            chatbotManageDo.setChatbotStatus(71);
            chatbotManageDo.setActualState(null);
            chatbotManageDao.updateById(chatbotManageDo);
        }
    }
}
