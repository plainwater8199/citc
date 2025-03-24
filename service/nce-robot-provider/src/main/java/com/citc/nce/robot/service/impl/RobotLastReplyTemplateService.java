package com.citc.nce.robot.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountChatbotAccountQueryReq;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateIdResp;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateReq;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.robot.vo.RebotSettingReq;
import com.citc.nce.robot.vo.RobotDefaultReplySettingVo;
import com.citc.nce.robot.vo.RobotProcessButtonReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yy
 * @date 2024-03-28 22:32:21
 */
@Component
@Slf4j
@EnableConfigurationProperties({SuperAdministratorUserIdConfigure.class})
public class RobotLastReplyTemplateService {
    @Resource
    private AccountManagementApi accountManagementApi;
    @Resource
    private MessageTemplateApi messageTemplateApi;
    private SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;

    /**
     * 机器人兜底回复模板审核
     *
     * @param model
     * @return
     */

    public List<String> auditLastReply(RebotSettingReq model) {
        List<String> templateIds = new ArrayList<>();
        //送审当前用户下 所有的机器人
        String buttonListStr = makeShortcutButtons(model.getButtonList());
        List<JSONObject> lastReplyJsonList = JSONObject.parseArray(model.getLastReply(), JSONObject.class);
        messageTemplateApi.deleteLastReplyTemplateByCustomerId(SessionContextUtil.getUserId());
        lastReplyJsonList.forEach(messageTemplateReqJson -> {
            MessageTemplateReq messageTemplateReq = makeTemplateBody(messageTemplateReqJson, buttonListStr);
            try {
                messageTemplateReq.setId(null);
                //每次保存机器人设置时都删除兜底模板，再新建（防止有templateId保存错误，导致蜂动兜底触发失败）
                MessageTemplateIdResp messageTemplateIdResp = messageTemplateApi.saveMessageTemplate(messageTemplateReq);
                if (messageTemplateIdResp.getSuccessNum() > 0) {
                    templateIds.add(Long.toString(messageTemplateIdResp.getId()));
                    messageTemplateReqJson.put("templateId", messageTemplateIdResp.getId() + "");
                }
            } catch (Exception exception) {
                log.error("兜底回复送审出错，", exception);
                log.error("兜底回复送审出错，模板信息：{}", messageTemplateReqJson);
            }
        });
        model.setLastReply(JSONObject.toJSONString(lastReplyJsonList));
        return templateIds;
    }

    public List<String> auditLastReply(RobotDefaultReplySettingVo settingVo,List<RobotProcessButtonReq> buttonList) {
        List<String> templateIds = new ArrayList<>();
        //送审当前用户下 所有的机器人
        List<AccountManagementResp> accountManagementResps = accountManagementApi.getChatbotAccountInfoByCustomerId(SessionContextUtil.getUser().getUserId());
        String buttonListStr = makeShortcutButtons(buttonList);
        List<JSONObject> lastReplyJsonList = JSONObject.parseArray(settingVo.getLastReply(), JSONObject.class);

        lastReplyJsonList.forEach(messageTemplateReqJson -> {
            MessageTemplateReq messageTemplateReq = makeTemplateBody(messageTemplateReqJson, buttonListStr);
            try {
                MessageTemplateIdResp messageTemplateIdResp=new MessageTemplateIdResp();
                if(ObjectUtil.isNotEmpty(messageTemplateReq.getId()))
                {
                    messageTemplateIdResp = messageTemplateApi.updateMessageTemplate(messageTemplateReq);

                }else {
                    messageTemplateIdResp = messageTemplateApi.saveMessageTemplate(messageTemplateReq);
                }
                if (messageTemplateIdResp.getSuccessNum() > 0) {
                    templateIds.add(Long.toString(messageTemplateIdResp.getId()));
                    messageTemplateReqJson.put("templateId", messageTemplateIdResp.getId());
                }
            } catch (Exception exception) {
                log.error("兜底回复送审出错，", exception);
                log.error("兜底回复送审出错，模板信息：{}",messageTemplateReqJson);
            }
        });
        settingVo.setLastReply(JSONObject.toJSONString(lastReplyJsonList));
        return templateIds;
    }

    public void addAuditLastReplyForChatbot(RebotSettingReq model, String chatbotAccount) {
        List<String> templateIds = new ArrayList<>();
        JSONArray templateArray = JSONArray.parseArray(model.getLastReply());
        //指定兜底模板送审chatbot账号
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            AccountChatbotAccountQueryReq accountChatbotAccountQueryReq = new AccountChatbotAccountQueryReq();
            accountChatbotAccountQueryReq.setChatbotAccountList(Arrays.asList(chatbotAccount));
            accountChatbotAccountQueryReq.setCreator(userId);
            List<AccountManagementResp> accountManagementResps = accountManagementApi.getListByChatbotAccounts(accountChatbotAccountQueryReq);
            if (ObjectUtil.isEmpty(accountManagementResps) || !accountManagementResps.get(0).getCreator().equals(userId)) {
                log.error("越权操作兜底送审,操作chatbot:{},用户:{}", chatbotAccount, SessionContextUtil.getUser().getUserId());
                throw new BizException("越权操作兜底送审");
            }
        }

        templateArray.forEach(itemJson -> {
            //chatbotAccount不为空表示新增送审通道
            Long templateId = (Long) ((JSONObject) itemJson).get("templateId");
            messageTemplateApi.publicTemplate(templateId, chatbotAccount, 2);

        });
    }

    public MessageTemplateReq makeTemplateBody(JSONObject replyJson, String buttListStr) {

        MessageTemplateReq messageTemplateReq = new MessageTemplateReq();
        messageTemplateReq.setTemplateName("lastRely_" + IdUtil.nanoId(11));
        messageTemplateReq.setTemplateSource(Constants.TEMPLATE_SOURCE_LASTREPLY);
        messageTemplateReq.setId(replyJson.getLong("templateId"));
        String messageContentStr = replyJson.getString("messageDetail");
        messageTemplateReq.setTemplateType(messageContentStr.contains("}}") ? Constants.TEMPLATE_TYPE_VARIABLE : Constants.TEMPLATE_TYPE_GENARAL);
        messageTemplateReq.setMessageType(replyJson.getInteger("type"));
        messageTemplateReq.setShortcutButton(buttListStr);
        messageTemplateReq.setStyleInformation(replyJson.getString("styleInfo"));
        messageTemplateReq.setModuleInformation(replyJson.getString("messageDetail"));
        //是否需要送审，1 保存是不需要送审，2发布时需要送审
        messageTemplateReq.setNeedAudit(2);
        return messageTemplateReq;
    }

    private String makeShortcutButtons(List<RobotProcessButtonReq> buttonList) {
        JSONArray objectList = new JSONArray();
        if (buttonList != null) {
            buttonList.forEach(robotProcessButtonResp -> {
                JSONObject btnItem = new JSONObject();
                String buttonDetail = robotProcessButtonResp.getButtonDetail();
                JSONObject button = JSONObject.parseObject(buttonDetail);
                btnItem.put("buttonDetail", button);
                btnItem.put("uuid", robotProcessButtonResp.getUuid());
                btnItem.put("type", robotProcessButtonResp.getButtonType());
                objectList.add(btnItem);

            });
        }
        return JSONObject.toJSONString(objectList);
    }
}
