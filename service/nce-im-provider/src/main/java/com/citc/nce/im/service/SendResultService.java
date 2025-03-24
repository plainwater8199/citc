package com.citc.nce.im.service;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum;
import com.citc.nce.auth.contactbacklist.ContactBackListApi;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListPageReq;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListResp;
import com.citc.nce.auth.csp.mediasms.template.MediaSmsTemplateApi;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsTemplatePreviewVo;
import com.citc.nce.auth.csp.videoSms.account.CspVideoSmsAccountApi;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDetailResp;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountQueryDetailReq;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.broadcast.client.FifthSendClient;
import com.citc.nce.im.broadcast.client.MediaSendClient;
import com.citc.nce.im.common.SendMsgClient;
import com.citc.nce.im.excle.VariableData;
import com.citc.nce.im.exp.SendGroupExp;
import com.citc.nce.im.mapper.RobotNodeResultMapper;
import com.citc.nce.im.session.entity.WsResp;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.robot.req.SendMsgReq;
import com.citc.nce.robot.req.TestSendMsgReq;
import com.citc.nce.robot.vo.CheckResult;
import com.citc.nce.robot.vo.MessageCount;
import com.citc.nce.robot.vo.MessageData;
import com.citc.nce.tenant.MsgRecordApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SendResultService {

    @Resource
    RobotNodeResultMapper robotNodeResultMapper;

    @Resource
    RedisTemplate<String, Integer> redisTemplate;

    private static final String phoneRegex = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";

    @Resource
    ContactBackListApi contactBackListApi;

    @Resource
    AccountManagementApi accountManagementApi;

    @Resource
    MediaSmsTemplateApi mediaSmsTemplateApi;

    @Resource
    CspVideoSmsAccountApi cspVideoSmsAccountApi;

    @Resource
    MsgRecordApi msgRecordApi;
    @Resource
    private MessageTemplateApi messageTemplateApi;
    @Autowired
    private FifthSendClient fifthSendClient;
    @Autowired
    private MediaSendClient mediaSendClient;
    @Autowired
    private SendMsgClient sendMsgClient;


    public List<MessageCount> planCount(Long planDetailId) {
        Map<Integer, Long> messageCountMap = msgRecordApi.countByMsgStatus(planDetailId);
        List<MessageCount> messageCounts = new ArrayList<>();
        if (!CollectionUtils.isEmpty(messageCountMap)) {
            MessageCount messageCount;
            for (Map.Entry<Integer, Long> entry : messageCountMap.entrySet()) {
                messageCount = new MessageCount();
                messageCount.setNum(Math.toIntExact(entry.getValue()));
                messageCount.setResultType(entry.getKey().toString());
                messageCounts.add(messageCount);
            }
        }

        //从redis中获取总数
        Integer total = redisTemplate.opsForValue().get("sendDetailId_" + planDetailId);
        MessageCount messageCount = new MessageCount();
        messageCount.setNum(total);
        messageCount.setResultType("total");
        messageCounts.add(messageCount);
        return messageCounts;
    }

    public CheckResult checkContacts(MultipartFile file) {
        try {
            List<VariableData> phoneDataList = EasyExcel.read(file.getInputStream()).sheet(1).head(VariableData.class).doReadSync();
            List<String> phones = phoneDataList.stream().map(VariableData::getPhone).collect(Collectors.toList());
            int mount = phones.size();
            AtomicReference<Long> successCount = new AtomicReference<>();
            successCount.set(0L);
            AtomicReference<Long> failedCount = new AtomicReference<>();
            failedCount.set(0L);
            CheckResult checkResult = new CheckResult();
            ContactBackListPageReq req = new ContactBackListPageReq();
            req.setCreator(SessionContextUtil.getUser().getUserId());
            List<String> blackList = contactBackListApi.getAllList(req).stream().map(ContactBackListResp::getPhoneNum).collect(Collectors.toList());
            phones = phones.stream().distinct().collect(Collectors.toList());
            log.info("黑名单列表为: {}", blackList);
            phones.forEach(p -> {
                if (Pattern.matches(phoneRegex, p) && !blackList.contains(p)) {
                    //手机号码格式正确
                    assert false;
                    successCount.getAndSet(successCount.get() + 1);
                } else {
                    //手机号码格式错误
                    assert false;
                    failedCount.getAndSet(failedCount.get() + 1);
                }
            });
            checkResult.setSuccessCount(successCount.get());
            checkResult.setFailedCount(failedCount.get());
            if (mount != phones.size()) {
                checkResult.setFailedCount(checkResult.getFailedCount() + (mount - phones.size()));
            }
            checkResult.setResult(checkResult.getFailedCount() == 0);
            return checkResult;
        } catch (Exception e) {
            log.error("excel解析错误  {}", e.getMessage());
            e.printStackTrace();
            throw new BizException(SendGroupExp.EXCEL_FORMAT_ERROR);
        }
    }

    /**
     * 测试发送接口
     **/
    public MessageData testSendMessage(TestSendMsgReq testSendMsgReq) {
        log.info("testSendMsgReq:{}", testSendMsgReq);
        if (testSendMsgReq.getPhoneNum() == null || testSendMsgReq.getPhoneNum().isEmpty() || !Pattern.matches(phoneRegex, testSendMsgReq.getPhoneNum())) {
            log.error("手机号码 {}", testSendMsgReq.getPhoneNum());
            throw new BizException(SendGroupExp.PHONE_ERROR);
        }
        BaseUser user = SessionContextUtil.getUser();
        AccountManagementResp account = accountManagementApi.getAccountManagementByAccountId(testSendMsgReq.getChatbotId());
        if (ObjectUtil.isEmpty(account)) {
            throw new BizException(SendGroupExp.TEMPLATE_ERROR);
        }
        if (testSendMsgReq.getResourceType() != MessageResourceType.DEVELOPER.getCode() && (testSendMsgReq.getResourceType() != MessageResourceType.MODULE_SUBSCRIBE.getCode() && testSendMsgReq.getResourceType() != MessageResourceType.MODULE_SIGN.getCode())) {
            if (user != null && !Objects.equals(account.getCustomerId(), user.getUserId())) {
                throw new BizException("机器人不是你的");
            }
        }
        //黑名单中的手机号不能进行测试发送
        ContactBackListPageReq req = new ContactBackListPageReq();
        req.setCreator(account.getCustomerId());
        List<String> blackList = contactBackListApi.getAllList(req).stream().map(ContactBackListResp::getPhoneNum).collect(Collectors.toList());
        boolean isblack = blackList.stream().anyMatch(black -> StringUtils.equals(black, testSendMsgReq.getPhoneNum()));
        if (isblack) {
            throw new BizException(SendGroupExp.BLACKLIST_ERROR);
        }
        //下线的机器人不能进行测试发送
        if (CSPChatbotStatusEnum.STATUS_31_OFFLINE.getCode().equals(account.getChatbotStatus())
                || CSPChatbotStatusEnum.STATUS_42_OFFLINE_CASE_CSP.getCode().equals(account.getChatbotStatus())
                || CSPChatbotStatusEnum.STATUS_71_OFFLINE.getCode().equals(account.getChatbotStatus())) {
            throw new BizException(SendGroupExp.CHATBOT_OFFLINE_ERROR);
        }
        List<String> phoneNums = new ArrayList<>();
        phoneNums.add(testSendMsgReq.getPhoneNum());
        MessageTemplateProvedReq templateProvedReq = new MessageTemplateProvedReq();
        templateProvedReq.setTemplateId(testSendMsgReq.getTemplateId());
        templateProvedReq.setSupplierTag(account.getSupplierTag());
        templateProvedReq.setOperator(account.getAccountTypeCode());
        templateProvedReq.setAccountType(account.getAccountType());
        MessageTemplateResp templateResp = messageTemplateApi.getProvedTemplate(templateProvedReq);
        if (ObjectUtil.isEmpty(templateResp)) {
            throw new BizException(SendGroupExp.TEMPLATE_ERROR);
        }

        //设置阅信/短信回落
        if (testSendMsgReq.getAllowFallback() == 1) {
            //允许回落类型( 1:短信 ,  2:5G阅信)
            Integer fallbackType = testSendMsgReq.getFallbackType();
            //5G阅信回落是否存在( 0:不存在 ,  1:存在)
            if (fallbackType == 2) {
                templateResp.setFallbackReadingLetterTemplateId(testSendMsgReq.getFallbackReadingLetterTemplateId());
            } else {
                templateResp.setFallbackSmsContent(testSendMsgReq.getFallbackSmsContent());
            }
        }

        if (testSendMsgReq.getResourceType() != MessageResourceType.DEVELOPER.getCode() && (testSendMsgReq.getResourceType() != MessageResourceType.MODULE_SUBSCRIBE.getCode() && testSendMsgReq.getResourceType() != MessageResourceType.MODULE_SIGN.getCode())) {
            if (user != null && !Objects.equals(templateResp.getCreator(), user.getUserId())) {
                throw new BizException("模板不是你的");
            }
        }
        if (StrUtil.isNotEmpty(testSendMsgReq.getVariables()) && testSendMsgReq.getVariables().length() > 200) {
            throw new BizException("变量长度大于200");
        }
        VariableData variableData = null;
        if (StringUtils.isNotEmpty(testSendMsgReq.getVariables())) {
            variableData = new VariableData();
            variableData.setPhone(testSendMsgReq.getPhoneNum());
            String[] variables = testSendMsgReq.getVariables().split(",");
            //这代码我是没办法，他原来实体就是这么设计的 ，里面代码也是这么设计的 ，我除非大改造 ，不然我只能这么写
            variableData.setVariables(variables);
        }
        log.info("account.supplierTag: {}, chatbotAccount: {}, chatbotAccountId: {},templateResp:{}", account.getSupplierTag(), account.getChatbotAccount(), account.getChatbotAccountId(), templateResp);
        MessageResourceType messageResourceType = MessageResourceType.fromCode(testSendMsgReq.getResourceType());
        MessageData messageData = fifthSendClient.deductAndSend(account, templateResp, phoneNums, variableData, messageResourceType, testSendMsgReq.getPaymentType(), null);
        if (MessageResourceType.MODULE_SIGN == messageResourceType || MessageResourceType.MODULE_SUBSCRIBE == messageResourceType) {
            saveChatHistoryForChatbot(testSendMsgReq, templateResp, messageData, account);
        }
        return messageData;
    }

    private void saveChatHistoryForChatbot(TestSendMsgReq testSendMsgReq, MessageTemplateResp templateResp, MessageData messageData, AccountManagementResp account) {
        JSONObject msgBody = new JSONObject()
                .fluentPut("type", templateResp.getMessageType())
                .fluentPut("templateId", templateResp.getId())
                .fluentPut("messageDetail", JSON.parse(messageData.getTemplateReplaceModuleInformation()));
        WsResp wsResp = new WsResp();
        wsResp.setPhone(testSendMsgReq.getPhoneNum());
        wsResp.setChatbotAccount(account.getChatbotAccount());
        wsResp.setChatbotAccountId(account.getChatbotAccountId());
        wsResp.setUserId(account.getCustomerId());
        wsResp.setChannelType(account.getAccountType());
        wsResp.setMsgType(templateResp.getMessageType());
        //保存打卡触发的下行消息
        sendMsgClient.saveRecord(wsResp, JSON.toJSONString(msgBody));
    }

    public RichMediaResultArray mediaTestSendMessage(TestSendMsgReq testSendMsgReq) {
        if (testSendMsgReq.getPhoneNum() == null || testSendMsgReq.getPhoneNum().isEmpty() || !Pattern.matches(phoneRegex, testSendMsgReq.getPhoneNum())) {
            log.error("手机号码 {}", testSendMsgReq.getPhoneNum());
            throw new BizException(SendGroupExp.PHONE_ERROR);
        }
        MediaSmsTemplatePreviewVo templateResp = mediaSmsTemplateApi.getContents(testSendMsgReq.getMediaTemplateId(), true);
        templateResp.setTemplateId(testSendMsgReq.getMediaTemplateId());
        ContactBackListPageReq req = new ContactBackListPageReq();
        req.setCreator(SessionContextUtil.getUser().getUserId());
        List<String> blackList = contactBackListApi.getAllList(req).stream().map(ContactBackListResp::getPhoneNum).collect(Collectors.toList());
        if (blackList.stream().anyMatch(black -> StringUtils.equals(black, testSendMsgReq.getPhoneNum()))) {
            throw new BizException(SendGroupExp.BLACKLIST_ERROR);
        }
        if (StringUtils.isEmpty(templateResp.getAccountId())) {
            throw new BizException(SendGroupExp.ACCOUNT_DELETE_ERROR);
        }
        CspVideoSmsAccountQueryDetailReq accountReq = new CspVideoSmsAccountQueryDetailReq();
        accountReq.setAccountId(templateResp.getAccountId());
        CspVideoSmsAccountDetailResp accountDetailResp = cspVideoSmsAccountApi.queryDetail(accountReq);
        if (ObjectUtil.isEmpty(accountDetailResp.getStatus())) {
            throw new BizException(SendGroupExp.ACCOUNT_DELETE_ERROR);
        }
        if (accountDetailResp.getStatus() == 0) {
            throw new BizException(SendGroupExp.ACCOUNT_OFFLINE_ERROR);
        }

        if (ObjectUtil.isEmpty(templateResp)) {
            throw new BizException(SendGroupExp.PHONE_ERROR);
        }
        return mediaSendClient.deductAndSend(
                templateResp,
                accountDetailResp,
                Collections.singletonList(testSendMsgReq.getPhoneNum()),
                testSendMsgReq.getVariables(),
                null,
                MessageResourceType.TEST_SEND,
                testSendMsgReq.getPaymentType()
        );
    }

    public RichMediaResultArray mediaSendMessage(SendMsgReq sendMsgReq) {
        MediaSmsTemplatePreviewVo templateResp = mediaSmsTemplateApi.getContents(sendMsgReq.getMediaTemplateId(), true);
        CspVideoSmsAccountQueryDetailReq accountReq = new CspVideoSmsAccountQueryDetailReq();
        accountReq.setAccountId(templateResp.getAccountId());
        CspVideoSmsAccountDetailResp accountDetailResp = cspVideoSmsAccountApi.queryDetail(accountReq);
        List<String> phones = new ArrayList<>();
        phones.add(sendMsgReq.getPhoneNum());
        return mediaSendClient.deductAndSend(
                templateResp,
                accountDetailResp,
                phones,
                sendMsgReq.getVariables(),
                sendMsgReq.getDeveloperSenId(),
                MessageResourceType.DEVELOPER,
                sendMsgReq.getPaymentType()
        );
    }
}
