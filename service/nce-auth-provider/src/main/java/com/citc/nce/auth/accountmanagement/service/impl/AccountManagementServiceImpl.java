package com.citc.nce.auth.accountmanagement.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementDao;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementMapper;
import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.auth.accountmanagement.service.AccountManagementService;
import com.citc.nce.auth.accountmanagement.vo.*;
import com.citc.nce.auth.configure.AccountUrlConfigure;
import com.citc.nce.auth.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.auth.configure.ZhongXunConfigure;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.csp.common.CSPChatbotSupplierTagEnum;
import com.citc.nce.auth.csp.csp.service.CspService;
import com.citc.nce.auth.csp.menu.dao.MenuDao;
import com.citc.nce.auth.csp.menu.entity.MenuDo;
import com.citc.nce.auth.csp.recharge.service.ChargeTariffService;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffAdd;
import com.citc.nce.auth.identification.dao.UserEnterpriseIdentificationDao;
import com.citc.nce.auth.identification.entity.UserEnterpriseIdentificationDo;
import com.citc.nce.auth.messagetemplate.dao.MessageTemplateAuditDao;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateAuditDo;
import com.citc.nce.auth.messagetemplate.service.MessageTemplateService;
import com.citc.nce.auth.postpay.order.service.PostpayOrderService;
import com.citc.nce.auth.prepayment.service.IPrepaymentOrderService;
import com.citc.nce.auth.prepayment.vo.FifthAccountVo;
import com.citc.nce.auth.prepayment.vo.FifthMessageAccountListVo;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.constants.RechargeTariffFallbackTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.util.UUIDUtils;
import com.citc.nce.fileApi.ExamineResultApi;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.RobotSceneNodeApi;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

import static com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum.*;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:43
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
@EnableConfigurationProperties({SuperAdministratorUserIdConfigure.class, AccountUrlConfigure.class, ZhongXunConfigure.class})
public class AccountManagementServiceImpl implements AccountManagementService {
    private final AccountManagementDao accountManagementDao;
    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;
    private final AccountUrlConfigure accountUrlConfigure;
    private final ZhongXunConfigure zhongXunConfigure;
    private final UserEnterpriseIdentificationDao userEnterpriseIdentificationDao;
    private final CspService cspService;
    private final MenuDao menuDao;
    private final StringRedisTemplate redisTemplate;
    private final AccountManagementMapper accountManagementMapper;
    private final MessageTemplateAuditDao messageTemplateAuditDao;
    private final MessageTemplateService messageTemplateService;
    private final ExamineResultApi examineResultApi;
    private final RobotSceneNodeApi robotSceneNodeApi;
    private final IPrepaymentOrderService prepaymentOrderService;
    private final PostpayOrderService postpayOrderService;
    private final CspCustomerApi cspCustomerApi;
    private final ChargeTariffService chargeTariffService;

    //用来给直连机器人做模板的自动送审
//    private final ExecutorService threadPool = new ThreadPoolExecutor(
//            1,
//            1,
//            60L,
//            TimeUnit.SECONDS,
//            new LinkedBlockingQueue<>(10),
//            Executors.defaultThreadFactory(),
//            new ThreadPoolExecutor.AbortPolicy()
//    );
    @Override
    public PageResultAccountResp<?> getAccountManagements(PageParam pageParam) {
        QueryWrapper<AccountManagementDo> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("customer_id", userId);
        }
        wrapper.orderByDesc("create_time");
        PageResult<AccountManagementDo> accountManagementDoPageResult = accountManagementDao.selectPage(pageParam, wrapper);
        List<AccountManagementResp> accountManagementResps = BeanUtil.copyToList(accountManagementDoPageResult.getList(), AccountManagementResp.class);
        for (AccountManagementResp accountManagementResp : accountManagementResps) {
            QueryWrapper<MenuDo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("chatbot_account_id", accountManagementResp.getChatbotAccountId());
            queryWrapper.orderByDesc("version");
            List<MenuDo> menuDoList = menuDao.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(menuDoList)) {
                accountManagementResp.setMenuStatus(menuDoList.get(0).getMenuStatus());
                accountManagementResp.setResult(menuDoList.get(0).getResult());
            } else {
                accountManagementResp.setMenuStatus(-1);
            }
        }
        return new PageResultAccountResp<>(accountManagementResps, accountManagementDoPageResult.getTotal(), pageParam.getPageNo(), accountUrlConfigure.getCallbackUrl());


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountManagementDo saveAccountManagement(AccountManagementReq saveReq) {
        saveReq.setChatbotAccount(saveReq.getChatbotAccount().replace("sip:", ""));
        this.ensureChatbotInfoUnique(null, saveReq.getChatbotAccount(), saveReq.getAccountName());
        AccountManagementDo accountManagementDo = new AccountManagementDo();
        BeanUtil.copyProperties(saveReq, accountManagementDo);
        accountManagementDo.setChatbotAccountId(UUIDUtils.generateUUID());
        accountManagementDo.setAccountTypeCode(CSPOperatorCodeEnum.byName(accountManagementDo.getAccountType()).getCode());
        //一个客户同一运营商最多允许创建一个机器人
        if (accountManagementDao.selectCount(
                Wrappers.<AccountManagementDo>lambdaQuery()
                        .eq(AccountManagementDo::getCustomerId, saveReq.getCustomerId())
                        .notIn(AccountManagementDo::getChatbotStatus, CSPChatbotStatusEnum.STATUS_69_LOG_OFF.getCode(), CSPChatbotStatusEnum.STATUS_43_LOG_OFF.getCode())
                        .eq(AccountManagementDo::getAccountTypeCode, saveReq.getAccountTypeCode())
        ) > 0) {
            throw new BizException(AuthError.ADD_CHATBOT_FAIL);
        }
        String userId = SessionContextUtil.getUser().getUserId();
        String cspId = cspService.obtainCspId(userId);
        accountManagementDo.setCspId(cspId);
        if (!Objects.equals(CSPOperatorCodeEnum.CMCC.getCode(), accountManagementDo.getAccountTypeCode())) {
            if (saveReq.getIsAddOther() == 0)
                accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_68_ONLINE.getCode());
            else
                accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_60_NEW_WAIT_AUDIT.getCode());
        } else {
            accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_20_CRATE_AND_IS_AUDITING.getCode());
        }
        //删除redis中对应账号的token
        if (accountManagementDo.getAppId() != null && StringUtils.isNotEmpty(redisTemplate.opsForValue().get(accountManagementDo.getAppId()))) {
            redisTemplate.delete(accountManagementDo.getAppId());
        }
        // 直连设置supplier_tag为owner
        accountManagementDo.setSupplierTag(CSPChatbotSupplierTagEnum.OWNER.getValue());
        accountManagementDao.insert(accountManagementDo);
        if (accountManagementDo.getIsAddOther() == 0) {
            int code = sendZx(accountManagementDo, accountUrlConfigure.getCallbackUrl(), 0);
            if (code != 200) {
                throw new BizException(AuthError.VARIABLE_BAD_ACCOUNT);
            }
            redisTemplate.delete(accountManagementDo.getAppId());
        }
        //新增直连chatbot的时候, 默认 客户下的所有模板都去送审一遍
//        Runnable runnable = () -> {
//            log.info("找到这个客户{}下的所有非机器人模板, 并且添加直连模板审核记录(直接通过)", accountManagementDo.getCustomerId());
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                log.info("线程休眠异常");
//            }
//            List<Long> templateIds = messageTemplateService.getTemplateIdsByCustmerId(accountManagementDo.getCustomerId());
//            if (CollectionUtils.isNotEmpty(templateIds)) {
//                for (Long templateId : templateIds) {
//                    //给所有模板都添加一条审核通过的记录
//                    messageTemplateService.publicTemplate(templateId, accountManagementDo.getChatbotAccount());
//                }
//            }
//        };
//        threadPool.execute(runnable);
        // 创建硬核桃chatbot的时候，默认资费都为0
        if (Objects.equals(saveReq.getAccountTypeCode(), CSPOperatorCodeEnum.DEFAULT.getCode())) {
            RechargeTariffAdd rechargeTariffAdd = new RechargeTariffAdd();
            rechargeTariffAdd.setAccountId(accountManagementDo.getChatbotAccountId());
            rechargeTariffAdd.setAccountType(MsgTypeEnum.M5G_MSG.getCode());
            rechargeTariffAdd.setTextMsgPrice(0);
            rechargeTariffAdd.setRichMsgPrice(0);
            rechargeTariffAdd.setSessionMsgPrice(0);
            rechargeTariffAdd.setFallbackType(RechargeTariffFallbackTypeEnum.SINGLE_PRICE.getCode());
            rechargeTariffAdd.setFallbackSmsPrice(0);
            chargeTariffService.addRechargeTariff(rechargeTariffAdd);
        }
        return accountManagementDo;
    }


    /**
     * 修改本地机器人
     * 如果修改了chatbotAccount，需要修改原有的模板审核记录的chatbotAccount为新数据
     *
     * @param editReq
     * @param syncZx  是否需要同步中讯
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAccountManagement(AccountManagementEditReq editReq, Boolean syncZx) {
        if (StringUtils.isNotEmpty(editReq.getChatbotAccount())) {
            String accountId = editReq.getChatbotAccount().replace("sip:", "");
            editReq.setChatbotAccount(accountId);
        }
        Long id = editReq.getId();
        AccountManagementDo oldManagementDo = accountManagementDao.selectById(id);
        this.ensureChatbotInfoUnique(id, editReq.getChatbotAccount(), editReq.getAccountName());

        BaseUser user = SessionContextUtil.getUser();

        //检查机器人是否产生了计费，如果是则不允许修改机器人账号和归属客户
        boolean isChangeChatbotAccount = !Objects.equals(oldManagementDo.getChatbotAccount(), editReq.getChatbotAccount());
        if ((isChangeChatbotAccount)
                && this.hasBillingOccurred(cspCustomerApi.getByCustomerId(oldManagementDo.getCustomerId()), oldManagementDo.getChatbotAccount())) {
            throw new BizException("机器人已经产生计费信息，不允许修改账号！");
        }

        boolean updated = new LambdaUpdateChainWrapper<>(AccountManagementDo.class)
                .eq(AccountManagementDo::getId, id)
                .set(Objects.nonNull(editReq.getAccountName()), AccountManagementDo::getAccountName, editReq.getAccountName())
                .set(Objects.nonNull(editReq.getChatbotAccount()), AccountManagementDo::getChatbotAccount, editReq.getChatbotAccount())
                .set(Objects.nonNull(editReq.getAppId()), AccountManagementDo::getAppId, editReq.getAppId())
                .set(Objects.nonNull(editReq.getAppKey()), AccountManagementDo::getAppKey, editReq.getAppKey())
                .set(Objects.nonNull(editReq.getToken()), AccountManagementDo::getToken, editReq.getToken())
                .set(Objects.nonNull(editReq.getMessageAddress()), AccountManagementDo::getMessageAddress, editReq.getMessageAddress())
                .set(Objects.nonNull(editReq.getFileAddress()), AccountManagementDo::getFileAddress, editReq.getFileAddress())
                .set(Objects.nonNull(editReq.getChatbotStatus()), AccountManagementDo::getChatbotStatus, editReq.getChatbotStatus())
                .update(new AccountManagementDo()); //需要传个空对象 才会自动填充update属性
        if (!updated)
            throw new BizException(500, "机器人账号不存在");
        AccountManagementDo managementDo = accountManagementDao.selectById(id);
        //同步中讯
        if (syncZx) {
            int code = sendZx(managementDo, accountUrlConfigure.getCallbackUrl(), 0);
            if (code != 200) {
                throw new BizException(AuthError.VARIABLE_BAD_ACCOUNT);
            }
            //删除redis中对应账号的token
            if (null != redisTemplate.opsForValue().get(managementDo.getAppId())) {
                redisTemplate.delete(managementDo.getAppId());
            }
        }
        //如果修改了chatbotAccount
        if (!oldManagementDo.getChatbotAccount().equals(managementDo.getChatbotAccount())) {
            //1.修改原有的模板审核记录的chatbotAccount为新数据
            messageTemplateService.replaceChatbotAccount(managementDo.getChatbotAccount(), oldManagementDo.getChatbotAccount());
            //2.断开原chatbot所有素材的关联关系
            examineResultApi.updateMaterialFromChatbotUpdate(oldManagementDo.getChatbotAccount());
            //3.断开原chatbot所有场景的关联关系
            robotSceneNodeApi.removeBandingChatbotByChatbotAccount(oldManagementDo.getChatbotAccount());
        }
    }

    /**
     * 根据支付类型和机器人账号查询是否已经产生了计费信息
     *
     * @param user           payType 预付费: 有了订单则视为产生计费，后付费：有了消息记录视为产生计费
     * @param chatbotAccount 机器人账号
     */
    private boolean hasBillingOccurred(UserInfoVo user, String chatbotAccount) {
        boolean hasBillingOccurred = false;
        switch (user.getPayType()) {
            case PREPAY:
                hasBillingOccurred = prepaymentOrderService.existsOrderByMsgTypeAndAccountId(user.getCustomerId(), MsgTypeEnum.M5G_MSG, chatbotAccount);
                break;
            case POSTPAY:
                hasBillingOccurred = postpayOrderService.existsMsgRecordByMsgTypeAndAccountId(user.getCustomerId(), MsgTypeEnum.M5G_MSG, chatbotAccount);
                break;
        }
        return hasBillingOccurred;
    }


    @Override
    public int delAccountManagementById(String chatbotAccountId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("chatbot_account_id", chatbotAccountId);
        map.put("deleted", 1);
        map.put("deleteTime", DateUtil.date());
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(Wrappers.<AccountManagementDo>lambdaQuery()
                .eq(AccountManagementDo::getDeleted, 0)
                .eq(AccountManagementDo::getChatbotAccountId, chatbotAccountId));
        if (accountManagementDo != null) {
            //同步中讯
            //int code = 200;
            int code = sendZx(accountManagementDo, accountUrlConfigure.getCallbackUrl(), 1);
            if (code != 200) {
                throw new BizException(AuthError.VARIABLE_BAD_ACCOUNT);
            }
            //删除chatbot对应的模板的审核记录等
            messageTemplateService.cancelAudit(accountManagementDo.getChatbotAccount());
        }
        return accountManagementDao.delAccountManagementById(map);
    }

    @Override
    public AccountManagementResp getAccountManagementById(String chatbotAccountId) {
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(AccountManagementDo::getChatbotAccountId, chatbotAccountId);
        if (accountManagementDo == null) {
            return null;
        }
        return BeanUtil.copyProperties(accountManagementDo, AccountManagementResp.class);
    }

    @Override
    public AccountManagementResp getAccountManagementById(Long id) {
        LambdaQueryWrapper<AccountManagementDo> accountManagementDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        accountManagementDoLambdaQueryWrapper.eq(AccountManagementDo::getId, id);
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(accountManagementDoLambdaQueryWrapper);
        if (ObjectUtil.isNull(accountManagementDo)) return null;
        return BeanUtil.copyProperties(accountManagementDo, AccountManagementResp.class);
    }

    @Override
    public AccountManagementResp getAccountManagementByAccountId(String accountId) {
        LambdaQueryWrapperX<AccountManagementDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(AccountManagementDo::getChatbotAccount, accountId)
                .eq(AccountManagementDo::getDeleted, 0);
        AccountManagementDo accountManagementDos = accountManagementDao.selectOne(queryWrapperX);
        if (Objects.nonNull(accountManagementDos)) {
            AccountManagementResp accountManagementResp = new AccountManagementResp();
            BeanUtil.copyProperties(accountManagementDos, accountManagementResp);
            return accountManagementResp;
        }
        //这个地方可能引起bug
        return new AccountManagementResp();
    }

    @Override
    public AccountManagementResp getAccountManagementByChatbotAccountId(String chatbotAccountId) {
        LambdaQueryWrapperX<AccountManagementDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(AccountManagementDo::getChatbotAccountId, chatbotAccountId)
                .eq(AccountManagementDo::getDeleted, 0);
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(queryWrapperX);
        if (accountManagementDo == null) {
            return null;
        }
        return BeanUtil.copyProperties(accountManagementDo, AccountManagementResp.class);
    }

    @Override
    public List<AccountManagementResp> getAccountManagementlist(String creator) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        if (!StringUtils.equals(creator, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("customer_id", creator);
        }
        wrapper.orderByDesc("create_time");
        List<AccountManagementDo> accountManagementDos = accountManagementDao.selectList(wrapper);
        List<AccountManagementResp> accountManagementResps = BeanUtil.copyToList(accountManagementDos, AccountManagementResp.class);
        return accountManagementResps;
    }


    @Override
    public AccountManagementResp getAccountManagementByAccountType(AccountManagementTypeReq accountManagementTypeReq) {
        LambdaQueryWrapper<AccountManagementDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountManagementDo::getCustomerId, accountManagementTypeReq.getCreator());
        queryWrapper.eq(AccountManagementDo::getDeleted, 0);
        if (StrUtil.isNotEmpty(accountManagementTypeReq.getAccountType())) {
            queryWrapper.eq(AccountManagementDo::getAccountType, accountManagementTypeReq.getAccountType());
        }
        if (StrUtil.isNotEmpty(accountManagementTypeReq.getChatbotAccount())) {
            queryWrapper.eq(AccountManagementDo::getChatbotAccount, accountManagementTypeReq.getChatbotAccount());
        }
        if (StrUtil.isNotEmpty(accountManagementTypeReq.getSupplier_tag())) {
            queryWrapper.eq(AccountManagementDo::getSupplierTag, accountManagementTypeReq.getSupplier_tag());
        }
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(queryWrapper);
        return BeanUtil.copyProperties(accountManagementDo, AccountManagementResp.class);
    }

    @Override
    public List<AccountManagementResp> getAccountManagementByAccountTypes(AccountManagementTypeReq accountManagementTypeReq) {
//        if (StrUtil.isEmpty(accountManagementTypeReq.getAccountType())) {
//            throw new BizException("运营商类型为空");
//        }
        LambdaQueryWrapper<AccountManagementDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountManagementDo::getCustomerId, accountManagementTypeReq.getCreator());
        queryWrapper.eq(AccountManagementDo::getDeleted, 0);
        if (StrUtil.isNotEmpty(accountManagementTypeReq.getAccountType())) {
            queryWrapper.in(AccountManagementDo::getAccountType, Arrays.asList(accountManagementTypeReq.getAccountType().split(",")));
        }

        if (StrUtil.isNotEmpty(accountManagementTypeReq.getChatbotName())) {
            queryWrapper.like(AccountManagementDo::getAccountName, accountManagementTypeReq.getChatbotName());
        }

        List<AccountManagementDo> accountManagementDo = accountManagementDao.selectList(queryWrapper);
        return BeanUtil.copyToList(accountManagementDo, AccountManagementResp.class);
    }

    @Override
    public AccountManagementResp getAccountManagementByAccountTypeAndSupplier(AccountManagementTypeReq accountManagementTypeReq) {
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(Wrappers.<AccountManagementDo>lambdaQuery()
                .eq(AccountManagementDo::getAccountType, accountManagementTypeReq.getAccountType())
                .eq(AccountManagementDo::getCustomerId, accountManagementTypeReq.getCreator())
                .eq(AccountManagementDo::getSupplierTag, accountManagementTypeReq.getSupplier_tag())
                .eq(AccountManagementDo::getDeleted, 0));
        return BeanUtil.copyProperties(accountManagementDo, AccountManagementResp.class);
    }

    @Override
    public long selectCountAll() {
        return accountManagementDao.selectCount();
    }

    @Override
    public List<AccountManagementResp> getListByChatbotAccounts(AccountChatbotAccountQueryReq queryReq) {
        List<AccountManagementResp> res = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(queryReq.getChatbotAccountList())) {
            LambdaQueryWrapperX<AccountManagementDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.in(AccountManagementDo::getChatbotAccount, queryReq.getChatbotAccountList());
            queryWrapperX.eq(AccountManagementDo::getCustomerId, queryReq.getCreator());
            queryWrapperX.eq(AccountManagementDo::getDeleted, 0);
            List<AccountManagementDo> accountManagementDoList = accountManagementDao.selectList(queryWrapperX);
            accountManagementDoList = accountManagementDoList.stream().filter(item -> {
                AccountManagementTreeResp accountManagementTreeResp = BeanUtil.copyProperties(item, AccountManagementTreeResp.class);
                accountManagementTreeResp.setState(item.getChatbotStatus());
                return isAvailableChanel(accountManagementTreeResp);
            }).collect(Collectors.toList());
            res = BeanUtil.copyToList(accountManagementDoList, AccountManagementResp.class);
        }
        return res;
    }

    @Override
    public List<AccountManagementResp> getListByCreators(List<String> creators) {
        List<AccountManagementResp> res = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(creators)) {
            LambdaQueryWrapperX<AccountManagementDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.in(AccountManagementDo::getCreator, creators);
            List<AccountManagementDo> accountManagementDoList = accountManagementDao.selectList(queryWrapperX);
            if (CollectionUtils.isNotEmpty(accountManagementDoList)) {
                res = BeanUtil.copyToList(accountManagementDoList, AccountManagementResp.class);
                if (!res.isEmpty()) {
                    for (AccountManagementResp accountManagementResp : res) {
                        if (StringUtils.isNotBlank(accountManagementResp.getCreator())) {
                            LambdaQueryWrapperX<UserEnterpriseIdentificationDo> enterpriseWrapper = new LambdaQueryWrapperX<>();
                            enterpriseWrapper.eq(UserEnterpriseIdentificationDo::getUserId, accountManagementResp.getCreator());
                            UserEnterpriseIdentificationDo enterpriseIdentificationDo = userEnterpriseIdentificationDao.selectOne(enterpriseWrapper);
                            if (enterpriseIdentificationDo != null) {
                                accountManagementResp.setEnterpriseName(enterpriseIdentificationDo.getEnterpriseName());
                            }
                        }
                    }
                }
            } else {
                return null;
            }
        }
        return res;
    }


    /**
     * 通过creator寻找chatBot状态是否已下线
     *
     * @param accountManagementTypeReq 请求体
     * @return 状态
     */
    @Override
    public Boolean checkChatBotStatus(AccountManagementTypeReq accountManagementTypeReq) {
        String creator = accountManagementTypeReq.getCreator();
        String accountType = accountManagementTypeReq.getAccountType();
        List<String> operators = Arrays.asList(accountType.split(","));
        LambdaQueryWrapper<AccountManagementDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountManagementDo::getCreator, creator);
        wrapper.in(AccountManagementDo::getAccountType, operators);
        List<AccountManagementDo> list = accountManagementDao.selectList(wrapper);
        //当前账号下账号的状态
        List<Integer> status = list.stream().map(AccountManagementDo::getChatbotStatus).collect(Collectors.toList());
        //如果状态中包含下线就false
        return !status.contains(31) || !status.contains(42);
    }

    @Override
    public void updateChatbotWhenGetAuditResult(AccountManagementDo accountManagementDo) {
        int code = sendZx(accountManagementDo, accountUrlConfigure.getCallbackUrl(), 0);
        if (code != 200) {
            throw new BizException(AuthError.VARIABLE_BAD_ACCOUNT);
        }
        //删除redis中对应账号的token
        if (null != redisTemplate.opsForValue().get(accountManagementDo.getAppId())) {
            redisTemplate.delete(accountManagementDo.getAppId());
        }
    }

    @Override
    @SuppressWarnings("all")
    public List<String> getChatbotAccountIdsByCustomerId(String customerId) {
        return accountManagementDao.selectList(
                        Wrappers.<AccountManagementDo>lambdaQuery()
                                .eq(AccountManagementDo::getCustomerId, customerId)
                                .select(AccountManagementDo::getChatbotAccountId)
                )
                .stream()
                .map(AccountManagementDo::getChatbotAccountId)
                .collect(Collectors.toList());
    }

    /**
     * @param channelAvailability 通道可用性
     * @return 用户机器人账号列表
     */
    @Override
    public List<AccountManagementOptionVo> getAllAccountManagement(Boolean channelAvailability) {
        String customerId = SessionContextUtil.getUser().getUserId();
        boolean isSuperAdmin = StringUtils.equals(customerId, superAdministratorUserIdConfigure.getSuperAdministrator());
        List<AccountManagementTreeResp> allChatbot = accountManagementMapper.getAllChatbot(isSuperAdmin ? null : customerId);
        Map<String, List<AccountManagementTreeResp>> accountManagementGroupMap = allChatbot.stream()
                .filter(chatbot -> !Boolean.TRUE.equals(channelAvailability) || isAvailableChanel(chatbot))
                .collect(Collectors.groupingBy(AccountManagementTreeResp::getAccountType));
        ArrayList<AccountManagementOptionVo> result = new ArrayList<>();
        for (Map.Entry<String, List<AccountManagementTreeResp>> entry : accountManagementGroupMap.entrySet()) {
            AccountManagementOptionVo optionVo = new AccountManagementOptionVo();
            optionVo.setId(entry.getKey());
            optionVo.setAccountName(entry.getKey());
            optionVo.setOptions(entry.getValue());
            result.add(optionVo);
        }
        return result;
    }

    @Override
    public List<AccountManagementOptionVo> getAccountManagementByIdIncludeDeleted(Boolean channelAvailability) {
        String customerId = SessionContextUtil.getUser().getUserId();
        boolean isSuperAdmin = StringUtils.equals(customerId, superAdministratorUserIdConfigure.getSuperAdministrator());
        List<AccountManagementTreeResp> allChatbot = accountManagementMapper.getAllChatbotIncludeDeleted(isSuperAdmin ? null : customerId);
        Map<String, List<AccountManagementTreeResp>> accountManagementGroupMap = allChatbot.stream()
                .filter(chatbot -> !Boolean.TRUE.equals(channelAvailability) || isAvailableChanel(chatbot))
                .collect(Collectors.groupingBy(AccountManagementTreeResp::getAccountType));
        ArrayList<AccountManagementOptionVo> result = new ArrayList<>();
        for (Map.Entry<String, List<AccountManagementTreeResp>> entry : accountManagementGroupMap.entrySet()) {
            AccountManagementOptionVo optionVo = new AccountManagementOptionVo();
            optionVo.setId(entry.getKey());
            optionVo.setAccountName(entry.getKey());
            optionVo.setOptions(entry.getValue());
            result.add(optionVo);
        }
        return result;
    }

    @Override
    public List<AccountManagementOptionVo> getProvedTreeList(@RequestBody AccountManagementForProvedTreeReq accountManagementForProvedTreeReq) {
        LambdaQueryWrapper<MessageTemplateAuditDo> queryWrapper = new LambdaQueryWrapper<>();

        String customerId = SessionContextUtil.getUser().getUserId();
        boolean isSuperAdmin = StringUtils.equals(customerId, superAdministratorUserIdConfigure.getSuperAdministrator());
        if (!isSuperAdmin) {
            queryWrapper.eq(MessageTemplateAuditDo::getCreator, customerId);
        }
        queryWrapper.eq(MessageTemplateAuditDo::getTemplateId, accountManagementForProvedTreeReq.getTemplateId());
        queryWrapper.eq(MessageTemplateAuditDo::getStatus, Constants.TEMPLATE_STATUS_SUCCESS);
        List<MessageTemplateAuditDo> messageTemplateAuditDos = messageTemplateAuditDao.selectList(queryWrapper);
        List<String> chatbotAccounts = messageTemplateAuditDos.stream().map(MessageTemplateAuditDo::getChatbotAccount).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(chatbotAccounts)) {
            return new ArrayList<>();
        }
        List<AccountManagementTreeResp> allChatbot = accountManagementMapper.getProvedChatbot(isSuperAdmin ? null : customerId, chatbotAccounts);
        Map<String, List<AccountManagementTreeResp>> accountManagementGroupMap = allChatbot.stream()
                .filter(chatbot -> 1 != accountManagementForProvedTreeReq.getIsHide() || isAvailableChanel(chatbot))
                .collect(Collectors.groupingBy(AccountManagementTreeResp::getAccountType));
        ArrayList<AccountManagementOptionVo> result = new ArrayList<>();
        for (Map.Entry<String, List<AccountManagementTreeResp>> entry : accountManagementGroupMap.entrySet()) {
            AccountManagementOptionVo optionVo = new AccountManagementOptionVo();
            optionVo.setId(entry.getKey());
            optionVo.setAccountName(entry.getKey());
            optionVo.setOptions(entry.getValue());
            result.add(optionVo);
        }
        return result;
    }

    @Override
    public List<AccountManagementResp> getChatbotAccountInfoByCustomerId(String customerId) {
        List<AccountManagementResp> accountManagementResps = new ArrayList<>();
        if (!Strings.isNullOrEmpty(customerId)) {
            LambdaQueryWrapper<AccountManagementDo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AccountManagementDo::getCustomerId, customerId);
            wrapper.eq(AccountManagementDo::getDeleted, 0);
            List<AccountManagementDo> accountManagementDos = accountManagementDao.selectList(wrapper);
            if (!org.springframework.util.CollectionUtils.isEmpty(accountManagementDos)) {
                AccountManagementResp resp;
                for (AccountManagementDo item : accountManagementDos) {
                    resp = new AccountManagementResp();
                    BeanUtils.copyProperties(item, resp);
                    accountManagementResps.add(resp);
                }
            }
        }
        return accountManagementResps;
    }

    @Override
    public List<AccountManagementResp> getListByChatbotAccountList(List<String> chatbotAccountList) {
        List<AccountManagementResp> res = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(chatbotAccountList)) {
            LambdaQueryWrapperX<AccountManagementDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.in(AccountManagementDo::getChatbotAccount, chatbotAccountList);
            List<AccountManagementDo> accountManagementDoList = accountManagementDao.selectList(queryWrapperX);
            res = BeanUtil.copyToList(accountManagementDoList, AccountManagementResp.class);
        }
        return res;
    }

    @Override
    public List<AccountManagementResp> getListByChatbotAccountIdList(List<String> chatbotAccountIdList) {
        List<AccountManagementResp> res = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(chatbotAccountIdList)) {
            LambdaQueryWrapperX<AccountManagementDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.in(AccountManagementDo::getChatbotAccountId, chatbotAccountIdList);
            List<AccountManagementDo> accountManagementDoList = accountManagementDao.selectList(queryWrapperX);
            res = BeanUtil.copyToList(accountManagementDoList, AccountManagementResp.class);
        }
        return res;
    }

    @Override
    public PageResult<FifthMessageAccountListVo> selectFifthMessageAccountByCustomer(MessageAccountSearchVo searchVo) {
        Page<FifthMessageAccountListVo> page = new Page<>(searchVo.getPageNo(), searchVo.getPageSize());
        page.setOrders(OrderItem.descs("create_time"));
        accountManagementMapper.selectAccountByCustomerId(SessionContextUtil.getLoginUser().getUserId(), page);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public FifthAccountVo queryFifthAccount(String chatbotAccount) {
        AccountManagementDo accountManagementDo = accountManagementMapper.selectOne(
                Wrappers.<AccountManagementDo>lambdaQuery()
                        .eq(AccountManagementDo::getChatbotAccount, chatbotAccount)
                        .eq(AccountManagementDo::getCustomerId, SessionContextUtil.getLoginUser().getUserId())
        );
        return new FifthAccountVo()
                .setAccountName(accountManagementDo.getAccountName())
                .setStatus(accountManagementDo.getChatbotStatus())
                .setChatbotAccount(accountManagementDo.getChatbotAccount())
                .setChatbotAccountId(accountManagementDo.getChatbotAccountId())
                .setAppId(accountManagementDo.getAppId())
                .setAppKey(accountManagementDo.getAppKey())
                .setToken(accountManagementDo.getToken());
    }

    /**
     * 返回机器人通道是否可用，可用返回true.
     * 1. 本地添加机器人通道始终可用
     * 2. 线上流程添加的机器人根据运营商类型按如下规则判断
     * - 移动：当状态为上架审核中、上架审核不通过、在线、调试时，表示运营商通道可用
     * - 电信、联通：上线、测试，表示可用
     *
     * @param chatbot
     * @return
     */
    private static boolean isAvailableChanel(AccountManagementTreeResp chatbot) {
        Assert.notNull(chatbot, "需要验证通道可用性的机器人不能为空");
        //软通禅道bug11914 模板送审时下线，已注销chatbot 不显示
//        if (0 == chatbot.getIsAddOther()) {
//            return true;
//        }
        CSPOperatorCodeEnum operatorCodeEnum = CSPOperatorCodeEnum.byName(chatbot.getAccountType());
        CSPChatbotStatusEnum cspChatbotStatusEnum = CSPChatbotStatusEnum.byCode(
                Optional.ofNullable(chatbot.getActualState())
                        .orElse(chatbot.getState())
        );
        if (operatorCodeEnum == null || cspChatbotStatusEnum == null)
            return false;
        switch (operatorCodeEnum) {
            case CMCC:
                return cspChatbotStatusEnum == STATUS_24_SHELVE_AND_IS_AUDITING
                        || cspChatbotStatusEnum == STATUS_25_SHELVE_AND_AUDIT_NOT_PASS
                        || cspChatbotStatusEnum == STATUS_30_ONLINE
                        || cspChatbotStatusEnum == STATUS_50_DEBUG
                        || cspChatbotStatusEnum == STATUS_31_OFFLINE
                        || cspChatbotStatusEnum == STATUS_71_OFFLINE;
            case CUNC:
            case CT:
            case DEFAULT:
                return cspChatbotStatusEnum == STATUS_68_ONLINE
                        || cspChatbotStatusEnum == STATUS_70_TEST
                        || cspChatbotStatusEnum == STATUS_31_OFFLINE
                        || cspChatbotStatusEnum == STATUS_71_OFFLINE;
            default:
                return false;
        }
    }


    /**
     * 查询数据库确保机器人信息唯一
     * 注：此处不能保证最终一致性，如果要保证绝对唯一应该使用数据库唯一索引做为兜底（没加）
     *
     * @param excludeId      不在校验集合里的机器人ID
     * @param chatbotAccount 机器人账号
     * @param accountName    账号名称
     * @throws BizException 当唯一性校验不通过时
     */
    private void ensureChatbotInfoUnique(Long excludeId, String chatbotAccount, String accountName) {
        if (StringUtils.isEmpty(chatbotAccount) && StringUtils.isEmpty(accountName))
            return;
        if (accountManagementDao.selectCount(
                Wrappers.<AccountManagementDo>lambdaQuery()
                        .ne(Objects.nonNull(excludeId), AccountManagementDo::getId, excludeId)
                        .and(qw -> qw.eq(StringUtils.isNotEmpty(chatbotAccount), AccountManagementDo::getChatbotAccount, chatbotAccount)
                                .or()
                                .eq(StringUtils.isNotEmpty(accountName), AccountManagementDo::getAccountName, accountName)
                        )) > 0) {
            throw new BizException(500, "机器人账号或账号名称重复");
        }
    }


    /**
     * @param accountManagementDo
     * @param callBack
     * @param state               0正常 1禁用
     * @return
     */
    private int sendZx(AccountManagementDo accountManagementDo, String callBack, int state) {
        String walnut;
        String accountid = accountManagementDo.getChatbotAccount();
        switch (accountManagementDo.getAccountType()) {
            case "联通":
                walnut = "unicom";
                if (accountid.contains("sip:") && accountid.contains("@")) {
                    String[] split = accountid.split("sip:");
                    String[] split1 = split[1].split("@");
                    accountid = split1[0];
                }
                break;
            case "电信":
                walnut = "telecom";
                if (accountid.contains("@")) {
                    String[] split1 = accountid.split("@");
                    accountid = split1[0];
                }
                break;
            case "移动":
                walnut = "cmcc";
                if (accountid.contains("@")) {
                    String[] split1 = accountid.split("@");
                    accountid = split1[0];
                }
                break;
            default:
                walnut = "walnut";
                if (accountid.contains("@")) {
                    String[] split1 = accountid.split("@");
                    accountid = split1[0];
                }
                break;
        }
        JSONObject paramMap = new JSONObject();
        paramMap.put("name", accountManagementDo.getAccountName());
        paramMap.put("state", state);
        paramMap.put("chatbotId", accountid);
        paramMap.put("appId", accountManagementDo.getAppId());
        paramMap.put("appKey", accountManagementDo.getAppKey());
        paramMap.put("chatbotToken", accountManagementDo.getToken());
        paramMap.put("callbackUrl", callBack);
        paramMap.put("carrier", walnut);
        paramMap.put("messageUrl", accountManagementDo.getMessageAddress());
        paramMap.put("mediaUrl", accountManagementDo.getFileAddress());
        String timestamp = DateUtil.format(DateUtil.date(), "yyyyMMddhhmmss");
        String sign = getSign(timestamp);
        try (CloseableHttpClient client = createSSLClientDefault()) {
            log.info("账户同步中讯");
            URIBuilder uriBuilder = new URIBuilder(zhongXunConfigure.getUrl());
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            String jsonString = paramMap.toJSONString();
            log.info("请求体数据为： {}", jsonString);
            httpPost.addHeader("sign", sign);
            httpPost.addHeader("accessKey", zhongXunConfigure.getAccessKey());
            httpPost.addHeader("timestamp", timestamp);
            HttpEntity entity = new StringEntity(jsonString, ContentType.create("application/json", "utf-8"));
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            String resultString = EntityUtils.toString(responseEntity);
            log.info("发送至网关的结果为  " + resultString);
            JSONObject jsonObject2 = JSONObject.parseObject(resultString);
            return jsonObject2.getInteger("code");
        } catch (Exception e) {
            log.error("账户同步中讯失败======{}", e.getMessage(), e);
        }
        return 0;
    }


    /**
     * 获取同步中讯sgin
     */
    private String getSign(String timestamp) {
        String signs = zhongXunConfigure.getAccessKey() + zhongXunConfigure.getAccessSecret() + timestamp;
        byte[] bytes = signs.getBytes(Charsets.UTF_8);
        byte[] digest = MD5.create().digest(bytes);
        return Base64Encoder.encode(digest);
    }

    /**
     * 获取https客户端
     *
     * @return https客户端
     */
    private CloseableHttpClient createSSLClientDefault() {
        try {
            X509TrustManager x509mgr = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509mgr}, null);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            throw new BizException(500, "创建https客户端失败");
        }
    }
}
