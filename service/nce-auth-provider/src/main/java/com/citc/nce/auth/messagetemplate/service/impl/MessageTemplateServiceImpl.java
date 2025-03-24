package com.citc.nce.auth.messagetemplate.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountChatbotAccountQueryReq;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.common.CSPChatbotSupplierTagEnum;
import com.citc.nce.auth.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.auth.messagetemplate.dao.MessageTemplateAuditDao;
import com.citc.nce.auth.messagetemplate.dao.MessageTemplateDao;
import com.citc.nce.auth.messagetemplate.dao.MessageTemplateProvedDao;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateAuditDo;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateDo;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateDoQueryParam;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateProvedDo;
import com.citc.nce.auth.messagetemplate.exception.MessageTemplateCode;
import com.citc.nce.auth.messagetemplate.service.MessageTemplateService;
import com.citc.nce.auth.messagetemplate.service.TemplateContentMakeUp;
import com.citc.nce.auth.messagetemplate.vo.DeleteTemplateForInvalidOfProcessReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateButtonReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateIdResp;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateMultiTriggerReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplatePageReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplatePageResp;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedForQueryReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedListReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedResp;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateTreeResp;
import com.citc.nce.auth.messagetemplate.vo.PageResultResp;
import com.citc.nce.auth.messagetemplate.vo.TemplateDataResp;
import com.citc.nce.auth.messagetemplate.vo.TemplateInfoItem;
import com.citc.nce.auth.messagetemplate.vo.TemplateOwnershipReflectResp;
import com.citc.nce.auth.messagetemplate.vo.TemplateStatusCallbackReq;
import com.citc.nce.auth.messagetemplate.vo.shortcutbutton.ButtonDetailInfo;
import com.citc.nce.auth.messagetemplate.vo.shortcutbutton.CardListInfo;
import com.citc.nce.auth.messagetemplate.vo.shortcutbutton.ModuleInformation;
import com.citc.nce.auth.messagetemplate.vo.shortcutbutton.ShortCutButtonInfo;
import com.citc.nce.auth.utils.FifthMessageTemplateUtils;
import com.citc.nce.common.constants.CarrierEnum;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.common.util.JsonSameUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.fileApi.ExamineResultApi;
import com.citc.nce.filecenter.vo.TemplateOwnershipReflect;
import com.citc.nce.module.ModuleApi;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.robot.enums.ButtonType;
import com.citc.nce.vo.ExamineResultVo;
import com.citc.nce.vo.FileExamineStatusDto;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.transaction.annotation.ShardingSphereTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.citc.nce.common.constants.Constants.TEMPLATE_SOURCE_LASTREPLY;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 16:14
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
public class MessageTemplateServiceImpl implements MessageTemplateService {
    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;

    @Resource
    private MessageTemplateDao messageTemplateDao;
    @Resource
    private ModuleApi moduleApi;
    private static final List<Integer> moduleTypeList = Arrays.asList(ButtonType.SUBSCRIBE_BTN.getCode(), ButtonType.CANCEL_SUBSCRIBE_BTN.getCode(), ButtonType.JOIN_SIGN_BTN.getCode(), ButtonType.SIGN_BTN.getCode());
    @Resource
    private MessageTemplateAuditDao messageTemplateAuditDao;
    @Resource
    private RedisService redisService;
    @Resource
    private MessageTemplateProvedDao messageTemplateProvedDao;
    @Resource
    AccountManagementApi accountManagementApi;
    @Resource
    MessageTemplateAuditManageService messageTemplateAuditManageService;
    @Resource
    PlatfomManageTemplateService platfomManageTemplateService;
    @Resource
    TemplateContentMakeUp templateContentMakeUp;
    @Resource
    private ExamineResultApi examineResultApi;

    @Override
    public PageResultResp getMessageTemplates(MessageTemplatePageReq messageTemplatePageReq) {
        MessageTemplateDoQueryParam messageTemplateDoQueryParam = BeanUtil.copyProperties(messageTemplatePageReq, MessageTemplateDoQueryParam.class);
        if (ObjectUtil.isNull(messageTemplateDoQueryParam.getTemplateSource())) {
            messageTemplateDoQueryParam.setTemplateSource(Constants.TEMPLATE_SOURCE_TEMPLATEMANAGE);
        }
        messageTemplatePageReq.setStatus((null == messageTemplatePageReq.getStatus() || -2 == messageTemplatePageReq.getStatus()) ? null : messageTemplatePageReq.getStatus());
        if (messageTemplatePageReq.getMessageType() == null || messageTemplatePageReq.getMessageType() == 0) {
            messageTemplatePageReq.setMessageType(null);
        }
        if (messageTemplatePageReq.getTemplateType() == null || messageTemplatePageReq.getTemplateType() == 0) {
            messageTemplatePageReq.setTemplateType(null);
        }
        if ("0".equals(messageTemplatePageReq.getChatbotAccount())) {
            messageTemplatePageReq.setChatbotAccount(null);
        }
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            messageTemplateDoQueryParam.setCreator(userId);
        }
        Page<MessageTemplateDo> page = new Page<>(messageTemplatePageReq.getPageParam().getPageNo(), messageTemplatePageReq.getPageParam().getPageSize());
        page.setOrders(OrderItem.descs("create_time"));
        List<String> chatbotAccounts = StrUtil.isEmpty(messageTemplatePageReq.getChatbotAccount()) ? null : Arrays.asList(messageTemplatePageReq.getChatbotAccount().split(","));
        messageTemplateDao.getTemplates(
                messageTemplatePageReq.getTemplateName(),
                messageTemplatePageReq.getTemplateType(),
                messageTemplatePageReq.getMessageType(),
                messageTemplatePageReq.getStatus(),
                messageTemplatePageReq.getTemplateSource(),
                chatbotAccounts,
                userId,
                page
        );


        List<MessageTemplateResp> messageTemplateResps = BeanUtil.copyToList(page.getRecords(), MessageTemplateResp.class);
        //获取模板的审核状态
        messageTemplateResps = getMessageTemplateRespAudit(messageTemplateResps, messageTemplatePageReq);
        //如果来源是群发，则需要返回模板内所有素材的状态
        final int groupMessagePlanSource = 1;
        final int reviewed = 2;
        if (messageTemplatePageReq.getTemplateSource() != null
                && groupMessagePlanSource == messageTemplatePageReq.getTemplateSource()
                && !org.springframework.util.CollectionUtils.isEmpty(chatbotAccounts)) {
            HashSet<String> chatbotAccountSet = new HashSet<>(chatbotAccounts);
            for (MessageTemplateResp templateResp : messageTemplateResps) {
                List<String> materialIds = FifthMessageTemplateUtils.find5gTemplateMaterialFileUuid(templateResp.getModuleInformation(), templateResp.getMessageType());
                if (org.springframework.util.CollectionUtils.isEmpty(materialIds)) {
                    continue;
                }
                //模板包含的所有素材的审核记录
                List<FileExamineStatusDto> statusDtos = examineResultApi.queryExamineResultBatch(materialIds);
                boolean hasNotReviewedMaterial = statusDtos.stream()
                        .anyMatch(material -> {
                                    Set<String> reviewedAccounts = material.getExamineResults().stream()
                                            .filter(examine -> examine.getFileStatus() == reviewed)
                                            .map(ExamineResultVo::getChatbotAccount)
                                            .collect(Collectors.toSet());
                                    //判断过审运营商集和和选中账号运营商集合是否不等
                                    return !reviewedAccounts.containsAll(chatbotAccountSet);
                                }
                        );
                templateResp.setHasNotReviewedMaterial(hasNotReviewedMaterial);
            }
        }
        return new PageResultResp(messageTemplateResps, page.getTotal(), messageTemplatePageReq.getPageParam().getPageNo());
    }

    /**
     * 模板筛账号，筛状态
     *
     * @param messageTemplateResps
     * @param messageTemplatePageReq
     * @return
     */
    List<MessageTemplateResp> getMessageTemplateRespAudit(List<MessageTemplateResp> messageTemplateResps, MessageTemplatePageReq messageTemplatePageReq) {
        if (messageTemplateResps.size() == 0) return messageTemplateResps;
        // 查询模板审核状态
        List<Long> templateId = messageTemplateResps.stream().map(messageTemplateResp -> messageTemplateResp.getId()).collect(Collectors.toList());
        LambdaQueryWrapper<MessageTemplateAuditDo> auditLambdaQueryWrapper = new LambdaQueryWrapper();
        auditLambdaQueryWrapper.in(MessageTemplateAuditDo::getTemplateId, templateId);
        auditLambdaQueryWrapper.eq(MessageTemplateAuditDo::getDeleted, 0);
        //先查出模板包含的所有审核记录
        List<MessageTemplateAuditDo> messageTemplateAuditDos = messageTemplateAuditDao.selectList(auditLambdaQueryWrapper);
        for (MessageTemplateResp messageTemplateResp : messageTemplateResps) {
            messageTemplateResp.setAudits(makeTemplateAuditOwnerShip(messageTemplateResp.getId(), messageTemplateAuditDos));
        }
        return messageTemplateResps;
    }

    public List<TemplateOwnershipReflectResp> makeTemplateAuditOwnerShip(Long templateId, List<MessageTemplateAuditDo> auditDoList) {
        List<TemplateOwnershipReflectResp> resultTemplateOwnershipReflectResp = new ArrayList<>();
        resultTemplateOwnershipReflectResp.add(makeTemplateAuditOwnerShipOperator(templateId, auditDoList, CarrierEnum.Walnut));
        resultTemplateOwnershipReflectResp.add(makeTemplateAuditOwnerShipOperator(templateId, auditDoList, CarrierEnum.Unicom));
        resultTemplateOwnershipReflectResp.add(makeTemplateAuditOwnerShipOperator(templateId, auditDoList, CarrierEnum.Telecom));
        resultTemplateOwnershipReflectResp.add(makeTemplateAuditOwnerShipOperator(templateId, auditDoList, CarrierEnum.CMCC));

        return resultTemplateOwnershipReflectResp;
    }

    public TemplateOwnershipReflectResp makeTemplateAuditOwnerShipOperator(Long templateId, List<MessageTemplateAuditDo> auditDoList, CarrierEnum carrierEnum) {
        TemplateOwnershipReflectResp templateOwnershipReflectResp = new TemplateOwnershipReflectResp();
        //归属运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信
        MessageTemplateAuditDo optionalMessageTemplateAuditDo = null;
        for (MessageTemplateAuditDo item : auditDoList) {
            if (item.getTemplateId().compareTo(templateId) == 0 && item.getOperator() == carrierEnum.getValue()) {
                optionalMessageTemplateAuditDo = item;
                break;
            }
        }
        if (Objects.nonNull(optionalMessageTemplateAuditDo)) {
            templateOwnershipReflectResp.setStatus(optionalMessageTemplateAuditDo.getStatus());
            templateOwnershipReflectResp.setSupplierTag(optionalMessageTemplateAuditDo.getSupplierTag());
            templateOwnershipReflectResp.setChatbotAccount(optionalMessageTemplateAuditDo.getChatbotAccount());
        } else {

            templateOwnershipReflectResp.setStatus(Constants.TEMPLATE_STATUS_WAITING);
            templateOwnershipReflectResp.setSupplierTag("");
        }
        templateOwnershipReflectResp.setOperator(carrierEnum.getName());
        return templateOwnershipReflectResp;
    }

    public List<AccountManagementResp> getAccountForProvedTemplateCarrier(@RequestParam("id") Long id) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);

        wrapper.eq("status", Constants.TEMPLATE_STATUS_SUCCESS);

        wrapper.eq("template_id", id);
        List<MessageTemplateAuditDo> messageTemplateAuditDos = messageTemplateAuditDao.selectList(wrapper);
        if (ObjectUtil.isEmpty(messageTemplateAuditDos)) {
            return null;
        }
        String creator = messageTemplateAuditDos.get(0).getCreator();
        List<String> chatbotAccounts = messageTemplateAuditDos.stream().map(MessageTemplateAuditDo::getChatbotAccount).collect(Collectors.toList());
        AccountChatbotAccountQueryReq accountChatbotAccountQueryReq = new AccountChatbotAccountQueryReq();
        accountChatbotAccountQueryReq.setChatbotAccountList(chatbotAccounts);
        accountChatbotAccountQueryReq.setCreator(creator);
        return accountManagementApi.getListByChatbotAccounts(accountChatbotAccountQueryReq);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageTemplateIdResp saveMessageTemplate(MessageTemplateReq messageTemplateReq) {
        checkMessageType(messageTemplateReq.getMessageType(), messageTemplateReq.getTemplateType());
        if (null != messageTemplateReq.getId()) {
            throw new BizException("参数包含模板id");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);

        wrapper.eq("template_name", messageTemplateReq.getTemplateName());

        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        //模板名不能重复
        List<MessageTemplateDo> messageTemplateDos = messageTemplateDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(messageTemplateDos)) {
            throw new BizException(MessageTemplateCode.VARIABLE_BAD_NAME);
        }
        //变量不能超过20个
        if (!messageTemplateAuditManageService.checkVariableJSONArray(messageTemplateReq.getModuleInformation())) {
            throw new BizException(MessageTemplateCode.VARIABLE_OVER_LIMIT);

        }
        MessageTemplateDo messageTemplateDo = BeanUtil.copyProperties(messageTemplateReq, MessageTemplateDo.class);
        String operators = messageTemplateReq.getOperators();
        messageTemplateDo.setCreator(userId);
        //检查素材在运营商之间是否共用
        //templateOwnershipReflects，标识所有素材没有共有的运营商和服务商
        List<TemplateOwnershipReflect> templateOwnershipReflects = messageTemplateAuditManageService.getAuditTemplateOwnershipReflects(messageTemplateDo, operators);

        if (messageTemplateReq.getImportFlag() == 0 && ObjectUtil.isEmpty(templateOwnershipReflects)) {
            throw new BizException(com.citc.nce.auth.messagetemplate.entity.Constants.MATERIAL_NOTEXISTS_SHARE);
        }


        int insert = messageTemplateDao.insert(messageTemplateDo);
        if (insert == 1) {
            //如果是组件就保存组件和按钮的关系
            savaModuleButtonRelation(messageTemplateReq.getShortcutButton());
            //单卡多卡里面的按钮
            savaModuleButtonRelationForKA(messageTemplateReq.getModuleInformation());
        }
        MessageTemplateIdResp messageTemplateIdResp = new MessageTemplateIdResp();
        messageTemplateIdResp.setId(messageTemplateDo.getId());
        messageTemplateIdResp.setSuccessNum(insert);
        if (insert == 0) {
            return messageTemplateIdResp;
        }
        log.info("模板保存完成,{}", messageTemplateIdResp);
        //如果是模板商城导入不执行后面逻辑
        if (messageTemplateReq.getImportFlag() == 1) return messageTemplateIdResp;

        //保存后继续处理，模板审核
        String auditMessage = messageTemplateAuditManageService.extraTemplateManage(messageTemplateDo, templateOwnershipReflects, messageTemplateReq.getNeedAudit(), null == messageTemplateReq.getIsChecked() ? Constants.isChecked_first : messageTemplateReq.getIsChecked());
        //释放模板送审缓存锁
        redisService.deleteObject(String.format(Constants.TEMPLATE_AUDIT_CACHE_REDIS_KEY, messageTemplateDo.getId()));
        if (messageTemplateReq.getNeedAudit() == Constants.TEMPLATE_AUDIT_NOT) {
            log.info("请求方设置模板不需要审核,{}", messageTemplateIdResp);
            return messageTemplateIdResp;
        } else {
            log.info("送审结果：" + (StrUtil.isEmpty(auditMessage) ? "审核成功" : auditMessage));
            messageTemplateIdResp.setDesc(StrUtil.isEmpty(auditMessage) ? "保存并送审成功!" : com.citc.nce.auth.messagetemplate.entity.Constants.TEMPLATE_SAVE_AUDIT_FAILED);
            if (StrUtil.isNotEmpty(auditMessage)) {
                messageTemplateIdResp.setSuccessNum(0);
            }
            return messageTemplateIdResp;
        }

    }

    /**
     * 保存组件和按钮的映射关系，--单卡多卡
     */
    private void savaModuleButtonRelationForKA(String moduleInformation) {
        if (!Strings.isNullOrEmpty(moduleInformation)) {
            ModuleInformation moduleInformationObj = JSON.parseObject(moduleInformation, ModuleInformation.class);
            if (moduleInformationObj != null) {
                List<ShortCutButtonInfo> buttonList = moduleInformationObj.getButtonList();
                if (CollectionUtils.isNotEmpty(buttonList)) {
                    saveRelationForButtonAndModule(buttonList);
                }
                List<CardListInfo> cardList = moduleInformationObj.getCardList();
                if (CollectionUtils.isNotEmpty(cardList)) {
                    for (CardListInfo item : cardList) {
                        List<ShortCutButtonInfo> buttonListForCard = item.getButtonList();
                        if (CollectionUtils.isNotEmpty(buttonListForCard)) {
                            saveRelationForButtonAndModule(buttonListForCard);
                        }
                    }
                }

            }
        }
    }


    /**
     * 保存组件和按钮的关联关系
     *
     * @param shortcutButton 按钮信息
     */
    private void savaModuleButtonRelation(String shortcutButton) {
        if (!Strings.isNullOrEmpty(shortcutButton)) {
            List<ShortCutButtonInfo> shortCutButtonInfos = JSON.parseArray(shortcutButton, ShortCutButtonInfo.class);
            saveRelationForButtonAndModule(shortCutButtonInfos);
        }
    }


    private void saveRelationForButtonAndModule(List<ShortCutButtonInfo> shortCutButtonInfoList) {
        if (CollectionUtils.isNotEmpty(shortCutButtonInfoList)) {
            for (ShortCutButtonInfo item : shortCutButtonInfoList) {
                Integer type = item.getType();
                ButtonDetailInfo buttonDetailInfo = item.getButtonDetail();
                String butUuid = item.getUuid();
                if ((moduleTypeList.contains(type) && !Strings.isNullOrEmpty(butUuid)) && (buttonDetailInfo != null && !Strings.isNullOrEmpty(buttonDetailInfo.getBusinessId()))) {
                    boolean result = moduleApi.saveModuleButUuidRelation(type, buttonDetailInfo.getBusinessId(), butUuid);
                    if (!result) {
                        throw new BizException("保存模板与按钮关联关系异常！");
                    }
                }
            }
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String publicTemplate(Long templateId, String operators, Integer isChecked) {
        MessageTemplateDo messageTemplateDo = messageTemplateDao.selectById(templateId);
        if (ObjectUtil.isEmpty(messageTemplateDo)) {
            log.info("模板未找到 templateId:{}", templateId);
            return "模板未找到";
        }
        String userId = SessionContextUtil.getUser().getUserId();

        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())
                &&
                (StrUtil.compare(userId, messageTemplateDo.getCreator(), true) != 0 && !(messageTemplateDo.getCreator().startsWith(userId)))) { //csp:userId creator:customer
            log.info("不能发布别人的模板, userId:{}, creator:{}", userId, messageTemplateDo.getCreator());
            return "不能发布别人的模板";
        }
        return messageTemplateAuditManageService.publicTemplate(templateId, operators, isChecked);
    }

    /**
     * 蜂动，直连版本上线时，用于模板的审核数据初始化。所有用户的模板都默认设置为审核通过
     */
    @Override
    public void templateInitForNewAuditBranch() {
        log.info("模板审核数据初始化开始");
        List<MessageTemplateDo> messageTemplateDos = messageTemplateDao.selectList();
        messageTemplateDos.forEach(item -> {
            try {
                LambdaQueryWrapper<MessageTemplateAuditDo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(MessageTemplateAuditDo::getTemplateId, item.getId());
                if (!messageTemplateAuditDao.exists(queryWrapper)) {
                    log.info("模板{}开始送审", item.getId());
                    List<AccountManagementResp> accountManagementResps = accountManagementApi.getChatbotAccountInfoByCustomerId(item.getCreator());

                    if (ObjectUtil.isNotEmpty(accountManagementResps)) {
                        accountManagementResps.forEach(accountManagementResp -> {
                            messageTemplateAuditManageService.publicTemplate(item.getId(), accountManagementResp.getAccountType(), 2);
                        });
                    }
                } else {
                    log.info("模板{}不需要送审", item.getId());
                }
            } catch (Exception exception) {
                log.error(String.format("出错模板：%s", JSONObject.toJSONString(item)));
                log.error("模板数据初始化异常", exception);
            }
        });
        log.info("模板审核数据初始化完成");
    }

    //查询某个流程下是否存在没有审核通过的模板
    @Override
    public boolean existAuditNotPassTemplateForProcess(Long processId) {
        Long result = messageTemplateAuditDao.selectCount(Wrappers.<MessageTemplateAuditDo>lambdaQuery()
                .eq(MessageTemplateAuditDo::getProcessId, processId)
                .eq(MessageTemplateAuditDo::getDeleted, 0)
                .ne(MessageTemplateAuditDo::getStatus, Constants.TEMPLATE_STATUS_SUCCESS));
        log.info("{}流程未审核通过的数量：{}", processId, result);
        return result > 0;
    }

    @Override
    public int getTemplateStatus(Long templateId, String chatbotAccount) {
        QueryWrapper<MessageTemplateAuditDo> messageTemplateAuditDoQueryWrapper = new QueryWrapper<>();
        messageTemplateAuditDoQueryWrapper.eq("template_id", templateId);
        messageTemplateAuditDoQueryWrapper.eq("chatbot_account", chatbotAccount);
        MessageTemplateAuditDo messageTemplateAuditDo = messageTemplateAuditDao.selectOne(messageTemplateAuditDoQueryWrapper);
        if (ObjectUtil.isNull(messageTemplateAuditDo)) return Constants.TEMPLATE_STATUS_WAITING;
        return messageTemplateAuditDo.getStatus();
    }

    /**
     * 更新时，判断模板是否有运营商通道在审核中
     *
     * @param templateId
     * @return
     */
    boolean checkoutTemplateStatus(Long templateId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("status", Constants.TEMPLATE_STATUS_PENDING);

        wrapper.eq("template_id", templateId);

        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        Long pendingCount = messageTemplateAuditDao.selectCount(wrapper);
        return pendingCount == 0;
    }

    void checkMessageType(int messageType, Integer templateType) {
        if ((messageType == 1 ||
                messageType == 6 ||
                messageType == 7 ||
                messageType == 8) &&
                templateType == null) {
            throw new BizException(500, "必须选择模板类型");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageTemplateIdResp updateMessageTemplate(MessageTemplateReq messageTemplateEditReq) {
        MessageTemplateIdResp messageTemplateIdResp = new MessageTemplateIdResp();
        if (null == messageTemplateEditReq.getId()) {
            throw new BizException("模板id不能为空");
        }
        checkMessageType(messageTemplateEditReq.getMessageType(), messageTemplateEditReq.getTemplateType());
        MessageTemplateDo messageTemplateDb = messageTemplateDao.selectById(messageTemplateEditReq.getId());
        if (Objects.isNull(messageTemplateDb)) {
            throw new BizException("模板不存在");
        }
        if (!StringUtils.equals(SessionContextUtil.getUser().getUserId(), superAdministratorUserIdConfigure.getSuperAdministrator()) && !SessionContextUtil.getUser().getUserId().equals(messageTemplateDb.getCreator())) {
            throw new BizException("不能修改别人的模板");
        }
        if (!checkoutTemplateStatus(messageTemplateEditReq.getId())) {
            throw new BizException("模板审核中不能编辑");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("template_name", messageTemplateEditReq.getTemplateName());
        wrapper.ne("id", messageTemplateEditReq.getId());

        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        List<MessageTemplateDo> messageTemplateDos = messageTemplateDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(messageTemplateDos)) {
            for (MessageTemplateDo messageTemplateDo : messageTemplateDos) {
                if (!messageTemplateEditReq.getId().equals(messageTemplateDo.getId())) {
                    throw new BizException(MessageTemplateCode.VARIABLE_BAD_NAME);
                }
            }
        }
        MessageTemplateDo messageTemplateDo = new MessageTemplateDo();
        String operators = messageTemplateEditReq.getOperators();

        BeanUtil.copyProperties(messageTemplateEditReq, messageTemplateDo);
        messageTemplateDo.setCreator(messageTemplateDb.getCreator());
        List<TemplateOwnershipReflect> templateOwnershipReflects = messageTemplateAuditManageService.getAuditTemplateOwnershipReflects(messageTemplateDo, operators);
        if (ObjectUtil.isEmpty(templateOwnershipReflects)) {
            cancelAuditByTemplateId(messageTemplateDb.getId(), null);
            throw new BizException(com.citc.nce.auth.messagetemplate.entity.Constants.MATERIAL_NOTEXISTS_SHARE);
        }
        int updateNum = messageTemplateDao.updateById(messageTemplateDo);
        if (updateNum == 1) {
            //如果是组件就保存组件和按钮的关系
            savaModuleButtonRelation(messageTemplateEditReq.getShortcutButton());
            //单卡多卡里面的按钮
            savaModuleButtonRelationForKA(messageTemplateEditReq.getModuleInformation());
        }
        log.info("模板更新保存数据库完成");
        messageTemplateIdResp.setSuccessNum(updateNum);
        messageTemplateIdResp.setId(messageTemplateDb.getId());
        //模板内容相同，且关联的chatbotAccount相同，且都已经审核通过了，则不需要再次发送审核
        if (compareTemplateContent(messageTemplateDo, messageTemplateDb)) {
            //存在已经送审过的账号，检查上次是否送审成功，否则重新送审
            QueryWrapper<MessageTemplateAuditDo> templateAuditDoQueryWrapper = new QueryWrapper<>();
            templateAuditDoQueryWrapper.eq("template_id", messageTemplateDb.getId());
            templateAuditDoQueryWrapper.eq("deleted", 0);
            templateAuditDoQueryWrapper.eq("status", Constants.TEMPLATE_STATUS_SUCCESS);
            List<MessageTemplateAuditDo> messageTemplateAuditFailDos = messageTemplateAuditDao.selectList(templateAuditDoQueryWrapper);

            if (ObjectUtil.isNotEmpty(messageTemplateAuditFailDos)) {
                List<Integer> exitstOperators = messageTemplateAuditFailDos.stream().map(MessageTemplateAuditDo::getOperator).distinct().collect(Collectors.toList());
                templateOwnershipReflects = templateOwnershipReflects.stream().filter(item -> !exitstOperators.contains(item.getAccountTypeCode())).collect(Collectors.toList());
            }
            //全是已经审核通过的
            if (ObjectUtil.isEmpty(templateOwnershipReflects)) {
                log.info("更新模板时内容相同，不需要送审,id:" + messageTemplateDo.getId());
                messageTemplateIdResp.setNeedAudit(false);
                return messageTemplateIdResp;
            } else {
                //需要送审的模板清除以前的送审状态
                templateOwnershipReflects.forEach(item -> {
                    cancelAuditByTemplateId(messageTemplateDb.getId(), CarrierEnum.getCarrierEnum(item.getOperator()).getValue());
                });
            }
        }
        //内容发送变化，模板状态变更为待审核，重新开始审核流程
        else {
            cancelAuditByTemplateId(messageTemplateDb.getId(), null);
        }
        messageTemplateIdResp.setNeedAudit(true);
        String auditMessage = messageTemplateAuditManageService.extraTemplateManage(messageTemplateDo, templateOwnershipReflects, messageTemplateEditReq.getNeedAudit(), null == messageTemplateEditReq.getIsChecked() ? Constants.isChecked_first : messageTemplateEditReq.getIsChecked());
        messageTemplateIdResp.setDesc(StrUtil.isEmpty(auditMessage) ? "更新成功!" : com.citc.nce.auth.messagetemplate.entity.Constants.TEMPLATE_SAVE_AUDIT_FAILED);
        if (StrUtil.isNotEmpty(auditMessage)) {
            messageTemplateIdResp.setSuccessNum(0);
        }
        //释放模板送审缓存锁
        redisService.deleteObject(String.format(Constants.TEMPLATE_AUDIT_CACHE_REDIS_KEY, messageTemplateDo.getId()));

        return messageTemplateIdResp;
    }


    @Override
    public int delMessageTemplateById(Long id) {
        MessageTemplateDo select = messageTemplateDao.selectById(id);
        if (Objects.isNull(select)) return 0;
        if (!SessionContextUtil.getUser().getUserId().equals(select.getCreator())) {
            throw new BizException("不能删除别人的模板");
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("deleted", 1);
        map.put("deleteTime", DateUtil.date());
        messageTemplateAuditDao.delMessageTemplateAuditByTemplateId(map);
        deleteProvedTemplate(map);
        return messageTemplateDao.delMessageTemplateById(map);
    }

    void deleteProvedTemplate(HashMap<String, Object> map) {
        deleteProvedTemplateFromPlatformForFontDoById((Long) map.get("id"));
        messageTemplateProvedDao.delProvedMessageTemplateByTemplateId(map);
    }

    void deleteProvedTemplateFromPlatformForFontDoById(Long templateId) {
        LambdaQueryWrapper<MessageTemplateProvedDo> messageTemplateProvedDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        messageTemplateProvedDoLambdaQueryWrapper.eq(MessageTemplateProvedDo::getTemplateId, templateId);
        messageTemplateProvedDoLambdaQueryWrapper.eq(MessageTemplateProvedDo::getDeleted, 0);
        messageTemplateProvedDoLambdaQueryWrapper.eq(MessageTemplateProvedDo::getCreator, SessionContextUtil.getUser().getUserId());
        messageTemplateProvedDoLambdaQueryWrapper.eq(MessageTemplateProvedDo::getSupplierTag, Constants.SUPPLIER_TAG_FONTDO);
        List<MessageTemplateProvedDo> messageTemplateProvedDos = messageTemplateProvedDao.selectList(messageTemplateProvedDoLambdaQueryWrapper);
        deleteProvedTemplateFromPlatformForFontDo(messageTemplateProvedDos);
    }

    void deleteProvedTemplateFromPlatformForFontDo(List<MessageTemplateProvedDo> messageTemplateProvedDos) {
        if (ObjectUtil.isNull(messageTemplateProvedDos)) return;
        messageTemplateProvedDos.forEach(messageTemplateProvedDo -> {
            AccountManagementResp accountManagementResp = accountManagementApi.getAccountManagementByAccountId(messageTemplateProvedDo.getChatbotAccount());
            if (ObjectUtil.isNotEmpty(accountManagementResp) && StrUtil.isNotEmpty(messageTemplateProvedDo.getPlatformTemplateId())) {
                try {
                    platfomManageTemplateService.deleteTemplateAsyn(messageTemplateProvedDo.getPlatformTemplateId(), accountManagementResp);
                } catch (Exception e) {
                    log.error("从蜂动删除模板出错", e);
                }
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.BASE)
    public void cancelAudit(String chatbotAccount) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("chatbotAccount", chatbotAccount);
        map.put("deleteTime", DateUtil.date());
        int num = messageTemplateAuditDao.delMessageTemplateAuditByChatbotAccount(map);
        log.info("删除模板审核记录完成,chatbotAccount:{}, deleteTime:{}, 删除{}条", chatbotAccount, DateUtil.date(), num);
        messageTemplateProvedDao.delProvedMessageTemplateByChatbotAccount(map);
    }

    public void cancelAuditByTemplateId(Long templateId, Integer operator) {

        UpdateWrapper<MessageTemplateAuditDo> messageTemplateAuditDoUpdateWrapper = new UpdateWrapper<>();
        messageTemplateAuditDoUpdateWrapper.eq("template_id", templateId);
        messageTemplateAuditDoUpdateWrapper.set("deleted", 1);
        messageTemplateAuditDoUpdateWrapper.set("delete_time", DateTime.now());
        if (ObjectUtil.isNotNull(operator)) {
            messageTemplateAuditDoUpdateWrapper.eq("operator", operator);
        }
        messageTemplateAuditDao.update(null, messageTemplateAuditDoUpdateWrapper);
//        UpdateWrapper<MessageTemplateProvedDo> messageTemplateProvedDoUpdateWrapper=new UpdateWrapper<>();
//        messageTemplateProvedDoUpdateWrapper.eq("template_id",templateId);
//        messageTemplateProvedDoUpdateWrapper.set("deleted",1);
//        if(ObjectUtil.isNotNull(operator)) {
//            messageTemplateProvedDoUpdateWrapper.set("operator", operator);
//        }
//        messageTemplateProvedDoUpdateWrapper.set("delete_time", DateTime.now());
//        messageTemplateProvedDao.update(null,messageTemplateProvedDoUpdateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceChatbotAccount(String newChatbotAccount, String oldChatbotAccount) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("newChatbotAccount", newChatbotAccount);
        map.put("oldChatbotAccount", oldChatbotAccount);
        messageTemplateAuditDao.replaceChatbotAccount(map);
        messageTemplateProvedDao.replaceChatbotAccount(map);
    }

    @Override
    public MessageTemplateResp getMessageTemplateById(Long id) {
        MessageTemplateDo messageTemplateDo = messageTemplateDao.selectOne(MessageTemplateDo::getId, id);
        if (messageTemplateDo == null) return null;
        return BeanUtil.copyProperties(messageTemplateDo, MessageTemplateResp.class);
    }

    @Override
    public MessageTemplateResp getProvedTemplate(MessageTemplateProvedReq messageTemplateProvedReq) {
        log.info("getProvedTemplate：{}", JSONObject.toJSONString(messageTemplateProvedReq));
        Long templateId = messageTemplateProvedReq.getTemplateId();
        String accountType = messageTemplateProvedReq.getAccountType();
        if (ObjectUtil.isEmpty(accountType)) {
            throw new BizException("运营商不能为空");
        }
        String[] operators = accountType.contains(",") ? messageTemplateProvedReq.getAccountType().split(",") : new String[]{accountType};
        MessageTemplateProvedDo messageTemplateDo = null;
        for (int i = 0; i < operators.length; i++) {
            LambdaQueryWrapper<MessageTemplateProvedDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MessageTemplateProvedDo::getTemplateId, templateId);
            queryWrapper.eq(MessageTemplateProvedDo::getDeleted, 0);
            Integer operator = CarrierEnum.getCarrierEnum(operators[i]).getValue();
            queryWrapper.eq(MessageTemplateProvedDo::getOperator, operator);
            messageTemplateDo = messageTemplateProvedDao.selectOne(queryWrapper);
            if (ObjectUtil.isEmpty(messageTemplateDo)) {
                throw new BizException(com.citc.nce.auth.messagetemplate.entity.Constants.TEMPLATE_NOT_PROVED);
            }
        }
        Assert.notNull(messageTemplateDo, "template can not be null");
        MessageTemplateResp messageTemplateResp = BeanUtil.copyProperties(messageTemplateDo, MessageTemplateResp.class);
        messageTemplateResp.setId(messageTemplateDo.getTemplateId());
        return messageTemplateResp;
    }

    @Override
    public List<MessageTemplateTreeResp> getTreeList() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        wrapper.orderByDesc("create_time");
        List<MessageTemplateDo> messageTemplateDos = messageTemplateDao.selectList(wrapper);
        List<MessageTemplateTreeResp> messageTemplateTreeResps = BeanUtil.copyToList(messageTemplateDos, MessageTemplateTreeResp.class);
        return messageTemplateTreeResps;
    }

    @Override
    public List<Long> getTemplateIdsByCustmerId(String customerId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        //非机器人模板
        wrapper.eq("template_source", 1);
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", customerId);
        }
        wrapper.orderByDesc("create_time");
        List<MessageTemplateDo> messageTemplateDos = messageTemplateDao.selectList(wrapper);
        return messageTemplateDos.stream().map(MessageTemplateDo::getId).collect(Collectors.toList());
    }

    /**
     * 获取当前状态模板为审核通过的模板，不是从proved表获取已审核通过的模板
     *
     * @param queryReq
     * @return
     */
    @Override
    public List<MessageTemplateTreeResp> getProvedTreeList(MessageTemplateProvedForQueryReq queryReq) {
        if (ObjectUtil.isEmpty(queryReq)) {
            throw new BizException("请求参数为空");
        }
        if (ObjectUtil.isEmpty(queryReq.getTemplateSource())) {
            queryReq.setTemplateSource(Constants.TEMPLATE_SOURCE_TEMPLATEMANAGE);
        }
        log.info("获取已审核通过模板：{}", JSONObject.toJSONString(queryReq));
        //先通过运营商类型查询chatbotAccount
        List<Integer> operators = new ArrayList<>();
        LambdaQueryWrapper<MessageTemplateAuditDo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotEmpty(queryReq.getAccountType())) {
            String[] accountTypeItem = queryReq.getAccountType().split(",");
            for (int i = 0; i < accountTypeItem.length; i++) {
                operators.add(CarrierEnum.getCarrierEnum(accountTypeItem[i]).getValue());
            }
            lambdaQueryWrapper.in(MessageTemplateAuditDo::getOperator, operators);

        }
        lambdaQueryWrapper.eq(MessageTemplateAuditDo::getDeleted, 0);
        lambdaQueryWrapper.eq(MessageTemplateAuditDo::getStatus, Constants.TEMPLATE_STATUS_SUCCESS);
        lambdaQueryWrapper.eq(MessageTemplateAuditDo::getTemplateSource, queryReq.getTemplateSource());
        lambdaQueryWrapper.eq(MessageTemplateAuditDo::getCreator, SessionContextUtil.getUser().getUserId());
        List<MessageTemplateAuditDo> messageTemplateProvedDos = messageTemplateAuditDao.selectList(lambdaQueryWrapper);
        Map<Long, Long> collect = messageTemplateProvedDos.stream().collect(Collectors.groupingBy(item -> item.getTemplateId(), Collectors.counting()));
        List<Long> provedTemplateIds = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : collect.entrySet()) {
            //模板在所有账号下都审核通过
            if ((long) entry.getValue() == Long.valueOf(operators.size()) || operators.size() == 0) {
                provedTemplateIds.add(entry.getKey());
            }
        }
        if (ObjectUtil.isEmpty(provedTemplateIds))
            return null;
        LambdaQueryWrapper<MessageTemplateDo> lambdaQueryMessageTemplateDoWrapper = new LambdaQueryWrapper<>();
        lambdaQueryMessageTemplateDoWrapper.eq(MessageTemplateDo::getDeleted, 0);
        lambdaQueryMessageTemplateDoWrapper.eq(MessageTemplateDo::getTemplateSource, queryReq.getTemplateSource());
        lambdaQueryMessageTemplateDoWrapper.in(MessageTemplateDo::getId, provedTemplateIds);
        if (ObjectUtil.isNotNull(queryReq.getTemplateType())) {
            lambdaQueryMessageTemplateDoWrapper.eq(MessageTemplateDo::getTemplateType, queryReq.getTemplateType());

        }
        lambdaQueryMessageTemplateDoWrapper.eq(MessageTemplateDo::getCreator, SessionContextUtil.getUser().getUserId());
        lambdaQueryMessageTemplateDoWrapper.orderByDesc(MessageTemplateDo::getId);
        List<MessageTemplateDo> messageTemplateDos = messageTemplateDao.selectList(lambdaQueryMessageTemplateDoWrapper);
        List<MessageTemplateTreeResp> messageTemplateResps = BeanUtil.copyToList(messageTemplateDos, MessageTemplateTreeResp.class);

        return messageTemplateResps;
    }

    /**
     * 通过模板id查找json
     *
     * @param templateIds 模板id
     * @return json
     */
    @Override
    public List<String> selectAllTemplateJson(List<Long> templateIds) {
        LambdaQueryWrapper<MessageTemplateDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MessageTemplateDo::getId, templateIds);
        wrapper.eq(MessageTemplateDo::getDeleted, 0);//查询未删除的
        return messageTemplateDao.selectList(wrapper).stream().map(MessageTemplateDo::getModuleInformation).collect(Collectors.toList());
    }

    @Override
    public List<String> selectAllTemplateJsonByCreator() {
        if (ObjectUtil.isNull(SessionContextUtil.getUser())) {
            throw new BizException("未登录");
        }
        LambdaQueryWrapper<MessageTemplateDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageTemplateDo::getCreator, SessionContextUtil.getUser().getUserId());
        wrapper.eq(MessageTemplateDo::getDeleted, 0);//查询未删除的
        wrapper.ne(MessageTemplateDo::getTemplateSource, Constants.TEMPLATE_SOURCE_ROBOT);
        return messageTemplateDao.selectList(wrapper).stream().map(MessageTemplateDo::getModuleInformation).collect(Collectors.toList());

    }

    @Override
    public List<MessageTemplateResp> getMessageTemplateListByButtonId(MessageTemplateButtonReq messageTemplateButtonReq) {
        ArrayList<MessageTemplateDo> objects = new ArrayList<>();
        QueryWrapper<MessageTemplateDo> wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("creator", messageTemplateButtonReq.getCreator());
        wrapper.like("shortcut_button", messageTemplateButtonReq.getButtonId());

        List<MessageTemplateDo> messageTemplateDos = messageTemplateDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(messageTemplateDos)) {
            objects.addAll(messageTemplateDos);
        }
        QueryWrapper<MessageTemplateDo> wrapper1 = new QueryWrapper();
        wrapper1.eq("deleted", 0);
        wrapper1.eq("creator", messageTemplateButtonReq.getCreator());
        wrapper1.like("module_Information", messageTemplateButtonReq.getButtonId());
        List<MessageTemplateDo> messageTemplateDos1 = messageTemplateDao.selectList(wrapper1);
        if (CollectionUtils.isNotEmpty(messageTemplateDos1)) {
            objects.addAll(messageTemplateDos1);
        }
        List<MessageTemplateResp> messageTemplateResps = BeanUtil.copyToList(objects, MessageTemplateResp.class);
        return messageTemplateResps;
    }


    @Override
    public void templateStatusCallabck(TemplateStatusCallbackReq templateStatusCallbackDTO) {
        messageTemplateAuditManageService.templateStatusCallabck(templateStatusCallbackDTO);
    }

    @Override
    public void deleteTemplateForInvalidOfProcess(DeleteTemplateForInvalidOfProcessReq deleteTemplateForInvalidOfProcessReq) {

        List<Long> templateIds = deleteTemplateForInvalidOfProcessReq.getTemplateIds();
        int deleted = 1;
        Date deleteTime = DateUtil.date();
        //String creator = SessionContextUtil.getUser().getUserId();
        Long processId = deleteTemplateForInvalidOfProcessReq.getProcessId();
        log.info("清理机器人流程的无效模板：{}", JSONObject.toJSONString(deleteTemplateForInvalidOfProcessReq));
        messageTemplateDao.deleteTemplateForInvalidOfProcess(deleteTime, deleted, processId, templateIds);
        messageTemplateAuditDao.deleteTemplateAuditForInvalidOfProcess(deleteTime, deleted, processId, templateIds);
        List<MessageTemplateProvedDo> messageTemplateProvedDos = messageTemplateProvedDao.selectTemplateProvedForInvalidOfProcess(processId, templateIds);
        deleteProvedTemplateFromPlatformForFontDo(messageTemplateProvedDos);
        messageTemplateProvedDao.deleteTemplateProvedForInvalidOfProcess(deleteTime, deleted, processId, templateIds);

    }

    /**
     * 比较模板内容是否一致 false 不相同 ，true 相同
     *
     * @param newMessageTemplateDo
     * @param oldMessageTemplateDo
     * @return
     */
    private boolean compareTemplateContent(MessageTemplateDo newMessageTemplateDo, MessageTemplateDo oldMessageTemplateDo) {
        if (!JsonSameUtil.same(newMessageTemplateDo.getModuleInformation(), oldMessageTemplateDo.getModuleInformation(), ""))
            return false;
        if (!JsonSameUtil.same(newMessageTemplateDo.getShortcutButton(), oldMessageTemplateDo.getShortcutButton(), ""))
            return false;
        return JsonSameUtil.same(newMessageTemplateDo.getStyleInformation(), oldMessageTemplateDo.getStyleInformation(), "");
    }

    @Override
    public List<MessageTemplateProvedResp> getPlatformTemplateIds(MessageTemplateProvedListReq req) {
        ArrayList<MessageTemplateProvedResp> result = new ArrayList<>();

        for (MessageTemplateProvedReq templateProvedReq : req.getTemplateProvedReqs()) {
            LambdaQueryWrapper<MessageTemplateProvedDo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MessageTemplateProvedDo::getTemplateId, templateProvedReq.getTemplateId())
                    .eq(MessageTemplateProvedDo::getSupplierTag, templateProvedReq.getSupplierTag())
                    .eq(MessageTemplateProvedDo::getOperator, templateProvedReq.getOperator());
            MessageTemplateProvedDo messageTemplateProvedDo = messageTemplateProvedDao.selectOne(wrapper);
            if (Objects.nonNull(messageTemplateProvedDo)) {
                MessageTemplateProvedResp templateProvedResp = new MessageTemplateProvedResp();
                BeanUtil.copyProperties(messageTemplateProvedDo, templateProvedResp);
                templateProvedResp.setId(messageTemplateProvedDo.getTemplateId());
                result.add(templateProvedResp);
            }
        }
        return result;
    }


    @Override
    public MessageTemplatePageResp get5gTemplatesList(MessageTemplatePageReq messageTemplatePageReq) {
        MessageTemplatePageResp resp = new MessageTemplatePageResp();
        QueryWrapper<MessageTemplateDo> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (messageTemplatePageReq.getMessageType() != null && messageTemplatePageReq.getMessageType() != 0) {
            wrapper.eq("message_type", messageTemplatePageReq.getMessageType());
        }
        if (messageTemplatePageReq.getTemplateType() != null && messageTemplatePageReq.getTemplateType() != 0) {
            wrapper.eq("template_type", messageTemplatePageReq.getTemplateType());
        }
        if (StringUtils.isNotEmpty(messageTemplatePageReq.getTemplateName())) {
            wrapper.like("template_name", messageTemplatePageReq.getTemplateName());
        }

        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        //根据创建时间排序
        wrapper.orderByDesc("create_time");
        List<MessageTemplateDo> messageTemplateDoPageResult = messageTemplateDao.selectList(wrapper);
        List<TemplateInfoItem> templateInfoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(messageTemplateDoPageResult)) {
            TemplateInfoItem templateInfo;
            for (MessageTemplateDo item : messageTemplateDoPageResult) {
                templateInfo = new TemplateInfoItem();
                templateInfo.setTemplateId(item.getId());
                templateInfo.setTemplateName(item.getTemplateName());
                templateInfoList.add(templateInfo);
            }
        }
        resp.setTemplateInfoList(templateInfoList);
        return resp;
    }

    @Override
    public MessageTemplateResp getByName(String templateName) {
        MessageTemplateResp resp = new MessageTemplateResp();
        QueryWrapper<MessageTemplateDo> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        wrapper.eq("template_name", templateName);
        wrapper.eq("creator", SessionContextUtil.getUser().getUserId());
        wrapper.orderByDesc("create_time");
        wrapper.last("limit 1");
        MessageTemplateDo messageTemplateDo = messageTemplateDao.selectOne(wrapper);
        BeanUtil.copyProperties(messageTemplateDo, resp);
        return resp;
    }

    @Override
    public Map<Long, Integer> queryMessageTypeByTemplateIds(List<Long> templateIds) {
        @SuppressWarnings("unchecked")
        LambdaQueryWrapper<MessageTemplateDo> qw = Wrappers.<MessageTemplateDo>lambdaQuery()
                .in(MessageTemplateDo::getId, templateIds)
                .select(MessageTemplateDo::getId, MessageTemplateDo::getMessageType);
        return messageTemplateDao.selectList(qw).stream()
                .collect(Collectors.toMap(MessageTemplateDo::getId, MessageTemplateDo::getMessageType));
    }

    @Override
    public Map<Long, String> queryTemplateNameByTemplateIds(List<Long> templateIds) {
        @SuppressWarnings("unchecked")
        LambdaQueryWrapper<MessageTemplateDo> qw = Wrappers.<MessageTemplateDo>lambdaQuery()
                .in(MessageTemplateDo::getId, templateIds)
                .select(MessageTemplateDo::getId, MessageTemplateDo::getTemplateName);
        return messageTemplateDao.selectList(qw).stream()
                .collect(Collectors.toMap(MessageTemplateDo::getId, MessageTemplateDo::getTemplateName));
    }

    @Override
    public Integer queryMessageTypeByTemplateId(Long templateId) {
        return Optional.ofNullable(messageTemplateDao.selectOne(MessageTemplateDo::getId, templateId))
                .map(MessageTemplateDo::getMessageType)
                .orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteLastReplyTemplateByCustomerId(String customerId) {
        Assert.isTrue(SessionContextUtil.getUserId().equals(customerId), "权限不足");
        if (messageTemplateDao.delete(
                new LambdaQueryWrapper<MessageTemplateDo>()
                        .eq(BaseDo::getCreator, customerId)
                        .eq(MessageTemplateDo::getTemplateSource, TEMPLATE_SOURCE_LASTREPLY)
        ) > 0) {
            log.info("清理:{}机器人兜底回复模板", customerId);
        }
    }

    @Override
    public TemplateDataResp createFontdoTemplate(MessageTemplateMultiTriggerReq messageTemplateMultiTriggerReq) {
        try {
            TemplateOwnershipReflect templateOwnershipReflect = new TemplateOwnershipReflect();
            //设置templateOwnershipReflect所有参数
            templateOwnershipReflect.setCreator("system");
            templateOwnershipReflect.setSupplierTag(CSPChatbotSupplierTagEnum.FONTDO.getValue());
            templateOwnershipReflect.setOperator(messageTemplateMultiTriggerReq.getOperator());
            templateOwnershipReflect.setChatbotAccount(messageTemplateMultiTriggerReq.getChatbotAccount());
            templateOwnershipReflect.setAccountTypeCode(CarrierEnum.getCarrierEnum(messageTemplateMultiTriggerReq.getOperator()).getValue());

            MessageTemplateDo messageTemplateDo = new MessageTemplateDo();
            BeanUtil.copyProperties(messageTemplateMultiTriggerReq, messageTemplateDo);
            String moduleInformation = messageTemplateDo.getModuleInformation();
            if (StrUtil.isNotEmpty(moduleInformation)) {
                JSONObject module = JSON.parseObject(moduleInformation);
                messageTemplateDo.setModuleInformation(module.toString().replace("*", ""));
            }
            AccountManagementResp accountManagementResp = accountManagementApi.getAccountManagementByAccountId(messageTemplateMultiTriggerReq.getChatbotAccount());

            com.citc.nce.auth.messagetemplate.entity.TemplateDataResp template = platfomManageTemplateService.createTemplate(templateContentMakeUp.buildMessageParams(messageTemplateDo, templateOwnershipReflect), accountManagementResp);
            TemplateDataResp templateDataResp = new TemplateDataResp();
            BeanUtil.copyProperties(template, templateDataResp);
            return templateDataResp;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("向网关请求创建模板是出错：");
            log.error(e.getMessage());
            TemplateDataResp templateDataResp = new TemplateDataResp();
            templateDataResp.setCode(500);
            templateDataResp.setMessage(e.getMessage());
            return templateDataResp;
        }
    }
}
