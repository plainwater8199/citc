package com.citc.nce.auth.csp.sms.account.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.constant.AimError;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.csp.csp.service.CspService;
import com.citc.nce.auth.csp.relationship.service.AccountRelationshipService;
import com.citc.nce.auth.csp.sms.account.dao.CspSmsAccountDao;
import com.citc.nce.auth.csp.sms.account.dto.CspSmsAccountDto;
import com.citc.nce.auth.csp.sms.account.entity.CspSmsAccountDo;
import com.citc.nce.auth.csp.sms.account.service.CspSmsAccountService;
import com.citc.nce.auth.csp.sms.account.vo.*;
import com.citc.nce.auth.csp.sms.signature.CspSmsSignatureApi;
import com.citc.nce.auth.csp.sms.signature.dao.CspSmsSignatureDao;
import com.citc.nce.auth.csp.sms.signature.entity.CspSmsAccountSignatureDo;
import com.citc.nce.auth.csp.sms.signature.service.CspSmsSignatureService;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureReq;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureResp;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureSubmitReq;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.auth.prepayment.vo.SmsMessageAccountListVo;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.RsaUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.conf.CommonKeyPairConfig;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.api.SmsPlatformApi;
import com.citc.nce.robot.vo.SmsBalanceReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CspSmsAccountServiceImpl implements CspSmsAccountService {

    private static final String ACCOUNT_ID_PREFIX = "VID";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Resource
    private CspSmsAccountDao cspSmsAccountDao;
    @Resource
    private CommonKeyPairConfig keyPairConfig;


    @Autowired
    private AccountRelationshipService accountRelationshipService;

    @Resource
    private CspSmsSignatureService cspSmsSignatureService;
    @Resource
    private CspSmsSignatureDao smsSignatureDao;


    @Autowired
    private RedissonClient redissonClient;

    @Resource
    private SmsPlatformApi smsPlatformApi;

    @Resource
    private CspCustomerApi cspCustomerApi;

    @Resource
    private CspService cspService;
    @Resource
    private CspApi cspApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(CspSmsAccountSaveReq req) {
        // 验证名字是否已存在
        String userId = SessionContextUtil.getUser().getUserId();
        //越权  userId就是客服id
        String cspId = cspService.obtainCspId(userId);
        if (!req.getUserId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }

        this.checkNameUnique(null, cspId, req.getAccountName());
        // 校验AppID AppSecret
        doValidate(req.getAppId(), req.getAppSecret());

        CspSmsAccountDo entity = new CspSmsAccountDo();
        BeanUtils.copyProperties(req, entity);
        // 开启账号使用权限
        entity.setStatus(1);
        // 账号Id
        String accountId = generateAccountId();
        entity.setAccountId(accountId);
        entity.setCustomerId(req.getUserId());
        entity.setCspId(cspId);
        entity.setAppSecret(RsaUtil.encryptByPublicKey(keyPairConfig.getPublicKey(), req.getAppSecret()));
        cspSmsAccountDao.insert(entity);

        // 设置签名
        CspSmsSignatureSubmitReq signatureSubmitReq = new CspSmsSignatureSubmitReq();
        signatureSubmitReq.setAccountId(accountId);
        signatureSubmitReq.setType(0); // 短信type为0
        signatureSubmitReq.setSignatureList(req.getSignatureList());
        cspSmsSignatureService.submit(signatureSubmitReq);
        return 0;
    }

    private void checkNameUnique(Long excludeId, String cspId, String accountName) {
        if (cspSmsAccountDao.selectCount(
                Wrappers.<CspSmsAccountDo>lambdaQuery()
                        .ne(excludeId != null, CspSmsAccountDo::getId, excludeId)
                        .eq(StringUtils.isNotEmpty(cspId), CspSmsAccountDo::getCspId, cspId)
                        .eq(CspSmsAccountDo::getAccountName, accountName)
        ) > 0) {
            throw new BizException(AuthError.ACCOUNT_NAME_IS_EXISTS);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int edit(CspSmsAccountEditReq req) {


        String userId = SessionContextUtil.getUser().getUserId();
        String cspId = cspService.obtainCspId(userId);

        this.checkNameUnique(req.getId(), cspId, req.getAccountName());
        // 校验AppID AppSecret
        doValidate(req.getAppId(), req.getAppSecret());

        LambdaUpdateWrapper<CspSmsAccountDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CspSmsAccountDo::getId, req.getId())
                .eq(CspSmsAccountDo::getAccountId, req.getAccountId());
        CspSmsAccountDo cspSmsAccountDo = cspSmsAccountDao.selectOne(updateWrapper);
        if (Objects.isNull(cspSmsAccountDo)) {
            throw new BizException("账号不存在");
        }
        checkCustomIsCsp(cspSmsAccountDo.getCustomerId());
        updateWrapper.set(CspSmsAccountDo::getAccountName, req.getAccountName())
                .set(CspSmsAccountDo::getAppId, req.getAppId())
                .set(CspSmsAccountDo::getAppSecret, RsaUtil.encryptByPublicKey(keyPairConfig.getPublicKey(), req.getAppSecret()))
                .set(CspSmsAccountDo::getUpdateTime, new Date());

        cspSmsAccountDao.update(new CspSmsAccountDo(), updateWrapper);

        // 设置签名
        CspSmsSignatureSubmitReq signatureSubmitReq = new CspSmsSignatureSubmitReq();
        signatureSubmitReq.setAccountId(req.getAccountId());
        signatureSubmitReq.setType(0); // 短信type为0
        signatureSubmitReq.setSignatureList(req.getSignatureList());
        cspSmsSignatureService.submit(signatureSubmitReq);
        return 0;
    }

    @Override
    public int updateStatus(CspSmsAccountUpdateStatusReq req) {
        LambdaUpdateWrapper<CspSmsAccountDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CspSmsAccountDo::getAccountId, req.getAccountId());
        CspSmsAccountDo accountDo = cspSmsAccountDao.selectOne(updateWrapper);
        if (Objects.isNull(accountDo)) {
            throw new BizException("账号不存在");
        }
        checkCustomIsCsp(accountDo.getCustomerId());

        updateWrapper.set(CspSmsAccountDo::getStatus, req.getStatus())
                .set(CspSmsAccountDo::getUpdateTime, new Date());
        cspSmsAccountDao.update(new CspSmsAccountDo(), updateWrapper);
        return 0;
    }

    @Override
    public int delete(CspSmsAccountDeleteReq req) {
        LambdaUpdateWrapper<CspSmsAccountDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CspSmsAccountDo::getAccountId, req.getAccountId());
        CspSmsAccountDo accountDo = cspSmsAccountDao.selectOne(updateWrapper);
        if (Objects.isNull(accountDo)) {
            throw new BizException("账号不存在");
        }
        checkCustomIsCsp(accountDo.getCustomerId());

        updateWrapper.set(CspSmsAccountDo::getDeleted, 1);
        cspSmsAccountDao.delete(updateWrapper);
        return 0;
    }

    @Override
    public CspSmsAccountDetailResp queryDetail(CspSmsAccountQueryDetailReq req) {
        CspSmsAccountDetailResp resp = new CspSmsAccountDetailResp();
        LambdaQueryWrapperX<CspSmsAccountDo> queryWrapperX = new LambdaQueryWrapperX<>();
        if (!Boolean.TRUE.equals(req.getInnerCall())) {
            BaseUser loginUser = SessionContextUtil.getLoginUser();
            if (loginUser.getIsCustomer()) {
                //登录的是客户
                queryWrapperX.eq(CspSmsAccountDo::getCustomerId, loginUser.getUserId());
            } else {
                //登录的是csp
                queryWrapperX.eq(CspSmsAccountDo::getCreator, loginUser.getUserId());
            }
        }
        queryWrapperX.eq(ObjectUtils.isNotEmpty(req.getId()), CspSmsAccountDo::getId, req.getId())
                .eq(ObjectUtils.isNotEmpty(req.getAccountId()), CspSmsAccountDo::getAccountId, req.getAccountId());
        CspSmsAccountDo videoAccountDo = cspSmsAccountDao.selectOne(queryWrapperX);
        if (ObjectUtils.isNotEmpty(videoAccountDo)) {
            BeanUtils.copyProperties(videoAccountDo, resp);
            resp.setAppSecret(RsaUtil.decryptByPrivateKey(keyPairConfig.getPrivateKey(), videoAccountDo.getAppSecret()));
        }
        // 查询签名
        CspSmsSignatureReq signatureReq = new CspSmsSignatureReq();
        signatureReq.setAccountId(resp.getAccountId());
        signatureReq.setType(0);
        List<CspSmsSignatureResp> signatureList = cspSmsSignatureService.getSignatureByAccountId(signatureReq);
        resp.setSignatureList(signatureList);
        return resp;
    }

    @Override
    public PageResult<CspSmsAccountResp> queryListByLoginUser() {
        PageResult<CspSmsAccountResp> result = new PageResult<>();
        result.setTotal(0L);
        String userId = SessionContextUtil.getUser().getUserId();
        LambdaQueryWrapperX<CspSmsAccountDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(CspSmsAccountDo::getCustomerId, userId);
        List<CspSmsAccountDo> cspVideoSmsAccountDoList = cspSmsAccountDao.selectList(queryWrapperX);
        if (CollectionUtils.isNotEmpty(cspVideoSmsAccountDoList)) {
            List<CspSmsAccountResp> smsAccountResps = BeanUtil.copyToList(cspVideoSmsAccountDoList, CspSmsAccountResp.class);

            result.setList(smsAccountResps);
            result.setTotal(Long.valueOf(smsAccountResps.size()));
            return result;
        }
        return null;
    }

    @Override
    public PageResult<CspSmsAccountResp> queryList(CspSmsAccountReq req) {
        if (null == req.getPageSize()) {
            req.setPageSize(20);
        }
        if (null == req.getPageNo()) {
            req.setPageNo(1);
        }
        CspSmsAccountDto dto = new CspSmsAccountDto();
        int currentPage = (req.getPageNo() - 1) * req.getPageSize() < req.getPageSize() ? (req.getPageNo() - 1) * req.getPageSize() : req.getPageSize();
        BeanUtils.copyProperties(req, dto);
        dto.setCurrentPage(currentPage);

        String userId = SessionContextUtil.getUser().getUserId();
        // 获取当前CSP名下的客户userId
        String cspId = cspService.obtainCspId(userId);
        List<String> customerIds = cspCustomerApi.queryCustomerIdsByCspId(cspId);
        dto.setCustomerIdList(customerIds);
        dto.setCustomerId(req.getCustomerId());
        dto.setCspId(cspId);
        Long count = cspSmsAccountDao.queryListCount(dto);
        PageResult result = new PageResult();
        result.setTotal(0L);
        if (count > 0) {
            List<CspSmsAccountResp> respList = cspSmsAccountDao.queryList(dto);

            result.setList(respList);
            result.setTotal(count);
        }
        return result;
    }

    @Override
    public PageResult<CspSmsAccountChatbotResp> queryListChatbot(CspSmsAccountChatbotReq req) {
        if (null == req.getPageSize()) {
            req.setPageSize(20);
        }
        if (null == req.getPageNo()) {
            req.setPageNo(1);
        }
        CspSmsAccountDto dto = new CspSmsAccountDto();
        int currentPage = (req.getPageNo() - 1) * req.getPageSize() < req.getPageSize() ? (req.getPageNo() - 1) * req.getPageSize() : req.getPageSize();
        BeanUtils.copyProperties(req, dto);
        dto.setCurrentPage(currentPage);
        dto.setCustomerId(SessionContextUtil.getUser().getUserId());

        Long count = cspSmsAccountDao.queryListChatbotCount(dto);
        PageResult result = new PageResult();
        result.setTotal(0L);
        if (count > 0) {
            List<CspSmsAccountChatbotResp> respList = cspSmsAccountDao.queryListChatbot(dto);
            // 绑定签名数据
            List<String> accountIdList = new ArrayList<>();
            for (CspSmsAccountChatbotResp accountChatbotResp :
                    respList) {
                accountIdList.add(accountChatbotResp.getAccountId());
            }
            List<CspSmsSignatureResp> signatureList = cspSmsSignatureService.getSignatureByAccountIdsAndType(accountIdList, 0);
            if (CollectionUtils.isNotEmpty(signatureList)) {
                for (CspSmsAccountChatbotResp accountChatbotResp :
                        respList) {
                    List<CspSmsSignatureResp> signature = new ArrayList<>();
                    for (CspSmsSignatureResp signatureResp :
                            signatureList) {
                        if (StringUtils.equals(accountChatbotResp.getAccountId(), signatureResp.getAccountId())) {
                            signature.add(signatureResp);
                        }
                    }
                    accountChatbotResp.setSignatureList(signature);
                }
            }

            result.setList(respList);
            result.setTotal(count);
        }
        return result;
    }

    @Override
    public List<String> queryAccountIdListByUserList(List<String> userIds) {
        if (CollectionUtils.isNotEmpty(userIds)) {
            LambdaQueryWrapperX<CspSmsAccountDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.in(CspSmsAccountDo::getCustomerId, userIds)
                    .eq(CspSmsAccountDo::getDeleted, 0);
            List<CspSmsAccountDo> accountDos = cspSmsAccountDao.selectList(queryWrapperX);

            if (CollectionUtils.isNotEmpty(accountDos)) {
                List<String> res = new ArrayList<>();
                accountDos.forEach(account -> res.add(account.getAccountId()));
                return res;
            }
        }
        return null;
    }

    @Override
    public List<String> queryAccountIdListByCspUserId(List<String> userIds) {
        if (CollectionUtils.isNotEmpty(userIds)) {
            List<String> userIdList = new ArrayList<>();
            for (String csp :
                    userIds) {
                List<String> userId = accountRelationshipService.getUserListByCspUserId(csp);
                if (CollectionUtils.isNotEmpty(userId)) {
                    userIdList.addAll(userId);
                }
            }
            List<String> stringList = queryAccountIdListByUserList(userIdList);
            return stringList;
        }
        return null;
    }

    @Override
    public List<CspSmsAccountResp> queryListByAccountIds(List<String> accountIds) {
        if (CollectionUtils.isNotEmpty(accountIds)) {
            LambdaQueryWrapperX<CspSmsAccountDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.in(CspSmsAccountDo::getAccountId, accountIds);
            List<CspSmsAccountDo> smsAccountDos = cspSmsAccountDao.selectList(queryWrapperX);
            if (CollectionUtils.isNotEmpty(smsAccountDos)) {
                List<CspSmsAccountResp> resps = BeanUtil.copyToList(smsAccountDos, CspSmsAccountResp.class);
                return resps;
            }
        }
        return null;
    }

    @Override
    public void deductResidue(CspSmsAccountDeductResidueReq req) {
        RLock lock = redissonClient.getLock("cspSmsDeductResidue");
        if (lock.tryLock()) {
            try {
                // 先查询
                LambdaQueryWrapperX<CspSmsAccountDo> queryWrapperX = new LambdaQueryWrapperX<>();
                queryWrapperX.eq(CspSmsAccountDo::getAccountId, req.getAccountId());
                CspSmsAccountDo smsAccountDo = cspSmsAccountDao.selectOne(queryWrapperX);
                // 再修改
                if (ObjectUtils.isNotEmpty(smsAccountDo)) {
                    long count = smsAccountDo.getResidualCount() + req.getNum();
                    smsAccountDo.setResidualCount(count);
                    cspSmsAccountDao.updateById(smsAccountDo);
                }
            } catch (Exception e) {
                log.error("更新剩余量异常: {}", e.getMessage());
            } finally {
                lock.unlock();
            }
        } else {
            throw new BizException("系统繁忙，请稍后再试");
        }
    }

    /**
     * 生成账号Id
     * VID + yyyy-MM-dd HH:mm:ss.SSS + 5位随机数
     * VID2023081509111011100000(25位）
     *
     * @return string
     */
    private String generateAccountId() {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String localDate = LocalDateTime.now().format(pattern);
        int idLength = 5;
        String randomID = randomID(idLength);
        String accountId = ACCOUNT_ID_PREFIX + localDate + randomID;
        return accountId;
    }


    /**
     * 生成随机ID
     *
     * @param length 长度
     * @return id
     */
    public static String randomID(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    private void doValidate(String accessKey, String accessSecret) {
        SmsBalanceReq smsBalanceReq = new SmsBalanceReq();
        smsBalanceReq.setAppId(accessKey);
        smsBalanceReq.setSecretKey(accessSecret);
        Long balance = smsPlatformApi.queryBalance(smsBalanceReq);
        if (balance == null) {
            throw new BizException(AimError.VALIDATE_ACCESS_ERROR);
        }
    }

    public void checkCustomIsCsp(String customerId) {
        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的） 判断越权
        String cspId = cspApi.queryCspId(SessionContextUtil.getUser().getUserId());
        if (!customerId.substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
    }


    @PostConstruct
    public void appSecretEncode() {
        List<CspSmsAccountDo> cspSmsAccountDos = cspSmsAccountDao.selectList();
        if (CollectionUtils.isEmpty(cspSmsAccountDos)) return;
        List<CspSmsAccountDo> doList = cspSmsAccountDos.stream().filter(s -> s.getAppSecret().length() < 50).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(doList)) return;
        doList.forEach(s -> s.setAppSecret(RsaUtil.encryptByPublicKey(keyPairConfig.getPublicKey(), s.getAppSecret())));
        cspSmsAccountDao.updateBatch(doList);
    }

    @Override
    public PageResult<SmsMessageAccountListVo> selectSmsMessageAccountByCustomer(MessageAccountSearchVo searchVo) {
        Page<SmsMessageAccountListVo> page = new Page<>(searchVo.getPageNo(), searchVo.getPageSize());
        page.setOrders(OrderItem.descs("create_time"));
        String cspId = null, customerId = null;
        BaseUser user = SessionContextUtil.getUser();
        if (user != null) {
            cspId = user.getCspId();
            if (user.getIsCustomer())
                customerId = user.getUserId();
        }
        cspSmsAccountDao.selectSmsAccount(cspId, customerId, searchVo.getName(), searchVo.getDictCode(), searchVo.getStatus(), searchVo.getAccountName(), page);
        List<String> accountIds = page.getRecords()
                .stream()
                .map(SmsMessageAccountListVo::getAccountId)
                .collect(Collectors.toList());
        //查询账号签名
        if (CollectionUtils.isNotEmpty(accountIds)) {
            //一个账号可能有多个签名
            Map<String, List<CspSmsAccountSignatureDo>> signatureMap = smsSignatureDao.selectList(
                            Wrappers.<CspSmsAccountSignatureDo>lambdaQuery()
                                    .in(CspSmsAccountSignatureDo::getAccountId, accountIds)
                    )
                    .stream()
                    .collect(Collectors.groupingBy(CspSmsAccountSignatureDo::getAccountId));
            List<CspSmsAccountSignatureDo> emptyList = new ArrayList<>();
            for (SmsMessageAccountListVo account : page.getRecords()) {
                account.setSignatureList(
                        signatureMap.getOrDefault(account.getAccountId(), emptyList)
                                .stream()
                                .map(CspSmsAccountSignatureDo::getSignature)
                                .collect(Collectors.toList())
                );
            }
        }
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public Map<String, String> queryAccountIdNameMapByCustomerId(String customerId) {
        Map<String, String> accountIdNameMap = new HashMap<>();
        LambdaQueryWrapperX<CspSmsAccountDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(CspSmsAccountDo::getCustomerId, customerId);
        List<CspSmsAccountDo> smsAccountDos = cspSmsAccountDao.selectList(queryWrapperX);
        if (CollectionUtils.isNotEmpty(smsAccountDos)) {
            smsAccountDos.forEach(s -> accountIdNameMap.put(s.getAccountId(), s.getAccountName()));
        }
        return accountIdNameMap;
    }

}
