package com.citc.nce.im.broadcast.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.csp.mediasms.template.MediaSmsTemplateApi;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsTemplatePreviewVo;
import com.citc.nce.auth.csp.smsTemplate.SmsTemplateApi;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateDetailVo;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.common.util.SpringUtils;
import com.citc.nce.dto.VerificationReq;
import com.citc.nce.fileApi.PlatformApi;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.ExamineReq;
import com.citc.nce.im.broadcast.exceptions.GroupPlanExecuteException;
import com.citc.nce.im.broadcast.exceptions.GroupPlanStoppedException;
import com.citc.nce.im.broadcast.node.BroadcastNode;
import com.citc.nce.im.broadcast.node.FifthNode;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.citc.nce.msgenum.SendStatus.MATERIAL_REVIEW;

/**
 * @author jcrenc
 * @since 2024/06/25 15:09
 */
public final class BroadcastTemplateUtils {


    /**
     * 根据模板id获取可用的5G消息模板,
     * 如果chatbotList运营商处的素材过期或临期（3天）则会自动送审素材（全部素材、全部运营商）并抛出GroupPlanExecuteException
     */
    public static MessageTemplateResp getCheckedFifthGenTemplate(Long templateId, List<AccountManagementResp> chatbotList, FifthNode node) {
        List<String> chatbotAccounts = new ArrayList<>(chatbotList.size());
        List<String> operators = new ArrayList<>(chatbotList.size());
        for (AccountManagementResp chatbot : chatbotList) {
            chatbotAccounts.add(chatbot.getChatbotAccount());
            operators.add(chatbot.getAccountType());
        }
        MessageTemplateApi messageTemplateApi = SpringUtils.getBean(MessageTemplateApi.class);
        PlatformApi platformApi = SpringUtils.getBean(PlatformApi.class);
        FileApi fileApi = SpringUtils.getBean(FileApi.class);
        String operatorStr = String.join(",", operators);
        MessageTemplateProvedReq provedReq = new MessageTemplateProvedReq();
        provedReq.setTemplateId(templateId);
        provedReq.setAccountType(operatorStr);
        MessageTemplateResp template = messageTemplateApi.getProvedTemplate(provedReq);
        if (template == null)
            throw new GroupPlanExecuteException("5G消息模板不存在");
        //设置回落内容,设置进MessageTemplateResp 中
        if (node.getAllowFallback() == 1) {
            //允许回落类型( 1:短信 ,  2:5G阅信)
            Integer fallbackType = node.getFallbackType();
            //5G阅信回落是否存在( 0:不存在 ,  1:存在)
            if (fallbackType == 2) {
                template.setFallbackReadingLetterTemplateId(node.getFallbackReadingLetterTemplateId());
            } else {
                template.setFallbackSmsContent(node.getFallbackSmsContent());
            }
        }
        List<String> materialUuids = find5gTemplateMaterialFileUuid(template.getModuleInformation(), template.getMessageType());
        if (!CollectionUtils.isEmpty(materialUuids)) {
            VerificationReq req = new VerificationReq();
            req.setFileIds(materialUuids);
            req.setDays(3);
            req.setAccounts("[" + String.join(",", chatbotAccounts) + "]");
            req.setOperators(operatorStr);
            if (!platformApi.verification(req)) {
                //如果素材临期，先送审素材，再抛出执行异常停止执行
                ExamineReq examineReq = new ExamineReq();
                examineReq.setFileUUIDs(materialUuids);
                examineReq.setChatbotAccounts(chatbotAccounts);
                examineReq.setCreator(template.getCreator());
                fileApi.examine(examineReq);
                throw new GroupPlanExecuteException("素材审核中");
            }
        }
        return template;
    }

    /**
     * 根据模板id获取可用的视频短信模板
     */
    public static MediaSmsTemplatePreviewVo getCheckedMediaTemplate(Long templateId) {
        MediaSmsTemplateApi mediaSmsTemplateApi = SpringUtils.getBean(MediaSmsTemplateApi.class);
        MediaSmsTemplatePreviewVo contents = mediaSmsTemplateApi.getContents(templateId, true);
        if (Objects.isNull(contents))
            throw new GroupPlanExecuteException("视频短信模板不存在");
        return contents;
    }

    /**
     * 根据模板id获取可用的短信模板
     */
    public static SmsTemplateDetailVo getCheckedSmsTemplate(Long templateId) {
        SmsTemplateApi smsTemplateApi = SpringUtils.getBean(SmsTemplateApi.class);
        SmsTemplateDetailVo template = smsTemplateApi.getTemplateInfoInner(templateId, false);
        if (Objects.isNull(template))
            throw new GroupPlanExecuteException("短信模板不存在");
        return template;
    }

    /**
     * 查找5G消息模板里的素材，并返回素材id列表
     *
     * @param templateContent 模板内容
     * @param messageType     模板消息类型
     * @return 素材id列表
     */
    public static List<String> find5gTemplateMaterialFileUuid(String templateContent, int messageType) {
        JSONObject templateJson = JSONObject.parseObject(templateContent);
        List<String> fileUrlIds = new ArrayList<>();
        switch (messageType) {
            //图片
            case 2:
                fileUrlIds.add(templateJson.getString("pictureUrlId"));
                break;
            //视频
            case 3:
                fileUrlIds.add(templateJson.getString("videoUrlId"));
                break;
            //音频
            case 4:
                fileUrlIds.add(templateJson.getString("audioUrlId"));
                break;
            //文件
            case 5:
                fileUrlIds.add(templateJson.getString("fileUrlId"));
                break;
            //单卡
            case 6:
                fileUrlIds.addAll(getTemplateFileIds(templateJson));
                break;
            //多卡
            case 7:
                JSONArray cardList = templateJson.getJSONArray("cardList");
                for (int i = 0; i < cardList.size(); i++) {
                    fileUrlIds.addAll(getTemplateFileIds(cardList.getJSONObject(i)));
                }
                break;
        }
        return fileUrlIds;
    }

    private static List<String> getTemplateFileIds(JSONObject templateJson) {
        List<String> fileIds = new ArrayList<>();
        if (StringUtils.isNotEmpty(templateJson.getString("pictureUrlId"))) {
            fileIds.add(templateJson.getString("pictureUrlId"));
        }
        if (StringUtils.isNotEmpty(templateJson.getString("videoUrlId"))) {
            fileIds.add(templateJson.getString("videoUrlId"));
        }
        if (StringUtils.isNotEmpty(templateJson.getString("audioUrlId"))) {
            fileIds.add(templateJson.getString("audioUrlId"));
        }
        return fileIds;
    }

}
