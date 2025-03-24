package com.citc.nce.auth.csp.readingLetter.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.accountmanagement.service.AccountManagementService;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.common.CSPChatbotSupplierTagEnum;
import com.citc.nce.auth.configure.ZhongXunConfigure;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.csp.common.CspReadingLetterAccountStatusEnum;
import com.citc.nce.auth.csp.readingLetter.dao.CspReadingLetterAccountDao;
import com.citc.nce.auth.csp.readingLetter.entity.CspReadingLetterAccountDo;
import com.citc.nce.auth.csp.readingLetter.service.CspReadingLetterAccountService;
import com.citc.nce.auth.csp.readingLetter.vo.CspReadingLetterAccountEditReq;
import com.citc.nce.auth.csp.readingLetter.vo.CspReadingLetterAccountListResp;
import com.citc.nce.auth.csp.readingLetter.vo.CspReadingLetterAccountNameCheckReq;
import com.citc.nce.auth.csp.readingLetter.vo.CspReadingLetterAccountSaveReq;
import com.citc.nce.auth.csp.readingLetter.vo.CspReadingLetterAccountUpdateStatusReq;
import com.citc.nce.auth.csp.readingLetter.vo.CspReadingLetterCustomerCheckReq;
import com.citc.nce.auth.csp.readingLetter.vo.CspReadingLetterDetailResp;
import com.citc.nce.auth.csp.readingLetter.vo.CspReadingLetterIdReq;
import com.citc.nce.auth.csp.readingLetter.vo.CspReadingLetterMsgResp;
import com.citc.nce.auth.csp.readingLetter.vo.CustomerReadingLetterAccountListVo;
import com.citc.nce.auth.csp.readingLetter.vo.CustomerReadingLetterAccountSearchReq;
import com.citc.nce.auth.csp.readingLetter.vo.CustomerReadingLetterAccountVo;
import com.citc.nce.auth.csp.recharge.dao.ChargeTariffDao;
import com.citc.nce.auth.csp.recharge.entity.ChargeTariffDo;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.auth.readingLetter.template.enums.SmsTypeEnum;
import com.citc.nce.auth.readingLetter.template.service.ReadingLetterTemplateAuditService;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterAuditDeleteReq;
import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.auth.vo.SyncAuthInfoModel;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.RsaUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.util.UUIDUtils;
import com.citc.nce.conf.CommonKeyPairConfig;
import com.citc.nce.filecenter.platform.vo.UpReceiveData;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.readingLetter.ReadingLetterParseRecordApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.citc.nce.auth.utils.HttpsUtil.createSSLClientDefault;

@Slf4j
@Service
public class CspReadingLetterAccountServiceImpl implements CspReadingLetterAccountService {

    private static final String ACCOUNT_ID_PREFIX = "YX";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Resource
    CspReadingLetterAccountDao cspReadingLetterAccountDao;
    @Resource
    ChargeTariffDao chargeTariffDao;
    //查询5G阅信账号(即蜂动账号)
    @Resource
    AccountManagementService accountManagementService;
    @Resource
    CspApi cspApi;
    @Resource
    private CommonKeyPairConfig keyPairConfig;
    @Resource
    private ReadingLetterParseRecordApi readingLetterParseRecordApi;
    @Resource
    private AdminAuthApi adminAuthApi;
    @Value("${gateway.callback.fontdo}")
    private String callbackUrl;
    @Resource
    private ZhongXunConfigure zhongXunConfigure;
    @Resource
    private ReadingLetterTemplateAuditService readingLetterTemplateAuditService;

    @Override
    public PageResult<CspReadingLetterAccountListResp> queryList(MessageAccountSearchVo req) {
        Page<CspReadingLetterAccountListResp> page = new Page<>(req.getPageNo(), req.getPageSize());
        String cspId = null, customerId = null;
        BaseUser user = SessionContextUtil.getUser();
        if (user != null) {
            cspId = user.getCspId();
            if (user.getIsCustomer())
                customerId = user.getUserId();
        }
        cspReadingLetterAccountDao.selectAccount(cspId, customerId, req.getName(), req.getStatus(), req.getAccountName(), page);
        List<CspReadingLetterAccountListResp> list = page.getRecords().stream().peek(record -> record.setOperatorName(
                CSPOperatorCodeEnum.byCode(record.getOperator()).getName()
        )).collect(Collectors.toList());
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public List<CspReadingLetterDetailResp> queryAllAccountsOfCustomer(Integer status) {
        String customerId = SessionContextUtil.getUser().getUserId();
        LambdaQueryWrapper<CspReadingLetterAccountDo> wrapper = new LambdaQueryWrapper<CspReadingLetterAccountDo>()
                .eq(CspReadingLetterAccountDo::getCustomerId, customerId)
                .eq(Objects.nonNull(status), CspReadingLetterAccountDo::getStatus, status);


        List<CspReadingLetterAccountDo> readingLetterPlusAccountDos = cspReadingLetterAccountDao.selectList(wrapper);
        List<CspReadingLetterDetailResp> cspReadingLetterDetailResp = new ArrayList<>();
        for (CspReadingLetterAccountDo accountDo : readingLetterPlusAccountDos) {
            CspReadingLetterDetailResp resp = new CspReadingLetterDetailResp();
            BeanUtils.copyProperties(accountDo, resp);
            resp.setAppKey(RsaUtil.decryptByPrivateKey(keyPairConfig.getPrivateKey(), accountDo.getAppKey()));
            cspReadingLetterDetailResp.add(resp);
        }

        return cspReadingLetterDetailResp;
    }

    @Override
    public Boolean save(CspReadingLetterAccountSaveReq req) {
        // 验证customerId
        String cspId = cspApi.queryCspId(SessionContextUtil.getUser().getUserId());
        if (!req.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
        // 验证accountName不能重复
        Boolean nameUnique = checkNameUnique(null, cspId, req.getAccountName());
        if (nameUnique) {
            throw new BizException(AuthError.ACCOUNT_NAME_IS_EXISTS);
        }

        // 验证customer和operator
        Boolean unique = checkCustomerOperatorUnique(null, req.getCustomerId(), req.getOperator());
        if (unique) {
            throw new BizException(AuthError.CUSTOMER_OPERATOR_ERROR);
        }
        CspReadingLetterAccountDo cspReadingLetterAccountDo = new CspReadingLetterAccountDo();
        BeanUtils.copyProperties(req, cspReadingLetterAccountDo);
        cspReadingLetterAccountDo.setAccountId(generateAccountId());
        cspReadingLetterAccountDo.setCspId(cspId);
        cspReadingLetterAccountDo.setAppKey(RsaUtil.encryptByPublicKey(keyPairConfig.getPublicKey(), req.getAppKey()));

        readingLetterParseRecordApi.createTable(cspId);

        cspReadingLetterAccountDao.insert(cspReadingLetterAccountDo);

        syncYxPlusInfoToGateway(cspReadingLetterAccountDo);
        return true;
    }

    @Override
    @Transactional
    public Boolean edit(CspReadingLetterAccountEditReq req) {
        // 验证customerId
        String cspId = cspApi.queryCspId(SessionContextUtil.getUser().getUserId());
        // 验证accountName不能重复
        Boolean nameUnique = checkNameUnique(req.getAccountId(), cspId, req.getAccountName());
        if (nameUnique) {
            throw new BizException(AuthError.ACCOUNT_NAME_IS_EXISTS);
        }

        CspReadingLetterAccountDo cspReadingLetterAccountDo = getAccountDo(req.getAccountId(), cspId);
        if (!cspReadingLetterAccountDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
        // 验证状态 禁用状态才允许编辑
        if (!Objects.equals(cspReadingLetterAccountDo.getStatus(), CspReadingLetterAccountStatusEnum.DISABLE.getValue())) {
            throw new BizException("禁用状态的账号才允许编辑");
        }
        // 验证customer和operator
        Boolean unique = checkCustomerOperatorUnique(req.getAccountId(), cspReadingLetterAccountDo.getCustomerId(), cspReadingLetterAccountDo.getOperator());
        if (unique) {
            throw new BizException(AuthError.CUSTOMER_OPERATOR_ERROR);
        }
        BeanUtils.copyProperties(req, cspReadingLetterAccountDo);
        cspReadingLetterAccountDo.setAppKey(RsaUtil.encryptByPublicKey(keyPairConfig.getPublicKey(), req.getAppKey()));
        cspReadingLetterAccountDao.updateById(cspReadingLetterAccountDo);

        syncYxPlusInfoToGateway(cspReadingLetterAccountDo);
        return true;
    }

    @Override
    public int updateStatus(CspReadingLetterAccountUpdateStatusReq req) {
        String cspId = cspApi.queryCspId(SessionContextUtil.getUser().getUserId());
        LambdaUpdateWrapper<CspReadingLetterAccountDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CspReadingLetterAccountDo::getAccountId, req.getAccountId());
        updateWrapper.eq(CspReadingLetterAccountDo::getCspId, cspId);
        CspReadingLetterAccountDo cspReadingLetterAccountDo = cspReadingLetterAccountDao.selectOne(updateWrapper);
        if (Objects.isNull(cspReadingLetterAccountDo)) {
            throw new BizException("账号不存在");
        }
        if (!cspReadingLetterAccountDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
        updateWrapper.set(CspReadingLetterAccountDo::getStatus, req.getStatus())
                .set(CspReadingLetterAccountDo::getUpdateTime, new Date());
        return cspReadingLetterAccountDao.update(new CspReadingLetterAccountDo(), updateWrapper);
    }

    @Override
    public int delete(CspReadingLetterIdReq req) {
        String cspId = cspApi.queryCspId(SessionContextUtil.getUser().getUserId());
        LambdaUpdateWrapper<CspReadingLetterAccountDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CspReadingLetterAccountDo::getAccountId, req.getAccountId());
        updateWrapper.eq(CspReadingLetterAccountDo::getCspId, cspId);
        CspReadingLetterAccountDo cspReadingLetterAccountDo = cspReadingLetterAccountDao.selectOne(updateWrapper);
        if (Objects.isNull(cspReadingLetterAccountDo)) {
            throw new BizException("账号不存在");
        }
        if (!cspReadingLetterAccountDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }

        updateWrapper.set(CspReadingLetterAccountDo::getDeleted, 1);
        int delete = cspReadingLetterAccountDao.delete(updateWrapper);

        ReadingLetterAuditDeleteReq readingLetterAuditDeleteReq = new ReadingLetterAuditDeleteReq();
        readingLetterAuditDeleteReq.setAppId(cspReadingLetterAccountDo.getAppId());
        readingLetterAuditDeleteReq.setAppKey(RsaUtil.decryptByPrivateKey(keyPairConfig.getPrivateKey(), cspReadingLetterAccountDo.getAppKey()));
        readingLetterAuditDeleteReq.setAgentId(cspReadingLetterAccountDo.getAgentId());
        readingLetterAuditDeleteReq.setAccountId(cspReadingLetterAccountDo.getAccountId());
        readingLetterAuditDeleteReq.setSmsType(SmsTypeEnum.READING_LETTER_PLUS.getCode());
        readingLetterTemplateAuditService.deleteAuditAndProvedByAccount(readingLetterAuditDeleteReq);
        return delete;
    }

    @Override
    public CspReadingLetterDetailResp queryDetail(CspReadingLetterIdReq req) {
        String cspId = cspApi.queryCspId(SessionContextUtil.getUser().getUserId());
        CspReadingLetterAccountDo accountDo = getAccountDo(req.getAccountId(), cspId);
        CspReadingLetterDetailResp resp = new CspReadingLetterDetailResp();
        BeanUtils.copyProperties(accountDo, resp);
        resp.setAppKey(RsaUtil.decryptByPrivateKey(keyPairConfig.getPrivateKey(), accountDo.getAppKey()));
        return resp;
    }


    @Override
    public CspReadingLetterMsgResp customerCheck(CspReadingLetterCustomerCheckReq req) {
        String cspId = cspApi.queryCspId(SessionContextUtil.getUser().getUserId());
        if (!req.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
        CspReadingLetterMsgResp resp = new CspReadingLetterMsgResp();
        Boolean unique = checkCustomerOperatorUnique(req.getAccountId(), req.getCustomerId(), req.getOperator());
        resp.setErrMsg(unique ? AuthError.CUSTOMER_OPERATOR_ERROR.getMsg() : "");
        return resp;
    }

    @Override
    public CspReadingLetterMsgResp accountNameCheck(CspReadingLetterAccountNameCheckReq req) {
        String cspId = cspApi.queryCspId(SessionContextUtil.getUser().getUserId());
        CspReadingLetterMsgResp resp = new CspReadingLetterMsgResp();
        Boolean unique = checkNameUnique(req.getAccountId(), cspId, req.getAccountName());
        resp.setErrMsg(unique ? AuthError.ACCOUNT_NAME_IS_EXISTS.getMsg() : "");
        return resp;
    }

    @Override
    public List<CustomerReadingLetterAccountVo> available() {
        String customerId = SessionContextUtil.getUser().getUserId();
        LambdaQueryWrapperX<CspReadingLetterAccountDo> query = new LambdaQueryWrapperX<>();
        query.eq(CspReadingLetterAccountDo::getCustomerId, customerId);
        query.eq(CspReadingLetterAccountDo::getStatus, 1);
        List<CspReadingLetterAccountDo> accountDos = cspReadingLetterAccountDao.selectList(query);
        List<CustomerReadingLetterAccountVo> result = new ArrayList<>();
        accountDos.forEach(accountDo -> {
            CustomerReadingLetterAccountVo resp = new CustomerReadingLetterAccountVo();
            resp.setId(accountDo.getId());
            resp.setAccountName(accountDo.getAccountName());
            resp.setOperator(accountDo.getOperator());
            resp.setAccountId(accountDo.getAccountId());
            resp.setCustomDomains(accountDo.getCustomDomains());
            result.add(resp);
        });
        return result;
    }

    @Override
    public CspReadingLetterDetailResp selectOne(String accountId, String userId) {
        LambdaQueryWrapperX<CspReadingLetterAccountDo> query = new LambdaQueryWrapperX<>();
        query.eq(CspReadingLetterAccountDo::getAccountId, accountId)
                .eq(CspReadingLetterAccountDo::getCustomerId, userId)
                .eq(CspReadingLetterAccountDo::getStatus, 1);
        CspReadingLetterAccountDo accountDo = cspReadingLetterAccountDao.selectOne(query);
        if (Objects.isNull(accountDo)) {
            throw new BizException(AuthError.READING_LETTER_PLUS_ACCOUNT_NOT_EXIST);
        }
        CspReadingLetterDetailResp resp = new CspReadingLetterDetailResp();
        BeanUtils.copyProperties(accountDo, resp);
        resp.setAppKey(RsaUtil.decryptByPrivateKey(keyPairConfig.getPrivateKey(), accountDo.getAppKey()));
        return resp;
    }

    @Override
    public List<CustomerReadingLetterAccountListVo> queryCustomerReadingLetterAccountList(CustomerReadingLetterAccountSearchReq searchVo) {
        List<CustomerReadingLetterAccountListVo> result = new ArrayList<>();
        Integer operator = searchVo.getOperator();
        String accountName = searchVo.getAccountName();
        //如果查询的账号类型不是阅信+ ,那么一定是包括查询所有的5G阅信账号
        if (!SmsTypeEnum.READING_LETTER_PLUS.getCode().equals(searchVo.getSmsType())) {
            //查找蜂动账号
            String userId = SessionContextUtil.getUser().getUserId();
            List<AccountManagementResp> accountManagementlist = accountManagementService.getAccountManagementlist(userId);
            accountManagementlist = accountManagementlist.stream()
                    .filter(account -> account.getSupplierTag().equals(CSPChatbotSupplierTagEnum.FONTDO.getValue()))
                    .collect(Collectors.toList());

            if (Objects.nonNull(operator)) {
                accountManagementlist = accountManagementlist.stream()
                        .filter(account -> account.getAccountTypeCode().equals(operator))
                        .collect(Collectors.toList());
            }

            if (StrUtil.isNotBlank(accountName)) {
                accountManagementlist = accountManagementlist.stream()
                        .filter(account -> account.getAccountName().contains(accountName))
                        .collect(Collectors.toList());
            }
            accountManagementlist.forEach(account -> {
                CustomerReadingLetterAccountListVo customerReadingLetterAccountListVo = new CustomerReadingLetterAccountListVo();
                customerReadingLetterAccountListVo.setAccountName(account.getAccountName());
                customerReadingLetterAccountListVo.setOperator(account.getAccountTypeCode());
                customerReadingLetterAccountListVo.setStatus(account.getChatbotStatus());
                customerReadingLetterAccountListVo.setCreateTime(account.getCreateTime());
                customerReadingLetterAccountListVo.setAccountId(account.getChatbotAccountId());
                customerReadingLetterAccountListVo.setSmsType(SmsTypeEnum.FIFTH_READING_LETTER.getCode());
                customerReadingLetterAccountListVo.setDomain("默认域名");
                result.add(customerReadingLetterAccountListVo);
            });
        }

        //如果查询的账号类型不是5G阅信 ,那么一定是包括查询所有的阅信+账号
        if (!SmsTypeEnum.FIFTH_READING_LETTER.getCode().equals(searchVo.getSmsType())) {
            // 查找(阅信+账号)
            List<CspReadingLetterDetailResp> readingLetterAccounts = this.queryAllAccountsOfCustomer(null);
            if (Objects.nonNull(operator)) {
                readingLetterAccounts = readingLetterAccounts.stream()
                        .filter(account -> account.getOperator().equals(operator))
                        .collect(Collectors.toList());
            }
            if (StrUtil.isNotBlank(accountName)) {
                readingLetterAccounts = readingLetterAccounts.stream()
                        .filter(account -> account.getAccountName().contains(accountName))
                        .collect(Collectors.toList());
            }
            readingLetterAccounts.forEach(account -> {
                CustomerReadingLetterAccountListVo customerReadingLetterAccountListVo = new CustomerReadingLetterAccountListVo();
                if (account.getTariffBatch() != null) {
                    ChargeTariffDo chargeTariffDo = chargeTariffDao.selectOne(new LambdaUpdateWrapper<ChargeTariffDo>().eq(
                            ChargeTariffDo::getBatch, account.getTariffBatch()
                    ));
                    customerReadingLetterAccountListVo.setTariffContent(chargeTariffDo.getTariffContent());
                }
                customerReadingLetterAccountListVo.setAccountName(account.getAccountName());
                customerReadingLetterAccountListVo.setOperator(account.getOperator());
                customerReadingLetterAccountListVo.setStatus(account.getStatus());
                customerReadingLetterAccountListVo.setCreateTime(account.getCreateTime());
                customerReadingLetterAccountListVo.setAccountId(account.getAccountId());
                customerReadingLetterAccountListVo.setSmsType(SmsTypeEnum.READING_LETTER_PLUS.getCode());
                customerReadingLetterAccountListVo.setDomain(account.getCustomDomains());
                result.add(customerReadingLetterAccountListVo);
            });
        }
        return result;
    }

    private void syncYxPlusInfoToGateway(CspReadingLetterAccountDo cspReadingLetterAccountDo) {
        SyncAuthInfoModel syncAuthInfoModel = new SyncAuthInfoModel();
        try {
            syncAuthInfoModel.setAppId(cspReadingLetterAccountDo.getAppId());
            syncAuthInfoModel.setOpenId(cspReadingLetterAccountDo.getAgentId());
            syncAuthInfoModel.setOpenSecret(RsaUtil.decryptByPrivateKey(keyPairConfig.getPrivateKey(), cspReadingLetterAccountDo.getAppKey()));
            syncAuthInfoModel.setEcId(cspReadingLetterAccountDo.getEcId());
            syncAuthInfoModel.setAgentName(cspReadingLetterAccountDo.getAccountName());
            syncAuthInfoModel.setYxAccountId(cspReadingLetterAccountDo.getAccountId());
            syncAuthInfoModel.setSupplierTag("fontdo");
            syncAuthInfoModel.setToken(UUIDUtils.generateUUID().substring(0, 16));
            syncAuthInfoModel.setCallbackUrl(callbackUrl);

            // 调用网关接口
            String supplierResultString = syncToSupplier(syncAuthInfoModel, zhongXunConfigure.getSupplierSyncUrl());
            log.info("向网关同步阅信+信息返回结果: {}", supplierResultString);
            UpReceiveData upReceiveData = JSONObject.parseObject(supplierResultString, UpReceiveData.class);
            if (upReceiveData.getCode() != 200) {
                throw new RuntimeException("向网关同步阅信+信息失败");
            }
        } catch (Exception e) {
            log.error("向网关同步阅信+信息失败: {},\nreason:{}", JSONObject.toJSONString(syncAuthInfoModel), e);
            throw new BizException(81001235, "向网关同步阅信+信息失败");
        }
    }

    public String syncToSupplier(Object obj, String url) throws URISyntaxException, IOException, NoSuchAlgorithmException {
        try (CloseableHttpClient client = createSSLClientDefault()) {
            URIBuilder uriBuilder = new URIBuilder(url);
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            String jsonString = JSONObject.toJSONString(obj);
            log.info("请求体数据为： {}", jsonString);
            //在网关这个是openId

            String timestamp = System.currentTimeMillis() + "";
            String input = zhongXunConfigure.getAccessKey() + zhongXunConfigure.getAccessSecret() + timestamp;
            //确定计算方法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            String sign = java.util.Base64.getEncoder().encodeToString(md5.digest(input.getBytes(StandardCharsets.UTF_8)));
            httpPost.addHeader("sign", sign);
            httpPost.addHeader("accessKey", zhongXunConfigure.getAccessKey());
            httpPost.addHeader("timestamp", timestamp);

            HttpEntity entity = new StringEntity(jsonString, ContentType.create("application/json", "utf-8"));
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            String resultString = EntityUtils.toString(responseEntity);
            log.info("发送至网关的结果为  " + resultString);
            return resultString;
        } finally {
            createSSLClientDefault().close();
        }
    }


    private CspReadingLetterAccountDo getAccountDo(String accountId, String cspId) {
        LambdaQueryWrapperX<CspReadingLetterAccountDo> query = new LambdaQueryWrapperX<>();
        query.eq(CspReadingLetterAccountDo::getAccountId, accountId);
        query.eq(CspReadingLetterAccountDo::getCspId, cspId);
        CspReadingLetterAccountDo accountDo = cspReadingLetterAccountDao.selectOne(query);
        if (Objects.isNull(accountDo)) {
            throw new BizException("账号不存在");
        }
        return accountDo;
    }

    private Boolean checkNameUnique(String excludeId, String cspId, String accountName) {
        return cspReadingLetterAccountDao.exists(
                Wrappers.<CspReadingLetterAccountDo>lambdaQuery()
                        .ne(StringUtils.isNotEmpty(excludeId), CspReadingLetterAccountDo::getAccountId, excludeId)
                        .eq(StringUtils.isNotEmpty(cspId), CspReadingLetterAccountDo::getCspId, cspId)
                        .eq(CspReadingLetterAccountDo::getAccountName, accountName)
        );
    }

    private Boolean checkCustomerOperatorUnique(String excludeId, String customerId, Integer operator) {
        return cspReadingLetterAccountDao.exists(
                Wrappers.<CspReadingLetterAccountDo>lambdaQuery()
                        .ne(StringUtils.isNotEmpty(excludeId), CspReadingLetterAccountDo::getAccountId, excludeId)
                        .eq(CspReadingLetterAccountDo::getCustomerId, customerId)
                        .eq(CspReadingLetterAccountDo::getOperator, operator)
        );
    }


    private String generateAccountId() {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String localDate = LocalDateTime.now().format(pattern);
        int idLength = 5;
        String randomID = randomID(idLength);
        return ACCOUNT_ID_PREFIX + localDate + randomID;
    }

    public static String randomID(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}
