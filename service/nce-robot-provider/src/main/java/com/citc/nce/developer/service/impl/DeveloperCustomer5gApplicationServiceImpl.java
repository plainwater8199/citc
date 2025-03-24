package com.citc.nce.developer.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.identification.IdentificationApi;
import com.citc.nce.auth.identification.vo.resp.WebEnterpriseIdentificationResp;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.auth.readingLetter.template.ReadingLetterTemplateApi;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.RsaUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.conf.CommonKeyPairConfig;
import com.citc.nce.configure.DeveloperReceiveConfigure;
import com.citc.nce.developer.dao.DeveloperCustomer5gApplicationMapper;
import com.citc.nce.developer.dao.DeveloperSendMapper;
import com.citc.nce.developer.entity.DeveloperCustomer5gApplicationDo;
import com.citc.nce.developer.entity.DeveloperCustomer5gDo;
import com.citc.nce.developer.service.DeveloperCustomer5gApplicationService;
import com.citc.nce.developer.service.DeveloperCustomer5gService;
import com.citc.nce.developer.vo.Developer5gApplicationNameVo;
import com.citc.nce.developer.vo.Developer5gApplicationVo;
import com.citc.nce.developer.vo.DeveloperAccountVo;
import com.citc.nce.developer.vo.DeveloperCustomer5gManagerVo;
import com.citc.nce.developer.vo.DeveloperQueryApplicationVo;
import com.citc.nce.developer.vo.DeveloperSend5gCountVo;
import com.citc.nce.developer.vo.DeveloperSetStateApplicationVo;
import com.citc.nce.developer.vo.SmsDeveloperCustomerReqVo;
import com.citc.nce.fileApi.ExamineResultApi;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.ExamineReq;
import com.citc.nce.utils.AuthInfoUtils;
import com.citc.nce.utils.UUIDUtil;
import com.citc.nce.vo.ExamineResultResp;
import com.citc.nce.vo.ExamineResultVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ping chen
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Service
public class DeveloperCustomer5gApplicationServiceImpl extends ServiceImpl<DeveloperCustomer5gApplicationMapper, DeveloperCustomer5gApplicationDo> implements DeveloperCustomer5gApplicationService {

    /**
     * AppId前缀
     */
    private static final String AppIdPrefix = "5G";

    private final MessageTemplateApi messageTemplateApi;

    private final ExamineResultApi examineResultApi;

    private final DeveloperCustomer5gService developerCustomer5gService;

    private final FileApi fileApi;

    private final CspCustomerApi cspCustomerApi;

    private final DeveloperCustomer5gApplicationMapper developerCustomer5gApplicationMapper;

    private final DeveloperSendMapper developerSendMapper;

    private final IdentificationApi identificationApi;

    private final AccountManagementApi accountManagementApi;
    private final DeveloperReceiveConfigure receiveConfigure;
    private final CommonKeyPairConfig commonKeyPairConfig;

    private final ReadingLetterTemplateApi readingLetterTemplateApi;

    @Override
    public void saveApplication(Developer5gApplicationVo developerSaveApplicationVo) {
        //判定新增是否已经超过10个
        if (this.lambdaQuery()
                .eq(DeveloperCustomer5gApplicationDo::getCustomerId, SessionContextUtil.getUser().getUserId())
                .isNull(DeveloperCustomer5gApplicationDo::getDeleteTime)
                .count() >= 10) {
            throw new BizException(AuthError.DEVELOPER_APPLICATION_MAX);
        }
        //获取该模板所有已审核通过的通道的chatbot信息
        List<AccountManagementResp> accountManagementResps = messageTemplateApi.getAccountForProvedTemplateCarrier(developerSaveApplicationVo.getTemplateId());
        if (ObjectUtil.isEmpty(accountManagementResps)) {
            throw new BizException("模板未过审");
        }
        if (StrUtil.isEmpty(developerSaveApplicationVo.getChatbotAccountId())) {
            throw new BizException("5G消息账号未选择");
        }
        String[] accountsId = developerSaveApplicationVo.getChatbotAccountId().split(",");
        List<String> notAuditAccountArr = new ArrayList<>();
        //模板在所选账号下未审核通过，不能创建应用
        for (int i = 0; i < accountsId.length; i++) {
            String accountId = accountsId[i];
            if (accountManagementResps.stream().filter(item -> item.getChatbotAccountId().equals(accountId)).count() == 0) {
                AccountManagementResp accountManagementResp = accountManagementApi.getAccountManagementByChatbotAccountId(accountId);
                notAuditAccountArr.add(accountManagementResp.getAccountName());
                continue;
            }
        }
        if (ObjectUtil.isNotEmpty(notAuditAccountArr)) {
            String accountsStr = notAuditAccountArr.stream().map(item -> "【" + item + "】").collect(Collectors.joining(""));
            throw new BizException(String.format("模板未在%s账号下审核通过", accountsStr));
        }
        this.saveOrUpdateApplication(developerSaveApplicationVo);
    }

    @Override
    public void editApplication(Developer5gApplicationVo developerSaveApplicationVo) {
        //获取该模板所有已审核通过的通道的chatbot信息
        List<AccountManagementResp> accountManagementResps = messageTemplateApi.getAccountForProvedTemplateCarrier(developerSaveApplicationVo.getTemplateId());
        if (ObjectUtil.isEmpty(accountManagementResps)) {
            throw new BizException("模板未过审");
        }
        if (StrUtil.isEmpty(developerSaveApplicationVo.getChatbotAccountId())) {
            throw new BizException("5G消息账号未选择");
        }
        String[] accountsId = developerSaveApplicationVo.getChatbotAccountId().split(",");
        List<String> notAuditAccountArr = new ArrayList<>();
        //模板在所选账号下未审核通过，不能创建应用
        for (int i = 0; i < accountsId.length; i++) {
            String accountId = accountsId[i];
            if (accountManagementResps.stream().filter(item -> item.getChatbotAccountId().equals(accountId)).count() == 0) {
                AccountManagementResp accountManagementResp = accountManagementApi.getAccountManagementByChatbotAccountId(accountId);
                notAuditAccountArr.add(accountManagementResp.getAccountName());
                continue;
            }
        }
        if (ObjectUtil.isNotEmpty(notAuditAccountArr)) {
            String accountsStr = notAuditAccountArr.stream().map(item -> "【" + item + "】").collect(Collectors.joining(""));
            throw new BizException(String.format("模板未在%s账号下审核通过", accountsStr));
        }
        this.saveOrUpdateApplication(developerSaveApplicationVo);
    }

    @Override
    public void deleteApplication(String uniqueId) {
        DeveloperCustomer5gApplicationDo app = getByUniqueId(uniqueId);
        if (Objects.isNull(app)) return;
        SessionContextUtil.sameCus(app.getCustomerId());
        removeById(app.getId());
    }

    private DeveloperCustomer5gApplicationDo getByUniqueId(String uniqueId) {
        return this.lambdaQuery().eq(DeveloperCustomer5gApplicationDo::getUniqueId, uniqueId).one();
    }

    @Override
    public void setStateApplication(DeveloperSetStateApplicationVo developerSetStateApplicationVo) {
        DeveloperCustomer5gApplicationDo unique = getByUniqueId(developerSetStateApplicationVo.getUniqueId());
        if (Objects.isNull(unique)) {
            throw new BizException("应用不存在或已删除");
        }
        //越权
        SessionContextUtil.sameCsp(unique.getCspId());

        this.update(new LambdaUpdateWrapper<DeveloperCustomer5gApplicationDo>().set(DeveloperCustomer5gApplicationDo::getApplicationState, developerSetStateApplicationVo.getState())
                .eq(DeveloperCustomer5gApplicationDo::getUniqueId, developerSetStateApplicationVo.getUniqueId()));
    }

    @Override
    public Developer5gApplicationVo queryApplicationWithoutLogin(String uniqueId) {
        DeveloperCustomer5gApplicationDo developerCustomer5gApplicationDo = getByUniqueId(uniqueId);
        if (Objects.isNull(developerCustomer5gApplicationDo)) {
            throw new BizException("应用不存在或已删除");
        }
        Developer5gApplicationVo developerSaveApplicationVo = new Developer5gApplicationVo();
        BeanUtils.copyProperties(developerCustomer5gApplicationDo, developerSaveApplicationVo);

        return developerSaveApplicationVo;
    }

    @Override
    public List<Developer5gApplicationNameVo> queryApplicationWithoutLogin(List<Long> ids) {
        List<Developer5gApplicationNameVo> result = new ArrayList<>();
        LambdaQueryWrapper<DeveloperCustomer5gApplicationDo> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        objectLambdaQueryWrapper.in(DeveloperCustomer5gApplicationDo::getId, ids);
        List<DeveloperCustomer5gApplicationDo> developerCustomer5gApplicationDos = developerCustomer5gApplicationMapper.selectList(objectLambdaQueryWrapper);
        for (DeveloperCustomer5gApplicationDo applicationDo : developerCustomer5gApplicationDos) {
            Developer5gApplicationNameVo developer5gApplicationNameVo = new Developer5gApplicationNameVo();
            developer5gApplicationNameVo.setId(applicationDo.getId());
            developer5gApplicationNameVo.setApplicationName(applicationDo.getApplicationName());
            result.add(developer5gApplicationNameVo);
        }
        return result;
    }

    @Override
    public Developer5gApplicationVo queryApplicationInfo(String uniqueId) {
        DeveloperCustomer5gApplicationDo developerCustomer5gApplicationDo = getByUniqueId(uniqueId);
        if (Objects.isNull(developerCustomer5gApplicationDo)) {
            throw new BizException("应用不存在或已删除");
        }
        //越权
        SessionContextUtil.sameCsp(developerCustomer5gApplicationDo.getCspId());

        Developer5gApplicationVo developerSaveApplicationVo = new Developer5gApplicationVo();
        BeanUtils.copyProperties(developerCustomer5gApplicationDo, developerSaveApplicationVo);
        DeveloperCustomer5gDo developerCustomer5gDo = developerCustomer5gService.lambdaQuery().eq(DeveloperCustomer5gDo::getUniqueId, developerCustomer5gApplicationDo.getDeveloperCustomerUniqueId()).one();
        if (developerCustomer5gDo != null) {
            developerSaveApplicationVo.setCallbackUrl(developerCustomer5gDo.getCallbackUrl());
        }
        MessageTemplateResp messageTemplateResp = messageTemplateApi.getMessageTemplateById(developerSaveApplicationVo.getTemplateId());
        if (messageTemplateResp != null) {
            developerSaveApplicationVo.setTemplateName(messageTemplateResp.getTemplateName());
        }
        WebEnterpriseIdentificationResp webEnterpriseIdentificationResp = identificationApi.getEnterpriseIdentificationInfoByUserId(developerCustomer5gApplicationDo.getCustomerId());
        if (webEnterpriseIdentificationResp != null) {
            developerSaveApplicationVo.setEnterpriseAccountName(webEnterpriseIdentificationResp.getEnterpriseName());
        }

        if (StringUtils.isNotBlank(developerSaveApplicationVo.getChatbotAccountId())) {
            String[] accountIds = developerSaveApplicationVo.getChatbotAccountId().split(",");
            List<String> accountNames = new ArrayList<>(accountIds.length);
            for (String accId : accountIds) {
                AccountManagementResp accountManagementResp = accountManagementApi.getAccountManagementByChatbotAccountId(accId);
                if (accountManagementResp != null) {
                    accountNames.add(accountManagementResp.getAccountName());
                }
            }
            developerSaveApplicationVo.setChatbotAccountName(String.join(",", accountNames));
        }
        return developerSaveApplicationVo;
    }

    @Override
    public PageResult<Developer5gApplicationVo> queryApplicationList(DeveloperQueryApplicationVo query) {
        Page<DeveloperCustomer5gApplicationDo> page = Page.of(query.getPageNo(), query.getPageSize());
        LambdaQueryWrapper<DeveloperCustomer5gApplicationDo> doLambdaQueryWrapper = new LambdaQueryWrapper<DeveloperCustomer5gApplicationDo>()
                .eq(Objects.nonNull(query.getState()) && query.getState() <= 1, DeveloperCustomer5gApplicationDo::getApplicationState, query.getState())
                .eq(Objects.nonNull(query.getState()) && query.getState() > 1, DeveloperCustomer5gApplicationDo::getApplicationTemplateState, query.getState())
                .eq(DeveloperCustomer5gApplicationDo::getCustomerId, SessionContextUtil.getLoginUser().getUserId())
                .isNull(DeveloperCustomer5gApplicationDo::getDeleteTime)
                .like(StringUtils.isNotBlank(query.getName()), DeveloperCustomer5gApplicationDo::getApplicationName, query.getName())
                .orderByDesc(DeveloperCustomer5gApplicationDo::getCreateTime);
        developerCustomer5gApplicationMapper.selectPage(page, doLambdaQueryWrapper);
        List<Developer5gApplicationVo> voArrayList = new ArrayList<>();
        for (DeveloperCustomer5gApplicationDo entity : page.getRecords()) {
            Developer5gApplicationVo vo = new Developer5gApplicationVo();
            BeanUtils.copyProperties(entity, vo);
            vo.setTemplateState(entity.getApplicationTemplateState());
            vo.setAppSecret(RsaUtil.decryptByPrivateKey(commonKeyPairConfig.getPrivateKey(), entity.getAppSecret()));
            //设置messageTemplate
            if (Objects.nonNull(entity.getTemplateId())) {
                vo.setMessageTemplate(messageTemplateApi.getMessageTemplateById(entity.getTemplateId()));
            }
            //设置fallbackReadingLetterTemplate
            if (Objects.nonNull(entity.getFallbackReadingLetterTemplateId())) {
                vo.setFallbackReadingLetterTemplate(readingLetterTemplateApi.getTemplateInfo(entity.getFallbackReadingLetterTemplateId()));
            }
            voArrayList.add(vo);
        }
        return new PageResult<>(voArrayList, page.getTotal());
    }


    @Override
    public PageResult<DeveloperCustomer5gManagerVo> queryList(SmsDeveloperCustomerReqVo smsDeveloperCustomerReqVo) {
        String cpsId = SessionContextUtil.getUser().getUserId();
        Page<DeveloperCustomer5gManagerVo> developerCustomer5gManagerVoPage = Page.of(smsDeveloperCustomerReqVo.getPageNo(), smsDeveloperCustomerReqVo.getPageSize());
        developerCustomer5gManagerVoPage.setOrders(OrderItem.descs("dc5ga.create_time"));
        Page<DeveloperCustomer5gManagerVo> page = baseMapper.searchCustomersManager(smsDeveloperCustomerReqVo.getCustomerId(),
                smsDeveloperCustomerReqVo.getState(), cpsId, smsDeveloperCustomerReqVo.getApplicationName(), developerCustomer5gManagerVoPage);
        List<DeveloperCustomer5gManagerVo> developerCustomer5gManagerVoList = page.getRecords();
        if (CollectionUtil.isNotEmpty(developerCustomer5gManagerVoList)) {
            List<String> customerIds = developerCustomer5gManagerVoList.stream().map(DeveloperCustomer5gManagerVo::getCustomerId).distinct().collect(Collectors.toList());
            Map<String,String> enterpriseNameMap = identificationApi.getEnterpriseIdentificationInfoByUserIds(customerIds);

            //查询发送表
            List<DeveloperSend5gCountVo> developerSend5gDoList = developerSendMapper.query5gCount(customerIds, 1);
            Map<String, Integer> sendMap = developerSend5gDoList.stream().collect(Collectors.toMap(DeveloperSend5gCountVo::getApplicationUniqueId, DeveloperSend5gCountVo::getCount));

            //查询统计表
            List<DeveloperSend5gCountVo> developerSendCountVoList = developerSendMapper.query5gCountAll(customerIds, 1);
            Map<String, Integer> sendStatisticsMap = developerSendCountVoList.stream().collect(Collectors.toMap(DeveloperSend5gCountVo::getApplicationUniqueId, DeveloperSend5gCountVo::getCount));
            developerCustomer5gManagerVoList.forEach(developerCustomer5gManagerVo -> {
                developerCustomer5gManagerVo.setEnterpriseAccountName(enterpriseNameMap.getOrDefault(developerCustomer5gManagerVo.getCustomerId(),null));
                if (sendMap.get(developerCustomer5gManagerVo.getUniqueId()) != null) {
                    developerCustomer5gManagerVo.setCallCountToday(sendMap.get(developerCustomer5gManagerVo.getUniqueId()));
                } else {
                    developerCustomer5gManagerVo.setCallCountToday(0);
                }
                if (sendStatisticsMap.get(developerCustomer5gManagerVo.getUniqueId()) != null) {
                    developerCustomer5gManagerVo.setCallCount(sendStatisticsMap.get(developerCustomer5gManagerVo.getUniqueId()));
                } else {
                    developerCustomer5gManagerVo.setCallCount(0);
                }
            });
        } else {
            developerCustomer5gManagerVoList = new ArrayList<>();
        }
        return new PageResult<>(developerCustomer5gManagerVoList, page.getTotal());
    }

    @Override
    public PageResult<DeveloperAccountVo> get5gDeveloperCustomerOption() {
        String cspId = SessionContextUtil.getLoginUser().getCspId();
        List<DeveloperCustomer5gApplicationDo> applicationList = this.lambdaQuery()
                .eq(DeveloperCustomer5gApplicationDo::getCspId, cspId)
                .select(DeveloperCustomer5gApplicationDo::getCustomerId)
                .list();
        if (CollectionUtils.isEmpty(applicationList))
            return new PageResult<>();
        List<String> customerIds = applicationList.stream()
                .map(DeveloperCustomer5gApplicationDo::getCustomerId)
                .distinct()
                .collect(Collectors.toList());
        List<DeveloperAccountVo> accountVos = cspCustomerApi.getUserEnterpriseInfoByUserIds(customerIds)
                .stream()
                .map(userEnterpriseInfoVo -> new DeveloperAccountVo().setCustomerId(userEnterpriseInfoVo.getUserId()).setEnterpriseName(userEnterpriseInfoVo.getEnterpriseName()))
                .collect(Collectors.toList());
        return new PageResult<>(accountVos, (long) accountVos.size());
    }

    public void saveOrUpdateApplication(Developer5gApplicationVo developerSaveApplicationVo) {
        String fileUuid = get5GFileUuid(developerSaveApplicationVo.getTemplateId(), developerSaveApplicationVo.getTemplateType());
        List<ExamineResultVo> examineResultVoList = new ArrayList<>();
        if (StringUtils.isNotBlank(fileUuid)) {
            String[] accountIds = developerSaveApplicationVo.getChatbotAccountId().split(",");
            for (String accountId : accountIds) {
                ExamineResultResp examineResultResp = new ExamineResultResp();
                AccountManagementResp accountInfo = accountManagementApi.getAccountManagementByChatbotAccountId(accountId);
                SessionContextUtil.sameCus(accountInfo.getCustomerId()); //越权
                examineResultResp.setChatbotId(accountInfo.getChatbotAccount());
                examineResultResp.setFileUuid(fileUuid);
                ExamineResultVo examineResultVo = examineResultApi.queryExamineResult(examineResultResp);
                //如果没查询到认为送审已经过期
                if (Objects.isNull(examineResultVo)) {
                    examineResultVo = new ExamineResultVo();
                    examineResultVo.setFileStatus(2);
                    examineResultVo.setValidity(new Date(0L));
                    examineResultVo.setOperator(accountInfo.getAccountType());
                }
                if (examineResultVo.getFileStatus() == 3) {
                    throw new BizException(AuthError.DEVELOPER_TEMPLATE_ABNORMAL);
                }
                examineResultVoList.add(examineResultVo);
            }
        }
        //判断是新增还是编辑
        DeveloperCustomer5gApplicationDo developerCustomer5gApplicationDo;
        if (StringUtils.isNotBlank(developerSaveApplicationVo.getUniqueId())) {
            developerCustomer5gApplicationDo = this.lambdaQuery().eq(DeveloperCustomer5gApplicationDo::getUniqueId, developerSaveApplicationVo.getUniqueId()).one();
        } else {
            developerCustomer5gApplicationDo = new DeveloperCustomer5gApplicationDo();
            String appId = UUIDUtil.getuuid();
            developerCustomer5gApplicationDo.setAppId(appId);
            developerCustomer5gApplicationDo.setAppKey(AuthInfoUtils.generateRandomString(AppIdPrefix));
            String appSecret = AuthInfoUtils.generateRandomString(32);
            String rawAppSecret = RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), appSecret);
            developerCustomer5gApplicationDo.setAppSecret(rawAppSecret);
            BaseUser user = SessionContextUtil.getUser();
            DeveloperCustomer5gDo developerCustomer5gDo = developerCustomer5gService.lambdaQuery().eq(DeveloperCustomer5gDo::getCustomerId, user.getUserId()).one();
            developerCustomer5gApplicationDo.setDeveloperCustomerUniqueId(developerCustomer5gDo.getUniqueId());
            developerCustomer5gApplicationDo.setUniqueId(UUIDUtil.getuuid());
            String customerId = user.getUserId();
            developerCustomer5gApplicationDo.setCustomerId(customerId);
            developerCustomer5gApplicationDo.setCspId(user.getCspId());
            WebEnterpriseIdentificationResp webEnterpriseIdentificationResp = identificationApi.getEnterpriseIdentificationInfoByUserId(customerId);
            if (webEnterpriseIdentificationResp != null) {
                developerCustomer5gApplicationDo.setEnterpriseId(webEnterpriseIdentificationResp.getId());
            }
        }
        developerCustomer5gApplicationDo.setChatbotAccountId(developerSaveApplicationVo.getChatbotAccountId());
        developerCustomer5gApplicationDo.setApplicationName(developerSaveApplicationVo.getApplicationName());
        developerCustomer5gApplicationDo.setApplicationDescribe(developerSaveApplicationVo.getApplicationDescribe());
        developerCustomer5gApplicationDo.setTemplateType(developerSaveApplicationVo.getTemplateType());
        developerCustomer5gApplicationDo.setTemplateId(developerSaveApplicationVo.getTemplateId());
        developerCustomer5gApplicationDo.setApplicationState(0);
        developerCustomer5gApplicationDo.setApplicationTemplateState(4);
        //设置阅信模板或短信回落
        developerCustomer5gApplicationDo.setAllowFallback(developerSaveApplicationVo.getAllowFallback());
        developerCustomer5gApplicationDo.setFallbackReadingLetterTemplateId(developerSaveApplicationVo.getFallbackReadingLetterTemplateId());
        developerCustomer5gApplicationDo.setFallbackSmsContent(developerSaveApplicationVo.getFallbackSmsContent());
        developerCustomer5gApplicationDo.setFallbackType(developerSaveApplicationVo.getFallbackType());

        //判断素材是否过期
        for (ExamineResultVo examineResultVo : examineResultVoList) {
            if (examineResultVo.getValidity() != null && examineResultVo.getValidity().compareTo(new Date()) < 0) {
                //素材过期送审
                ExamineReq examineReq = new ExamineReq();
                examineReq.setOperators(Collections.singletonList(examineResultVo.getOperator()));
                examineReq.setFileUUIDs(Collections.singletonList(fileUuid));
                fileApi.examine(examineReq);
                developerCustomer5gApplicationDo.setApplicationTemplateState(2);
            }
        }

        //越权
        if (Objects.nonNull(developerCustomer5gApplicationDo.getId())) {
            SessionContextUtil.sameCus(developerCustomer5gApplicationDo.getCustomerId());
        }

        this.saveOrUpdate(developerCustomer5gApplicationDo);
    }


    //模板状态:2:模板检查中，3:模板异常,4:审核成功
    public Integer refreshTemplateStatus(DeveloperCustomer5gApplicationDo entity) {
        String fileUuid = null;
        try {
            fileUuid = get5GFileUuid(entity.getTemplateId(), entity.getTemplateType());
            //没有素材自动审核成功
            if (StringUtils.isEmpty(fileUuid)) return 4;
        } catch (BizException e) {
            log.warn("refreshTemplateStatus templateId {} 模板异常 {}", entity.getTemplateId(), e.getMsg());
            return 3;
        }

        String[] accountIds = entity.getChatbotAccountId().split(",");
        Integer targetNum = accountIds.length;
        Integer passNum = 0;
        for (String accountId : accountIds) {
            AccountManagementResp accountInfo = accountManagementApi.getAccountManagementByChatbotAccountId(accountId);
            if (Objects.isNull(accountInfo)) {
                log.warn("机器人被删除，不做处理 我们平台accountId {}", accountId);
                --targetNum;
                continue;
            }
            ExamineResultVo examineResultVo = examineResultApi.queryExamineResult(new ExamineResultResp(accountInfo.getChatbotAccount(), fileUuid));
            //重新送审 状态不变找需求确认
            if (Objects.isNull(examineResultVo) || (Objects.nonNull(examineResultVo.getValidity()) && examineResultVo.getValidity().compareTo(new Date()) < 0)) {
                //素材过期送审
                ExamineReq examineReq = new ExamineReq();
                examineReq.setOperators(Collections.singletonList(accountInfo.getAccountType()));
                examineReq.setFileUUIDs(Collections.singletonList(fileUuid));
                examineReq.setCreator(entity.getCreator());
                fileApi.examine(examineReq);
            } else if (examineResultVo.getFileStatus() == 2) {
                ++passNum;
            }
        }
        //如果素材判断出是通过那么审核成功，审核失败时如果还在检查中那么不改变状态 (检测中只出现先创建应用之后，如果有了结果后，那么需要修改状态是成功还是失败，)
        return targetNum.equals(passNum) ? 4 : entity.getApplicationTemplateState() == 2 ? 2 : 3;
    }


    @Override
    public void appSecretEncode() {
        List<DeveloperCustomer5gApplicationDo> list = getBaseMapper().selectListEncode();
        if (CollectionUtil.isEmpty(list)) return;
        list.forEach(s -> s.setAppSecret(RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), s.getAppSecret())));
        updateBatchById(list);
    }

    private String get5GFileUuid(Long templateId, Integer templateType) {
        String fileUuid = null;
        //检查模板是否存在素材未通过
        MessageTemplateResp messageTemplateResp = messageTemplateApi.getMessageTemplateById(templateId);
        if (Objects.isNull(messageTemplateResp)) {
            throw new BizException("模板不存在");
        }
        if (!templateType.equals(messageTemplateResp.getTemplateType())) {
            throw new BizException("模板类型不匹配");
        }
//        //越权 如果是登录用户就判断数据是自己的
        if(Objects.isNull(SessionContextUtil.getUser()) || (Objects.nonNull(SessionContextUtil.getUser()) && (SessionContextUtil.getLoginUser().getUserId().equals(messageTemplateResp.getCreator())))){
            String moduleInformation = messageTemplateResp.getModuleInformation();
            JSONObject jsonObject = JsonUtils.string2Obj(moduleInformation, JSONObject.class);

            switch (messageTemplateResp.getMessageType()) {
                case 2:
                    //图片
                    fileUuid = jsonObject.getString("pictureUrlId");
                    break;
                case 3:
                    //视屏
                    fileUuid = jsonObject.getString("videoUrlId");
                    break;
                case 4:
                    //音频
                    fileUuid = jsonObject.getString("audioUrlId");
                    break;
                default:
                    break;
            }
        }
        return fileUuid;
    }
}
