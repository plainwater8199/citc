package com.citc.nce.auth.csp.chatbot.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementDao;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementMapper;
import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.auth.accountmanagement.service.AccountManagementService;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementEditReq;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementReq;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTreeResp;
import com.citc.nce.auth.configure.ChatBotMobileConfigure;
import com.citc.nce.auth.configure.CspUnicomAndTelecomConfigure;
import com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum;
import com.citc.nce.auth.constant.csp.common.CSPContractStatusEnum;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.csp.account.entity.AccountManageDo;
import com.citc.nce.auth.csp.account.service.AccountManageService;
import com.citc.nce.auth.csp.chatbot.dao.*;
import com.citc.nce.auth.csp.chatbot.entity.*;
import com.citc.nce.auth.csp.chatbot.service.ChatbotManageService;
import com.citc.nce.auth.csp.chatbot.vo.*;
import com.citc.nce.auth.csp.common.CSPChannelEnum;
import com.citc.nce.auth.csp.common.CSPChatbotSupplierTagEnum;
import com.citc.nce.auth.csp.common.CSPStatusOptionsEnum;
import com.citc.nce.auth.csp.contract.dao.ContractManageDao;
import com.citc.nce.auth.csp.contract.entity.ContractManageDo;
import com.citc.nce.auth.csp.csp.service.CspService;
import com.citc.nce.auth.csp.recharge.dao.ChargeTariffDao;
import com.citc.nce.auth.identification.service.UserEnterpriseIdentificationService;
import com.citc.nce.auth.identification.vo.resp.WebEnterpriseIdentificationResp;
import com.citc.nce.auth.messagetemplate.service.MessageTemplateService;
import com.citc.nce.auth.mobile.req.ChatBotLogOff;
import com.citc.nce.auth.mobile.req.ChatBotNew;
import com.citc.nce.auth.mobile.req.ChatBotShelfApply;
import com.citc.nce.auth.mobile.req.ChatBotSubmitWhiteList;
import com.citc.nce.auth.mobile.resp.SyncResult;
import com.citc.nce.auth.mobile.service.ChatBotService;
import com.citc.nce.auth.readingLetter.template.service.ReadingLetterTemplateAuditService;
import com.citc.nce.auth.unicomAndTelecom.req.ChatBotReq;
import com.citc.nce.auth.unicomAndTelecom.req.DeveloperReq;
import com.citc.nce.auth.unicomAndTelecom.service.CspPlatformService;
import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.auth.vo.req.GetUserInfoReq;
import com.citc.nce.authcenter.auth.vo.resp.ChannelInfoResp;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.CustomerDetailReq;
import com.citc.nce.authcenter.csp.vo.CustomerDetailResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.util.UUIDUtils;
import com.citc.nce.dto.FileExamineDeleteReq;
import com.citc.nce.fileApi.ExamineResultApi;
import com.citc.nce.fileApi.PlatformApi;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.RobotSceneNodeApi;
import com.citc.nce.robot.api.RobotGroupSendPlansApi;
import com.citc.nce.tenant.MsgRecordApi;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shardingsphere.transaction.annotation.ShardingSphereTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>csp-chatbot管理</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:51
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatbotManageServiceImpl implements ChatbotManageService {

    public static final String SUCCESS_CODE = "00000";

    @Autowired
    private CspUnicomAndTelecomConfigure cspUnicomAndTelecomConfigure;

    @Autowired
    private ChatbotManageDao chatbotManageDao;

    @Autowired
    private AccountManagementMapper accountManagementMapper;

    @Autowired
    private AccountManagementDao accountManagementDao;

    @Autowired
    private AccountManagementService accountManagementService;

    @Autowired
    private ChatbotManageWhiteListDao chatbotManageWhiteListDao;

    @Autowired
    private ChatbotManageShelvesDao chatbotManageShelvesDao;
    @Resource
    private RobotGroupSendPlansApi robotGroupSendPlansApi;
    @Resource
    ChatBotService chatBotService;

    @Resource
    ContractManageDao contractManageDao;

    @Autowired
    private ChargeTariffDao chargeTariffDao;

    @Resource
    ChatbotManageChangeDao chatbotManageChangeDao;

    @Resource
    CspPlatformService cspPlatformService;

    @Resource
    CspService cspService;

    @Autowired
    private UserEnterpriseIdentificationService userEnterpriseIdentificationService;

    @Autowired
    private ChatBotMobileConfigure chatBotMobileConfigure;
    @Autowired
    private AccountManageService accountManageService;

    @Autowired
    private AdminAuthApi adminAuthApi;
    @Resource
    private CspCustomerApi cspCustomerApi;
    @Resource
    private MessageTemplateService messageTemplateService;
    @Resource
    private final ExamineResultApi examineResultApi;
    @Resource
    private final RobotSceneNodeApi robotSceneNodeApi;
    @Resource
    private ReadingLetterTemplateAuditService readingLetterTemplateAuditService;
    @Resource
    private PlatformApi platformApi;
    @Resource
    private MsgRecordApi msgRecordApi;


    private static final Map<Integer, String> INDUSTRY_MAP;

    static {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "其他行业");
        map.put(1, "农、林、牧、渔业");
        map.put(2, "采矿业");
        map.put(3, "制造业");
        map.put(4, "电力、燃气及水的生产和供应业");
        map.put(5, "建筑业");
        map.put(6, "交通运输、仓储和邮政业");
        map.put(7, "信息传输、软件和信息技术服务业");
        map.put(8, "批发和零售业");
        map.put(9, "住宿和餐饮业");
        map.put(10, "金融业");
        map.put(11, "房地产业");
        map.put(12, "租赁和商务服务业");
        map.put(13, "科学研究和技术服务业");
        map.put(14, "水利、环境和公共设施管理业");
        map.put(15, "居民服务、修理和其他服务业");
        map.put(16, "教育");
        map.put(17, "卫生、社会保障和社会福利业");
        map.put(18, "文化、体育和娱乐业");
        map.put(19, "公共管理和社会组织");
        map.put(20, "国际组织");
        map.put(21, "国防");
        INDUSTRY_MAP = Collections.unmodifiableMap(map);
    }

    @Override
    public AccountManagementResp getOtherByAccountManagementId(ChatbotGetReq req) {
        //非移动
        AccountManagementResp resp = new AccountManagementResp();
        List<AccountManagementDo> accountManagementDos;
        if (Strings.isNullOrEmpty(req.getCustomerId())) {
            BaseUser user = SessionContextUtil.getUser();
            accountManagementDos = accountManagementDao.selectList(Wrappers.<AccountManagementDo>lambdaQuery()
                    .eq(AccountManagementDo::getCspId, cspService.obtainCspId(user.getUserId()))
                    .eq(AccountManagementDo::getChatbotAccountId, req.getChatbotAccountId()));
        } else {
            //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的）
            String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
            if (!req.getCustomerId().substring(0, 10).equals(cspId)) {
                throw new BizException("你操作的客户不属于你");
            }
            accountManagementDos = accountManagementDao.selectList(Wrappers.<AccountManagementDo>lambdaQuery()
                    .eq(AccountManagementDo::getCustomerId, req.getCustomerId())
                    .eq(AccountManagementDo::getChatbotAccountId, req.getChatbotAccountId()));
        }
        if (ObjectUtil.isNotEmpty(accountManagementDos)) {
            AccountManagementDo accountManagementDo = accountManagementDos.get(0);
            BeanUtils.copyProperties(accountManagementDo, resp);
            String customerId = accountManagementDo.getCustomerId();
            WebEnterpriseIdentificationResp identificationInfo = userEnterpriseIdentificationService.getIdentificationInfo(customerId);
            if (identificationInfo != null) {
                resp.setEnterpriseName(identificationInfo.getEnterpriseName());
                resp.setEnterpriseAccountName(identificationInfo.getEnterpriseAccountName());
            }
        }
        return resp;
    }

    @Override
    public void sendChatbot2ServerWhenAudit(ChatbotManageDo chatbotManageDo, AccountManagementDo accountManagementDo) {
        log.info("进入审核后处理, sendChatbot2ServerWhenAudit");
        String token = UUIDUtils.generateUUID().substring(0, 16);
        log.info("sendChatbot2ServerWhenAudit 准备更新token token : {}", token);
        // 设置chatbot 的token
        accountManagementDo.setToken(token);
        DeveloperReq developerReq = new DeveloperReq();
        developerReq.setAgreement("2");
        developerReq.setToken(token);
        developerReq.setUrl(cspUnicomAndTelecomConfigure.getGatewayCallbackUrl());
        developerReq.setAccessTagNo(chatbotManageDo.getAccessTagNo());
        developerReq.setEnable("1");
        log.info("sendChatbot2ServerWhenAudit developerReq : {}", developerReq);
        AccountManageDo cspInfo = accountManageService.getCspAccount(chatbotManageDo.getOperatorCode(), accountManagementDo.getCspId());
        // 把token和回调地址，发给运营商
        JSONObject jsonObject = cspPlatformService.updateDeveloper(developerReq, cspInfo.getCspPassword(), cspInfo.getCspAccount(), chatbotManageDo.getOperatorCode());
        log.info("sendChatbot2ServerWhenAudit 运营商返回的数据 jsonObject : {}", jsonObject);
        // 把运营商返回的数据塞进chatbot
        if (ObjectUtil.isNotEmpty(jsonObject)) {
            log.info("sendChatbot2ServerWhenAudit 获取到返回数据，准备更新 appId ：{} appKey : {}", jsonObject.getString("appId"), jsonObject.getString("appKey"));
            // 设置chatbot 的appId,appKey
            accountManagementDo.setAppId(jsonObject.getString("appId"));
            accountManagementDo.setAppKey(jsonObject.getString("appKey"));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelCMCCChatbot(String chatbotAccount) {
        accountManagementDao.delete(
                Wrappers.<AccountManagementDo>lambdaQuery()
                        .eq(AccountManagementDo::getChatbotAccount, chatbotAccount)
        );
        chatbotManageDao.delete(
                Wrappers.<ChatbotManageDo>lambdaQuery()
                        .eq(ChatbotManageDo::getChatbotId, chatbotAccount)
        );
    }

    /**
     * 注销移动chatbot
     *
     * @param chatbotAccount chatbot账号
     */
    @Override
    public void realCancelCMCCChatbot(String chatbotAccount) {

        LambdaUpdateWrapper<AccountManagementDo> eq = Wrappers.<AccountManagementDo>lambdaUpdate()
                .eq(AccountManagementDo::getChatbotAccount, chatbotAccount)
                .eq(AccountManagementDo::getSupplierTag, CSPChatbotSupplierTagEnum.OWNER.getValue())
                .eq(AccountManagementDo::getDeleted, 0);
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(eq);
        if (Objects.isNull(accountManagementDo)) {
            throw new BizException("机器人账号不存在");
        }

        LambdaUpdateWrapper<AccountManagementDo> whereWrapper = Wrappers.<AccountManagementDo>lambdaUpdate()
                .eq(AccountManagementDo::getChatbotAccount, chatbotAccount)
                .set(AccountManagementDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_43_LOG_OFF.getCode());

        //将机器人状态设置为已注销
        accountManagementDao.update(new AccountManagementDo(), whereWrapper);
        //删除模板审核记录
        messageTemplateService.cancelAudit(chatbotAccount);
        //删除素材审核记录
        FileExamineDeleteReq fileExamineDeleteReq = new FileExamineDeleteReq();
        fileExamineDeleteReq.setChatbotAccount(chatbotAccount);
        platformApi.deleteAuditRecord(fileExamineDeleteReq);
        //群发计划取消关联账号绑定
        robotGroupSendPlansApi.removeChatbotAccount(accountManagementDo.getChatbotAccount(), accountManagementDo.getAccountType(), accountManagementDo.getSupplierTag());
        //机器人场景取消关联账号
        robotSceneNodeApi.removeChatbotAccount(accountManagementDo.getChatbotAccount());
    }

    @Override
    public ChatbotGetResp queryByAccountManagementId(ChatbotGetReq req) {
        //移动
        ChatbotGetResp res = new ChatbotGetResp();
        LambdaQueryWrapperX<ChatbotManageDo> cWrapper = new LambdaQueryWrapperX<>();
        cWrapper.eq(ChatbotManageDo::getDeleted, 0)
                .eq(ChatbotManageDo::getChatbotAccountId, req.getChatbotAccountId());
        ChatbotManageDo list = chatbotManageDao.selectOne(cWrapper);
        if (ObjectUtils.isEmpty(list)) {
            return res;
        }
        if (req.getChangeDetails() == 1) {
            ChatbotManageChangeDo chatbotManageChangeDo = chatbotManageChangeDao.selectOne(
                    Wrappers.<ChatbotManageChangeDo>lambdaQuery()
                            .eq(ChatbotManageChangeDo::getChatbotAccountId, list.getChatbotAccountId())
            );
            ChatbotManageDo chatbotManageDo;
            if (null != chatbotManageChangeDo) {
                chatbotManageDo = new ChatbotManageDo();
                BeanUtils.copyProperties(chatbotManageChangeDo, chatbotManageDo);
            } else {
                chatbotManageDo = chatbotManageDao.selectById(list.getId());
            }
            buildResp(res, chatbotManageDo);
        } else {
            ChatbotManageDo chatbotManageDo = chatbotManageDao.selectById(list.getId());
            buildResp(res, chatbotManageDo);
        }

        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的）
        if (SessionContextUtil.getLoginUser().getIsCustomer()) {
            if (!SessionContextUtil.getLoginUser().getUserId().equals(res.getCustomerId())) {
                throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
            }
        } else {
            String cspId = SessionContextUtil.getLoginUser().getCspId();
            if (!cspId.equals(res.getCreator())) {
                throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
            }
        }

        return res;
    }

    private void buildResp(ChatbotGetResp res, ChatbotManageDo chatbotManageDo) {
        BeanUtils.copyProperties(chatbotManageDo, res);
        // 查客户名称
        WebEnterpriseIdentificationResp enterprise = userEnterpriseIdentificationService.getEnterpriseIdentificationByUserId(chatbotManageDo.getCustomerId());
        if (enterprise != null) {
            res.setEnterpriseName(enterprise.getEnterpriseName());
            res.setEnterpriseId(enterprise.getId());
        }
        res.setCustomerId(chatbotManageDo.getCustomerId());
        // 查询白名单
        ChatbotManageWhiteListDo whiteListDo = getChatbotManageWhiteListDo(chatbotManageDo.getChatbotId(), chatbotManageDo.getChatbotAccountId());
        if (Objects.nonNull(whiteListDo) && org.springframework.util.StringUtils.hasLength(whiteListDo.getWhiteList())) {
            res.setWhiteList(whiteListDo.getWhiteList());
        } else {
            res.setWhiteList(null);
        }
    }

    private ChatbotManageDo getChatbotManageDo(String chatbotAccountId) {
        LambdaQueryWrapperX<ChatbotManageDo> cWrapper = new LambdaQueryWrapperX<>();
        cWrapper.eq(ChatbotManageDo::getDeleted, 0)
                .eq(ChatbotManageDo::getChatbotAccountId, chatbotAccountId);
        return chatbotManageDao.selectOne(cWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int activeOn(ChatbotActiveOnReq req) {

        chatbotManageShelvesDao.delete(
                Wrappers.<ChatbotManageShelvesDo>lambdaQuery()
                        .eq(ChatbotManageShelvesDo::getChatbotAccountId, req.getChatbotAccountId())
        );
        ChatbotManageShelvesDo insert = new ChatbotManageShelvesDo();
        insert.setChatbotId(req.getChatbotId());
        insert.setChatbotAccountId(req.getChatbotAccountId());
        insert.setShelvesFileUrl(req.getShelvesFileUrl());
        insert.setShelvesDesc(req.getShelvesDesc());
        // 审核状态 0：待审核
        insert.setStatus(0);
        chatbotManageShelvesDao.insert(insert);


        //查询全都是自己创建的机器人
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!accountManagementDao.selectList(new LambdaQueryWrapperX<AccountManagementDo>()
                        .eq(AccountManagementDo::getChatbotAccountId, req.getChatbotAccountId()))
                .stream().allMatch(s -> s.getCustomerId().substring(0, 10).equals(cspId))) {
            throw new BizException("你操作的客户不属于你");
        }

        // 更改移动chatbot状态
        accountManagementDao.update(
                new AccountManagementDo(),
                Wrappers.<AccountManagementDo>lambdaUpdate()
                        .eq(AccountManagementDo::getChatbotAccountId, req.getChatbotAccountId())
                        .set(AccountManagementDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_24_SHELVE_AND_IS_AUDITING.getCode())
        );
        chatbotManageDao.update(
                new ChatbotManageDo(),
                Wrappers.<ChatbotManageDo>lambdaUpdate()
                        .eq(ChatbotManageDo::getChatbotAccountId, req.getChatbotAccountId())
                        .set(ChatbotManageDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_24_SHELVE_AND_IS_AUDITING.getCode())
        );

        //向运营商发起上架申请
        ChatBotShelfApply chatBotShelfApply = new ChatBotShelfApply();
        chatBotShelfApply.setChatbotId(req.getChatbotId() + "@botplatform.rcs.chinamobile.com");
        chatBotShelfApply.setTestReportUrl("isoftstone/" + req.getShelvesFileUrl() + ".jpg");
        chatBotShelfApply.setPutAwayExplain(req.getShelvesDesc());
        chatBotShelfApply.setCreator(SessionContextUtil.getUser().getUserId());
        SyncResult syncResult = chatBotService.chatBotShelfApply(chatBotShelfApply);
        if (!StringUtils.equals(syncResult.getResultCode(), SUCCESS_CODE)) {
            throw new BizException(820103002, syncResult.getResultDesc());
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCMCCById(ChatbotUpdateCMCCReq req) {
        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的）
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!req.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }

        AccountManagementDo accountManagementDo = accountManagementDao.selectById(req.getId());
        if (accountManagementDo == null)
            throw new BizException(500, "机器人账号不存在");
        //cmcc详情表
        ChatbotManageDo chatbotManageDo = chatbotManageDao.selectOne(
                Wrappers.<ChatbotManageDo>lambdaQuery()
                        .eq(ChatbotManageDo::getChatbotAccountId, accountManagementDo.getChatbotAccountId())
        );

        Integer operatorCode = accountManagementDo.getAccountTypeCode();
        String customerId = req.getCustomerId();
        //校验第三方csp信息
        AccountManageDo accountManageDo = accountManageService.getCspAccount(operatorCode, cspId);
        //校验合同信息
        ContractManageDo contractManageDo = this.getContract(operatorCode, customerId);
        validateContractForChatbot(contractManageDo, operatorCode);
        CSPOperatorCodeEnum operatorCodeEnum = CSPOperatorCodeEnum.byCode(operatorCode);
        Assert.notNull(operatorCodeEnum, "运营商编码无效:" + operatorCode);
        //先修改本地状态

        // 更新关联白名单
        chatbotManageWhiteListDao.delete(
                Wrappers.<ChatbotManageWhiteListDo>lambdaQuery()
                        .eq(ChatbotManageWhiteListDo::getChatbotAccountId, accountManagementDo.getChatbotAccountId())
        );
        ChatbotManageWhiteListDo whiteListDo = new ChatbotManageWhiteListDo();
        whiteListDo.setWhiteList(req.getWhiteList());
        whiteListDo.setStatus(0);
        whiteListDo.setChatbotAccountId(accountManagementDo.getChatbotAccountId());
        whiteListDo.setChatbotId(accountManagementDo.getChatbotAccount());
        chatbotManageWhiteListDao.insert(whiteListDo);

        //如果机器人主表信息有修改
        AccountManagementEditReq accountManagementEditReq = new AccountManagementEditReq();
        accountManagementEditReq.setId(accountManagementDo.getId());
        accountManagementEditReq.setAccountName(req.getChatbotName());
        //修改chatbot不支持修改关联客户
//        accountManagementEditReq.setCustomerId(req.getCustomerId());

        //保存变更信息，先删除此机器人的变更历史，再插入新的变更信息
        LambdaQueryWrapper<ChatbotManageChangeDo> changeQueryWrapper = new LambdaQueryWrapper<ChatbotManageChangeDo>()
                .eq(ChatbotManageChangeDo::getChatbotAccountId, accountManagementDo.getChatbotAccountId());
        chatbotManageChangeDao.delete(changeQueryWrapper);
        ChatbotManageChangeDo changeDo = new ChatbotManageChangeDo();
        BeanUtils.copyProperties(chatbotManageDo, changeDo);
        com.citc.nce.auth.utils.BeanUtil.copyNonNullProperties(req, changeDo);
        changeDo.setId(null);
        chatbotManageChangeDao.insert(changeDo);

        switch (operatorCodeEnum) {
            case DEFAULT:
            case CT:
            case CUNC: {
                List<CSPChatbotStatusEnum> allowEditStatus = Arrays.asList(
                        CSPChatbotStatusEnum.STATUS_64_NEW_AUDIT_NOT_PASS,
                        CSPChatbotStatusEnum.STATUS_70_TEST,
                        CSPChatbotStatusEnum.STATUS_68_ONLINE,
                        CSPChatbotStatusEnum.STATUS_71_OFFLINE
                );
                CSPChatbotStatusEnum state = CSPChatbotStatusEnum.byCode(accountManagementDo.getChatbotStatus());
                Assert.notNull(state, "无效机器人状态");
                if (!allowEditStatus.contains(state))
                    throw new BizException(500, "机器人当前状态:" + state.getDesc() + "不允许编辑");

                CSPChatbotStatusEnum targetState = state == CSPChatbotStatusEnum.STATUS_64_NEW_AUDIT_NOT_PASS
                        ? CSPChatbotStatusEnum.STATUS_60_NEW_WAIT_AUDIT
                        : CSPChatbotStatusEnum.STATUS_61_EDIT_WAIT_AUDIT;

                //修改主表
                accountManagementEditReq.setChatbotStatus(targetState.getCode());
                //如果修改了chatbotAccount，会修改原有的模板审核记录的chatbotAccount为新数据
                accountManagementService.updateAccountManagement(accountManagementEditReq, false);
                //将变更前状态存到ActualState字段，机器人状态修改成变更中，当变更审核通过后将状态设置回变更前的状态并把ActualState清空 修改机器人详情表
                chatbotManageDo.setActualState(state.getCode());
                chatbotManageDo.setChatbotStatus(targetState.getCode());
                chatbotManageDao.updateById(chatbotManageDo);
                //同步运营商
                ChatbotSaveReq cmccSaveReq = new ChatbotSaveReq();
                BeanUtils.copyProperties(req, cmccSaveReq);
                if (Objects.equals(CSPOperatorCodeEnum.CT.getCode(), req.getOperatorCode())) {
                    String longitude = longitudeAndLatitudeRoundHalfUp(cmccSaveReq.getLongitude());
                    cmccSaveReq.setLongitude(longitude);
                    String latitude = longitudeAndLatitudeRoundHalfUp(cmccSaveReq.getLatitude());
                    cmccSaveReq.setLatitude(latitude);
                }
                // 如果是新增审核不通过，需要走新增流程
                if (state == CSPChatbotStatusEnum.STATUS_64_NEW_AUDIT_NOT_PASS) {
                    ChatBotReq chatBotReq = buildParma(cmccSaveReq, accountManageDo.getCspAccount(), contractManageDo.getCustomerNum(), null);
                    String newAccessTagNo = cspPlatformService.addChatBot(chatBotReq, accountManageDo.getCspPassword(), accountManageDo.getCspAccount(), req.getOperatorCode());
                    chatbotManageDo.setAccessTagNo(newAccessTagNo);
                    chatbotManageDao.updateById(chatbotManageDo);
                } else {
                    ChatBotReq chatBotReq = buildParma(cmccSaveReq, accountManageDo.getCspAccount(), contractManageDo.getCustomerNum(), chatbotManageDo.getAccessTagNo());
                    cspPlatformService.updateChatBot(chatBotReq, accountManageDo.getCspPassword(), accountManageDo.getCspAccount(), req.getOperatorCode());
                }
                break;
            }
            case CMCC: {
                List<CSPChatbotStatusEnum> allowEditStatus = Arrays.asList(
                        CSPChatbotStatusEnum.STATUS_11_CRATE_AND_AUDIT_NOT_PASS,
                        CSPChatbotStatusEnum.STATUS_50_DEBUG,
                        CSPChatbotStatusEnum.STATUS_30_ONLINE,
                        CSPChatbotStatusEnum.STATUS_31_OFFLINE,
                        CSPChatbotStatusEnum.STATUS_12_UPDATE_AND_AUDIT_NOT_PASS,
                        CSPChatbotStatusEnum.STATUS_25_SHELVE_AND_AUDIT_NOT_PASS
                );
                CSPChatbotStatusEnum state = CSPChatbotStatusEnum.byCode(accountManagementDo.getChatbotStatus());
                Assert.notNull(state, "无效机器人状态");
                if (!allowEditStatus.contains(state))
                    throw new BizException(500, "机器人当前状态:" + state.getDesc() + "不允许编辑");

                CSPChatbotStatusEnum targetState = state == CSPChatbotStatusEnum.STATUS_11_CRATE_AND_AUDIT_NOT_PASS
                        ? CSPChatbotStatusEnum.STATUS_20_CRATE_AND_IS_AUDITING
                        : CSPChatbotStatusEnum.STATUS_21_UPDATE_AND_IS_AUDITING;

                //修改主表
                accountManagementEditReq.setChatbotStatus(targetState.getCode());
                //如果修改了chatbotAccount，会修改原有的模板审核记录的chatbotAccount为新数据
                accountManagementService.updateAccountManagement(accountManagementEditReq, false);

                //修改机器人详情表
                chatbotManageDo.setActualState(state.getCode());
                chatbotManageDo.setChatbotStatus(targetState.getCode());
                chatbotManageDao.updateById(chatbotManageDo);
                //同步运营商
                com.citc.nce.auth.utils.BeanUtil.copyNonNullProperties(req, chatbotManageDo);

                ChatBotNew chatBotNew = buildChatBot(chatbotManageDo, req.getWhiteList(), INDUSTRY_MAP.get(Integer.valueOf(req.getChatbotIndustryType())), contractManageDo, accountManageDo);
                SyncResult syncResult;
                // 移动运营商，如果是新增审核不通过，要重新走新增流程
                if (targetState == CSPChatbotStatusEnum.STATUS_20_CRATE_AND_IS_AUDITING) {
                    syncResult = chatBotService.chatBotNew(chatBotNew);
                } else {
                    syncResult = chatBotService.chatBotChange(chatBotNew);
                }
                if (!StringUtils.equals(syncResult.getResultCode(), SUCCESS_CODE)) {
                    throw new BizException(820103010, syncResult.getResultDesc());
                }
                break;
            }
            default:
                throw new BizException(500, "不能识别的运营商编码");
        }
    }

    private static String longitudeAndLatitudeRoundHalfUp(String longitudeOrLatitude) {
        BigDecimal temp;
        temp = BigDecimal.valueOf(Double.parseDouble(longitudeOrLatitude)).setScale(3, RoundingMode.HALF_UP);
        return temp.toString();
    }

    /**
     * 修改非移动机器人状态
     * 先把机器人表状态修改到目标状态，当收到回调时再将cmcc表机器人修改到目标状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateChatbotStatusByAccountManagementId(ChatbotStatusUpdateReq req) {
        AccountManagementResp accountManagement = accountManagementService.getAccountManagementById(req.getChatbotAccountId());
        if (Objects.isNull(accountManagement)) {
            throw new BizException("账号不存在");
        }
        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的）
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!accountManagement.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }

        if (Objects.equals(CSPOperatorCodeEnum.CMCC.getCode(), accountManagement.getAccountTypeCode())) {
            throw new BizException(500, "非法运营商类型");
        }
        //0是人工提供数据的机器人1是我们平台的代码创建的机器人
        int targetState = req.getChatbotStatus();
        if (accountManagement.getIsAddOther() == 1 && (accountManagement.getAccountTypeCode() == 1 || accountManagement.getAccountTypeCode() == 3)) {
            ChatbotManageDo chatbotManageDo = chatbotManageDao.selectOne("chatbot_account_id", req.getChatbotAccountId());
            //同步进行上线
            AccountManageDo cspInfo = accountManageService.getCspAccount(accountManagement.getAccountTypeCode(), SessionContextUtil.getUser().getUserId());
            String accessTagNo = chatbotManageDo.getAccessTagNo();
            if (targetState == 71) {
                //下线
                cspPlatformService.isOnlineUpdate(accessTagNo, cspInfo.getCspPassword(), cspInfo.getCspAccount(), 2, req.getOperatorCode());
                chatbotManageDo.setChatbotStatus(71);
            } else if (targetState == 68) {
                //上线
                cspPlatformService.isOnlineUpdate(accessTagNo, cspInfo.getCspPassword(), cspInfo.getCspAccount(), 1, req.getOperatorCode());
                chatbotManageDo.setChatbotStatus(68);
            } else if (targetState == 70) {
                if (!Objects.equals(accountManagement.getChatbotStatus(), 68))
                    throw new BizException("非上线机器人不能直接转测试");
                //上线转测试无需审核 可以直接变更
                cspPlatformService.isOnlineUpdate(accessTagNo, cspInfo.getCspPassword(), cspInfo.getCspAccount(), 3, req.getOperatorCode());
                chatbotManageDo.setChatbotStatus(70);
            } else {
                throw new BizException("非法目标状态");
            }
            chatbotManageDao.updateById(chatbotManageDo);
        }
        //更改主表目标状态
        accountManagementDao.update(
                new AccountManagementDo(),
                Wrappers.<AccountManagementDo>lambdaUpdate()
                        .eq(AccountManagementDo::getChatbotAccountId, accountManagement.getChatbotAccountId())
                        .set(AccountManagementDo::getChatbotStatus, targetState)
        );
        //上下线不影响客户侧Chatbot与素材，模板，流程，发送计划的绑定关系
//        if (targetState == 71) {
//            //断开原chatbot所有素材的关联关系
//            examineResultApi.updateMaterialFromChatbotUpdate(accountManagement.getChatbotAccount());
//            //断开原chatbot所有场景的关联关系
//            robotSceneNodeApi.removeBandingChatbotByChatbotAccount(accountManagement.getChatbotAccount());
//        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCMCCChatbotStatus(String chatbotAccountId, String chatbotId, int chatbotStatus) {
        ChatbotManageDo chatbotManageDo = getChatbotManageDo(chatbotAccountId);
        ChatbotManageDo update = new ChatbotManageDo();
        update.setId(chatbotManageDo.getId());
        update.setChatbotStatus(chatbotStatus);
        chatbotManageDao.updateById(update);
    }

    @Override
    public List<ChatbotSetWhiteListResp> queryWhiteList(ChatbotSetWhiteListReq req) {
        //先查询机器人账号是否存在
        ChatbotManageDo chatbotManageDo = chatbotManageDao.selectOne("chatbot_account_id", req.getChatbotAccountId());
        if (Objects.isNull(chatbotManageDo)) {
            throw new BizException("机器人账号不存在");
        }

        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的） 判断越权
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!chatbotManageDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }

        List<ChatbotSetWhiteListResp> res = new ArrayList<>();
        ChatbotManageWhiteListDo selectOne = getChatbotManageWhiteListDo(req.getChatbotId(), req.getChatbotAccountId());
        if (null != selectOne) {
            String[] split = selectOne.getWhiteList().split(",");
            for (int i = 0; i < split.length; i++) {
                ChatbotSetWhiteListResp resp = new ChatbotSetWhiteListResp();
                resp.setSort(i + 1);
                resp.setPhone(split[i]);
                res.add(resp);
            }
        }
        return res;
    }

    private ChatbotManageWhiteListDo getChatbotManageWhiteListDo(String chatbotId, String chatbotAccountId) {
        LambdaQueryWrapperX<ChatbotManageWhiteListDo> wrapperX = new LambdaQueryWrapperX<>();
        wrapperX.eq(ChatbotManageWhiteListDo::getChatbotId, chatbotId)
                .eq(ChatbotManageWhiteListDo::getChatbotAccountId, chatbotAccountId)
                .eq(ChatbotManageWhiteListDo::getDeleted, 0);
        return chatbotManageWhiteListDao.selectOne(wrapperX);
    }

    /**
     * 重新提交白名单，不修改机器人状态，前台根据白名单状态显示白名单信息
     *
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setWhiteList(ChatbotSetWhiteListReq req) {
        //先查询机器人账号是否存在
        ChatbotManageDo chatbotManageDo = chatbotManageDao.selectOne("chatbot_account_id", req.getChatbotAccountId());
        if (Objects.isNull(chatbotManageDo)) {
            throw new BizException("机器人账号不存在");
        }
        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的） 判断越权
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!chatbotManageDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }

        if (StringUtils.isEmpty(req.getWhiteList()))
            throw new BizException("白名单手机号不能为空");
        ChatbotManageWhiteListDo whiteListDo = chatbotManageWhiteListDao.selectOne(
                Wrappers.<ChatbotManageWhiteListDo>lambdaQuery()
                        .eq(ChatbotManageWhiteListDo::getChatbotAccountId, req.getChatbotAccountId())
        );
        if (CSPOperatorCodeEnum.CMCC.getCode().equals(req.getOperatorCode()) && whiteListDo.getStatus() == 0)
            throw new BizException("白名单审核中，不允许再次提交");
        chatbotManageWhiteListDao.deleteById(whiteListDo);
        ChatbotManageWhiteListDo insert = new ChatbotManageWhiteListDo();
        insert.setWhiteList(req.getWhiteList());
        insert.setStatus(0);
        insert.setChatbotAccountId(req.getChatbotAccountId());
        insert.setChatbotId(req.getChatbotId());
        chatbotManageWhiteListDao.insert(insert);
        // 是移动
        if (CSPOperatorCodeEnum.CMCC.getCode().equals(req.getOperatorCode())) {
            ChatBotSubmitWhiteList whiteList = new ChatBotSubmitWhiteList();
            whiteList.setChatbotId(req.getChatbotId() + "@botplatform.rcs.chinamobile.com");
            whiteList.setChatBotWhiteList(req.getWhiteList());
            whiteList.setCreator(SessionContextUtil.getUser().getUserId());
            SyncResult syncResult = chatBotService.resubmitDebugWhite(whiteList);
            if (!StringUtils.equals(syncResult.getResultCode(), SUCCESS_CODE)) {
                throw new BizException(820103001, syncResult.getResultDesc());
            }
        } else {
            if (!chatbotManageDo.getChatbotStatus().equals(CSPChatbotStatusEnum.STATUS_70_TEST.getCode())) {
                throw new BizException("非调试状态不能设置白名单");
            }
            LambdaQueryWrapper<ChatbotManageDo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ChatbotManageDo::getChatbotId, req.getChatbotId())
                    .eq(ChatbotManageDo::getChatbotAccountId, req.getChatbotAccountId())
                    .eq(ChatbotManageDo::getDeleted, 0);
            chatbotManageDo = chatbotManageDao.selectOne(wrapper);
            List<String> list = Arrays.asList(req.getWhiteList().split(","));
            AccountManageDo cspInfo = accountManageService.getCspAccount(req.getOperatorCode(), SessionContextUtil.getUser().getUserId());
            String accessTagNo = chatbotManageDo.getAccessTagNo();
            cspPlatformService.whiteListPhone(list, accessTagNo, cspInfo.getCspPassword(), cspInfo.getCspAccount(), req.getOperatorCode());
            insert.setStatus(1);
            chatbotManageWhiteListDao.updateById(insert);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLocalChatbot(ChatbotOtherSaveReq req) {
        AccountManagementReq accountManagementReq = new AccountManagementReq();
        BeanUtils.copyProperties(req, accountManagementReq);
        accountManagementReq.setIsAddOther(0);
        //新增直连chatbot的时候, 默认所有模板都去送审
        accountManagementService.saveAccountManagement(accountManagementReq);
    }

    /**
     * 编辑本地机器人，修改后重新同步网关
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateLocalRobot(ChatbotOtherUpdateReq req) {
        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的）
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!req.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
        AccountManagementEditReq updateReq = new AccountManagementEditReq();
        BeanUtils.copyProperties(req, updateReq);
        AccountManagementResp accountManagementResp = accountManagementService.getAccountManagementById(updateReq.getId());
        if (ObjectUtil.isNull(accountManagementResp)) throw new BizException("未找到chatbot");
        if (accountManagementResp.getSupplierTag().equals(CSPChatbotSupplierTagEnum.FONTDO.getValue())) {
            throw new BizException("非法操作");
        }
        accountManagementService.updateAccountManagement(updateReq, true);
    }

    @Override
    public PageResult<ChatbotResp> queryList(ChatbotReq req) {
        Page<ChatbotResp> page = new Page<>(req.getPageNo(), req.getPageSize());
        page.setOrders(OrderItem.descs("createTime"));
        String userId = SessionContextUtil.getUser().getUserId();
        String cspId = cspService.obtainCspId(userId);
        accountManagementMapper.queryChatbot(
                cspId,
                req.getChatbotString(),
                req.getCustomerId(),
                req.getOperatorCode(),
                req.getChatbotStatus(),
                page
        );
        // 获取列表中的chatbotId
        List<String> chatbotIds = page.getRecords().stream()
                .map(ChatbotResp::getChatbotAccount)
                .collect(Collectors.toList());
        List<ChatbotPackageAvailableAmount> availableAmounts = accountManagementMapper.selectChatbotAvaliableAmountByChatbotIds(chatbotIds);
        if (!availableAmounts.isEmpty()) {
            PropertyFilter filter = (object, name, value) -> !name.equals("accountId");
            for (ChatbotResp resp : page.getRecords()) {
                availableAmounts.stream()
                        .filter(amount -> Objects.equals(amount.getAccountId(), resp.getChatbotAccount()))
                        .findFirst()
                        .ifPresent(chatbotPackageAvailableAmount -> resp.setPackageContent(JSON.toJSONString(chatbotPackageAvailableAmount, filter)));
            }
        }
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    public List<AccountManagementTreeResp> queryBychatbotAccounts(List<String> chatbotAccounts) {
        if (ObjectUtil.isEmpty(chatbotAccounts)) return null;
        String customerId = SessionContextUtil.getUser().getUserId();
        return accountManagementMapper.getChatbotByChatbotAccounts(customerId, chatbotAccounts);
    }

    @Override
    public ChatbotGetStatusResp getChatbotStatus(ChatbotGetReq req) {
        LambdaUpdateWrapper<AccountManagementDo> eq = Wrappers.<AccountManagementDo>lambdaUpdate()
                .eq(AccountManagementDo::getChatbotAccountId, req.getChatbotAccountId());
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(eq);
        ChatbotGetStatusResp resp = new ChatbotGetStatusResp();
        resp.setChatbotStatus(accountManagementDo.getChatbotStatus());
        return resp;
    }

    /**
     * 新增chatbot（线上流程机器人）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveChatbot(ChatbotSaveReq req) {
        String userId = SessionContextUtil.getUser().getUserId();
        String cspId = cspService.obtainCspId(userId);
        Integer operatorCode = req.getOperatorCode();
        CSPOperatorCodeEnum operatorCodeEnum = CSPOperatorCodeEnum.byCode(operatorCode);
        Assert.notNull(operatorCodeEnum, "operatorCodeEnum can not be null!");
        String customerId = req.getCustomerId();
        //校验第三方csp信息
        AccountManageDo accountManageDo = accountManageService.getCspAccount(operatorCode, cspId);
        //校验合同信息
        ContractManageDo contractManageDo = this.getContract(operatorCode, customerId);
        validateContractForChatbot(contractManageDo, operatorCode);

        // 插入机器人主表
        AccountManagementReq accountManagementReq = new AccountManagementReq();
        accountManagementReq.setChatbotAccount(req.getChatbotId());
        accountManagementReq.setAccountType(operatorCodeEnum.getName());
        accountManagementReq.setAccountTypeCode(operatorCode);
        //线上流程机器人服务由csp自身提供，供应商设为owner
        accountManagementReq.setSupplierTag(CSPChatbotSupplierTagEnum.OWNER.getValue());
        accountManagementReq.setAccountName(req.getChatbotName());
        accountManagementReq.setCustomerId(customerId);
        if (Objects.equals(operatorCodeEnum.getCode(), CSPOperatorCodeEnum.CMCC.getCode())) {
            String serverRoot = Boolean.TRUE.equals(chatBotMobileConfigure.getTest())
                    ? chatBotMobileConfigure.getTestServerRoot()
                    : chatBotMobileConfigure.getServerRootMap().get(req.getRegion());
            Assert.notNull(serverRoot, "serverRoot地址不能为空");
            accountManagementReq.setMessageAddress(serverRoot);
            accountManagementReq.setFileAddress(serverRoot);
            accountManagementReq.setAppKey(accountManageDo.getCspCode());
        } else {
            //如果是新增电联机器人，使用配置文件中的网关消息地址和网关文件地址
            accountManagementReq.setMessageAddress(cspUnicomAndTelecomConfigure.getServerRootMessageUrl());
            accountManagementReq.setFileAddress(cspUnicomAndTelecomConfigure.getServerRootMediaUrl());
            accountManagementReq.setAppKey(accountManageDo.getCspAccount());
        }
        accountManagementReq.setIsAddOther(1);
        AccountManagementDo managementDo = accountManagementService.saveAccountManagement(accountManagementReq);
        // 插入关联白名单
        ChatbotManageWhiteListDo whiteListDo = new ChatbotManageWhiteListDo();
        whiteListDo.setWhiteList(req.getWhiteList());
        whiteListDo.setStatus(0);
        whiteListDo.setChatbotAccountId(managementDo.getChatbotAccountId());
        whiteListDo.setChatbotId(managementDo.getChatbotAccount());
        chatbotManageWhiteListDao.insert(whiteListDo);

        //插入机器人详情表，同步运营商拿到access_tag_no后再修改记录，这样会多操作一次数据库，但是防止了先同步运营商再落库因为落库失败而导致运营商侧存在脏数据的情况
        ChatbotManageDo chatbotManageDo = new ChatbotManageDo();
        BeanUtils.copyProperties(req, chatbotManageDo);
        chatbotManageDo.setChatbotId(managementDo.getChatbotAccount());
        chatbotManageDo.setChatbotAccountId(managementDo.getChatbotAccountId());
        if (Objects.equals(operatorCode, CSPOperatorCodeEnum.CMCC.getCode())) {
            chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_20_CRATE_AND_IS_AUDITING.getCode());
        } else {
            chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_60_NEW_WAIT_AUDIT.getCode());
        }
        //插入cmcc机器人详情表
        chatbotManageDao.insert(chatbotManageDo);
        try {
            //同步运营商
            if (Objects.equals(operatorCode, CSPOperatorCodeEnum.CMCC.getCode())) {
                ChatBotNew chatBotNew = buildChatBot(chatbotManageDo, req.getWhiteList(), INDUSTRY_MAP.get(Integer.valueOf(req.getChatbotIndustryType())), contractManageDo, accountManageDo);
                SyncResult syncResult = chatBotService.chatBotNew(chatBotNew);
                if (!StringUtils.equals(syncResult.getResultCode(), SUCCESS_CODE)) {
                    throw new BizException(820103003, syncResult.getResultDesc());
                }
            } else {
                String customerNum = contractManageDo.getCustomerNum();
                //电信的精度要求是3位小数，需要进行处理
                if (Objects.equals(CSPOperatorCodeEnum.CT.getCode(), operatorCode)) {
                    String longitude = longitudeAndLatitudeRoundHalfUp(req.getLongitude());
                    req.setLongitude(longitude);
                    String latitude = longitudeAndLatitudeRoundHalfUp(req.getLatitude());
                    req.setLatitude(latitude);
                }
                ChatBotReq chatBotReq = buildParma(req, accountManageDo.getCspAccount(), customerNum, null);
                String accessTagNo = cspPlatformService.addChatBot(chatBotReq, accountManageDo.getCspPassword(), accountManageDo.getCspAccount(), operatorCode);
                chatbotManageDo.setAccessTagNo(accessTagNo);
                //更新机器人信息
                chatbotManageDao.update(
                        null,
                        Wrappers.<ChatbotManageDo>lambdaUpdate().
                                eq(ChatbotManageDo::getId, chatbotManageDo.getId())
                                .set(ChatbotManageDo::getAccessTagNo, accessTagNo)
                );
            }
        } catch (Throwable e) {
            log.error("新增非本地机器人失败:{}", e.getMessage(), e);
            //因为先同步运营商再落库，如果落库失败，先到运营商处注销机器人（忽略结果），再抛出自定义异常回滚事务
            try {
                if (Objects.equals(operatorCode, CSPOperatorCodeEnum.CMCC.getCode())) {
                    cancelCMCCChatbot(chatbotManageDo.getChatbotId());
                } else {
                    cspPlatformService.deleteChatBot(chatbotManageDo.getAccessTagNo(), accountManageDo.getCspPassword(), accountManageDo.getCspAccount(), operatorCode);
                }
            } catch (Throwable reason) {
                log.error("注销运营商机器人失败:{}", reason.getMessage());
            }
            throw new BizException(500, "新增非本地机器人失败:" + e.getMessage());
        }
    }

    /*
     * @describe  注销Cmcc机器人
     * @Param
     * @param req
     * @return void
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logOffCmccRobot(ChatbotCmccLogOffReq req) {
        String chatbotAccountId = req.getChatbotAccountId();
        //本地机器人,，Chatbot处于已下线状态时， 才能进行注销操作
        LambdaUpdateWrapper<AccountManagementDo> eq = Wrappers.<AccountManagementDo>lambdaUpdate()
                .eq(AccountManagementDo::getChatbotAccountId, chatbotAccountId)
                .eq(AccountManagementDo::getSupplierTag, CSPChatbotSupplierTagEnum.OWNER.getValue())
                .eq(AccountManagementDo::getDeleted, 0);
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(eq);
        if (Objects.isNull(accountManagementDo)) {
            throw new BizException("机器人账号不存在");
        }

        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的）
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!accountManagementDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }

        //判断Chatbot的状态 在线、已下线、暂停、黑名单、调试 时,可以进行注销
        if (!CSPChatbotStatusEnum.STATUS_31_OFFLINE.getCode().equals(accountManagementDo.getChatbotStatus()) &&
                !CSPChatbotStatusEnum.STATUS_30_ONLINE.getCode().equals(accountManagementDo.getChatbotStatus()) &&
                !CSPChatbotStatusEnum.STATUS_41_BAN.getCode().equals(accountManagementDo.getChatbotStatus()) &&
                !CSPChatbotStatusEnum.STATUS_42_OFFLINE_CASE_CSP.getCode().equals(accountManagementDo.getChatbotStatus()) &&
                !CSPChatbotStatusEnum.STATUS_50_DEBUG.getCode().equals(accountManagementDo.getChatbotStatus()) &&
                !CSPChatbotStatusEnum.STATUS_40_PAUSE.getCode().equals(accountManagementDo.getChatbotStatus())) {
            throw new BizException("机器人处于非已下线状态，不能进行注销操作");
        }

        LambdaUpdateWrapper<AccountManagementDo> whereWrapper = Wrappers.<AccountManagementDo>lambdaUpdate()
                .eq(AccountManagementDo::getChatbotAccountId, chatbotAccountId)
                .set(AccountManagementDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_43_LOG_OFF.getCode());


        //将机器人状态设置为已注销
        accountManagementDao.update(new AccountManagementDo(), whereWrapper);
        //删除模板审核记录
        messageTemplateService.cancelAudit(accountManagementDo.getChatbotAccount());
        //删除素材审核记录
        FileExamineDeleteReq fileExamineDeleteReq = new FileExamineDeleteReq();
        fileExamineDeleteReq.setChatbotAccount(accountManagementDo.getChatbotAccount());
        platformApi.deleteAuditRecord(fileExamineDeleteReq);

        //群发计划取消关联账号绑定
        robotGroupSendPlansApi.removeChatbotAccount(accountManagementDo.getChatbotAccount(), accountManagementDo.getAccountType(), accountManagementDo.getSupplierTag());
        //机器人场景取消关联账号
        robotSceneNodeApi.removeChatbotAccount(accountManagementDo.getChatbotAccount());
        //远程调用移动 的机器人注销接口
        logOffCMCCChatbot(accountManagementDo.getChatbotAccount());
    }

    private void logOffCMCCChatbot(String chatbotAccount) {

        chatbotAccount = chatbotAccount + "@botplatform.rcs.chinamobile.com";
        ChatBotLogOff chatBotLogOff = new ChatBotLogOff();
        chatBotLogOff.setChatbotId(chatbotAccount);
        chatBotLogOff.setCreator(SessionContextUtil.getUser().getUserId());
        chatBotService.logOffCmccChatbot(chatBotLogOff);
    }

    /**
     * 注销本地电联机器人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.BASE)
    public void logOffLocalRobot(ChatbotLocalLogOffReq req) {
        String chatbotAccountId = req.getChatbotAccountId();
        //本地机器人,，Chatbot处于已下线状态时， 才能进行注销操作
        LambdaUpdateWrapper<AccountManagementDo> eq = Wrappers.<AccountManagementDo>lambdaUpdate()
                .eq(AccountManagementDo::getChatbotAccountId, chatbotAccountId)
                .eq(AccountManagementDo::getSupplierTag, CSPChatbotSupplierTagEnum.OWNER.getValue())
                .eq(AccountManagementDo::getDeleted, 0);
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(eq);
        if (Objects.isNull(accountManagementDo)) {
            throw new BizException("机器人账号不存在");
        }

        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的）
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!accountManagementDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }

        //判断Chatbot的状态是否为已下线
        if (!CSPChatbotStatusEnum.STATUS_31_OFFLINE.getCode().equals(accountManagementDo.getChatbotStatus()) &&
                !CSPChatbotStatusEnum.STATUS_71_OFFLINE.getCode().equals(accountManagementDo.getChatbotStatus()) &&
                !CSPChatbotStatusEnum.STATUS_42_OFFLINE_CASE_CSP.getCode().equals(accountManagementDo.getChatbotStatus())) {
            throw new BizException("机器人处于非已下线状态，不能进行注销操作");
        }

        LambdaUpdateWrapper<AccountManagementDo> whereWrapper = Wrappers.<AccountManagementDo>lambdaUpdate()
                .eq(AccountManagementDo::getChatbotAccountId, chatbotAccountId);

        Integer accountTypeCode = accountManagementDo.getAccountTypeCode();
        if (CSPOperatorCodeEnum.CMCC.getCode().equals(accountTypeCode)) {
            //移动机器人注销
            whereWrapper.set(AccountManagementDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_43_LOG_OFF.getCode());
        } else {
            //非移动机器人注销
            whereWrapper.set(AccountManagementDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_69_LOG_OFF.getCode());
        }
        //将机器人状态设置为已注销
        accountManagementDao.update(new AccountManagementDo(), whereWrapper);
        //删除模板审核记录
        messageTemplateService.cancelAudit(accountManagementDo.getChatbotAccount());
        //删除素材审核记录
        FileExamineDeleteReq fileExamineDeleteReq = new FileExamineDeleteReq();
        fileExamineDeleteReq.setChatbotAccount(accountManagementDo.getChatbotAccount());
        platformApi.deleteAuditRecord(fileExamineDeleteReq);
        //群发计划取消关联账号绑定
        robotGroupSendPlansApi.removeChatbotAccount(accountManagementDo.getChatbotAccount(), accountManagementDo.getAccountType(), accountManagementDo.getSupplierTag());
        //机器人场景取消关联账号
        robotSceneNodeApi.removeChatbotAccount(accountManagementDo.getChatbotAccount());

    }

    @Override
    public boolean checkForCanCreate(checkForCanCreateChatbotReq req) {
        String customerId = req.getCustomerId();
        Integer accountTypeCode = req.getAccountTypeCode();
        // 1.判断是否有权限
        BaseUser user = SessionContextUtil.getUser();
        String userId = user.getUserId();
        String cspId = cspService.obtainCspId(userId);
        // 校验CSP及客户用户信息
        validateCspId(cspId, req.getCustomerId());
        // 校验用户是否可用
        CustomerDetailReq customerDetailReq = new CustomerDetailReq();
        customerDetailReq.setCustomerId(customerId);
        CustomerDetailResp cspCustomer = cspCustomerApi.getDetailByCustomerId(customerDetailReq);
        if (cspCustomer == null || !cspCustomer.getCustomerActive()) {
            throw new BizException("账号已经被禁用，请重新选择！");
        }

        //判断用户是否已经存在该通道下非注销状态的Chatbot
        LambdaQueryWrapper<AccountManagementDo> wrapper = Wrappers.<AccountManagementDo>lambdaQuery()
                .eq(AccountManagementDo::getCustomerId, customerId)
                .eq(AccountManagementDo::getAccountTypeCode, accountTypeCode)
                .eq(AccountManagementDo::getDeleted, 0);
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(wrapper);
        //如果不存在(新建功能),那么可以直接创建
        if (Objects.isNull(accountManagementDo) || Objects.isNull(accountManagementDo.getChatbotAccountId())) {
            return true;
        }
        //如果是修改功能(未切换所属用户)
        if (accountManagementDo.getChatbotAccountId().equals(req.getChatbotAccountId())) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteLocalRobot(ChatbotLocalDeleteReq req) {
        String chatbotAccountId = req.getChatbotAccountId();
        //本地机器人,，Chatbot处于已下线状态时， 才能进行删除操作
        LambdaUpdateWrapper<AccountManagementDo> eq = Wrappers.<AccountManagementDo>lambdaUpdate()
                .eq(AccountManagementDo::getChatbotAccountId, chatbotAccountId)
                .eq(AccountManagementDo::getSupplierTag, CSPChatbotSupplierTagEnum.OWNER.getValue())
                .eq(AccountManagementDo::getDeleted, 0);
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(eq);
        if (Objects.isNull(accountManagementDo)) {
            throw new BizException("机器人账号不存在");
        }

        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的）
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!accountManagementDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }

        //判断Chatbot的状态是否为已注销
        if (!CSPChatbotStatusEnum.STATUS_69_LOG_OFF.getCode().equals(accountManagementDo.getChatbotStatus()) &&
                !CSPChatbotStatusEnum.STATUS_43_LOG_OFF.getCode().equals(accountManagementDo.getChatbotStatus())) {
            throw new BizException("机器人处于非注销状态，不能进行删除操作");
        }

        //删除机器人
        accountManagementDao.delete(Wrappers.<AccountManagementDo>lambdaUpdate()
                .eq(AccountManagementDo::getChatbotAccountId, chatbotAccountId)
        );

        //Chatbot的历史数据在页面保留（包括消息数据统计、话单、订购数据（目前主要是消息收发的数据））

    }

    @Override
    @Transactional
    public void saveSupplierChatbot(ChatbotSupplierAdd req) {
        BaseUser user = SessionContextUtil.getUser();
        String userId = user.getUserId();
        String cspId = cspService.obtainCspId(userId);
        Integer operatorCode = req.getOperatorCode();
        String customerId = req.getCustomerId();
        // 校验用户信息
        validateCspId(cspId, req.getCustomerId());
        // 校验用户是否可用
        CustomerDetailReq customerDetailReq = new CustomerDetailReq();
        customerDetailReq.setCustomerId(customerId);
        CustomerDetailResp cspCustomer = cspCustomerApi.getDetailByCustomerId(customerDetailReq);
        if (cspCustomer == null || !cspCustomer.getCustomerActive()) {
            throw new BizException("用户不可用");
        }
        // 校验chatbot状态
        validateChatbot(cspId, req.getCustomerId(), req.getOperatorCode(), 0L);
        //校验合同信息
        ContractManageDo contractManageDo = getContract(operatorCode, customerId);
        validateContractForSupplierChatbot(contractManageDo, operatorCode);

        // 插入机器人表
        AccountManagementDo accountManagementDo = new AccountManagementDo();
        BeanUtils.copyProperties(req, accountManagementDo);
        //设置名字,方便后台管理进行查询
        accountManagementDo.setCustomerName(cspCustomer.getName());
        accountManagementDo.setChatbotAccountId(UUIDUtils.generateUUID());
        accountManagementDo.setAccountName(req.getChatbotName());
        accountManagementDo.setCspId(cspId);
        accountManagementDo.setAccountType(CSPOperatorCodeEnum.byCode(operatorCode).getName());
        accountManagementDo.setAccountTypeCode(operatorCode);
        accountManagementDo.setChannel(CSPChannelEnum.FONTDO.getValue());
        accountManagementDo.setIsAddOther(1);
        accountManagementDo.setToken(UUIDUtils.generateUUID().substring(0, 16));
        if (Objects.equals(req.getOperatorCode(), CSPOperatorCodeEnum.CMCC.getCode())) {
            accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_10_NO_COMMIT.getCode());
        }
        if (Objects.equals(req.getOperatorCode(), CSPOperatorCodeEnum.CT.getCode())
                || Objects.equals(req.getOperatorCode(), CSPOperatorCodeEnum.CUNC.getCode())
        ) {
            accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_72_NO_COMMIT.getCode());
        }
        // 设置supplier_tag为fontdo
        accountManagementDo.setSupplierTag(CSPChatbotSupplierTagEnum.FONTDO.getValue());
        accountManagementDao.insert(accountManagementDo);

        // 插入关联白名单
        ChatbotManageWhiteListDo whiteListDo = new ChatbotManageWhiteListDo();
        whiteListDo.setWhiteList(req.getWhiteList());
        whiteListDo.setStatus(0);
        whiteListDo.setChatbotAccountId(accountManagementDo.getChatbotAccountId());
        chatbotManageWhiteListDao.insert(whiteListDo);

        // 插入chatbot_manage_cmcc表(类似于detail)
        ChatbotManageDo chatbotManageDo = new ChatbotManageDo();
        BeanUtils.copyProperties(req, chatbotManageDo);
        chatbotManageDo.setChatbotAccountId(accountManagementDo.getChatbotAccountId());
        chatbotManageDo.setChatbotStatus(accountManagementDo.getChatbotStatus());
        chatbotManageDao.insert(chatbotManageDo);
    }

    ;

    @Override
    @Transactional
    public void updateSupplierChatbot(ChatbotSupplierUpdate req) {
        String userId = SessionContextUtil.getUser().getUserId();
        String cspId = cspService.obtainCspId(userId);
        Integer operatorCode = req.getOperatorCode();
        String customerId = req.getCustomerId();
        // 校验用户信息
        validateCspId(cspId, req.getCustomerId());
        // 校验用户是否可用
        // 校验用户是否可用
        CustomerDetailReq customerDetailReq = new CustomerDetailReq();
        customerDetailReq.setCustomerId(customerId);
        CustomerDetailResp cspCustomer = cspCustomerApi.getDetailByCustomerId(customerDetailReq);
        if (cspCustomer == null || !cspCustomer.getCustomerActive()) {
            throw new BizException("用户不可用");
        }
        // 校验chatbot状态
        validateChatbot(cspId, req.getCustomerId(), req.getOperatorCode(), req.getId());
        //校验合同信息
        ContractManageDo contractManageDo = getContract(operatorCode, customerId);
        validateContractForSupplierChatbot(contractManageDo, operatorCode);

        // 更新机器人表
        AccountManagementDo accountManagementDo = new AccountManagementDo();
        BeanUtils.copyProperties(req, accountManagementDo);
        accountManagementDo.setAccountName(req.getChatbotName());
        accountManagementDo.setCustomerName(cspCustomer.getName());
        accountManagementDao.updateById(accountManagementDo);

        // 获取chatbot信息
        AccountManagementDo chatbotDo = accountManagementDao.selectById(req.getId());

        // 更新关联白名单
        LambdaUpdateWrapper<ChatbotManageWhiteListDo> whiteListWrapper = new LambdaUpdateWrapper<>();
        whiteListWrapper.eq(ChatbotManageWhiteListDo::getChatbotAccountId, chatbotDo.getChatbotAccountId());
        ChatbotManageWhiteListDo whiteListDo = new ChatbotManageWhiteListDo();
        whiteListDo.setWhiteList(req.getWhiteList());
        chatbotManageWhiteListDao.update(whiteListDo, whiteListWrapper);

        // 更新chatbot_manage_cmcc表
        LambdaUpdateWrapper<ChatbotManageDo> detailWrapper = new LambdaUpdateWrapper<>();
        detailWrapper.eq(ChatbotManageDo::getChatbotAccountId, chatbotDo.getChatbotAccountId());
        ChatbotManageDo chatbotManageDo = new ChatbotManageDo();
        BeanUtils.copyProperties(req, chatbotManageDo);
        chatbotManageDao.update(chatbotManageDo, detailWrapper);
    }

    @Override
    public ChatbotSupplierInfo getSupplierChatbotInfo(Long id) {
        ChatbotSupplierInfo chatbotSupplierInfo = new ChatbotSupplierInfo();
        AccountManagementDo chatbotDo = accountManagementDao.selectById(id);
        BeanUtils.copyProperties(chatbotDo, chatbotSupplierInfo);
        ChatbotManageWhiteListDo whiteListDo = chatbotManageWhiteListDao.selectOne(ChatbotManageWhiteListDo::getChatbotAccountId, chatbotDo.getChatbotAccountId());
        if (ObjectUtil.isNotNull(whiteListDo))
            BeanUtils.copyProperties(whiteListDo, chatbotSupplierInfo);
        ChatbotManageDo chatbotManageDo = chatbotManageDao.selectOne(ChatbotManageDo::getChatbotAccountId, chatbotDo.getChatbotAccountId());
        if (ObjectUtil.isNotNull(chatbotManageDo))
            BeanUtils.copyProperties(chatbotManageDo, chatbotSupplierInfo);
        return chatbotSupplierInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSupplierChatbot(Long id) {
        AccountManagementDo accountManagementDo = accountManagementDao.selectById(id);
        if (accountManagementDo == null) {
            throw new BizException(820103033, "没查到对应的机器人信息");
        }
        Integer[] canDeleteStatus = {
                CSPChatbotStatusEnum.STATUS_10_NO_COMMIT.getCode(),
                CSPChatbotStatusEnum.STATUS_72_NO_COMMIT.getCode(),
                CSPChatbotStatusEnum.STATUS_43_LOG_OFF.getCode(),
                CSPChatbotStatusEnum.STATUS_69_LOG_OFF.getCode(),
                CSPChatbotStatusEnum.STATUS_28_REJECT.getCode(),
                CSPChatbotStatusEnum.STATUS_74_REJECT.getCode()
        };
        if (!Arrays.asList(canDeleteStatus).contains(accountManagementDo.getChatbotStatus())) {
            throw new BizException(820103033, "机器人无法删除");
        }
        LambdaUpdateWrapper<AccountManagementDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AccountManagementDo::getId, id);
        updateWrapper.set(AccountManagementDo::getDeleted, 1);
        updateWrapper.set(AccountManagementDo::getDeleteTime, DateUtil.date());
        accountManagementDao.delete(updateWrapper);

        //更改对应的合同的状态为未提交
        ContractManageDo contractManageDo = new ContractManageDo();
        contractManageDo.setContractStatus(CSPContractStatusEnum.STATUS_69_NO_COMMIT.getCode());

        //修改合同状态为已完成
        int updateNum = contractManageDao.update(contractManageDo, new LambdaQueryWrapper<ContractManageDo>()
                .eq(ContractManageDo::getCustomerId, accountManagementDo.getCustomerId())
                .eq(ContractManageDo::getChannel, com.citc.nce.auth.common.CSPChannelEnum.FONTDO.getValue())
                .eq(ContractManageDo::getOperatorCode, accountManagementDo.getAccountTypeCode())
                .notIn(ContractManageDo::getContractStatus, CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_40_PAUSE.getCode())
                .eq(ContractManageDo::getDeleted, 0)
        );
        if (updateNum < 1) {
            log.error("合同更改状态失败,CustomerId:{},AccountTypeCode:{},Channel:{}", accountManagementDo.getCustomerId(), accountManagementDo.getAccountTypeCode(), com.citc.nce.auth.common.CSPChannelEnum.FONTDO.getValue());
        }
        //注销时已删除
        // 删除关联的模板审核记录
        //  messageTemplateService.cancelAudit(accountManagementDo.getChatbotAccountId());
    }


    @Override
    @Transactional
    public void onlineSupplierChatbot(Long id) {
        AccountManagementDo accountManagementDo = accountManagementDao.selectById(id);
        if (accountManagementDo == null) {
            throw new BizException(820103033, "没查到对应的机器人信息");
        }
        if (!Objects.equals(accountManagementDo.getSupplierTag(), "fontdo")) {
            throw new BizException(820103033, "通道有误，机器人无法上线");
        }
        Integer[] canOnlineStatus = {
                CSPChatbotStatusEnum.STATUS_31_OFFLINE.getCode(),
                CSPChatbotStatusEnum.STATUS_71_OFFLINE.getCode()
        };
        if (!Arrays.asList(canOnlineStatus).contains(accountManagementDo.getChatbotStatus())) {
            throw new BizException(820103033, "机器人无法上线");
        }
        // 移动
        if (Objects.equals(accountManagementDo.getAccountTypeCode(), CSPOperatorCodeEnum.CMCC.getCode())) {
            accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_30_ONLINE.getCode());
        } else {
            accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_68_ONLINE.getCode());
        }
        accountManagementDao.updateById(accountManagementDo);
    }

    @Override
    @Transactional
    public void offlineSupplierChatbot(Long id) {
        AccountManagementDo accountManagementDo = accountManagementDao.selectById(id);
        if (accountManagementDo == null) {
            throw new BizException(820103033, "没查到对应的机器人信息");
        }
        if (!Objects.equals(accountManagementDo.getSupplierTag(), "fontdo")) {
            throw new BizException(820103033, "通道有误，机器人无法下线");
        }
        Integer[] canOfflineStatus = {
                CSPChatbotStatusEnum.STATUS_30_ONLINE.getCode(),
                CSPChatbotStatusEnum.STATUS_68_ONLINE.getCode()
        };
        if (!Arrays.asList(canOfflineStatus).contains(accountManagementDo.getChatbotStatus())) {
            throw new BizException(820103033, "机器人无法下线");
        }
        // 移动
        if (Objects.equals(accountManagementDo.getAccountTypeCode(), CSPOperatorCodeEnum.CMCC.getCode())) {
            accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_31_OFFLINE.getCode());
        } else {
            accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_71_OFFLINE.getCode());
        }
        accountManagementDao.updateById(accountManagementDo);
    }

    @Override
    @Transactional
    public void submitSupplierChatbot(Long id) {
        AccountManagementDo accountManagementDo = accountManagementDao.selectById(id);
        if (accountManagementDo == null) {
            throw new BizException(820103033, "没查到对应的机器人信息");
        }
        Integer[] canSubmitStatus = { // 未提交、已驳回 => 处理中
                CSPChatbotStatusEnum.STATUS_10_NO_COMMIT.getCode(),
                CSPChatbotStatusEnum.STATUS_72_NO_COMMIT.getCode(),
                CSPChatbotStatusEnum.STATUS_28_REJECT.getCode(),
                CSPChatbotStatusEnum.STATUS_74_REJECT.getCode()
        };
        if (!Arrays.asList(canSubmitStatus).contains(accountManagementDo.getChatbotStatus())) {
            throw new BizException(820103033, "机器人无法提交");
        }
        // 查询对应的合同
        ContractManageDo contractManageDo = getContract(accountManagementDo.getAccountTypeCode(), accountManagementDo.getCustomerId());
        // 查询对应的chatbot详情
        ChatbotManageDo chatbotManageDo = getChatbotManageDo(accountManagementDo.getChatbotAccountId());

        // 更新chatbot状态以及对应合同的状态
        if (Objects.equals(accountManagementDo.getAccountTypeCode(), CSPOperatorCodeEnum.CMCC.getCode())) {
            accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_13_PROCESSING.getCode());
            chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_13_PROCESSING.getCode());
            contractManageDo.setContractStatus(CSPContractStatusEnum.STATUS_26_PROCESSING.getCode());
        } else {
            accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_73_PROCESSING.getCode());
            chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_73_PROCESSING.getCode());
            contractManageDo.setContractStatus(CSPContractStatusEnum.STATUS_70_PROCESSING.getCode());
        }
        accountManagementDao.updateById(accountManagementDo);
        chatbotManageDao.updateById(chatbotManageDo);

        // chatbot创建成功后更新合同表状态
        contractManageDao.updateById(contractManageDo);
    }

    /**
     * 查询chatbot状态配置
     */
    @Override
    public ChatbotStatusResp getChatbotStatusOptions() {
        String userId = SessionContextUtil.getUser().getUserId();
        ChatbotStatusResp resp = new ChatbotStatusResp();
        resp.setUnicomChatbotOptions(CSPStatusOptionsEnum.DIRECT.getValue());
        // 查询当前用户的通道
        GetUserInfoReq req = new GetUserInfoReq();
        req.setUserId(userId);
        ChannelInfoResp channelResp = adminAuthApi.getCspUserChannel(req);
        // 查询当前CSP用户签的chatbot类型（暂时只有电信和移动有代理chatbot）
        // 电信
        QueryWrapper<AccountManagementDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("customer_id", userId);
        queryWrapper.eq("account_type_code", CSPOperatorCodeEnum.CT.getCode());
        queryWrapper.ne("channel", channelResp.getTelecomChannel()); // 查询当前用户不是当前通道的合同
        long telecomCount = accountManagementDao.selectCount(queryWrapper);
        if (telecomCount == 0) {
            resp.setTelecomChatbotOptions(channelResp.getTelecomChannel());
        } else {
            resp.setTelecomChatbotOptions(CSPStatusOptionsEnum.UNION.getValue());
        }
        // 移动
        queryWrapper.eq("account_type_code", CSPOperatorCodeEnum.CMCC.getCode());
        queryWrapper.ne("channel", channelResp.getMobileChannel()); // 查询当前用户不是当前通道的合同
        long mobileCount = accountManagementDao.selectCount(queryWrapper);
        if (mobileCount == 0) {
            resp.setMobileChatbotOptions(channelResp.getMobileChannel());
        } else {
            resp.setMobileChatbotOptions(CSPStatusOptionsEnum.UNION.getValue());
        }
        // 联通
        queryWrapper.eq("account_type_code", CSPOperatorCodeEnum.CUNC.getCode());
        queryWrapper.ne("channel", channelResp.getUnicomChannel()); // 查询当前用户不是当前通道的合同
        long unionCount = accountManagementDao.selectCount(queryWrapper);
        if (unionCount == 0) {
            resp.setTelecomChatbotOptions(channelResp.getUnicomChannel());
        } else {
            resp.setTelecomChatbotOptions(CSPStatusOptionsEnum.UNION.getValue());
        }
        return resp;
    }

    /**
     * 查询chatbot通道
     */
    @Override
    public ChatbotChannelResp getChatbotChannel(ChatbotChannelReq req) {
        ChatbotChannelResp resp = new ChatbotChannelResp();
        QueryWrapper<AccountManagementDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_type", req.getAccountType());
        queryWrapper.eq("account_name", req.getAccountName());
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(queryWrapper);
        resp.setChannel(accountManagementDo.getChannel());
        return resp;
    }

    /**
     * 查询新增CHATBOT权限
     */
    @Override
    public Boolean getNewChatbotPermission(ChatbotNewPermissionReq req) {
        String userId = SessionContextUtil.getUser().getUserId();
        String cspId = cspService.obtainCspId(userId);
        validateCspId(cspId, req.getCustomerId());
        QueryWrapper<AccountManagementDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id", req.getCustomerId());
        queryWrapper.eq("account_type_code", req.getOperatorCode());
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(queryWrapper);
        // 没有chatbot，可以新建
        if (accountManagementDo == null) {
            return true;
        }
        Integer[] noPassNewPermissionStatus = {
                CSPChatbotStatusEnum.STATUS_68_ONLINE.getCode(),
                CSPChatbotStatusEnum.STATUS_30_ONLINE.getCode(),
                CSPChatbotStatusEnum.STATUS_70_TEST.getCode(),
                CSPChatbotStatusEnum.STATUS_50_DEBUG.getCode()
        };

        // 合同处于未提交，处理中，正常的不能再次创建合同
        return !Arrays.asList(noPassNewPermissionStatus).contains(accountManagementDo.getChatbotStatus());
    }


    /*---------------------------------------工具方法------------------------------------------------*/

    /**
     * 机器人合同校验
     */
    private static void validateContractForChatbot(ContractManageDo contractManageDo, Integer operatorCode) {
        if (ObjectUtil.isEmpty(contractManageDo.getCustomerNum())) {
            throw new BizException(820103012, "客户未同步");
        }
        Integer state = contractManageDo.getActualState() != null
                ? contractManageDo.getActualState()
                : contractManageDo.getContractStatus();
        if (Objects.equals(operatorCode, CSPOperatorCodeEnum.CMCC.getCode())) {
            if (!Objects.equals(state, CSPContractStatusEnum.STATUS_30_ONLINE.getCode()))
                throw new BizException(820103033, "合同处于不可用状态");
        } else {
            if (!Objects.equals(state, CSPContractStatusEnum.STATUS_66_NORMAL.getCode()))
                throw new BizException(820103033, "合同处于不可用状态");
        }
        Date effectiveDate = contractManageDo.getContractEffectiveDate();
        Date expireDate = contractManageDo.getContractExpireDate();
        if (effectiveDate == null || expireDate == null) //永久合同没有起止日期
            return;
        Date now = new Date();
        if (now.before(effectiveDate))
            throw new BizException("合同未生效");
        if (now.after(expireDate))
            throw new BizException("合同已过期");
    }

    private static void validateContractForSupplierChatbot(ContractManageDo contractManageDo, Integer operatorCode) {
        // 已注销，返回错误
        if (Objects.equals(contractManageDo.getContractStatus(), CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode()) ||
                Objects.equals(contractManageDo.getContractStatus(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode())) {
            throw new BizException(820103033, "该客户无可用合同");
        }
    }

    private static void validateCspId(String cspId, String customerId) {
        if (!customerId.substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
    }

    private void validateChatbot(String cspId, String customerId, Integer operatorCode, Long chatbotId) {
        QueryWrapper<AccountManagementDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id", customerId);
        queryWrapper.eq("csp_id", cspId);
        queryWrapper.eq("account_type_code", operatorCode);
        queryWrapper.eq("deleted", 0);
//        queryWrapper.notIn("chatbot_status", CSPChatbotStatusEnum.STATUS_69_LOG_OFF.getCode(), CSPChatbotStatusEnum.STATUS_43_LOG_OFF.getCode());
        if (chatbotId != 0) {
            queryWrapper.ne("id", chatbotId);
        }
        if (accountManagementDao.selectCount(queryWrapper) > 0) {
            throw new BizException(820103033, "该客户已具备正在使用中的本网通道Chatbot");
        }

    }

    private ChatBotReq buildParma(ChatbotSaveReq req, String cspId, String cspEcNo, String accessTagNo) {
        ChatBotReq chatBotReq = new ChatBotReq();
        List<String> list = Arrays.asList(cspUnicomAndTelecomConfigure.getIpWhiteList().split(","));
        chatBotReq.setCspId(cspId)
                .setCspEcNo(cspEcNo)
                .setChatbotId(req.getChatbotId())
                .setServiceName(req.getChatbotName())
                .setServiceIcon(req.getChatbotLogoUrl())
                .setServiceDescription(req.getChatbotServiceDesc())
                .setSMSNumber(req.getChatbotSmsPort())
                .setAutograph(req.getChatbotSmsSign())
                .setCategory(Collections.singletonList(req.getChatbotIndustryType()))
                .setShowProvider(req.getChatbotISPIsDisplay())
                .setProvider("软通")
                .setTCPage(req.getServiceTerm())
                .setEmailAddress(req.getChatbotMail())
                .setServiceWebsite(req.getServiceWebsite())
                .setCallBackNumber(req.getCallBackNumber())
                .setAddress(req.getAddress())
                .setLatitude(Double.parseDouble(req.getLatitude()))
                .setLongitude(Double.parseDouble(req.getLongitude()))
                .setIpWhiteList(list);
        if (StringUtils.isNotEmpty(accessTagNo)) {
            chatBotReq.setAccessTagNo(accessTagNo);
        }
        if (StringUtils.isNotEmpty(cspEcNo)) {
            chatBotReq.setCspEcNo(cspEcNo);
        }
        return chatBotReq;
    }

    /**
     * 根据客户ID和运营商编码查询合同信息（也就是运营商客户）
     *
     * @param operatorCode 运营商编码
     * @param customerId   客户ID
     * @return 客户合同
     * @throws BizException 当未查询到合同时抛出次异常
     */
    private ContractManageDo getContract(Integer operatorCode, String customerId) {
        ContractManageDo contractManageDo = contractManageDao.selectOne(
                Wrappers.<ContractManageDo>lambdaQuery()
                        .eq(ContractManageDo::getCustomerId, customerId)
                        //非注销及暂停合同
                        .notIn(ContractManageDo::getContractStatus, CSPContractStatusEnum.STATUS_31_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_67_LOG_OFF.getCode(), CSPContractStatusEnum.STATUS_40_PAUSE.getCode())
                        .eq(ContractManageDo::getOperatorCode, operatorCode)
                        .eq(ContractManageDo::getDeleted, 0)
        );
        if (ObjectUtil.isEmpty(contractManageDo)) {
            throw new BizException(500, "未查询到可用合同");
        }
        return contractManageDo;
    }

    private ChatBotNew buildChatBot(ChatbotManageDo chatbotManageDo, String whiteList, String chatbotType, ContractManageDo contractManageDo, AccountManageDo accountManageDo) {
        ChatBotNew chatBotNew = new ChatBotNew()
                .setName(chatbotManageDo.getChatbotName())
                .setChatbotId(chatbotManageDo.getChatbotId() + "@botplatform.rcs.chinamobile.com")
                .setLogo("isoftstone/" + chatbotManageDo.getChatbotLogoUrl() + ".jpg")
                .setOfficeCode(chatbotManageDo.getRegion())
                .setProvinceCode(chatbotManageDo.getProvince())
                .setCityCode(chatbotManageDo.getCity())
                .setCategory(chatbotType.split(","))
                .setDebugWhiteAddress(whiteList)
                .setSms(chatbotManageDo.getChatbotSmsPort())
                .setAutograph(chatbotManageDo.getChatbotSmsSign())
                .setDescription(chatbotManageDo.getChatbotServiceDesc())
                .setCallback(chatbotManageDo.getCallBackNumber())
                .setEmail(chatbotManageDo.getChatbotMail())
                .setWebsite(chatbotManageDo.getServiceWebsite())
                .setColour(chatbotManageDo.getChatbotBubbleColorRGB())
                .setAuditPerson("5G消息测试")
                .setAuditOpinion("审核通过")
                .setAuditTime(DateUtil.formatDate(new Date()))
                .setActualIssueIndustry(chatbotManageDo.getChatbotIndustryType());
        if (chatbotManageDo.getChatbotFileUrl() != null) {
            chatBotNew.setAttachment("isoftstone/" + chatbotManageDo.getChatbotFileUrl() + ".jpg");
        }
        if (chatbotManageDo.getChatbotISPIsDisplay() != null) {
            chatBotNew.setProviderSwitchCode(chatbotManageDo.getChatbotISPIsDisplay().toString());
            if (chatbotManageDo.getChatbotISPIsDisplay() == 1)
                chatBotNew.setProvider(contractManageDo.getEnterpriseName());
        }
        chatBotNew.setCreator(SessionContextUtil.getUser().getUserId());
        chatBotNew.setCustomerNum(contractManageDo.getCustomerNum());
        chatBotNew.setServiceCode(contractManageDo.getContractServiceCode());
        chatBotNew.setAgentCustomerNum(accountManageDo.getAgentCustomerNum());
        chatBotNew.setCspId(accountManageDo.getCspCode());
        return chatBotNew;
    }
}
