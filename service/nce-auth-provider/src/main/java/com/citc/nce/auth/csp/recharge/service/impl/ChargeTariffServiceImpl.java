package com.citc.nce.auth.csp.recharge.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementDao;
import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.csp.readingLetter.dao.CspReadingLetterAccountDao;
import com.citc.nce.auth.csp.readingLetter.entity.CspReadingLetterAccountDo;
import com.citc.nce.auth.csp.recharge.Const.AccountTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.ConsumeTypeEnum;
import com.citc.nce.auth.csp.recharge.dao.ChargeTariffDao;
import com.citc.nce.auth.csp.recharge.entity.ChargeTariffDo;
import com.citc.nce.auth.csp.recharge.service.ChargeConsumeRecordService;
import com.citc.nce.auth.csp.recharge.service.ChargeTariffService;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffAdd;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffExistReq;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffOptionsReq;
import com.citc.nce.auth.csp.recharge.vo.TariffOptions;
import com.citc.nce.auth.csp.sms.account.dao.CspSmsAccountDao;
import com.citc.nce.auth.csp.sms.account.entity.CspSmsAccountDo;
import com.citc.nce.auth.csp.videoSms.account.dao.CspVideoSmsAccountDao;
import com.citc.nce.auth.csp.videoSms.account.entity.CspVideoSmsAccountDo;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.constants.RechargeTariffFallbackTypeEnum;
import com.citc.nce.common.core.enums.CustomerPayType;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.UUIDUtils;
import com.citc.nce.tenant.MsgRecordApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author yy
 * @date 2024-10-18 16:00:57
 */
@Service
public class ChargeTariffServiceImpl implements ChargeTariffService {
    @Autowired
    private AccountManagementDao accountManagementDao;

    @Autowired
    private ChargeTariffDao chargeTariffDao;

    @Autowired
    private CspVideoSmsAccountDao cspVideoSmsAccountDao;

    @Autowired
    private CspSmsAccountDao cspSmsAccountDao;

    @Autowired
    private CspReadingLetterAccountDao cspReadingLetterAccountDao;

    @Resource
    private CspCustomerApi cspCustomerApi;

    @Resource
    private MsgRecordApi msgRecordApi;

    @Resource
    private ChargeConsumeRecordService chargeConsumeRecordService;

    @Override
    public RechargeTariffDetailResp getById(Long tariffId) {
        QueryWrapper<ChargeTariffDo> tariffQueryWrapper = new QueryWrapper<>();
        tariffQueryWrapper.eq("id", tariffId);
        ChargeTariffDo chargeTariffDo = chargeTariffDao.selectOne(tariffQueryWrapper);
        RechargeTariffDetailResp resp = JSON.parseObject(chargeTariffDo.getTariffContent(), RechargeTariffDetailResp.class);
        resp.setFallbackType(chargeTariffDo.getFallbackType());
        resp.setAccountId(chargeTariffDo.getAccountId());
        resp.setId(chargeTariffDo.getId());
        return resp;
    }

    @Override
    @Transactional
    public void addRechargeTariff(RechargeTariffAdd req) {
        // 账号类型
        Integer accountType = req.getAccountType();
        List<Integer> allowAccountTypes = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        if (ObjectUtil.isNull(accountType) || (!allowAccountTypes.contains(accountType))) {
            throw new BizException(AuthError.TARIFF_ACCOUNT_TYPE_NOT_ALLOW);
        }

        // 判断值不为空
        // 5G消息
        if (accountType.equals(MsgTypeEnum.M5G_MSG.getCode())) {
            if (req.getTextMsgPrice() == null ||
                    req.getRichMsgPrice() == null ||
                    req.getSessionMsgPrice() == null ||
                    req.getFallbackSmsPrice() == null
            ) {
                throw new BizException(AuthError.TARIFF_NOT_ALL);
            }
        }
        // 视频短信
        if (accountType.equals(MsgTypeEnum.MEDIA_MSG.getCode())) {
            if (req.getVideoSmsPrice() == null) {
                throw new BizException(AuthError.TARIFF_NOT_ALL);
            }
        }
        // 短信
        if (accountType.equals(MsgTypeEnum.SHORT_MSG.getCode())) {
            if (req.getSmsPrice() == null) {
                throw new BizException(AuthError.TARIFF_NOT_ALL);
            }
        }
        // 阅信+
        if (accountType.equals(MsgTypeEnum.YXPLUS_MSG.getCode())) {
            if (req.getYxPlusAnalysisPrice() == null) {
                throw new BizException(AuthError.TARIFF_NOT_ALL);
            }
        }


        // 回落资费类型
        Integer fallbackType = req.getFallbackType();
        if (accountType.equals(MsgTypeEnum.M5G_MSG.getCode())) {
            if (ObjectUtil.isNull(fallbackType) || (
                    !fallbackType.equals(RechargeTariffFallbackTypeEnum.SINGLE_PRICE.getCode()) &&
                            !fallbackType.equals(RechargeTariffFallbackTypeEnum.COMPOSITE_PRICE.getCode())
            )) {
                throw new BizException(AuthError.TARIFF_TYPE_ZERO_ERROR);
            }
            if (fallbackType.equals(RechargeTariffFallbackTypeEnum.SINGLE_PRICE.getCode())) { // 单一价默认5G阅信解析单价为0
                req.setYxAnalysisPrice(0);
            }
            if (fallbackType.equals(RechargeTariffFallbackTypeEnum.COMPOSITE_PRICE.getCode()) && ObjectUtil.isNull(req.getYxAnalysisPrice())) {
                throw new BizException(AuthError.TARIFF_TYPE_ONE_NOT_HAVE_YX);
            }
        }

        // 查询CustomerId
        String customerId = getCustomerIdByAccount(accountType, req.getAccountId());

        // 写入数据库
        String batch = UUIDUtils.generateUUID();
        ChargeTariffDo chargeTariffDo = new ChargeTariffDo();
        chargeTariffDo.setCustomerId(customerId);
        chargeTariffDo.setAccountId(req.getAccountId());
        chargeTariffDo.setAccountType(req.getAccountType());
        chargeTariffDo.setFallbackType(req.getFallbackType());
        chargeTariffDo.setBatch(batch);
        chargeTariffDo.setTariffContent(JSON.toJSONString(req));
        chargeTariffDao.insert(chargeTariffDo);

        // 回填batch
        updateAccountBatch(accountType, req.getAccountId(), batch);
    }

    @Override
    public RechargeTariffDetailResp getRechargeTariff(String accountId) {
        QueryWrapper<ChargeTariffDo> tariffQueryWrapper = new QueryWrapper<>();
        tariffQueryWrapper.eq("account_id", accountId);
        tariffQueryWrapper.orderByDesc("id");
        tariffQueryWrapper.last("limit 1");
        ChargeTariffDo chargeTariffDo = chargeTariffDao.selectOne(tariffQueryWrapper);
        if (ObjectUtil.isNull(chargeTariffDo)) {
            return null;
        }
        RechargeTariffDetailResp resp = JSON.parseObject(chargeTariffDo.getTariffContent(), RechargeTariffDetailResp.class);

        resp.setFallbackType(chargeTariffDo.getFallbackType());
        resp.setAccountId(chargeTariffDo.getAccountId());
        resp.setId(chargeTariffDo.getId());
        return resp;
    }

    @Override
    public TariffOptions showRechargeTariffOptions(RechargeTariffOptionsReq req) {
        TariffOptions options = new TariffOptions();
        // 查询账号付费方式
        UserInfoVo user = cspCustomerApi.getByCustomerId(req.getCustomerId());
        if (user == null) {
            throw new BizException("未查询到用户，无法修改对应账号的资费！");
        }
        options.setEnableEdit(true);
        options.setPayType(user.getPayType().getCode());
        if (user.getPayType() == CustomerPayType.PREPAY) { // 预付费不进行判断
            return options;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
        String accountId = req.getAccountId();
        // 如果是5G消息，查询对应的chatbotAccount
//        if (req.getAccountType() == MsgTypeEnum.M5G_MSG.getCode()) {
//            QueryWrapper<AccountManagementDo> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("chatbot_account_id", req.getAccountId());
//            AccountManagementDo accountManagementDo = accountManagementDao.selectOne(queryWrapper);
//            if (accountManagementDo == null) {
//                throw new BizException("未查询到对应的chatbot，无法设置资费！");
//            }
//            accountId = accountManagementDo.getChatbotAccount();
//        }
        if (StringUtils.isNotEmpty(accountId)) {
            // 查询消息在当前账期是否有消费记录，如果有，不允许进行编辑
            Long deductSum = chargeConsumeRecordService.getChargeConsumeRecordByMsgTypeAndConsumeTypeAndAccountIdAndBetweenDate(
                    user.getCustomerId(),
                    accountId,
                    ConsumeTypeEnum.FEE_DEDUCTION.getCode(),
                    MsgTypeEnum.getValue(req.getAccountType()),
                    start, end
            );
            // 如果没有消费记录
            if (deductSum == 0L) {
                return options;
            }
            // 如果有消费记录，查询返还记录，如果不相等，则有实际消费发生，不允许修改资费
            Long returnSum = chargeConsumeRecordService.getChargeConsumeRecordByMsgTypeAndConsumeTypeAndAccountIdAndBetweenDate(
                    user.getCustomerId(),
                    accountId,
                    ConsumeTypeEnum.RETURN.getCode(),
                    MsgTypeEnum.getValue(req.getAccountType()),
                    start, end
            );
            if (!deductSum.equals(returnSum)) {
                options.setEnableEdit(false);
            }
        }
        return options;
    }

    @Override
    public RechargeTariffDetailResp checkRechargeTariffExist(RechargeTariffExistReq req) {
        if (Objects.equals(req.getAccountType(), MsgTypeEnum.M5G_MSG.getCode())) {
            QueryWrapper<AccountManagementDo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("chatbot_account", req.getAccountId());
            AccountManagementDo accountManagementDo = accountManagementDao.selectOne(queryWrapper);
            return getRechargeTariff(accountManagementDo.getChatbotAccountId());
        }
        if (
                Objects.equals(req.getAccountType(), MsgTypeEnum.MEDIA_MSG.getCode()) ||
                        Objects.equals(req.getAccountType(), MsgTypeEnum.SHORT_MSG.getCode())
        ) {
            return getRechargeTariff(req.getAccountId());
        }
        return null;
    }

    private String getCustomerIdByAccount(Integer accountType, String accountId) {
        String customerId = "";
        // 5G消息
        if (Objects.equals(accountType, MsgTypeEnum.M5G_MSG.getCode())) {
            QueryWrapper<AccountManagementDo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("chatbot_account_id", accountId);
            AccountManagementDo accountManagementDo = accountManagementDao.selectOne(queryWrapper);
            customerId = accountManagementDo.getCustomerId();
        }
        // 视频短信
        if (Objects.equals(accountType, MsgTypeEnum.MEDIA_MSG.getCode())) {
            QueryWrapper<CspVideoSmsAccountDo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account_id", accountId);
            CspVideoSmsAccountDo cspVideoSmsAccountDo = cspVideoSmsAccountDao.selectOne(queryWrapper);
            customerId = cspVideoSmsAccountDo.getCustomerId();
        }
        // 短信
        if (Objects.equals(accountType, MsgTypeEnum.SHORT_MSG.getCode())) {
            QueryWrapper<CspSmsAccountDo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account_id", accountId);
            CspSmsAccountDo cspSmsAccountDo = cspSmsAccountDao.selectOne(queryWrapper);
            customerId = cspSmsAccountDo.getCustomerId();
        }
        // 阅信+
        if (Objects.equals(accountType, MsgTypeEnum.YXPLUS_MSG.getCode())) {
            QueryWrapper<CspReadingLetterAccountDo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account_id", accountId);
            CspReadingLetterAccountDo cspReadingLetterAccountDo = cspReadingLetterAccountDao.selectOne(queryWrapper);
            customerId = cspReadingLetterAccountDo.getCustomerId();
        }
        return customerId;
    }

    private void updateAccountBatch(Integer accountType, String accountId, String batch) {
        // 5G消息
        if (Objects.equals(accountType, MsgTypeEnum.M5G_MSG.getCode())) {
            AccountManagementDo accountManagementDo = new AccountManagementDo();
            accountManagementDo.setTariffBatch(batch);
            LambdaUpdateWrapper<AccountManagementDo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(AccountManagementDo::getChatbotAccountId, accountId);
            accountManagementDao.update(accountManagementDo, updateWrapper);
        }
        // 视频短信
        if (Objects.equals(accountType, MsgTypeEnum.MEDIA_MSG.getCode())) {
            CspVideoSmsAccountDo cspVideoSmsAccountDo = new CspVideoSmsAccountDo();
            cspVideoSmsAccountDo.setTariffBatch(batch);
            LambdaUpdateWrapper<CspVideoSmsAccountDo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(CspVideoSmsAccountDo::getAccountId, accountId);
            cspVideoSmsAccountDao.update(cspVideoSmsAccountDo, updateWrapper);
        }
        // 短信
        if (Objects.equals(accountType, MsgTypeEnum.SHORT_MSG.getCode())) {
            CspSmsAccountDo cspSmsAccountDo = new CspSmsAccountDo();
            cspSmsAccountDo.setTariffBatch(batch);
            LambdaUpdateWrapper<CspSmsAccountDo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(CspSmsAccountDo::getAccountId, accountId);
            cspSmsAccountDao.update(cspSmsAccountDo, updateWrapper);
        }
        // 阅信+
        if (Objects.equals(accountType, MsgTypeEnum.YXPLUS_MSG.getCode())) {
            CspReadingLetterAccountDo cspReadingLetterAccountDo = new CspReadingLetterAccountDo();
            cspReadingLetterAccountDo.setTariffBatch(batch);
            LambdaUpdateWrapper<CspReadingLetterAccountDo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(CspReadingLetterAccountDo::getAccountId, accountId);
            cspReadingLetterAccountDao.update(cspReadingLetterAccountDo, updateWrapper);
        }
    }

}
