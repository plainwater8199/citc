package com.citc.nce.auth.readingLetter.shortUrl.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.csp.customer.dao.CspCustomerDao;
import com.citc.nce.auth.csp.readingLetter.service.CspReadingLetterAccountService;
import com.citc.nce.auth.csp.readingLetter.vo.CspReadingLetterDetailResp;
import com.citc.nce.auth.csp.recharge.Const.ProcessStatusEnum;
import com.citc.nce.auth.csp.recharge.Const.TariffTypeEnum;
import com.citc.nce.auth.csp.recharge.RechargeTariffApi;
import com.citc.nce.auth.csp.recharge.service.ChargeTariffService;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.auth.messagetemplate.entity.TemplateDataResp;
import com.citc.nce.auth.readingLetter.apply.PlatfomManageReadingLetterService;
import com.citc.nce.auth.readingLetter.shortUrl.dao.ReadingLetterShortUrlDao;
import com.citc.nce.auth.readingLetter.shortUrl.dto.ApplyShortUrlEntity;
import com.citc.nce.auth.readingLetter.shortUrl.dto.ApplyShortUrlResultVo;
import com.citc.nce.auth.readingLetter.shortUrl.dto.Params;
import com.citc.nce.auth.readingLetter.shortUrl.dto.SendParam;
import com.citc.nce.auth.readingLetter.shortUrl.entity.ReadingLetterShortUrlDo;
import com.citc.nce.auth.readingLetter.shortUrl.service.ReadingLetterShortUrlService;
import com.citc.nce.auth.readingLetter.shortUrl.vo.QueryShortUrlResultVo;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlAddReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlDetailVo;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlListReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlReApplyReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlSearchReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlVo;
import com.citc.nce.auth.readingLetter.template.dao.ReadingLetterTemplateAuditDao;
import com.citc.nce.auth.readingLetter.template.dao.ReadingLetterTemplateDao;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateAuditDo;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateDo;
import com.citc.nce.auth.readingLetter.template.enums.AuditStatus;
import com.citc.nce.auth.readingLetter.template.enums.SmsTypeEnum;
import com.citc.nce.auth.readingLetter.template.service.ReadingLetterTemplateService;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.ReduceBalanceResp;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.enums.CustomerPayType;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
public class ReadingLetterShortUrlServiceImpl implements ReadingLetterShortUrlService {
    @Resource
    private ReadingLetterShortUrlDao readingLetterShortUrlDao;
    @Resource
    private ReadingLetterTemplateDao readingLetterTemplateDao;
    @Resource
    private ReadingLetterTemplateAuditDao readingLetterTemplateAuditDao;
    @Resource
    private CspReadingLetterAccountService cspReadingLetterAccountService;
    @Resource
    private CspCustomerDao cspCustomerDao;
    @Resource
    private PlatfomManageReadingLetterService platfomManageReadingLetterService;
    @Resource
    private ReadingLetterTemplateService readingLetterTemplateService;

    @Value("${platform.applyShortUrlAddress}")
    private String applyShortUrlAddress;
    @Resource
    RechargeTariffApi rechargeTariffApi;
    @Resource
    CspCustomerApi customerApi;
    @Resource
    ChargeTariffService chargeTariffService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(ReadingLetterShortUrlAddReq readingLetterShortUrlAddReq) {
        String userId = SessionContextUtil.getUser().getUserId();
        String accountId = readingLetterShortUrlAddReq.getAccountId();
        //找到申请短链的(阅信+账号)
        CspReadingLetterDetailResp readingLetterDetail = cspReadingLetterAccountService.selectOne(accountId, userId);
        //创建短链实体对象
        ReadingLetterShortUrlDo readingLetterShortUrlDo = new ReadingLetterShortUrlDo();
        BeanUtil.copyProperties(readingLetterShortUrlAddReq, readingLetterShortUrlDo);
        //查找阅信基础模板
        Long templateId = readingLetterShortUrlAddReq.getTemplateId();
        ReadingLetterTemplateDo readingLetterTemplateDo = readingLetterTemplateDao.selectById(templateId);
        if (Objects.isNull(readingLetterTemplateDo)) {
            throw new BizException(AuthError.READING_LETTER_TEMPLATE_NOT_EXIST);
        }
        readingLetterShortUrlDo.setTemplateType(readingLetterTemplateDo.getTemplateType());
        readingLetterShortUrlDo.setTemplateName(readingLetterTemplateDo.getTemplateName());
        //扣除费用
        ReduceBalanceResp reduceBalanceResp = null;
        //预付费客户才进行扣费
        if (SessionContextUtil.getUser().getPayType() == CustomerPayType.PREPAY) {
            reduceBalanceResp = deduct(readingLetterDetail, readingLetterShortUrlDo);
        } else {
            //获取资费
            RechargeTariffDetailResp tariff = chargeTariffService.getRechargeTariff(readingLetterDetail.getAccountId());
            if (ObjUtil.isNull(tariff)) {
                throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), readingLetterDetail.getAccountName()));
            }
            reduceBalanceResp = new ReduceBalanceResp();
        }
        //查找阅信+模板审核记录(为了获取平台模板ID)
        LambdaQueryWrapper<ReadingLetterTemplateAuditDo> auditWrapper = new LambdaQueryWrapper<>();
        auditWrapper.eq(ReadingLetterTemplateAuditDo::getCustomerId, userId)
                .eq(ReadingLetterTemplateAuditDo::getTemplateId, templateId)
                .eq(ReadingLetterTemplateAuditDo::getSmsType, SmsTypeEnum.READING_LETTER_PLUS.getCode())
                .eq(ReadingLetterTemplateAuditDo::getStatus, AuditStatus.SUCCESS.getCode())
                .eq(ReadingLetterTemplateAuditDo::getOperatorCode, readingLetterDetail.getOperator());
        ReadingLetterTemplateAuditDo readingLetterPlusDo = readingLetterTemplateAuditDao.selectOne(auditWrapper);
        if (Objects.isNull(readingLetterPlusDo)) {
            throw new BizException(AuthError.READING_LETTER_PLUS_STATUS_ERROR);
        }

        //有效期
        Date createDate = new Date();
        DateTime dateTime = DateUtil.offsetDay(createDate, readingLetterShortUrlAddReq.getDurationOfValidity());
        readingLetterShortUrlDo.setValidityDate(dateTime.toJdkDate());
        readingLetterShortUrlDo.setCreateTime(createDate);
        readingLetterShortUrlDo.setCustomerId(userId);
        readingLetterShortUrlDo.setCreator(userId);

        //根据返回结果修改短链记录
        TemplateDataResp applyShortUrlResultVoTemplateDataResp = new TemplateDataResp();
        boolean success = false;
        try {
            //阅信+账号申请短链
            SendParam result = new SendParam();
            applyShortUrlResultVoTemplateDataResp = doApplyShortUrl(readingLetterDetail, readingLetterShortUrlAddReq, readingLetterPlusDo.getPlatformTemplateId());
            success = applyShortUrlResultVoTemplateDataResp.getCode() == 200;
            if (Objects.nonNull(applyShortUrlResultVoTemplateDataResp.getData())) {
                JSONObject data = (JSONObject) applyShortUrlResultVoTemplateDataResp.getData();
                ApplyShortUrlResultVo applyShortUrlResultVo = JSONObject.parseObject(data.toString(), ApplyShortUrlResultVo.class);
                log.info("申请短链结果:{}", applyShortUrlResultVoTemplateDataResp);
                result = applyShortUrlResultVo.getParamList().get(0);
                log.info("申请短链结果SendParam result:{}", result);
                success = applyShortUrlResultVoTemplateDataResp.getCode() == 200 || "0".equals(applyShortUrlResultVo.getParamList().get(0).getResultCode());
            }
            readingLetterShortUrlDo.setAuditStatus(success ? 1 : 2);
            readingLetterShortUrlDo.setShortUrl(result.getAimUrl());
            readingLetterShortUrlDo.setShortUrlId(result.getAimUrlId());
            readingLetterShortUrlDo.setPrice(reduceBalanceResp.getPrice());
            readingLetterShortUrlDo.setTarrifId(reduceBalanceResp.getTarrifId());
        } catch (Exception e) {
            log.error("申请短链出错 error", e);
            readingLetterShortUrlDo.setAuditStatus(2);
            readingLetterShortUrlDo.setRemark(applyShortUrlResultVoTemplateDataResp.getMessage());
        } finally {
            //申请失败，返还金额
            if (!success && SessionContextUtil.getUser().getPayType() == CustomerPayType.PREPAY) {
                customerApi.addBalance(readingLetterDetail.getCustomerId(), reduceBalanceResp.getDeductAmount());
            }
        }
        readingLetterShortUrlDao.insert(readingLetterShortUrlDo);
    }

    ReduceBalanceResp deduct(CspReadingLetterDetailResp cspReadingLetterDetailResp, ReadingLetterShortUrlDo readingLetterShortUrlDo) {
        //获取客户账号信息
        UserInfoVo userInfoVo = customerApi.getByCustomerId(cspReadingLetterDetailResp.getCustomerId());
        if (ObjUtil.isNull(userInfoVo)) {
            throw new BizException(AuthCenterError.ACCOUNT_USER_ABSENT);
        }
        if (ObjUtil.isNull(userInfoVo.getBalance()) || userInfoVo.getBalance() <= 0L) {
            throw new BizException(AuthCenterError.BALANCE_EMPTY);
        }
        //获取资费
        RechargeTariffDetailResp tariff = chargeTariffService.getRechargeTariff(cspReadingLetterDetailResp.getAccountId());
        if (ObjUtil.isNull(tariff)) {
            throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), cspReadingLetterDetailResp.getAccountName()));
        }
        Integer price = tariff.getPriceOfTariffType(TariffTypeEnum.READING_LETTER_PLUS_PARSE.getCode());
        if (ObjUtil.isNull(price)) {
            throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), cspReadingLetterDetailResp.getAccountName()));
        }
        //扣减余额
        ReduceBalanceResp reduceBalanceResp = customerApi.reduceBalance(userInfoVo.getCustomerId(), price.longValue(), readingLetterShortUrlDo.getRequestParseNumber().longValue(), false);
        reduceBalanceResp.setPrice(price);
        reduceBalanceResp.setTarrifId(tariff.getId());
        return reduceBalanceResp;
    }

    @Override
    public PageResult<ReadingLetterShortUrlVo> list(ReadingLetterShortUrlListReq readingLetterShortUrlListReq) {
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        Page<ReadingLetterShortUrlVo> page = new Page<>(readingLetterShortUrlListReq.getPageNo(), readingLetterShortUrlListReq.getPageSize());
        page.setOrders(OrderItem.descs("create_time"));

        String templateName = readingLetterShortUrlListReq.getTemplateName();
        List<Long> templateIdList = CollectionUtil.newArrayList();
        if (StrUtil.isNotBlank(templateName)) {
            //找到对应的阅信+ 模板
            LambdaQueryWrapper<ReadingLetterTemplateDo> templateDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
            templateDoLambdaQueryWrapper.like(ReadingLetterTemplateDo::getTemplateName, templateName)
                    .eq(ReadingLetterTemplateDo::getCustomerId, customerId);
            List<ReadingLetterTemplateDo> readingLetterTemplates = readingLetterTemplateService.list(templateDoLambdaQueryWrapper);
            if (!CollectionUtil.isEmpty(readingLetterTemplates)) {
                templateIdList = readingLetterTemplates.stream()
                        .map(ReadingLetterTemplateDo::getId)
                        .collect(Collectors.toList());
            } else {
                return PageResult.empty();
            }
        }
        if (CollectionUtil.isNotEmpty(templateIdList)) {
            readingLetterShortUrlDao.selectShortUrl(customerId,
                    templateIdList,
                    readingLetterShortUrlListReq.getOperatorCode(),
                    readingLetterShortUrlListReq.getAuditStatus(),
                    readingLetterShortUrlListReq.getTaskStatus(),
                    page);
        } else {
            readingLetterShortUrlDao.selectShortUrlWithoutTemplateId(customerId,
                    readingLetterShortUrlListReq.getOperatorCode(),
                    readingLetterShortUrlListReq.getAuditStatus(),
                    readingLetterShortUrlListReq.getTaskStatus(),
                    page);
        }

        List<ReadingLetterShortUrlVo> records = page.getRecords();

        records.forEach(readingLetterShortUrlVo -> {
            //审核通过,设置任务状态等
            if (readingLetterShortUrlVo.getAuditStatus().equals(1)) {
                Date validityDate = readingLetterShortUrlVo.getValidityDate();
                Integer resolvedNumber = readingLetterShortUrlVo.getResolvedNumber();
                Integer requestParseNumber = readingLetterShortUrlVo.getRequestParseNumber();
                if (requestParseNumber > resolvedNumber && DateTime.now().isBefore(validityDate)) {
                    readingLetterShortUrlVo.setTaskStatus(1);
                } else if (requestParseNumber > resolvedNumber && DateTime.now().isAfterOrEquals(validityDate)) {
                    readingLetterShortUrlVo.setTaskStatus(2);
                } else if (requestParseNumber.equals(resolvedNumber)) {
                    readingLetterShortUrlVo.setTaskStatus(3);
                }
            }
        });
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public ReadingLetterShortUrlDetailVo search(ReadingLetterShortUrlSearchReq readingLetterShortUrlSearchReq) {
        Long id = readingLetterShortUrlSearchReq.getId();
        if (Objects.nonNull(id)) {
            ReadingLetterShortUrlDo readingLetterShortUrlDo = readingLetterShortUrlDao.selectById(id);
            if (Objects.nonNull(readingLetterShortUrlDo)) {
                ReadingLetterShortUrlDetailVo readingLetterShortUrlDetailVo = new ReadingLetterShortUrlDetailVo();
                BeanUtil.copyProperties(readingLetterShortUrlDo, readingLetterShortUrlDetailVo);
                return readingLetterShortUrlDetailVo;
            }
        }
        return null;
    }

    @Override
    public void reApply(ReadingLetterShortUrlReApplyReq readingLetterShortUrlReApplyReq) {
        Long id = readingLetterShortUrlReApplyReq.getId();
        ReadingLetterShortUrlDo fail = readingLetterShortUrlDao.selectById(id);
        //只有审核失败的短链可以重新申请
        if (fail.getAuditStatus() != 2) {
            throw new BizException(AuthError.READING_LETTER_SHORT_URL_STATUS_ERROR);
        }

        String userId = SessionContextUtil.getUser().getUserId();
        String accountId = readingLetterShortUrlReApplyReq.getAccountId();
        //找到申请锻炼的账号
        // (阅信+账号)
        CspReadingLetterDetailResp readingLetterDetail = cspReadingLetterAccountService.selectOne(accountId, userId);
        if (Objects.isNull(readingLetterDetail)) {
            throw new BizException(AuthError.READING_LETTER_PLUS_ACCOUNT_NOT_EXIST);
        }

        Long templateId = readingLetterShortUrlReApplyReq.getTemplateId();
        // 模板
        ReadingLetterTemplateDo readingLetterTemplateDo = readingLetterTemplateDao.selectById(templateId);
        if (Objects.isNull(readingLetterTemplateDo)) {
            throw new BizException(AuthError.READING_LETTER_TEMPLATE_NOT_EXIST);
        }

        //阅信+模板
        LambdaQueryWrapper<ReadingLetterTemplateAuditDo> auditWrapper = new LambdaQueryWrapper<>();
        auditWrapper.eq(ReadingLetterTemplateAuditDo::getCustomerId, userId)
                .eq(ReadingLetterTemplateAuditDo::getTemplateId, templateId)
                .eq(ReadingLetterTemplateAuditDo::getSmsType, SmsTypeEnum.READING_LETTER_PLUS.getCode())
                .eq(ReadingLetterTemplateAuditDo::getStatus, AuditStatus.SUCCESS.getCode())
                .eq(ReadingLetterTemplateAuditDo::getOperatorCode, readingLetterDetail.getOperator());
        ReadingLetterTemplateAuditDo readingLetterPlusDo = readingLetterTemplateAuditDao.selectOne(auditWrapper);
        if (Objects.isNull(readingLetterPlusDo)) {
            throw new BizException(AuthError.READING_LETTER_PLUS_STATUS_ERROR);
        }

        ReadingLetterShortUrlDo readingLetterShortUrlDo = new ReadingLetterShortUrlDo();
        BeanUtil.copyProperties(readingLetterShortUrlReApplyReq, readingLetterShortUrlDo);

        readingLetterShortUrlDo.setTemplateType(readingLetterTemplateDo.getTemplateType());
        Date now = new Date();
        readingLetterShortUrlDo.setCreateTime(now);
        //有效期
        DateTime dateTime = DateUtil.offsetDay(now, readingLetterShortUrlReApplyReq.getDurationOfValidity());
        readingLetterShortUrlDo.setValidityDate(dateTime.toJdkDate());

        TemplateDataResp applyShortUrlResultVoTemplateDataResp = new TemplateDataResp();
        SendParam result = new SendParam();
        //扣除费用
        ReduceBalanceResp reduceBalanceResp = null;
        //预付费客户才进行扣费
        if (SessionContextUtil.getUser().getPayType() == CustomerPayType.PREPAY) {
            reduceBalanceResp = deduct(readingLetterDetail, readingLetterShortUrlDo);
        } else {
            //获取资费
            RechargeTariffDetailResp tariff = chargeTariffService.getRechargeTariff(readingLetterDetail.getAccountId());
            if (ObjUtil.isNull(tariff)) {
                throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), readingLetterDetail.getAccountName()));
            }
            reduceBalanceResp = new ReduceBalanceResp();
        }
        boolean success = false;
        try {
            //阅信+账号申请短链
            applyShortUrlResultVoTemplateDataResp = doApplyShortUrl(readingLetterDetail, readingLetterShortUrlReApplyReq, readingLetterPlusDo.getPlatformTemplateId());
            success = applyShortUrlResultVoTemplateDataResp.getCode() == 200;
            if (Objects.nonNull(applyShortUrlResultVoTemplateDataResp.getData())) {
                JSONObject data = (JSONObject) applyShortUrlResultVoTemplateDataResp.getData();
                ApplyShortUrlResultVo applyShortUrlResultVo = JSONObject.parseObject(data.toString(), ApplyShortUrlResultVo.class);

                result = applyShortUrlResultVo.getParamList().get(0);
                success = applyShortUrlResultVoTemplateDataResp.getCode() == 200 || "0".equals(applyShortUrlResultVo.getParamList().get(0).getResultCode());
            }
            readingLetterShortUrlDo.setAuditStatus(success ? 1 : 2);
            readingLetterShortUrlDo.setShortUrl(result.getAimUrl());
            readingLetterShortUrlDo.setShortUrlId(result.getAimUrlId());
            readingLetterShortUrlDo.setPrice(reduceBalanceResp.getPrice());
            readingLetterShortUrlDo.setTarrifId(reduceBalanceResp.getTarrifId());
            readingLetterShortUrlDo.setAmount(reduceBalanceResp.getDeductAmount());
        } catch (Exception e) {
            log.error("申请短链出错 error", e);
            readingLetterShortUrlDo.setAuditStatus(2);
            readingLetterShortUrlDo.setRemark(applyShortUrlResultVoTemplateDataResp.getMessage());
        } finally {
            //申请失败，返还金额
            if (!success && SessionContextUtil.getUser().getPayType() == CustomerPayType.PREPAY) {
                customerApi.addBalance(readingLetterDetail.getCustomerId(), reduceBalanceResp.getDeductAmount());
            }
        }
        //修改申请记录
        readingLetterShortUrlDao.updateById(readingLetterShortUrlDo);
    }

    @Override
    public ReadingLetterShortUrlVo findShortUrl(String shortUrl) {
        LambdaQueryWrapper<ReadingLetterShortUrlDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReadingLetterShortUrlDo::getShortUrl, shortUrl);
        queryWrapper.eq(ReadingLetterShortUrlDo::getDeleted, 0);
        ReadingLetterShortUrlDo readingLetterShortUrlDo = readingLetterShortUrlDao.selectOne(queryWrapper);
        if (Objects.nonNull(readingLetterShortUrlDo)) {
            ReadingLetterShortUrlVo readingLetterShortUrlVo = new ReadingLetterShortUrlVo();
            BeanUtil.copyProperties(readingLetterShortUrlDo, readingLetterShortUrlVo);
            return readingLetterShortUrlVo;
        }
        return null;
    }

    @Override
    public Map<Long, ReadingLetterShortUrlVo> findShortUrls(List<Long> urlIds) {
//        String urlIdsString = urlIds.stream().map(Object::toString).collect(Collectors.joining(","));
        List<ReadingLetterShortUrlVo> readingLetterShortUrlDos = readingLetterShortUrlDao.selectListByIds(urlIds);

        HashMap<Long, ReadingLetterShortUrlVo> result = new HashMap<>();
        readingLetterShortUrlDos.forEach(readingLetterShortUrlVo -> {
            result.put(readingLetterShortUrlVo.getId(), readingLetterShortUrlVo);
        });
        return result;
    }


    @Override
    public void handleParseRecord(Long shortUrlId) {
        int i = readingLetterShortUrlDao.addResolvedNumber(shortUrlId);
        log.info("shortUrlId:{}, 解析次数+{}", shortUrlId, i);
    }

    @Override
    public List<ReadingLetterShortUrlVo> findAvailableShortUrls(String nameOrUrl) {
        String customerId = SessionContextUtil.getUser().getUserId();
        List<ReadingLetterShortUrlDo> shortUrlDos = readingLetterShortUrlDao.findAvailableShortUrls(customerId, nameOrUrl);
        if (Objects.nonNull(shortUrlDos) && !shortUrlDos.isEmpty()) {
            return BeanUtil.copyToList(shortUrlDos, ReadingLetterShortUrlVo.class);
        }
        return Collections.emptyList();
    }

    @Override
    public QueryShortUrlResultVo findShortUrlFromPlatform(Long id) {

        //通过id找到readingLetterShortUrl
        ReadingLetterShortUrlDo readingLetterShortUrlDo = readingLetterShortUrlDao.selectById(id);
        String platFormShortUrlId = readingLetterShortUrlDo.getShortUrlId();
        //通过accountId找到 阅信+账号
        String accountId = readingLetterShortUrlDo.getAccountId();
        CspReadingLetterDetailResp readingLetterAccount = cspReadingLetterAccountService.selectOne(accountId, SessionContextUtil.getUser().getUserId());
        if (readingLetterAccount == null) {
            throw new BizException("阅信+账户异常");
        }
        TemplateDataResp queryShortUrlResultVo = queryShortUrlResultVo(platFormShortUrlId, readingLetterAccount);
        return JSONObject.parseObject(queryShortUrlResultVo.getData().toString(), QueryShortUrlResultVo.class);
    }

    @Override
    public List<ReadingLetterShortUrlVo> findShortUrlByTemplateId(Long templateId) {
        if (Objects.nonNull(templateId)) {
            LambdaQueryWrapper<ReadingLetterShortUrlDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ReadingLetterShortUrlDo::getTemplateId, templateId)
                    .eq(ReadingLetterShortUrlDo::getAuditStatus, 1)
                    .select(ReadingLetterShortUrlDo::getShortUrl);
            List<ReadingLetterShortUrlDo> readingLetterShortUrlDos = readingLetterShortUrlDao.selectList(queryWrapper);
            if (Objects.nonNull(readingLetterShortUrlDos) && !readingLetterShortUrlDos.isEmpty()) {
                return BeanUtil.copyToList(readingLetterShortUrlDos, ReadingLetterShortUrlVo.class);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<ReadingLetterShortUrlVo> findExpiredUnprocessedShortUrl() {
        List<ReadingLetterShortUrlVo> expiredUnprocessedShortUrl = readingLetterShortUrlDao.findExpiredUnprocessedShortUrl(new Date());

        if (CollectionUtil.isEmpty(expiredUnprocessedShortUrl)) {
            return expiredUnprocessedShortUrl;
        }
        LambdaUpdateWrapper<ReadingLetterShortUrlDo> updateWrapper = new LambdaUpdateWrapper<>();

        updateWrapper.in(ReadingLetterShortUrlDo::getId, expiredUnprocessedShortUrl.stream().map(ReadingLetterShortUrlVo::getId).collect(Collectors.toList()))
                .set(ReadingLetterShortUrlDo::getProcessed, ProcessStatusEnum.PROCESSED)
                .set(ReadingLetterShortUrlDo::getUpdateTime, new Date());
        readingLetterShortUrlDao.update(null, updateWrapper);
        return expiredUnprocessedShortUrl;
    }

    private TemplateDataResp<QueryShortUrlResultVo> queryShortUrlResultVo(String platFormShortUrlId, CspReadingLetterDetailResp readingLetterAccount) {
        //适配蜂动账号
        AccountManagementResp account = new AccountManagementResp();
        account.setAppId(readingLetterAccount.getAppId());
        account.setAppKey(readingLetterAccount.getAppKey());
        account.setAgentId(readingLetterAccount.getAgentId());

        return platfomManageReadingLetterService.queryReadingLetterShortUrl(platFormShortUrlId, account);
    }

    //阅信+账号申请短链
    private TemplateDataResp<ApplyShortUrlResultVo> doApplyShortUrl(CspReadingLetterDetailResp readingLetterDetail
            , ReadingLetterShortUrlAddReq readingLetterShortUrlAddReq, String platformTemplateId) {

        //阅信+ 适配蜂动账号
        AccountManagementResp account = new AccountManagementResp();
        account.setAppId(readingLetterDetail.getAppId());
        account.setAppKey(readingLetterDetail.getAppKey());
        account.setAgentId(readingLetterDetail.getAgentId());

        ArrayList<String> signs = CollectionUtil.newArrayList(readingLetterShortUrlAddReq.getSigns().split(","));
        String domain = readingLetterShortUrlAddReq.getDomain();
        //发送创建模板请求
        ApplyShortUrlEntity applyShortUrl = ApplyShortUrlEntity.builder()
                .smsSigns(signs)
                .codeType("GROUP_SEND")
                .expireTime(readingLetterShortUrlAddReq.getDurationOfValidity())
                .showTimes(readingLetterShortUrlAddReq.getRequestParseNumber())
                .templateId(Long.parseLong(platformTemplateId))
                .domain(StrUtil.isNotBlank(domain) ? domain : null)
                .build();
        //custId 为手机编码
//        String phone = "86" + SessionContextUtil.getUser().getPhone();
//        String custId = MyBase64Util.encodeToString(phone);

        String shortCode = null;
        String customUrl = null;
//        0默认域名  1自定义域名
        if (readingLetterShortUrlAddReq.getDomainNameType() == 1) {
            String jumpLink = readingLetterShortUrlAddReq.getJumpLink();
            if (StrUtil.isNotBlank(jumpLink)) {
                shortCode = jumpLink.replace(domain, "");
                shortCode = shortCode.replace("/", "");
            }
        } else {
            String userDefinedJumpLink = readingLetterShortUrlAddReq.getUserDefinedJumpLink();
            customUrl = StrUtil.isNotBlank(userDefinedJumpLink) ? userDefinedJumpLink : null;
        }
        Params params = new Params(readingLetterShortUrlAddReq.getAccountId(), null, shortCode, customUrl);
        applyShortUrl.setParamList(CollectionUtil.newArrayList(params));

        TemplateDataResp<ApplyShortUrlResultVo> templateDataResp = platfomManageReadingLetterService.createReadingLetterShortUrl(applyShortUrl, account);
        return templateDataResp;
    }

}
