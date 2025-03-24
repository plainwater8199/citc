package com.citc.nce.auth.messagetemplate;


import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.messagetemplate.vo.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 消息模板
 */
@FeignClient(value = "auth-service", contextId = "MessageTemplateApi", url = "${auth:}")
public interface MessageTemplateApi {

    /**
     * 消息模板列表分页获取
     *
     * @param
     * @return
     */
    @PostMapping("/message/template/pageList")
    PageResultResp getMessageTemplates(@RequestBody @Valid MessageTemplatePageReq messageTemplatePageReq);
    @GetMapping("/message/template/transfer")
    public void templateInitForNewAuditBranch();
    @PostMapping("/message/template/getAccountForProvedTemplateCarrier")
    List<AccountManagementResp> getAccountForProvedTemplateCarrier(@RequestParam("id") Long id);
    @GetMapping("/message/template/auditStatus")
    int getTemplateStatus(@RequestParam("templateId")Long templateId,@RequestParam("chatbotAccount") String chatbotAccount);

    /**
     * 删除chatbot账号下所有模板的审核记录
     * @param chatbotAccount
     */
    @GetMapping("/message/template/cancelAudit")
    void cancelAudit(@RequestParam("chatbotAccount")String chatbotAccount);

    /**
     * 旧chatbot账号下的模板的所有chatbotAccount替换为新的ChatbotAccount
     * @param newChatbotAccount
     * @param oldChatbotAccount
     */
    @GetMapping("/message/template/replaceChatbotAccount")
    void replaceChatbotAccount(@RequestParam("newChatbotAccount")String newChatbotAccount,@RequestParam("oldChatbotAccount")String oldChatbotAccount);

    @GetMapping("/message/template/existAuditNotPass")
    public boolean existAuditNotPassTemplateForProcess(@RequestParam("processId")Long processId);
        /**
         * 新增消息模板
         *
         * @param
         * @return
         */
    @PostMapping("/message/template/save")
    MessageTemplateIdResp saveMessageTemplate(@RequestBody @Valid MessageTemplateReq messageTemplateReq);

    @PostMapping("/message/template/callback")
    public void templateStatusCallabck(@RequestBody TemplateStatusCallbackReq templateStatusCallbackReq);

    /**
     * 修改消息模板
     *
     * @param
     * @return
     */
    @PostMapping("/message/template/edit")
    MessageTemplateIdResp updateMessageTemplate(@RequestBody @Valid MessageTemplateReq messageTemplateEditReq);

    /**
     * 删除消息模板
     *
     * @param
     * @return
     */
    @PostMapping("/message/template/delete")
    int delMessageTemplateById(@RequestParam("id") Long id);

    /**
     * 获取单个消息模板
     *
     * @param
     * @return
     */
    @PostMapping("/message/template/getOne")
    MessageTemplateResp getMessageTemplateById(@RequestParam("id") Long id);
    /**
     * 获取单个审核通过的消息模板
     *
     * @param
     * @return
     */
    @PostMapping("/message/template/getProvedOne")
    MessageTemplateResp getProvedTemplate(@RequestBody MessageTemplateProvedReq messageTemplateProvedReq);

    /**
     * 获取消息模板树
     *
     * @param
     * @return
     */
    @PostMapping("/message/template/getTreeList")
    List<MessageTemplateTreeResp> getTreeList();
    /**
     * 获取审核通过的消息模板树
     *
     * @param
     * @return
     */
    @PostMapping("/message/template/getProvedTreeList")
    public List<MessageTemplateTreeResp> getProvedTreeList(@RequestBody @NotNull MessageTemplateProvedForQueryReq queryReq);
        /**
         * 通过模板Id查找所有对应的json
         *
         * @param templateIds 模板id
         * @return json
         */
    @PostMapping("/message/template/selectAllTemplateJson")
    List<String> selectAllTemplateJson(@RequestBody List<Long> templateIds);
    @GetMapping("/message/template/selectAllTemplateJsonByCreator")
    List<String> selectAllTemplateJsonByCreator();
    @PostMapping("/message/template/getMessageTemplateListByButtonId")
    List<MessageTemplateResp> getMessageTemplateListByButtonId(@RequestBody @Valid MessageTemplateButtonReq messageTemplateButtonReq);

    @PostMapping("/message/template/getPlatformTemplateIds")
    public List<MessageTemplateProvedResp> getPlatformTemplateIds(@RequestBody @Valid MessageTemplateProvedListReq req);

    @PostMapping("/message/template/public")
    String publicTemplate(@RequestParam("templateId") Long templateId,@RequestParam("operators") String operators,@RequestParam("isChecked") Integer isChecked);

    @ApiOperation(value = "获取5G模板列表", notes = "消息模板列表分页获取")
    @PostMapping("/message/template/getByName")
    MessageTemplateResp getByName(@RequestBody @Valid MessageTemplatePageReq messageTemplatePageReq);

    @ApiOperation(value = "获取5G模板列表", notes = "消息模板列表分页获取")
    @PostMapping("/message/template/get5gTemplatesList")
    MessageTemplatePageResp get5gTemplatesList(@RequestBody @Valid MessageTemplatePageReq messageTemplatePageReq);

    @ApiOperation(value = "创建蜂动5G消息模板", notes = "创建蜂动5G消息模板")
    @PostMapping("/message/template/createFontdoTemplate")
    TemplateDataResp createFontdoTemplate(@RequestBody @Valid MessageTemplateMultiTriggerReq messageTemplateMultiTriggerReq);

    @GetMapping("/message/template/queryMessageTypeByTemplateIds")
    @ApiOperation("根据模板id列表获取模板对应的消息类型")
    Map<Long,Integer> queryMessageTypeByTemplateIds(@RequestParam("templateIds") List<Long> templateIds);

    @GetMapping("/message/template/queryTemplateNameByTemplateIds")
    @ApiOperation("根据模板id列表获取模板名称")
    Map<Long,String> queryTemplateNameByTemplateIds(@RequestParam("templateIds") List<Long> templateIds);

    @GetMapping("/message/template/queryMessageTypeByTemplateId")
    @ApiOperation("根据模板id获取模板对应的消息类型")
    Integer queryMessageTypeByTemplateId(@RequestParam("templateId") Long templateId);
    @ApiOperation(value = "清理机器人流程无效模板", notes = "清理机器人流程无效模板")
    @PostMapping("/message/template/clearProcess")
    void deleteTemplateForInvalidOfProcess(@RequestBody DeleteTemplateForInvalidOfProcessReq deleteTemplateForInvalidOfProcessReq);

    @ApiOperation(value = "清理机器人兜底回复模板", notes = "清理机器人兜底回复模板")
    @PostMapping("/message/template/clearLastReply")
    void deleteLastReplyTemplateByCustomerId(@RequestParam("customerId")String customerId);

}
