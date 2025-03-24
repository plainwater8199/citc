package com.citc.nce.auth.messagetemplate.service;

import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.messagetemplate.vo.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 16:12
 * @Version: 1.0
 * @Description:
 */
public interface MessageTemplateService {
    //查询某个流程下是否存在没有审核通过的模板
    boolean existAuditNotPassTemplateForProcess(Long processId);
    PageResultResp getMessageTemplates(MessageTemplatePageReq messageTemplatePageReq);

    List<AccountManagementResp> getAccountForProvedTemplateCarrier(@RequestParam("id") Long id);

    MessageTemplateIdResp saveMessageTemplate(MessageTemplateReq messageTemplateReq);

    MessageTemplateIdResp updateMessageTemplate(MessageTemplateReq messageTemplateEditReq);

    void cancelAudit(String chatbotAccount);

    void replaceChatbotAccount(String newChatbotAccount,String oldChatbotAccount);


    String publicTemplate(Long templateId,String operators,Integer isChecked);
    void templateInitForNewAuditBranch();
    int getTemplateStatus(Long templateId,String chatbotAccount);

    int delMessageTemplateById(Long id);

    MessageTemplateResp getMessageTemplateById(Long id);
    MessageTemplateResp getProvedTemplate(MessageTemplateProvedReq messageTemplateProvedReq);

    List<MessageTemplateTreeResp> getTreeList();

    List<Long> getTemplateIdsByCustmerId(String customerId);

    List<MessageTemplateTreeResp>  getProvedTreeList(MessageTemplateProvedForQueryReq queryReq);


    /**
     * 蜂动模板状态回调
     *
     * @param templateStatusCallbackReq
     */
    void templateStatusCallabck(TemplateStatusCallbackReq templateStatusCallbackReq);

    void deleteTemplateForInvalidOfProcess(DeleteTemplateForInvalidOfProcessReq deleteTemplateForInvalidOfProcessReq);

    /**
     * 通过模板id查找json
     *
     * @param templateIds 模板id
     * @return json
     */
    List<String> selectAllTemplateJson(List<Long> templateIds);
    List<String> selectAllTemplateJsonByCreator();
    List<MessageTemplateResp> getMessageTemplateListByButtonId(MessageTemplateButtonReq messageTemplateButtonReq);
    MessageTemplateResp getByName(String templateName);

    MessageTemplatePageResp get5gTemplatesList(MessageTemplatePageReq messageTemplatePageReq);

    List<MessageTemplateProvedResp> getPlatformTemplateIds(MessageTemplateProvedListReq req);

    TemplateDataResp createFontdoTemplate(MessageTemplateMultiTriggerReq messageTemplateMultiTriggerReq);

    Map<Long, Integer> queryMessageTypeByTemplateIds(List<Long> templateIds);

    Integer queryMessageTypeByTemplateId(Long templateId);

    void deleteLastReplyTemplateByCustomerId(String customerId);

    Map<Long, String> queryTemplateNameByTemplateIds(List<Long> templateIds);
}
