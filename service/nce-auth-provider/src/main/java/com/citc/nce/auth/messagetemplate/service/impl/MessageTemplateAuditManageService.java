package com.citc.nce.auth.messagetemplate.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTypeReq;
import com.citc.nce.auth.messagetemplate.dao.MessageTemplateAuditDao;
import com.citc.nce.auth.messagetemplate.dao.MessageTemplateDao;
import com.citc.nce.auth.messagetemplate.dao.MessageTemplateProvedDao;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateAuditDo;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateDo;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateProvedDo;
import com.citc.nce.auth.messagetemplate.entity.TemplateDataResp;
import com.citc.nce.auth.messagetemplate.service.MessageTemplateAuditService;
import com.citc.nce.auth.messagetemplate.service.TemplateContentMakeUp;
import com.citc.nce.auth.messagetemplate.vo.SupportDetailsReq;
import com.citc.nce.auth.messagetemplate.vo.TemplateStatusCallbackReq;
import com.citc.nce.auth.messagetemplate.vo.TemplateSupportInfoReq;
import com.citc.nce.auth.readingLetter.template.dao.ReadingLetterTemplateAuditDao;
import com.citc.nce.auth.readingLetter.template.dao.ReadingLetterTemplateProvedDao;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateAuditDo;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateDo;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateProvedDo;
import com.citc.nce.common.constants.CarrierEnum;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.constants.TemplateButtonTypeConst;
import com.citc.nce.common.constants.TemplateMessageTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.BizExposeStatusException;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.filecenter.vo.TemplateOwnershipReflect;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 模板保存时送审(5G消息模板+阅信模板)
 *
 * @author yy
 * @date 2024-03-12 10:08:52
 */
@Component
@Slf4j
public class MessageTemplateAuditManageService {
    @Resource
    private UpdateProcessStatus UpdateProcessStatus;
    @Resource
    private TemplateContentMakeUp templateContentMakeUp;
    @Resource
    private MessageTemplateAuditDao messageTemplateAuditDao;
    @Resource
    private ReadingLetterTemplateAuditDao readingLetterTemplateAuditDao;
    @Resource
    private ReadingLetterTemplateProvedDao readingLetterTemplateProvedDao;
    @Resource
    private MessageTemplateDao messageTemplateDao;
    @Resource
    private MessageTemplateProvedDao messageTemplateProvedDao;
    @Resource
    private MessageTemplateAuditFontdoService messageTemplateAuditFontdoService;
    @Resource
    private MessageTemplateAuditOwnerService messageTemplateAuditOwnerService;
    @Resource
    private AccountManagementApi accountManagementApi;
    @Resource
    RedissonClient redissonClient;
    @Resource
    private RedisService redisService;
    @Value("${template.variable_limit}")
    int templateVariableLimit;
    @Value("${rocketmq.multiTrigger.topic}")
    private String multiTriggerTopic;
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    /**
     * 网关模板状态和系统状态映射
     */
    static Map<String, Integer> templateStatusMap = new HashMap<>();

    static {
        templateStatusMap.put("WAITING", -1);
        templateStatusMap.put("PENDING", 1);
        templateStatusMap.put("FAILED", 2);
        templateStatusMap.put("SUCCESS", 0);
    }

    public String publicTemplate(Long templateId, String operators, Integer isChecked) {
        log.info("发布模板：id：{}", templateId);
        log.info("指定用户：operators：{},为空为未指定", operators);
        MessageTemplateDo messageTemplateDo = messageTemplateDao.selectById(templateId);
        String errMsg = "";
        if (ObjectUtil.isEmpty(messageTemplateDo)) {
            errMsg = "模板不存在," + templateId;
            log.error(errMsg);
            return errMsg;
        }
        List<TemplateOwnershipReflect> templateOwnershipReflects = getAuditTemplateOwnershipReflects(messageTemplateDo, operators);
        if (ObjectUtil.isEmpty(templateOwnershipReflects)) {
            errMsg = com.citc.nce.auth.messagetemplate.entity.Constants.MATERIAL_NOTSUPPORT_ACCOUNT;
            log.error(errMsg);
            return errMsg;
        }
        String result = extraTemplateManage(messageTemplateDo, templateOwnershipReflects, 2, null == isChecked ? Constants.isChecked_first : isChecked);
        //释放模板送审缓存锁
        redisService.deleteObject(String.format(Constants.TEMPLATE_AUDIT_CACHE_REDIS_KEY, messageTemplateDo.getId()));
        log.error("送审结果：" + result);
        return result;
    }

    /**
     * 保存后，需要送审过期素材，和送审模板
     *
     * @return
     */
    public String extraTemplateManage(MessageTemplateDo messageTemplateDo, List<TemplateOwnershipReflect> shareTemplateOwnershipReflects, int needAudit, int isChecked) {
        List<String> auditResultStr = new ArrayList<>();
        MessageTemplateAuditService messageTemplateAuditService;
        if (Constants.isChecked_first == isChecked && checkFontdoNotSupportButtonContain(messageTemplateDo)) {
            List<TemplateOwnershipReflect> fontdoShipReflect = shareTemplateOwnershipReflects.stream().filter(templateOwnershipReflectUnit -> templateOwnershipReflectUnit.getSupplierTag().equals(Constants.SUPPLIER_TAG_FONTDO)).collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(fontdoShipReflect)) {
                String chatbots = fontdoShipReflect.stream().map(TemplateOwnershipReflect::getChatbotName).collect(Collectors.joining(","));
                String errorMsg = chatbots;
                throw new BizExposeStatusException(408, errorMsg);
            }
        }
        for (int i = 0; i < shareTemplateOwnershipReflects.size(); i++) {
            TemplateOwnershipReflect templateOwnershipReflect = shareTemplateOwnershipReflects.get(i);
            if (StrUtil.isEmpty(templateOwnershipReflect.getSupplierTag())) {
                AccountManagementTypeReq accountManagementTypeReq = new AccountManagementTypeReq();
                accountManagementTypeReq.setAccountType(templateOwnershipReflect.getOperator());
                accountManagementTypeReq.setCreator(messageTemplateDo.getCreator());
                AccountManagementResp accountManagementResp = accountManagementApi.getAccountManagementByAccountType(accountManagementTypeReq);
                if (ObjectUtil.isEmpty(accountManagementResp)) {
                    log.error("模板审核，无该运营商chatbot有效账号," + JSONObject.toJSONString(templateOwnershipReflect));
                    auditResultStr.add(templateOwnershipReflect.getOperator());
                    continue;
                }
                templateOwnershipReflect.setSupplierTag(accountManagementResp.getSupplierTag());
            }
            if (templateOwnershipReflect.getSupplierTag().equals(Constants.SUPPLIER_TAG_FONTDO)) {
                messageTemplateAuditService = messageTemplateAuditFontdoService;
            } else {
                messageTemplateAuditService = messageTemplateAuditOwnerService;
            }
            //2.保存模板待审核信息，将状态改为待审核
            TemplateDataResp tmpTemplateDataResp = new TemplateDataResp();
            tmpTemplateDataResp.setCode(500);
            MessageTemplateAuditDo messageTemplateAuditDo = saveAuditTemplateAudit(messageTemplateDo, templateOwnershipReflect, tmpTemplateDataResp);
            //保存模板不需要送审
            if (Constants.TEMPLATE_AUDIT_NOT == needAudit) {
                log.info("模板不送审");
                continue;
            }
            TemplateDataResp<String> templateDataResp = new TemplateDataResp<>();
            try {
                //2.去服务商创建模板
                log.info("模板送审,通道：" + templateOwnershipReflect.getOperator() + "#" + templateOwnershipReflect.getSupplierTag());
                templateDataResp = messageTemplateAuditService.applyCreateTemplate(messageTemplateDo, templateOwnershipReflect);
                if (ObjectUtil.isNotNull(templateDataResp) && ObjectUtil.isNotEmpty(templateDataResp.getData())) {
                    redisService.setCacheObject(String.format(Constants.TEMPLATE_AUDIT_CACHE_REDIS_KEY, templateDataResp.getData()), templateDataResp.getData(), 300L, TimeUnit.SECONDS);
                }
                log.info("模板送审结果,通道：" + templateOwnershipReflect.getOperator() + "#" + templateOwnershipReflect.getSupplierTag() + "，返回内容：" + JSONObject.toJSONString(templateDataResp));
            } catch (Exception exception) {
                log.error("模板送审失败", exception);
                templateDataResp.setMessage(exception.getMessage());
                templateDataResp.setCode(500);
            } finally {
                //3.存储服务商返回信息
                messageTemplateAuditDo = updateMessageTemplateAuditDo(templateOwnershipReflect, templateDataResp, messageTemplateAuditDo);
                log.info("保存模板送审结果完成");
            }
            log.info("模板来源：{},通道：{}", messageTemplateAuditDo.getTemplateSource(), messageTemplateAuditDo.getSupplierTag());
            log.info("模板来源是否相同：{}", messageTemplateAuditDo.getTemplateSource() == Constants.TEMPLATE_SOURCE_ROBOT);
            log.info("通道是否自有：{}", Constants.SUPPLIER_TAG_OWNER.equals(messageTemplateAuditDo.getSupplierTag()));
            if (messageTemplateAuditDo.getTemplateSource() == Constants.TEMPLATE_SOURCE_ROBOT && Constants.SUPPLIER_TAG_OWNER.equals(messageTemplateAuditDo.getSupplierTag())) {
                UpdateProcessStatus.updateProcessStatus(messageTemplateAuditDo.getProcessId(), messageTemplateAuditDo.getProcessDescId(), messageTemplateAuditDo.getStatus(), messageTemplateAuditDo.getTemplateId());
            }
            //csp自身通道的模板直接审核通过，直接回填流程状态
            // 4.非蜂动的审核通过的，复制一份到审核通过表
            saveMessageTemplateProved(messageTemplateDo, messageTemplateAuditDo);
            if (templateDataResp.getCode() != 200) {
                auditResultStr.add(templateOwnershipReflect.getOperator());
            }
            //删除模板审核缓存
            if (ObjectUtil.isNotNull(templateDataResp) && ObjectUtil.isNotEmpty(templateDataResp.getData())) {
                redisService.deleteObject(String.format(Constants.TEMPLATE_AUDIT_CACHE_REDIS_KEY, templateDataResp.getData()));
            }
        }
        //送审不完全成功
        if (auditResultStr.size() != 0) {
            return String.format(com.citc.nce.auth.messagetemplate.entity.Constants.TEMPLATE_AUDIT_FAILED, String.join(", ", auditResultStr));
        }

        return "";
    }

    //检查模板里是否包含了拍摄和调起联系人按钮
    boolean checkFontdoNotSupportButtonContain(MessageTemplateDo messageTemplateReq) {
        String shortcutButtonStr = messageTemplateReq.getShortcutButton();
        if (StrUtil.isNotEmpty(shortcutButtonStr)) {
            JSONArray shortcutButtonJsonArr = JSONObject.parseArray(shortcutButtonStr);
            if (checkButtonArrContailFontdoNotSupport(shortcutButtonJsonArr)) return true;
        }
        int type = messageTemplateReq.getMessageType();
        JSONObject jsonObject = JSONObject.parseObject(messageTemplateReq.getModuleInformation(), JSONObject.class);
        switch (type) {
            //发送卡片消息
            case 6:
            case 7:
                JSONArray cardList = jsonObject.getJSONArray("cardList");
                //单卡
                if (CollectionUtil.isEmpty(cardList)) {
                    if (null != jsonObject.getJSONArray("buttonList")) {
                        JSONArray btnArray = jsonObject.getJSONArray("buttonList");
                        return (checkButtonArrContailFontdoNotSupport(btnArray));
                    }
                } else {
                    //多卡
                    for (int i = 0; i < cardList.size(); i++) {
                        JSONObject cardJson = cardList.getJSONObject(i);
                        if (null != cardJson.getJSONArray("buttonList")) {
                            JSONArray btnArray = cardJson.getJSONArray("buttonList");
                            if (checkButtonArrContailFontdoNotSupport(btnArray)) return true;
                        }
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    boolean checkButtonArrContailFontdoNotSupport(JSONArray jsonArr) {
        Long notSupportButtonJsonCount = jsonArr.stream().filter(jsonUnit -> {
            JSONObject jsonObject = (JSONObject) jsonUnit;
            Integer buttonType = jsonObject.getInteger("type");
            return TemplateButtonTypeConst.capture.equals(buttonType) || TemplateButtonTypeConst.sendMsg.equals(buttonType);
        }).count();
        return notSupportButtonJsonCount.compareTo(0L) > 0;
    }

    MessageTemplateAuditDo updateMessageTemplateAuditDo(TemplateOwnershipReflect
                                                                templateOwnershipReflect, TemplateDataResp<String> templateDataResp, MessageTemplateAuditDo
                                                                messageTemplateAuditDo) {
        messageTemplateAuditDo.setStatus(templateDataResp.getCode() == 200 ? templateOwnershipReflect.getSupplierTag().equals(Constants.SUPPLIER_TAG_FONTDO) ? Constants.TEMPLATE_STATUS_PENDING : Constants.TEMPLATE_STATUS_SUCCESS : Constants.TEMPLATE_STATUS_FAILED);
        messageTemplateAuditDo.setRemark(templateDataResp.getMessage());
        if (ObjectUtil.isNotEmpty(templateDataResp.getData())) {
            messageTemplateAuditDo.setPlatformTemplateId(templateDataResp.getData());
        }
        messageTemplateAuditDao.updateById(messageTemplateAuditDo);
        return messageTemplateAuditDo;
    }

    public List<TemplateOwnershipReflect> getAuditTemplateOwnershipReflects(MessageTemplateDo
                                                                                    messageTemplateDo, String operators) {
        //如果是文本或者位置，没有素材，送审该用户下所有账号，或送审accounts账号
        List<TemplateOwnershipReflect> shareTemplateOwnershipReflects;
        if ((TemplateMessageTypeEnum.Text.getType() == messageTemplateDo.getMessageType() ||
                TemplateMessageTypeEnum.Location.getType() == messageTemplateDo.getMessageType())) {
            return templateContentMakeUp.getTemplateOwnershipReflects(operators, messageTemplateDo);
        }
        //1.检查素材是否曾审核通过 ，电联移动，硬核桃直接模板审核通过，蜂动发申请到网关
        shareTemplateOwnershipReflects = checkMaterial(messageTemplateDo, operators);
        return shareTemplateOwnershipReflects;
    }

    /**
     * 检查变量数量
     *
     * @param templateModuleJsonStr
     * @return
     */
    public boolean checkVariableJSONArray(String templateModuleJsonStr) {
        JSONArray jsonCount = templateContentMakeUp.getVariableJSONArray(templateModuleJsonStr);
        return jsonCount.size() <= templateVariableLimit;
    }


    /**
     * 素材检查是否送审和过期
     *
     * @param messageTemplateDo
     * @param operators
     * @return
     */
    public List<TemplateOwnershipReflect> checkMaterial(MessageTemplateDo messageTemplateDo, String operators) {
        return templateContentMakeUp.checkMaterialAndProve(messageTemplateDo, operators);
    }

    /**
     * 网关模板状态回调
     *
     * @param templateStatusCallbackDTO
     */
    public void templateStatusCallabck(TemplateStatusCallbackReq templateStatusCallbackDTO) {
        //防止同时进入
        String key = String.format(Constants.TEMPLATE_AUDIT_CALLBACK_LOCK_REDIS_KEY, templateStatusCallbackDTO.getId());
        RLock lock = redissonClient.getLock(key);
        try {
            lock.lock();
            while (redisService.hasKey(String.format(Constants.TEMPLATE_AUDIT_CACHE_REDIS_KEY, templateStatusCallbackDTO.getId()))) {
                try {
                    log.info("模板审核回调等待释放中:{}",templateStatusCallbackDTO);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
            log.info("模板状态审核回调：{}", JSONObject.toJSONString(templateStatusCallbackDTO));
            if (StrUtil.isEmpty(templateStatusCallbackDTO.getTemplateStatus()) && CollectionUtil.isEmpty(templateStatusCallbackDTO.getSupportDetails())) {
                log.info("收到回调状态为空，手动设置为：FAILED");
                templateStatusCallbackDTO.setTemplateStatus("FAILED");
                templateStatusCallbackDTO.setRemark("审核状态为空");
            }
            // AIM 智能短信模板 (阅信模板)
            if ("AIM".equalsIgnoreCase(templateStatusCallbackDTO.getType())) {
                aimTemplateCallbackStatus(templateStatusCallbackDTO);
            }
            //     * RCS 5G消息模板
            else {
                QueryWrapper<MessageTemplateAuditDo> wrapper = new QueryWrapper();
                wrapper.eq("platform_template_id", templateStatusCallbackDTO.getId());
                wrapper.eq("deleted", 0);
                MessageTemplateAuditDo messageTemplateAuditDo = messageTemplateAuditDao.selectOne(wrapper);
                if (ObjectUtil.isNull(messageTemplateAuditDo)) {
                    //如果是机器人多触发流程里的审核
                    String templateId = templateStatusCallbackDTO.getId();
                    String task = redisService.getCacheObject(Constants.FONTDO_NEW_TEMPLATE_SEND_TASK_PREFIX + templateId);
                    if (StrUtil.isNotBlank(task)) {
                        //远程调用im-servcie 创建一次发送任务
                        Message<String> message = MessageBuilder.withPayload(task + ":" + templateId).build();
                        log.info("创建一次发送任务,task:" + task + ":" + templateId);
                        rocketMQTemplate.asyncSend(multiTriggerTopic, message, new SendCallback() {
                            @Override
                            public void onSuccess(SendResult sendResult) {
                                log.info("send multi trigger message success,task+platformTemplateId:{}", task + ":" + messageTemplateAuditDo.getPlatformTemplateId());
                            }

                            @Override
                            public void onException(Throwable e) {
                                log.error("{}.send multi trigger message error", task + ":" + messageTemplateAuditDo.getPlatformTemplateId(), e);
                            }
                        });
                    } else {
                        log.error("机器人多触发流程里的模板状态审核回调错误，模板审核记录不存在");
                    }
                    return;
                }
                MessageTemplateDo messageTemplateDo = messageTemplateDao.getTemplateByPlatformTemplateId(templateStatusCallbackDTO.getId());
                if (ObjectUtil.isNull(messageTemplateDo)) {
                    log.error("模板状态审核回调错误，系统模板不存在");
                    return;
                }
                int auditStatus = 1;
                if (ObjectUtil.isNotNull(templateStatusCallbackDTO.getTemplateStatus())) {
                    auditStatus = templateStatusMap.get(templateStatusCallbackDTO.getTemplateStatus());
                }
                if ((messageTemplateAuditDo.getStatus() == 0 || messageTemplateAuditDo.getStatus() == 2) && auditStatus == 1) {
                    log.error("模板状态实际已经审核通过或审核失败，收到审核中状态不进行处理");
                    return;
                }
                if (ObjectUtil.isNotEmpty(templateStatusCallbackDTO.getSupportDetails())) {
                    for (SupportDetailsReq item : templateStatusCallbackDTO.getSupportDetails()) {
                        if (item.getCarrierType().equalsIgnoreCase(CarrierEnum.getCarrierEnum(messageTemplateAuditDo.getOperator()).getTag())) {
                            auditStatus = templateStatusMap.get(item.getAuditStatus());
                            break;
                        }
                    }
                }
                messageTemplateAuditDo.setStatus(auditStatus);
                messageTemplateAuditDao.updateById(messageTemplateAuditDo);
                //如果审核成功把模板移动到审核成功表，防止修改模板影响现有群发计划
                if (auditStatus == Constants.TEMPLATE_STATUS_SUCCESS) {
                    saveMessageTemplateProved(messageTemplateDo, messageTemplateAuditDo);
                }
                //更新机器人流程信息
                if (messageTemplateAuditDo.getTemplateSource() == Constants.TEMPLATE_SOURCE_ROBOT) {
                    UpdateProcessStatus.updateProcessStatus(messageTemplateAuditDo.getProcessId(), messageTemplateAuditDo.getProcessDescId(), messageTemplateAuditDo.getStatus(), messageTemplateAuditDo.getTemplateId());
                }
                log.info("模板状态审核回调完成：{}", templateStatusCallbackDTO.getId());
            }
        } finally {
            //使用lock方法加锁，一定会持有锁，不需要再判断是否加锁以及是否被当前线程加锁
            lock.unlock();
        }
    }

    /**
     * 阅信回调
     *
     * @param templateStatusCallbackDTO
     */
    private void aimTemplateCallbackStatus(TemplateStatusCallbackReq templateStatusCallbackDTO) {
        QueryWrapper<ReadingLetterTemplateAuditDo> wrapper = new QueryWrapper();
        //假设一个平台id可能对应多个运营商，查询返回list
        wrapper.eq("platform_template_id", templateStatusCallbackDTO.getId());
        wrapper.eq("deleted", 0);
        ReadingLetterTemplateAuditDo readingLetterTemplateAuditDo = readingLetterTemplateAuditDao.selectOne(wrapper);
        if (ObjectUtil.isEmpty(readingLetterTemplateAuditDo)) {
            log.error("模板状态审核回调错误，模板审核记录不存在,platform_template_id:{}", templateStatusCallbackDTO.getId());
            throw new BizException("模板状态审核回调错误，模板审核记录不存在,platform_template_id: " + templateStatusCallbackDTO.getId());
        }

        ReadingLetterTemplateDo readingLetterTemplateDo = readingLetterTemplateAuditDao.getTemplateByPlatformTemplateId(templateStatusCallbackDTO.getId());
        if (ObjectUtil.isNull(readingLetterTemplateDo)) {
            log.error("模板状态审核回调错误，系统模板不存在,platform_template_id:{}", templateStatusCallbackDTO.getId());
            throw new BizException("模板状态审核回调错误，系统模板不存在,platform_template_id: " + templateStatusCallbackDTO.getId());
        }
        int auditStatus = 1;

        //   包含多个运营商的情况
        if (ObjectUtil.isNotEmpty(templateStatusCallbackDTO.getSupportDetails())) {
            //可能出现多个,甚至多个同运营商的
            List<SupportDetailsReq> supportDetails = templateStatusCallbackDTO.getSupportDetails();
            //过滤掉whole,按照运营商分组
            supportDetails = supportDetails.stream().filter(item -> !"WHOLE".equalsIgnoreCase(item.getCarrierType())).collect(Collectors.toList());
            //找到通过的SupportDetails
            List<SupportDetailsReq> successSupportDetails = supportDetails.stream()
                    .filter(item -> "SUCCESS".equalsIgnoreCase(item.getAuditStatus()))
                    .collect(Collectors.toList());
            //有通过的
            if (CollectionUtil.isNotEmpty(successSupportDetails)) {
                for (SupportDetailsReq supportDetailsReq :
                        supportDetails) {
                    //筛选送审记录和回调信息里运营商一致的supportDetail 和送审记录
                    if (!supportDetailsReq.getCarrierType().equalsIgnoreCase(CarrierEnum.getCarrierEnum(readingLetterTemplateAuditDo.getOperatorCode()).getTag()))
                        continue;
                    //修改备注
                    readingLetterTemplateAuditDo.setRemark(supportDetailsReq.getAuditRemark());
                    //修改状态
                    readingLetterTemplateAuditDo.setStatus(templateStatusMap.get("SUCCESS"));
                    //拿到所有的supports
                    List<TemplateSupportInfoReq> supports = CollectionUtil.newArrayList();
                    successSupportDetails.forEach(detail -> supports.addAll(supportDetailsReq.getSupports()));

                    String applicableTerminal = supports.stream()
                            .filter(support -> support.getAuditStatus().equalsIgnoreCase("SUCCESS"))
                            .map(TemplateSupportInfoReq::getSupport)
                            .map(String::toLowerCase)
                            .distinct()
                            .collect(Collectors.joining(","));
                    //设置支持厂商
                    readingLetterTemplateAuditDo.setApplicableTerminal(StrUtil.isBlank(applicableTerminal) ? null : applicableTerminal);
                    //修改
                    readingLetterTemplateAuditDao.updateById(readingLetterTemplateAuditDo);
                    //如果审核成功把模板移动到审核成功表，防止修改模板影响现有群发计划
                    saveReadingLetterTemplateProved(readingLetterTemplateDo, readingLetterTemplateAuditDo, templateStatusCallbackDTO);
                    break;
                }
            }
            //所有运营商都没有审核通过
            else {
                for (SupportDetailsReq supportDetailsReq : supportDetails) {
                    if ("PENDING".equalsIgnoreCase(supportDetailsReq.getAuditStatus())) {
                        continue;
                    }
                    if (!supportDetailsReq.getCarrierType().equalsIgnoreCase(CarrierEnum.getCarrierEnum(readingLetterTemplateAuditDo.getOperatorCode()).getTag()))
                        continue;
                    readingLetterTemplateAuditDo.setRemark(supportDetailsReq.getAuditRemark());
                    //修改状态
                    auditStatus = templateStatusMap.get(supportDetailsReq.getAuditStatus());
                    readingLetterTemplateAuditDo.setStatus(auditStatus);
                    //修改
                    readingLetterTemplateAuditDao.updateById(readingLetterTemplateAuditDo);
                    break;
                }
            }
        } else {
            //supportDetails为空时
            auditStatus = templateStatusMap.get(templateStatusCallbackDTO.getTemplateStatus());
            readingLetterTemplateAuditDo.setRemark(templateStatusCallbackDTO.getRemark());
            readingLetterTemplateAuditDo.setStatus(auditStatus);
            readingLetterTemplateAuditDao.updateById(readingLetterTemplateAuditDo);
        }
        log.info("模板状态审核回调完成：{}", templateStatusCallbackDTO.getId());
    }

    /**
     * 保存已经审核通过的模板到另一张审核通过表
     */
    private void saveMessageTemplateProved(MessageTemplateDo messageTemplateDo, MessageTemplateAuditDo messageTemplateAuditDo) {
        if (messageTemplateAuditDo.getStatus() != Constants.TEMPLATE_STATUS_SUCCESS) {
            return;
        }
        log.info("模板审核通过,开始备份");
        Long templateId = messageTemplateDo.getId();
        MessageTemplateProvedDo messageTemplateProvedDo = new MessageTemplateProvedDo();
        messageTemplateProvedDo.setTemplateId(templateId);
        BeanUtil.copyProperties(messageTemplateDo, messageTemplateProvedDo);
        BeanUtil.copyProperties(messageTemplateAuditDo, messageTemplateProvedDo);
        messageTemplateProvedDo.setId(null);
        messageTemplateProvedDo.setPlatformTemplateId(messageTemplateAuditDo.getPlatformTemplateId());
        QueryWrapper<MessageTemplateProvedDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("template_id", templateId);
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("operator", messageTemplateAuditDo.getOperator());
        MessageTemplateProvedDo sourceMessageTemplateProvedDo = messageTemplateProvedDao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(sourceMessageTemplateProvedDo)) {
            messageTemplateProvedDao.insert(messageTemplateProvedDo);
        } else {
            messageTemplateProvedDo.setId(sourceMessageTemplateProvedDo.getId());
            BeanUtil.copyProperties(messageTemplateProvedDo, sourceMessageTemplateProvedDo);
            messageTemplateProvedDao.updateById(messageTemplateProvedDo);
        }
        log.info("模板审核通过备份完成");
    }

    /**
     * 阅信模板 :保存已经审核通过的模板到另一张审核通过表
     */
    private void saveReadingLetterTemplateProved(ReadingLetterTemplateDo readingLetterTemplateDo, ReadingLetterTemplateAuditDo readingLetterTemplateAuditDo, TemplateStatusCallbackReq templateStatusCallbackDTO) {
        if (readingLetterTemplateAuditDo.getStatus() != Constants.TEMPLATE_STATUS_SUCCESS) {
            return;
        }
        log.info("阅信模板审核通过,开始备份");
        //将Proved对象赋值
        Long templateId = readingLetterTemplateDo.getId();
        ReadingLetterTemplateProvedDo readingLetterTemplateProvedDo = new ReadingLetterTemplateProvedDo();
        readingLetterTemplateProvedDo.setTemplateId(templateId);
        BeanUtil.copyProperties(readingLetterTemplateDo, readingLetterTemplateProvedDo);
        BeanUtil.copyProperties(readingLetterTemplateAuditDo, readingLetterTemplateProvedDo);
        readingLetterTemplateProvedDo.setId(null);
        //阅信模板查询是否已经存在
        QueryWrapper<ReadingLetterTemplateProvedDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("template_id", templateId);
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("operator_code", readingLetterTemplateAuditDo.getOperatorCode());
        ReadingLetterTemplateProvedDo sourceMessageTemplateProvedDo = readingLetterTemplateProvedDao.selectOne(queryWrapper);

//        readingLetterTemplateProvedDo.set
        if (ObjectUtil.isNull(sourceMessageTemplateProvedDo)) {
            readingLetterTemplateProvedDao.insert(readingLetterTemplateProvedDo);
        } else {
            readingLetterTemplateProvedDo.setId(sourceMessageTemplateProvedDo.getId());
            BeanUtil.copyProperties(sourceMessageTemplateProvedDo, readingLetterTemplateProvedDo);
            readingLetterTemplateProvedDao.updateById(readingLetterTemplateProvedDo);
        }
        log.info("模板审核通过备份完成");
    }

    /**
     * 当模板是机器人流程里的审核通过后要修改机器人流程状态
     *
     * @param
     */


    /**
     * 系统发起重新审核 （审核就是调用网关创建或者更新模板接口）
     * 重新设置审核状态，发送审核失败为待审核，发送审核成功为审核中
     *
     * @param messageTemplateDo
     * @param templateDataResp
     * @return
     */
    private MessageTemplateAuditDo saveAuditTemplateAudit(MessageTemplateDo
                                                                  messageTemplateDo, TemplateOwnershipReflect
                                                                  templateOwnershipReflect, TemplateDataResp<String> templateDataResp) {
        QueryWrapper<MessageTemplateAuditDo> wrapper = new QueryWrapper();
        wrapper.eq("template_id", messageTemplateDo.getId());
        wrapper.eq("deleted", 0);
        wrapper.eq("operator", CarrierEnum.getCarrierEnum(templateOwnershipReflect.getOperator()).getValue());
        log.info("保存模板审核记录查询条件：{},template_id:{}", templateOwnershipReflect, messageTemplateDo.getId());
        MessageTemplateAuditDo sourceMessageTemplateAuditDo = messageTemplateAuditDao.selectOne(wrapper);
        if (ObjectUtil.isNotNull(sourceMessageTemplateAuditDo)) {
            setSourceMessageTemplateAuditDo(messageTemplateDo.getId(), sourceMessageTemplateAuditDo, templateOwnershipReflect, templateDataResp, messageTemplateDo);
            messageTemplateAuditDao.updateById(sourceMessageTemplateAuditDo);
        } else {
            sourceMessageTemplateAuditDo = new MessageTemplateAuditDo();
            setSourceMessageTemplateAuditDo(messageTemplateDo.getId(), sourceMessageTemplateAuditDo, templateOwnershipReflect, templateDataResp, messageTemplateDo);
            sourceMessageTemplateAuditDo.setId(null);
            messageTemplateAuditDao.insert(sourceMessageTemplateAuditDo);
        }
        return sourceMessageTemplateAuditDo;

    }

    void setSourceMessageTemplateAuditDo(Long templateId, MessageTemplateAuditDo
            sourceMessageTemplateAuditDo, TemplateOwnershipReflect
                                                 templateOwnershipReflect, TemplateDataResp<String> templateDataResp, MessageTemplateDo messageTemplateDo) {
        Long sourceAuditId = sourceMessageTemplateAuditDo.getId();
        BeanUtil.copyProperties(messageTemplateDo, sourceMessageTemplateAuditDo);
        sourceMessageTemplateAuditDo.setId(sourceAuditId);
        if (ObjectUtil.isNotNull(templateDataResp.getData())) {
            sourceMessageTemplateAuditDo.setPlatformTemplateId(templateDataResp.getData());
        }
        sourceMessageTemplateAuditDo.setRemark(templateDataResp.getMessage());
        sourceMessageTemplateAuditDo.setStatus(templateDataResp.getCode() == 200 ? templateOwnershipReflect.getSupplierTag().equals(Constants.SUPPLIER_TAG_FONTDO) ? Constants.TEMPLATE_STATUS_PENDING : Constants.TEMPLATE_STATUS_SUCCESS : Constants.TEMPLATE_STATUS_WAITING);
        sourceMessageTemplateAuditDo.setTemplateId(templateId);
        sourceMessageTemplateAuditDo.setOperator(ObjectUtil.isNull(templateOwnershipReflect.getAccountTypeCode()) ? CarrierEnum.getCarrierEnum(templateOwnershipReflect.getOperator()).getValue() : templateOwnershipReflect.getAccountTypeCode());
        sourceMessageTemplateAuditDo.setSupplierTag(templateOwnershipReflect.getSupplierTag());
        sourceMessageTemplateAuditDo.setChatbotAccount(templateOwnershipReflect.getChatbotAccount());
    }

}
