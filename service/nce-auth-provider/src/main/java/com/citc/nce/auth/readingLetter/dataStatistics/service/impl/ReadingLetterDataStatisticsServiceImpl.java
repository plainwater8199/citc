package com.citc.nce.auth.readingLetter.dataStatistics.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTypeReq;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.csp.readingLetter.service.CspReadingLetterAccountService;
import com.citc.nce.auth.csp.readingLetter.vo.CustomerReadingLetterAccountListVo;
import com.citc.nce.auth.csp.readingLetter.vo.CustomerReadingLetterAccountSearchReq;
import com.citc.nce.auth.readingLetter.dataStatistics.dto.DataStatisticsReq;
import com.citc.nce.auth.readingLetter.dataStatistics.dto.FifthDataStatisticsReq;
import com.citc.nce.auth.readingLetter.dataStatistics.service.ReadingLetterDataStatisticsService;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.DetailInfoVo;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.FifthReadingLetterDailyReportSelectVo;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.ReadingLetterDailyReportSelectVo;
import com.citc.nce.auth.readingLetter.shortUrl.dao.ReadingLetterShortUrlDao;
import com.citc.nce.auth.readingLetter.template.enums.SmsTypeEnum;
import com.citc.nce.auth.readingLetter.template.service.ReadingLetterTemplateAuditService;
import com.citc.nce.auth.readingLetter.template.service.ReadingLetterTemplateService;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateNameVo;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.developer.DeveloperCustomer5gApplicationApi;
import com.citc.nce.developer.vo.Developer5gApplicationNameVo;
import com.citc.nce.readingLetter.ReadingLetterParseRecordApi;
import com.citc.nce.readingLetter.req.FifthReadingLetterDailyReportSelectReq;
import com.citc.nce.readingLetter.req.FifthReadingLetterPlanAndChatbotAccountResp;
import com.citc.nce.readingLetter.req.ReadingLetterDailyReportSelectReq;
import com.citc.nce.robot.api.RobotGroupSendPlansApi;
import com.citc.nce.robot.req.RobotGroupSendPlansByPlanNameReq;
import com.citc.nce.robot.vo.RobotGroupSendPlansAndChatbotAccount;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zjy
 */
@Service
@Slf4j
public class ReadingLetterDataStatisticsServiceImpl implements ReadingLetterDataStatisticsService {

    @Resource
    private ReadingLetterTemplateService templateService;
    @Resource
    private ReadingLetterTemplateAuditService readingLetterTemplateAuditService;
    @Resource
    private ReadingLetterParseRecordApi readingLetterParseRecordApi;
    @Resource
    private ReadingLetterShortUrlDao readingLetterShortUrlDao;
    @Resource
    private RobotGroupSendPlansApi robotGroupSendPlansApi;
    @Resource
    private AccountManagementApi accountManagementApi;
    @Resource
    private DeveloperCustomer5gApplicationApi developerCustomer5gApplicationApi;
    @Resource
    private CspReadingLetterAccountService cspReadingLetterAccountService;

    @Override
    public List<ReadingLetterDailyReportSelectVo> dataStatistics(DataStatisticsReq req) {
        ReadingLetterDailyReportSelectReq dailyReportSelectReq = getReadingLetterDailyReportSelectReq(req);
        //看看是否能提前确定shortUrl范围
        List<Long> shortUrlIds = getShortUrlIds(req);
        //有其他的过滤条件
        if (CollectionUtil.isNotEmpty(shortUrlIds)) {
            dailyReportSelectReq.setShortUrlIds(shortUrlIds);
        }
        //一个也没查到
        if (shortUrlIds != null && shortUrlIds.isEmpty()) {
            return null;
        }

        List<ReadingLetterDailyReportSelectVo> dailyReports = readingLetterParseRecordApi.selectRecords(dailyReportSelectReq);
        for (int i = dailyReports.size() - 1; i >= 0; i--) {
            ReadingLetterDailyReportSelectVo readingLetterDailyReportSelectVo = dailyReports.get(i);
            Long shortUrlId = readingLetterDailyReportSelectVo.getShortUrlId();
            DetailInfoVo detailInfo = readingLetterShortUrlDao.getDetailInfo(shortUrlId);
            readingLetterDailyReportSelectVo.setAccountName(detailInfo.getAccountName());
            readingLetterDailyReportSelectVo.setTemplateName(detailInfo.getTemplateName());
            readingLetterDailyReportSelectVo.setShortUrl(detailInfo.getShortUrl());
            readingLetterDailyReportSelectVo.setOperatorCode(detailInfo.getOperatorCode());
        }
        return dailyReports;
    }


    @Override
    public List<FifthReadingLetterDailyReportSelectVo> fifthDataStatistics(FifthDataStatisticsReq req) {
        log.info("fifthDataStatistics查询:{}", req);
        FifthReadingLetterDailyReportSelectReq dailyReportSelectReq = getFifthReadingLetterDailyReportSelectReq(req);
        //看看是否能提前确定Plan 和 Chatbot范围
        FifthReadingLetterPlanAndChatbotAccountResp planIdsAndChatbotAccounts = getPlanIdsAndChatbotAccounts(req);
        //有其他的过滤条件
        if (planIdsAndChatbotAccounts.getChatbots() != null && CollectionUtil.isEmpty(planIdsAndChatbotAccounts.getChatbots())) {
            //一个也没查到
            return null;
        }
        if (planIdsAndChatbotAccounts.getPlans() != null && CollectionUtil.isEmpty(planIdsAndChatbotAccounts.getPlans())) {
            //一个也没查到
            return null;
        }

        //添加查询条件
        if (CollectionUtil.isNotEmpty(planIdsAndChatbotAccounts.getPlans())) {
            List<Long> collect = planIdsAndChatbotAccounts.getPlans().stream().map(RobotGroupSendPlansAndChatbotAccount::getId).collect(Collectors.toList());
            dailyReportSelectReq.setPlanIds(collect);
        }
        if (CollectionUtil.isNotEmpty(planIdsAndChatbotAccounts.getChatbots())) {
            List<String> collect = planIdsAndChatbotAccounts.getChatbots().stream().map(AccountManagementResp::getChatbotAccount).collect(Collectors.toList());
            dailyReportSelectReq.setChatbotAccounts(collect);
        }
        //找到了聚合日报中的主体信息
        List<FifthReadingLetterDailyReportSelectVo> dailyReports = readingLetterParseRecordApi.selectRecords(dailyReportSelectReq);
        log.info("dailyReports:{}", dailyReports);
        if (CollectionUtil.isEmpty(dailyReports)) return null;

        //查找查询结果中的群发计划的信息
        List<String> selectPlanIds = dailyReports.stream()
                .filter(vo -> vo.getSourceType() == 1)
                .filter(vo -> Objects.nonNull(vo.getGroupSendId()))
                .map(vo -> vo.getGroupSendId().toString())
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(planIdsAndChatbotAccounts.getPlans())) {
            if (CollectionUtil.isNotEmpty(selectPlanIds)) {
                List<RobotGroupSendPlansAndChatbotAccount> plans = robotGroupSendPlansApi.getByGroupSendIds(selectPlanIds);
                planIdsAndChatbotAccounts.setPlans(plans);
            }
        }

        //查找开发者服务相关信息
        Map<Long, String> developer5gApplicationNameMap = new HashMap<>();
        List<Long> developerCustomer5gApplicationIds = dailyReports.stream().filter(vo -> vo.getSourceType() == 2).map(FifthReadingLetterDailyReportSelectVo::getGroupSendId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(developerCustomer5gApplicationIds)) {
            List<Developer5gApplicationNameVo> developer5gApplicationNameVos = developerCustomer5gApplicationApi.queryApplicationNameWithoutLogin(developerCustomer5gApplicationIds);
            developer5gApplicationNameMap = developer5gApplicationNameVos.stream().collect(Collectors.toMap(Developer5gApplicationNameVo::getId, Developer5gApplicationNameVo::getApplicationName));
        }

        //通过蜂动平台模板id查找去阅信模板信息
        List<String> platformTemplateIdList = dailyReports.stream().map(vo -> vo.getPlatformTemplateId().toString()).collect(Collectors.toList());
        List<ReadingLetterTemplateNameVo> templatesByPlatformTemplateIdList = readingLetterTemplateAuditService.getTemplatesByPlatformTemplateIdList(platformTemplateIdList);
        Map<String, String> templateNameMap = templatesByPlatformTemplateIdList.stream().collect(Collectors.toMap(ReadingLetterTemplateNameVo::getPlatformTemplateId, ReadingLetterTemplateNameVo::getTemplateName));

        //查找查询结果中的chatbot的信息
        List<String> selectChatbotAccounts = dailyReports.stream().map(FifthReadingLetterDailyReportSelectVo::getChatbotAccount).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(planIdsAndChatbotAccounts.getChatbots())) {
            List<AccountManagementResp> accountManagementResp = accountManagementApi.getListByChatbotAccountList(selectChatbotAccounts);
            planIdsAndChatbotAccounts.setChatbots(accountManagementResp);
        }
        HashMap<Long, RobotGroupSendPlansAndChatbotAccount> planMap = new HashMap<>();
        HashMap<String, AccountManagementResp> chatbotMap = new HashMap<>();

        //只有群发计划生成的记录需要进行最后的plan查询
        if (CollectionUtil.isNotEmpty(planIdsAndChatbotAccounts.getPlans())) {
            planIdsAndChatbotAccounts.getPlans().forEach(plan -> {
                planMap.put(plan.getId(), plan);
            });
        }
        //预防性编程
        if (CollectionUtil.isNotEmpty(planIdsAndChatbotAccounts.getChatbots())) {
            planIdsAndChatbotAccounts.getChatbots().forEach(chatbot -> {
                chatbotMap.put(chatbot.getChatbotAccount(), chatbot);
            });
        }

        for (int i = dailyReports.size() - 1; i >= 0; i--) {
            FifthReadingLetterDailyReportSelectVo firstReportSelectVo = dailyReports.get(i);
            String chatbotAccount = firstReportSelectVo.getChatbotAccount();
            //群发ID 或者是 开发者服务应用ID
            Long planId = firstReportSelectVo.getGroupSendId();
            AccountManagementResp accountManagementResp = chatbotMap.get(chatbotAccount);
            if (firstReportSelectVo.getSourceType() == 1) {
                RobotGroupSendPlansAndChatbotAccount robotGroupSendPlansAndChatbotAccount = planMap.get(planId);
                firstReportSelectVo.setPlanName(robotGroupSendPlansAndChatbotAccount.getPlanName());
            } else if (firstReportSelectVo.getSourceType() == 2) {
                firstReportSelectVo.setPlanName(developer5gApplicationNameMap.get(planId));
            }

            firstReportSelectVo.setChatbotName(accountManagementResp.getAccountName());
            firstReportSelectVo.setOperatorCode(accountManagementResp.getAccountTypeCode());

            //设置阅信模板名
            Long platformTemplateId = firstReportSelectVo.getPlatformTemplateId();
            firstReportSelectVo.setTemplateName(templateNameMap.get(platformTemplateId.toString()));

        }
        return dailyReports;
    }


    private FifthReadingLetterDailyReportSelectReq getFifthReadingLetterDailyReportSelectReq(FifthDataStatisticsReq req) {
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        FifthReadingLetterDailyReportSelectReq dailyReportSelectReq = new FifthReadingLetterDailyReportSelectReq();
        dailyReportSelectReq.setEndTime(req.getEndTime());
        dailyReportSelectReq.setStartTime(req.getStartTime());
        dailyReportSelectReq.setCustomerId(customerId);
        return dailyReportSelectReq;
    }

    //在robot服务查找符合条件的plan和Chatbot
    private FifthReadingLetterPlanAndChatbotAccountResp getPlanIdsAndChatbotAccounts(FifthDataStatisticsReq req) {
        FifthReadingLetterPlanAndChatbotAccountResp result = new FifthReadingLetterPlanAndChatbotAccountResp();
        String planName = req.getPlanName();
        String chatbotName = req.getChatbotName();
        Integer operatorCode = req.getOperatorCode();
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        // 群发计划查询条件不为空
        if (StrUtil.isNotBlank(planName)) {
            RobotGroupSendPlansByPlanNameReq robotGroupSendPlansByPlanNameReq = new RobotGroupSendPlansByPlanNameReq();
            robotGroupSendPlansByPlanNameReq.setPlanName(planName);
            robotGroupSendPlansByPlanNameReq.setCustomerId(customerId);
            List<RobotGroupSendPlansAndChatbotAccount> robotGroupSendPlansAndChatbotAccountList = robotGroupSendPlansApi.selectByPlanName(robotGroupSendPlansByPlanNameReq);

            result.setPlans(robotGroupSendPlansAndChatbotAccountList);
        }
        // Chatbot查询条件不为空
        if (StrUtil.isNotBlank(chatbotName) || operatorCode != null) {
            AccountManagementTypeReq accountManagementTypeReq = new AccountManagementTypeReq().setCreator(customerId);
            if (StrUtil.isNotBlank(chatbotName)) {
                accountManagementTypeReq.setChatbotName(chatbotName);
            }
            if (operatorCode != null) {
                String accountType = CSPOperatorCodeEnum.byCode(operatorCode).getName();
                accountManagementTypeReq.setAccountType(accountType);
            }
            List<AccountManagementResp> accountManagementByAccountType = accountManagementApi.getAccountManagementByAccountTypes(accountManagementTypeReq);
            result.setChatbots(accountManagementByAccountType);
        }
        return result;
    }

    private List<Long> getShortUrlIds(DataStatisticsReq req) {
        String accountName = req.getAccountName();
        String shortUrl = req.getShortUrl();
        List<String> accountIdList = CollectionUtil.newArrayList();
        //无需前置查询
        if (StrUtil.isBlank(accountName) && StrUtil.isBlank(shortUrl)) {
            return null;
        }
        //需要前置查询
        if (StrUtil.isNotBlank(accountName)) {
            //找到阅信账号id
            CustomerReadingLetterAccountSearchReq customerReadingLetterAccountSearchReq = new CustomerReadingLetterAccountSearchReq();
            customerReadingLetterAccountSearchReq.setAccountName(accountName);
            customerReadingLetterAccountSearchReq.setSmsType(SmsTypeEnum.READING_LETTER_PLUS.getCode());
            List<CustomerReadingLetterAccountListVo> customerReadingLetterAccountListVos = cspReadingLetterAccountService.queryCustomerReadingLetterAccountList(customerReadingLetterAccountSearchReq);
            accountIdList = customerReadingLetterAccountListVos.stream().map(CustomerReadingLetterAccountListVo::getAccountId).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(accountIdList)) {
                return CollectionUtil.newArrayList();
            }
        }
        return readingLetterShortUrlDao.getShortUrlIds(accountIdList, shortUrl);
    }

    @NotNull
    private ReadingLetterDailyReportSelectReq getReadingLetterDailyReportSelectReq(DataStatisticsReq req) {
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        ReadingLetterDailyReportSelectReq dailyReportSelectReq = new ReadingLetterDailyReportSelectReq();
        dailyReportSelectReq.setEndTime(req.getEndTime());
        dailyReportSelectReq.setStartTime(req.getStartTime());
        dailyReportSelectReq.setCustomerId(customerId);
        return dailyReportSelectReq;
    }
}
