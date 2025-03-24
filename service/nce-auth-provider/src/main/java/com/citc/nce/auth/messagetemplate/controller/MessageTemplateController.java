package com.citc.nce.auth.messagetemplate.controller;

import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.service.MessageTemplateService;
import com.citc.nce.auth.messagetemplate.vo.*;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 消息模板
 */
@RestController()
@Slf4j
public class MessageTemplateController implements MessageTemplateApi {
    @Resource
    private MessageTemplateService messageTemplateService;

    /**
     * 消息模板列表分页获取
     *
     * @param
     * @return
     */
    @PostMapping("/message/template/pageList")
    @Override
    public PageResultResp getMessageTemplates(@RequestBody @Valid MessageTemplatePageReq messageTemplatePageReq) {
        return messageTemplateService.getMessageTemplates(messageTemplatePageReq);
    }

    /**
     * 查询某个流程下是否存在没有审核通过的模板
     */

    @GetMapping("/message/template/existAuditNotPass")
    @Override
    public boolean existAuditNotPassTemplateForProcess(Long processId) {
        return messageTemplateService.existAuditNotPassTemplateForProcess(processId);
    }

    /**
     * 蜂动版本 发布，历史模板数据处理
     */
    @GetMapping("/message/template/transfer")
    @Override
    public void templateInitForNewAuditBranch() {
        messageTemplateService.templateInitForNewAuditBranch();

    }

    @PostMapping("/message/template/getAccountForProvedTemplateCarrier")
    public List<AccountManagementResp> getAccountForProvedTemplateCarrier(@RequestParam("id") Long id) {
        return messageTemplateService.getAccountForProvedTemplateCarrier(id);

    }

    /**
     * 新增消息模板
     *
     * @param
     * @return
     */
    @PostMapping("/message/template/save")
    @Override
    @XssCleanIgnore
    public MessageTemplateIdResp saveMessageTemplate(@RequestBody @Valid MessageTemplateReq messageTemplateReq) {
        return messageTemplateService.saveMessageTemplate(messageTemplateReq);
    }

    /**
     * 模板审核状态回调
     *
     * @param
     * @return
     */
    @PostMapping("/message/template/callback")
    public void templateStatusCallabck(@RequestBody TemplateStatusCallbackReq templateStatusCallbackReq) {
        messageTemplateService.templateStatusCallabck(templateStatusCallbackReq);
    }

    /**
     * 修改消息模板
     *
     * @param
     * @return
     */
    @PostMapping("/message/template/edit")
    @Override
    @XssCleanIgnore
    public MessageTemplateIdResp updateMessageTemplate(@RequestBody @Valid MessageTemplateReq messageTemplateEditReq) {
        return messageTemplateService.updateMessageTemplate(messageTemplateEditReq);
    }

    @GetMapping("/message/template/auditStatus")
    @Override
    public int getTemplateStatus(@RequestParam("templateId") Long templateId, @RequestParam("chatbotAccount") String chatbotAccount) {
        return messageTemplateService.getTemplateStatus(templateId, chatbotAccount);
    }

    @GetMapping("/message/template/cancelAudit")
    @Override
    public void cancelAudit(@RequestParam("chatbotAccount") String chatbotAccount) {
        messageTemplateService.cancelAudit(chatbotAccount);
    }

    @GetMapping("/message/template/replaceChatbotAccount")
    @Override
    public void replaceChatbotAccount(@RequestParam("newChatbotAccount") String newChatbotAccount, @RequestParam("oldChatbotAccount") String oldChatbotAccount) {
        messageTemplateService.replaceChatbotAccount(newChatbotAccount, oldChatbotAccount);
    }


    @PostMapping("/message/template/public")
    @Override
    public String publicTemplate(@RequestParam("templateId") Long templateId, @RequestParam("operators") String operators, @RequestParam("isChecked") Integer isChecked) {
        return messageTemplateService.publicTemplate(templateId, operators, isChecked);
    }

    /**
     * 删除消息模板
     *
     * @param
     * @return
     */
    @PostMapping("/message/template/delete")
    @Override
    public int delMessageTemplateById(@RequestParam("id") Long id) {
        return messageTemplateService.delMessageTemplateById(id);
    }

    /**
     * 获取单个消息模板
     *
     * @param
     * @return
     */
    @PostMapping("/message/template/getOne")
    @Override
    @XssCleanIgnore
    public MessageTemplateResp getMessageTemplateById(@RequestParam("id") Long id) {
        return messageTemplateService.getMessageTemplateById(id);
    }

    @XssCleanIgnore
    @PostMapping("/message/template/getProvedOne")
    public MessageTemplateResp getProvedTemplate(@RequestBody MessageTemplateProvedReq messageTemplateProvedReq) {
        return messageTemplateService.getProvedTemplate(messageTemplateProvedReq);
    }

    /**
     * 获取消息模板树
     *
     * @param
     * @return
     */
    @PostMapping("/message/template/getTreeList")
    @Override
    @XssCleanIgnore
    public List<MessageTemplateTreeResp> getTreeList() {
        return messageTemplateService.getTreeList();
    }

    @PostMapping("/message/template/getProvedTreeList")
    @XssCleanIgnore
    public List<MessageTemplateTreeResp> getProvedTreeList(@RequestBody @NotNull MessageTemplateProvedForQueryReq queryReq) {
        return messageTemplateService.getProvedTreeList(queryReq);
    }

    @PostMapping("/message/template/selectAllTemplateJson")
    @Override
    public List<String> selectAllTemplateJson(@RequestBody List<Long> templateIds) {
        return messageTemplateService.selectAllTemplateJson(templateIds);
    }

    @GetMapping("/message/template/selectAllTemplateJsonByCreator")
    public List<String> selectAllTemplateJsonByCreator() {
        return messageTemplateService.selectAllTemplateJsonByCreator();
    }

    @PostMapping("/message/template/getMessageTemplateListByButtonId")
    @Override
    @XssCleanIgnore
    public List<MessageTemplateResp> getMessageTemplateListByButtonId(@RequestBody @Valid MessageTemplateButtonReq messageTemplateButtonReq) {
        return messageTemplateService.getMessageTemplateListByButtonId(messageTemplateButtonReq);
    }

    @PostMapping("/message/template/getPlatformTemplateIds")
    @Override
    @XssCleanIgnore
    public List<MessageTemplateProvedResp> getPlatformTemplateIds(@RequestBody MessageTemplateProvedListReq req) {
        return messageTemplateService.getPlatformTemplateIds(req);
    }

    @Override
    @XssCleanIgnore
    @PostMapping("/message/template/getByName")
    public MessageTemplateResp getByName(MessageTemplatePageReq messageTemplatePageReq) {
        return messageTemplateService.getByName(messageTemplatePageReq.getTemplateName());
    }

    @Override
    @XssCleanIgnore
    @PostMapping("/message/template/get5gTemplatesList")
    public MessageTemplatePageResp get5gTemplatesList(@RequestBody @Valid MessageTemplatePageReq messageTemplatePageReq) {
        return messageTemplateService.get5gTemplatesList(messageTemplatePageReq);
    }

    @Override
    @XssCleanIgnore
    @PostMapping("/message/template/createFontdoTemplate")
    public TemplateDataResp createFontdoTemplate(@RequestBody @Valid MessageTemplateMultiTriggerReq messageTemplateMultiTriggerReq) {
        return messageTemplateService.createFontdoTemplate(messageTemplateMultiTriggerReq);
    }

    @Override
    public Map<Long, Integer> queryMessageTypeByTemplateIds(List<Long> templateIds) {
        return messageTemplateService.queryMessageTypeByTemplateIds(templateIds);
    }

    @Override
    public Map<Long, String> queryTemplateNameByTemplateIds(List<Long> templateIds) {
        return messageTemplateService.queryTemplateNameByTemplateIds(templateIds);
    }

    @Override
    public Integer queryMessageTypeByTemplateId(Long templateId) {
        return messageTemplateService.queryMessageTypeByTemplateId(templateId);
    }

    @Override
    @PostMapping("/message/template/clearProcess")
    public void deleteTemplateForInvalidOfProcess(@RequestBody DeleteTemplateForInvalidOfProcessReq deleteTemplateForInvalidOfProcessReq) {
        try {
            messageTemplateService.deleteTemplateForInvalidOfProcess(deleteTemplateForInvalidOfProcessReq);
        } catch (Exception exception) {
            log.error("清理机器人无效流程出错", exception);
        }
    }

    @Override
    public void deleteLastReplyTemplateByCustomerId(String customerId) {
        messageTemplateService.deleteLastReplyTemplateByCustomerId(customerId);
    }
}
