package com.citc.nce.auth.csp.videoSms.account.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.csp.csp.service.CspService;
import com.citc.nce.auth.csp.recharge.dao.ChargeTariffDao;
import com.citc.nce.auth.csp.videoSms.account.dao.CspVideoSmsAccountDao;
import com.citc.nce.auth.csp.videoSms.account.dto.CspVideoSmsAccountDto;
import com.citc.nce.auth.csp.videoSms.account.entity.CspVideoSmsAccountDo;
import com.citc.nce.auth.csp.videoSms.account.service.CspVideoSmsAccountService;
import com.citc.nce.auth.csp.videoSms.account.vo.*;
import com.citc.nce.auth.csp.videoSms.signature.dao.CspVideoSmsSignatureDao;
import com.citc.nce.auth.csp.videoSms.signature.entity.CspVideoSmsSignatureDo;
import com.citc.nce.auth.csp.videoSms.signature.service.CspVideoSmsSignatureService;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureReq;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureResp;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureSubmitReq;
import com.citc.nce.auth.identification.dao.UserEnterpriseIdentificationDao;
import com.citc.nce.auth.identification.entity.UserEnterpriseIdentificationDo;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.auth.prepayment.vo.VideoSmsMessageAccountListVo;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.RsaUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.conf.CommonKeyPairConfig;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.tenant.MsgRecordApi;
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

/**
 * <p></p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:51
 */
@Service
@Slf4j
public class CspVideoSmsAccountServiceImpl implements CspVideoSmsAccountService {

    @Autowired
    private CspVideoSmsAccountDao cspVideoSmsAccountDao;
    @Autowired
    private CspVideoSmsSignatureDao videoSmsSignatureDao;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserEnterpriseIdentificationDao userEnterpriseIdentificationDao;


    @Autowired
    private ChargeTariffDao chargeTariffDao;

    @Resource
    private CommonKeyPairConfig keyPairConfig;

    @Resource
    private CspCustomerApi cspCustomerApi;

    @Resource
    private MsgRecordApi msgRecordApi;

    @Resource
    private CspService cspService;

    @Resource
    private CspVideoSmsSignatureService cspVideoSmsSignatureService;

    private static final String ACCOUNT_ID_PREFIX = "VID";

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 列表查询
     *
     * @param
     * @return
     * @author zy.qiu
     * @createdTime 2023/8/15 9:27
     */
    @Override
    public PageResult<CspVideoSmsAccountResp> queryList(CspVideoSmsAccountReq req) {
        if (null == req.getPageSize()) {
            req.setPageSize(20);
        }
        if (null == req.getPageNo()) {
            req.setPageNo(1);
        }
        CspVideoSmsAccountDto dto = new CspVideoSmsAccountDto();
        int currentPage = Math.max((req.getPageNo() - 1) * req.getPageSize(), 0);
        BeanUtils.copyProperties(req, dto);
        dto.setCurrentPage(currentPage);

        String userId = SessionContextUtil.getUser().getUserId();
        // 获取当前CSP名下的客户userId
        String cspId = cspService.obtainCspId(userId);
        dto.setCspId(cspId);
        dto.setCustomerId(req.getCustomerId());

        Long count = cspVideoSmsAccountDao.queryListCount(dto);
        PageResult result = new PageResult();
        result.setTotal(0L);
        if (count > 0) {
            List<CspVideoSmsAccountResp> respList = cspVideoSmsAccountDao.queryList(dto);
            result.setList(respList);
            result.setTotal(count);
        }
        return result;
    }


    @Override
    public PageResult<CspVideoSmsAccountResp> queryListByLoginUser() {
        PageResult<CspVideoSmsAccountResp> result = new PageResult<>();
        result.setTotal(0L);
        String userId = SessionContextUtil.getUser().getUserId();
        LambdaQueryWrapperX<CspVideoSmsAccountDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(CspVideoSmsAccountDo::getCustomerId, userId).orderByDesc(CspVideoSmsAccountDo::getCreateTime);
        List<CspVideoSmsAccountDo> cspVideoSmsAccountDoList = cspVideoSmsAccountDao.selectList(queryWrapperX);
        if (CollectionUtils.isNotEmpty(cspVideoSmsAccountDoList)) {
            List<CspVideoSmsAccountResp> smsAccountResps = BeanUtil.copyToList(cspVideoSmsAccountDoList, CspVideoSmsAccountResp.class);
            result.setList(smsAccountResps);
            result.setTotal((long) smsAccountResps.size());
            return result;
        }
        return null;
    }

    @Override
    public List<String> queryAccountIdListByUserList(List<String> userIds) {
        if (CollectionUtils.isNotEmpty(userIds)) {
            LambdaQueryWrapperX<CspVideoSmsAccountDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.in(CspVideoSmsAccountDo::getCustomerId, userIds)
                    .eq(CspVideoSmsAccountDo::getDeleted, 0);
            List<CspVideoSmsAccountDo> accountDos = cspVideoSmsAccountDao.selectList(queryWrapperX);

            if (CollectionUtils.isNotEmpty(accountDos)) {
                List<String> res = new ArrayList<>();
                accountDos.stream().forEach(account -> res.add(account.getAccountId()));
                return res;
            }
        }
        return null;
    }

    @Override
    public List<String> queryAccountIdListByCspUserId(List<String> userIds) {
        if (CollectionUtils.isNotEmpty(userIds)) {
            List<String> customerIdList = new ArrayList<>();
            for (String cspId : userIds) {
                List<String> customerIdListByCsp = cspCustomerApi.queryCustomerIdsByCspId(cspId);
                if (CollectionUtils.isNotEmpty(customerIdListByCsp)) {
                    customerIdList.addAll(customerIdListByCsp);
                }
            }
            return queryAccountIdListByUserList(customerIdList);
        }
        return Collections.emptyList();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(CspVideoSmsAccountSaveReq req) {

        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的） 判断越权
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!req.getUserId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }

        // 验证名字是否已存在
        Long aLong = cspVideoSmsAccountDao.checkNameUnique(req.getAccountName(), 0L);
        if (aLong > 0) {
            throw new BizException(AuthError.ACCOUNT_NAME_IS_EXISTS);
        }
        CspVideoSmsAccountDo entity = new CspVideoSmsAccountDo();
        BeanUtils.copyProperties(req, entity);
        // 开启账号使用权限
        entity.setStatus(1);
        // 账号Id
        String accountId = generateAccountId();
        entity.setAccountId(accountId);
        entity.setCustomerId(req.getUserId());
        entity.setCspId(cspId);
        entity.setAppSecret(RsaUtil.encryptByPublicKey(keyPairConfig.getPublicKey(), req.getAppSecret()));
        cspVideoSmsAccountDao.insert(entity);

        // 设置签名
        CspVideoSmsSignatureSubmitReq signatureSubmitReq = new CspVideoSmsSignatureSubmitReq();
        signatureSubmitReq.setAccountId(accountId);
        signatureSubmitReq.setType(0); // 视频短信type为0
        signatureSubmitReq.setSignatureList(req.getSignatureList().stream().peek(sign -> sign.setAccountId(accountId)).collect(Collectors.toList()));
        cspVideoSmsSignatureService.submit(signatureSubmitReq);

        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int edit(CspVideoSmsAccountEditReq req) {
        Long aLong = cspVideoSmsAccountDao.checkNameUnique(req.getAccountName(), req.getId());
        if (aLong > 0) {
            throw new BizException(AuthError.ACCOUNT_NAME_IS_EXISTS);
        }
        //查询是否越权
        LambdaUpdateWrapper<CspVideoSmsAccountDo> eq = new LambdaUpdateWrapper<>();
        eq.eq(CspVideoSmsAccountDo::getId, req.getId())
                .eq(CspVideoSmsAccountDo::getAccountId, req.getAccountId());
        CspVideoSmsAccountDo cspVideoSmsAccountDo = cspVideoSmsAccountDao.selectOne(eq);
        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的） 判断越权
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!cspVideoSmsAccountDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }

        LambdaUpdateWrapper<CspVideoSmsAccountDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CspVideoSmsAccountDo::getId, req.getId())
                .eq(CspVideoSmsAccountDo::getAccountId, req.getAccountId())
                .set(CspVideoSmsAccountDo::getAccountName, req.getAccountName())
                .set(CspVideoSmsAccountDo::getAppId, req.getAppId())
                .set(CspVideoSmsAccountDo::getAppSecret, RsaUtil.encryptByPublicKey(keyPairConfig.getPublicKey(), req.getAppSecret()))
                .set(CspVideoSmsAccountDo::getUpdateTime, new Date());

        cspVideoSmsAccountDao.update(new CspVideoSmsAccountDo(), updateWrapper);

        // 设置签名
        CspVideoSmsSignatureSubmitReq signatureSubmitReq = new CspVideoSmsSignatureSubmitReq();
        signatureSubmitReq.setAccountId(req.getAccountId());
        signatureSubmitReq.setType(0); // 视频短信type为0
        signatureSubmitReq.setSignatureList(req.getSignatureList());
        cspVideoSmsSignatureService.submit(signatureSubmitReq);

        return 0;
    }

    @Override
    public int updateStatus(CspVideoSmsAccountUpdateStatusReq req) {
        LambdaUpdateWrapper<CspVideoSmsAccountDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CspVideoSmsAccountDo::getAccountId, req.getAccountId());
        CspVideoSmsAccountDo accountDo = cspVideoSmsAccountDao.selectOne(updateWrapper);
        if (Objects.isNull(accountDo)) {
            throw new BizException("账号不存在");
        }
        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的） 判断越权
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!accountDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }

        updateWrapper.set(CspVideoSmsAccountDo::getStatus, req.getStatus())
                .set(CspVideoSmsAccountDo::getUpdateTime, new Date());

        cspVideoSmsAccountDao.update(new CspVideoSmsAccountDo(), updateWrapper);
        return 0;
    }

    @Override
    public int delete(CspVideoSmsAccountDeleteReq req) {
        LambdaUpdateWrapper<CspVideoSmsAccountDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CspVideoSmsAccountDo::getAccountId, req.getAccountId());

        //数据越权
        CspVideoSmsAccountDo cspVideoSmsAccountDo = cspVideoSmsAccountDao.selectOne(updateWrapper);
        if (Objects.isNull(cspVideoSmsAccountDo)) {
            return 0;
        }
        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的） 判断越权
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!cspVideoSmsAccountDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }


        updateWrapper.set(CspVideoSmsAccountDo::getDeleted, 1);
        cspVideoSmsAccountDao.delete(updateWrapper);
        return 0;
    }

    @Override
    public CspVideoSmsAccountDetailResp queryDetail(CspVideoSmsAccountQueryDetailReq req) {
        if (Objects.isNull(req.getId()) && StringUtils.isBlank(req.getAccountId())) {
            throw new BizException("id 和 accountId 不能同时为空");
        }
        CspVideoSmsAccountDetailResp resp = new CspVideoSmsAccountDetailResp();
        LambdaQueryWrapperX<CspVideoSmsAccountDo> queryWrapperX = new LambdaQueryWrapperX<>();
        if (ObjectUtils.isNotEmpty(req.getId())) {
            queryWrapperX.eq(CspVideoSmsAccountDo::getId, req.getId());
        }
        if (ObjectUtils.isNotEmpty(req.getAccountId())) {
            queryWrapperX.eq(CspVideoSmsAccountDo::getAccountId, req.getAccountId());
        }
        CspVideoSmsAccountDo videoSmsAccountDo = cspVideoSmsAccountDao.selectOne(queryWrapperX);
        if (ObjectUtils.isNotEmpty(videoSmsAccountDo)) {
            BeanUtils.copyProperties(videoSmsAccountDo, resp);
            String customerId = videoSmsAccountDo.getCustomerId();
            resp.setCustomerId(customerId);
            LambdaQueryWrapperX<UserEnterpriseIdentificationDo> enterpriseWrapper = new LambdaQueryWrapperX<>();
            enterpriseWrapper.eq(UserEnterpriseIdentificationDo::getUserId, customerId);
            UserEnterpriseIdentificationDo enterpriseIdentificationDo = userEnterpriseIdentificationDao.selectOne(enterpriseWrapper);
            if (enterpriseIdentificationDo != null) {
                resp.setEnterpriseName(enterpriseIdentificationDo.getEnterpriseName());
            }
            resp.setAppSecret(RsaUtil.decryptByPrivateKey(keyPairConfig.getPrivateKey(), videoSmsAccountDo.getAppSecret()));
        }

        // 查询签名
        CspVideoSmsSignatureReq signatureReq = new CspVideoSmsSignatureReq();
        signatureReq.setAccountId(resp.getAccountId());
        signatureReq.setType(0);
        List<CspVideoSmsSignatureResp> signatureList = cspVideoSmsSignatureService.getSignatureByAccountId(signatureReq);
        resp.setSignatureList(signatureList);

        return resp;
    }

    @Override
    public List<CspVideoSmsAccountResp> queryListByAccountIds(List<String> accountIds) {
        if (CollectionUtils.isNotEmpty(accountIds)) {
            LambdaQueryWrapperX<CspVideoSmsAccountDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.in(CspVideoSmsAccountDo::getAccountId, accountIds);
            List<CspVideoSmsAccountDo> videoSmsAccountDos = cspVideoSmsAccountDao.selectList(queryWrapperX);
            if (CollectionUtils.isNotEmpty(videoSmsAccountDos)) {
                List<CspVideoSmsAccountResp> resps = BeanUtil.copyToList(videoSmsAccountDos, CspVideoSmsAccountResp.class);
                return resps;
            }
        }
        return null;
    }

    @Override
    public void deductResidue(CspVideoSmsAccountDeductResidueReq req) {
        RLock lock = redissonClient.getLock("cspVideoSmsDeductResidue");
        if (lock.tryLock()) {
            try {
                // 先查询
                LambdaQueryWrapperX<CspVideoSmsAccountDo> queryWrapperX = new LambdaQueryWrapperX<>();
                queryWrapperX.eq(CspVideoSmsAccountDo::getAccountId, req.getAccountId());
                CspVideoSmsAccountDo videoSmsAccountDo = cspVideoSmsAccountDao.selectOne(queryWrapperX);
                // 再修改
                if (ObjectUtils.isNotEmpty(videoSmsAccountDo)) {
                    long count = videoSmsAccountDo.getResidualCount() + req.getNum();
                    videoSmsAccountDo.setResidualCount(count);
                    cspVideoSmsAccountDao.updateById(videoSmsAccountDo);
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

    @Override
    public Long countVideoSmsAccount(String userId) {
        String cspId = cspService.obtainCspId(userId);
        return cspVideoSmsAccountDao.selectCount(
                Wrappers.<CspVideoSmsAccountDo>lambdaQuery()
                        .eq(CspVideoSmsAccountDo::getCspId, cspId)
        );
    }

    @Override
    public PageResult<VideoSmsMessageAccountListVo> selectVideoSmsMessageAccountByCustomer(MessageAccountSearchVo searchVo) {
        Page<VideoSmsMessageAccountListVo> page = new Page<>(searchVo.getPageNo(), searchVo.getPageSize());
        page.setOrders(OrderItem.descs("create_time"));
        String cspId = null, customerId = null;
        BaseUser user = SessionContextUtil.getUser();
        if (user != null) {
            cspId = user.getCspId();
            if (user.getIsCustomer())
                customerId = user.getUserId();
        }
        cspVideoSmsAccountDao.selectAccount(cspId, customerId, searchVo.getName(), searchVo.getDictCode(), searchVo.getStatus(), searchVo.getAccountName(), page);
        List<String> accountIds = page.getRecords()
                .stream()
                .map(VideoSmsMessageAccountListVo::getAccountId)
                .collect(Collectors.toList());
        //查询账号签名
        if (CollectionUtils.isNotEmpty(accountIds)) {
            //一个账号可能有多个签名
            Map<String, List<CspVideoSmsSignatureDo>> signatureMap = videoSmsSignatureDao.selectList(
                            Wrappers.<CspVideoSmsSignatureDo>lambdaQuery()
                                    .in(CspVideoSmsSignatureDo::getAccountId, accountIds)
                    )
                    .stream()
                    .collect(Collectors.groupingBy(CspVideoSmsSignatureDo::getAccountId));
            List<CspVideoSmsSignatureDo> emptyList = new ArrayList<>();
            for (VideoSmsMessageAccountListVo account : page.getRecords()) {
                account.setSignatureList(
                        signatureMap.getOrDefault(account.getAccountId(), emptyList)
                                .stream()
                                .map(CspVideoSmsSignatureDo::getSignature)
                                .collect(Collectors.toList())
                );
            }
        }
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public Map<String, String> queryAccountINameMapByCustomerId(String customerId) {
        Map<String, String> accountINameMap = new HashMap<>();
        LambdaQueryWrapperX<CspVideoSmsAccountDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(CspVideoSmsAccountDo::getCustomerId, customerId);
        List<CspVideoSmsAccountDo> videoSmsAccountDos = cspVideoSmsAccountDao.selectList(queryWrapperX);
        if (CollectionUtils.isNotEmpty(videoSmsAccountDos)) {
            videoSmsAccountDos.forEach(s -> accountINameMap.put(s.getAccountId(), s.getAccountName()));
        }
        return accountINameMap;
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

    @PostConstruct
    public void appSecretEncode() {
        List<CspVideoSmsAccountDo> cspSmsAccountDos = cspVideoSmsAccountDao.selectList();
        if (CollectionUtils.isEmpty(cspSmsAccountDos)) return;
        List<CspVideoSmsAccountDo> doList = cspSmsAccountDos.stream().filter(s -> s.getAppSecret().length() < 50).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(doList)) return;
        doList.forEach(s -> s.setAppSecret(RsaUtil.encryptByPublicKey(keyPairConfig.getPublicKey(), s.getAppSecret())));
        cspVideoSmsAccountDao.updateBatch(doList);
    }
}
