package com.citc.nce.auth.csp.smsTemplate.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.contactbacklist.service.ContactBackListService;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListPageReq;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListResp;
import com.citc.nce.auth.csp.mediasms.template.enums.AuditStatus;
import com.citc.nce.auth.csp.sms.account.dao.CspSmsAccountDao;
import com.citc.nce.auth.csp.sms.account.entity.CspSmsAccountDo;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountQueryDetailReq;
import com.citc.nce.auth.csp.sms.signature.dao.CspSmsSignatureDao;
import com.citc.nce.auth.csp.sms.signature.entity.CspSmsAccountSignatureDo;
import com.citc.nce.auth.csp.sms.signature.service.CspSmsSignatureService;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureResp;
import com.citc.nce.auth.csp.smsTemplate.dao.SmsTemplateMapper;
import com.citc.nce.auth.csp.smsTemplate.entity.SmsTemplateAuditDo;
import com.citc.nce.auth.csp.smsTemplate.entity.SmsTemplateDo;
import com.citc.nce.auth.csp.smsTemplate.enums.SmsAuditStatus;
import com.citc.nce.auth.csp.smsTemplate.service.SmsTemplateAuditService;
import com.citc.nce.auth.csp.smsTemplate.service.SmsTemplateService;
import com.citc.nce.auth.csp.smsTemplate.vo.*;
import com.citc.nce.auth.readingLetter.shortUrl.service.ReadingLetterShortUrlService;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.RsaUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.conf.CommonKeyPairConfig;
import com.citc.nce.robot.api.RobotGroupSendPlansApi;
import com.citc.nce.robot.api.SmsPlatformApi;
import com.citc.nce.robot.req.RobotGroupSendPlanIdReq;
import com.citc.nce.robot.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.citc.nce.common.core.exception.GlobalErrorCode.USER_AUTH_ERROR;

/**
 * @author jiancheng
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SmsTemplateServiceImpl extends ServiceImpl<SmsTemplateMapper, SmsTemplateDo> implements SmsTemplateService {

    @Resource
    private CommonKeyPairConfig keyPairConfig;

    // 加密算法

    private final SmsTemplateAuditService smsTemplateAuditService;
    private final RobotGroupSendPlansApi robotGroupSendPlansApi;
    private final CspSmsSignatureService signatureService;
    private final CspSmsSignatureDao cspSmsSignatureDao;
    private final ContactBackListService contactBackListService;
    private final CspSmsAccountDao cspSmsAccountDao;
    private final SmsPlatformApi smsPlatformApi;

    private CspSmsAccountDo getCustomerSmsAccountByAccountId(String accountId) {
        return cspSmsAccountDao.selectOne(
                Wrappers.<CspSmsAccountDo>lambdaQuery()
                        .eq(CspSmsAccountDo::getAccountId, accountId)
                        .eq(CspSmsAccountDo::getCustomerId, SessionContextUtil.getUser().getUserId())
        );
    }

    /**
     * 新增模板
     *
     * @param smsTemplateAddVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTemplate(SmsTemplateAddVo smsTemplateAddVo) {
        String templateName = smsTemplateAddVo.getTemplateName();
        String accountId = smsTemplateAddVo.getAccountId();
        if (isRepeatTemplateName(accountId, templateName))
            throw new BizException(500, "模板名称重复");
        CspSmsAccountDo smsAccountDetailResp = this.getCustomerSmsAccountByAccountId(accountId);
        if (smsAccountDetailResp == null || smsAccountDetailResp.getStatus() == null || !smsAccountDetailResp.getStatus().equals(1)) {
            throw new BizException(500, "该短信账号已删除或禁用，请重新选择");
        }

        CspSmsAccountSignatureDo cspSmsAccountSignatureDo = cspSmsSignatureDao.selectById(smsTemplateAddVo.getSignatureId());
        if (cspSmsAccountSignatureDo == null || cspSmsAccountSignatureDo.getDeleted() == 1) {
            throw new BizException(500, "该签名已被删除，请重新选择");
        }

        String userId = SessionContextUtil.getUser().getUserId();
        SmsTemplateDo smsTemplateDo = new SmsTemplateDo();
        BeanUtils.copyProperties(smsTemplateAddVo, smsTemplateDo);
        smsTemplateDo.setCustomerId(userId);

        try {
            this.save(smsTemplateDo);
        } catch (DataIntegrityViolationException e) {
            log.error("模板保存错误", e);
            throw new BizException(500, "模板保存错误");
        }
        //保存送审信息
        SmsTemplateAuditDo smsTemplateAuditDo = new SmsTemplateAuditDo();
        smsTemplateAuditDo.setSmsTemplateId(smsTemplateDo.getId());
        smsTemplateAuditDo.setStatus(SmsAuditStatus.WAIT_POST);
        this.smsTemplateAuditService.save(smsTemplateAuditDo);

        if (smsTemplateAddVo.getSubmitForAudit()) {
            String signature = this.getSignatureById(smsTemplateAddVo.getSignatureId());
            this.reportTemplate(smsTemplateDo.getId(), signature, smsTemplateDo.getAccountId(), smsTemplateDo.getContent(), smsTemplateDo.getTemplateType());
        }
    }

    public void reportTemplate(Long templateId, String signature, String accountId, String content, Integer templateType) {
        CspSmsAccountQueryDetailReq cspSmsAccountQueryDetailReq = new CspSmsAccountQueryDetailReq();
        cspSmsAccountQueryDetailReq.setAccountId(accountId);
        CspSmsAccountDo smsAccountDetailResp = this.getCustomerSmsAccountByAccountId(accountId);
        if (smsAccountDetailResp == null || smsAccountDetailResp.getStatus() == null || !smsAccountDetailResp.getStatus().equals(1)) {
            throw new BizException(500, "该短信账号已删除或禁用，请重新选择");
        }
        //判断是否已经送审
        boolean updateDenied = smsTemplateAuditService.lambdaQuery()
                .eq(SmsTemplateAuditDo::getSmsTemplateId, templateId)
                .ne(SmsTemplateAuditDo::getStatus, AuditStatus.WAIT_POST)
                .exists();
        if (updateDenied) {
            throw new BizException(500, "请不要重复送审");
        }

        Map<String, String> templateMap = new HashMap<String, String>();
        String contentNew = "";
        //个性模板-变量模板
        SmsTemplateContentVo smsTemplateContentVo = JSONObject.parseObject(content, SmsTemplateContentVo.class);
        List<SmsTemplateDataVo> smsTemplateDataVos = smsTemplateContentVo.getNames();
        contentNew = smsTemplateContentVo.getValue();
        if (templateType == 2) {
            if (smsTemplateDataVos != null && !smsTemplateDataVos.isEmpty()) {
                for (SmsTemplateDataVo smsTemplateDataVo : smsTemplateDataVos) {
                    contentNew = contentNew.replace("{{" + smsTemplateDataVo.getId() + "}}", "{#" + smsTemplateDataVo.getName() + "#}");
                }
            } else {
                throw new BizException(500, "个性模板必须选择变量");
            }
        }
        templateMap.put("templateContent", "【" + signature + "】" + contentNew);
        templateMap.put("requestTime", String.valueOf(System.currentTimeMillis()));
        templateMap.put("requestValidPeriod", "30");
        log.info("=============begin createTemplateSMS==================");
        SmsTemplateReq smsTemplateReq = new SmsTemplateReq();
        smsTemplateReq.setAppId(smsAccountDetailResp.getAppId());
        smsTemplateReq.setSecretKey(RsaUtil.decryptByPrivateKey(keyPairConfig.getPrivateKey(), smsAccountDetailResp.getAppSecret()));
        smsTemplateReq.setTemplateMap(templateMap);
        String platformTemplateId = smsPlatformApi.reportTemplate(smsTemplateReq);
        if (StringUtils.isNotBlank(platformTemplateId)) {
            //更新平台Id
            LambdaUpdateWrapper<SmsTemplateAuditDo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(SmsTemplateAuditDo::getPlatformTemplateId, platformTemplateId);
            updateWrapper.set(SmsTemplateAuditDo::getStatus, 1);
            updateWrapper.eq(SmsTemplateAuditDo::getSmsTemplateId, templateId);
            smsTemplateAuditService.update(updateWrapper);

            LambdaUpdateWrapper<SmsTemplateDo> smsTemplateDoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            smsTemplateDoLambdaUpdateWrapper.set(SmsTemplateDo::getPlatformTemplateId, platformTemplateId);
            smsTemplateDoLambdaUpdateWrapper.eq(SmsTemplateDo::getId, templateId);
            this.update(smsTemplateDoLambdaUpdateWrapper);

        } else {
            throw new BizException(500, "送审失败");
        }
        log.info("=============end createTemplateSMS==================");
    }

    /**
     * 查询用户签名
     *
     * @param signatureId
     * @return
     */
    public String getSignatureById(Long signatureId) {
        //判断签名是否为空
        List<CspSmsSignatureResp> signatures = signatureService.getSignatureByIdsDelete(Collections.singletonList(signatureId));
        if (CollectionUtils.isNotEmpty(signatures))
            return signatures.get(0).getSignature();
        return null;
    }

    /**
     * 修改模板
     *
     * @param smsTemplateUpdateVo
     */
    @Override
    public void updateTemplate(SmsTemplateUpdateVo smsTemplateUpdateVo) {
        //查询该用户是否有此模板权限
        checkSmsTemplateAuth(smsTemplateUpdateVo.getId(), false);
        boolean updateDenied = smsTemplateAuditService.lambdaQuery()
                .eq(SmsTemplateAuditDo::getSmsTemplateId, smsTemplateUpdateVo.getId())
                .ne(SmsTemplateAuditDo::getStatus, AuditStatus.WAIT_POST)
                .exists();
        if (updateDenied) {
            throw new BizException(500, "模板已经送审，不能修改");
        }
        CspSmsAccountDo smsAccountDetailResp = this.getCustomerSmsAccountByAccountId(smsTemplateUpdateVo.getAccountId());
        if (smsAccountDetailResp == null || smsAccountDetailResp.getStatus() == null || !smsAccountDetailResp.getStatus().equals(1)) {
            throw new BizException(500, "该短信账号已删除或禁用，请重新选择");
        }

        CspSmsAccountSignatureDo cspSmsAccountSignatureDo = cspSmsSignatureDao.selectById(smsTemplateUpdateVo.getSignatureId());
        if (cspSmsAccountSignatureDo == null || cspSmsAccountSignatureDo.getDeleted() == 1) {
            throw new BizException(500, "该签名已被删除，请重新选择");
        }

        SmsTemplateDo smsTemplateDo = new SmsTemplateDo();
        BeanUtils.copyProperties(smsTemplateUpdateVo, smsTemplateDo);
        this.updateById(smsTemplateDo);

        if (smsTemplateUpdateVo.getSubmitForAudit()) {
            String signature = this.getSignatureById(smsTemplateDo.getSignatureId());
            this.reportTemplate(smsTemplateDo.getId(), signature, smsTemplateDo.getAccountId(), smsTemplateDo.getContent(), smsTemplateDo.getTemplateType());
        }
    }

    /**
     * 查询用户是否有此模板的权限
     *
     * @param templateId 模板ID
     */
    private void checkSmsTemplateAuth(Long templateId, Boolean delete) {
        if (templateId != null) {
            String userId = SessionContextUtil.getUser().getUserId();
            boolean exists;
            if (!delete) {
                LambdaQueryWrapper<SmsTemplateDo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SmsTemplateDo::getCustomerId, userId);
                queryWrapper.eq(SmsTemplateDo::getId, templateId);
                exists = baseMapper.exists(queryWrapper);
            } else {
                exists = this.baseMapper.exists(templateId, userId);
            }
            if (!exists)
                throw new BizException(USER_AUTH_ERROR);
        } else {
            throw new BizException(USER_AUTH_ERROR);
        }
    }

    @Override
    public boolean isRepeatTemplateName(String accountId, String templateName) {
        return this.lambdaQuery()
                .eq(SmsTemplateDo::getAccountId, accountId)
                .eq(SmsTemplateDo::getTemplateName, templateName)
                .exists();
    }


    @Override
    public PageResult<SmsTemplateSimpleVo> searchTemplate(SmsTemplateSearchVo smsTemplateSearchVo) {
        Page<SmsTemplateSimpleVo> smsTemplateSimpleVoPage = Page.of(smsTemplateSearchVo.getPageNo(), smsTemplateSearchVo.getPageSize());
        smsTemplateSimpleVoPage.setOrders(OrderItem.descs("create_time"));
        Page<SmsTemplateSimpleVo> page;
        if (StringUtils.isNotBlank(smsTemplateSearchVo.getAccountId()) && smsTemplateSearchVo.getAccountId().equals("-1")) {
            page = this.baseMapper.searchTemplateOther(SessionContextUtil.getUser().getUserId(),
                    smsTemplateSearchVo.getTemplateName(), smsTemplateSearchVo.getTemplateType(), smsTemplateSearchVo.getAccountId(), smsTemplateSearchVo.getStatus(), smsTemplateSimpleVoPage);
        } else {
            page = this.baseMapper.searchTemplate(SessionContextUtil.getUser().getUserId(),
                    smsTemplateSearchVo.getTemplateName(), smsTemplateSearchVo.getTemplateType(), smsTemplateSearchVo.getAccountId(), smsTemplateSearchVo.getStatus(), smsTemplateSimpleVoPage);
        }
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public SmsTemplateDetailVo getTemplateInfo(Long templateId, Boolean delete) {
        //查询该用户是否有此模板权限
        checkSmsTemplateAuth(templateId, delete);
        return getTemplateInfoInner(templateId, delete);
    }

    @Override
    public SmsTemplateDetailVo getTemplateInfoInner(Long templateId, Boolean delete) {
        SmsTemplateDetailVo smsTemplateDetailVo = new SmsTemplateDetailVo();
        SmsTemplateDo smsTemplateDo;
        if (!delete) {
            smsTemplateDo = this.getById(templateId);
        } else {
            smsTemplateDo = this.baseMapper.querySmsTemplate(templateId);
        }
        if (smsTemplateDo == null)
            throw new BizException(500, "模板不存在");
        BeanUtils.copyProperties(smsTemplateDo, smsTemplateDetailVo);
        SmsTemplateAuditDo smsTemplateAuditDo = smsTemplateAuditService.lambdaQuery().eq(SmsTemplateAuditDo::getSmsTemplateId, smsTemplateDo.getId()).one();
        smsTemplateDetailVo.setAudit(smsTemplateAuditDo == null || smsTemplateAuditDo.getStatus() == null ? 0 : smsTemplateAuditDo.getStatus().getValue());
        smsTemplateDetailVo.setSignatureContent(this.getSignatureById(smsTemplateDo.getSignatureId()));

        if (StringUtils.isNotBlank(smsTemplateDetailVo.getSignatureContent())) {
            smsTemplateDetailVo.setSignatureDelete(0);
        } else {
            smsTemplateDetailVo.setSignatureDelete(1);
        }
        CspSmsAccountDo smsAccountDetailResp = cspSmsAccountDao.selectOne(
                Wrappers.<CspSmsAccountDo>lambdaQuery()
                        .eq(CspSmsAccountDo::getAccountId, smsTemplateDo.getAccountId())
        );
        if (smsAccountDetailResp == null || smsAccountDetailResp.getStatus() == null || !smsAccountDetailResp.getStatus().equals(1)) {
            smsTemplateDetailVo.setAccountDelete(1);
        } else {
            smsTemplateDetailVo.setAccountDelete(0);
        }

        return smsTemplateDetailVo;
    }


    /**
     * 慢查询-----------water
     * @param platformTemplateId
     * @return
     */
    @Override
    public SmsTemplateDetailVo getTemplateInfoByPlatformTemplateId(String platformTemplateId) {
        SmsTemplateDo smsTemplateDo = this.lambdaQuery().eq(SmsTemplateDo::getPlatformTemplateId, platformTemplateId).one();
        if (Objects.isNull(smsTemplateDo)) return null;

        SmsTemplateDetailVo smsTemplateDetailVo = new SmsTemplateDetailVo();
        BeanUtils.copyProperties(smsTemplateDo, smsTemplateDetailVo);
        //查询签名内容
        Long signatureId = smsTemplateDo.getSignatureId();
        if (Objects.nonNull(signatureId)) {
            List<CspSmsAccountSignatureDo> signature = cspSmsSignatureDao.getCspSmsAccountSignatureDoList(signatureId + "");
            if (CollectionUtils.isNotEmpty(signature)) {
                smsTemplateDetailVo.setSignatureContent(signature.get(0).getSignature());
            }
        }
        SmsTemplateAuditDo smsTemplateAuditDo = smsTemplateAuditService.lambdaQuery().eq(SmsTemplateAuditDo::getPlatformTemplateId, platformTemplateId).one();
        if (smsTemplateAuditDo != null) {
            smsTemplateDetailVo.setAudit(smsTemplateAuditDo.getStatus().getValue());
        }
        return smsTemplateDetailVo;
    }

    /**
     * 更新模板审核状态
     *
     * @param smsTemplateAuditUpdateVo
     */
    @Override
    public void updateAuditStatus(SmsTemplateAuditUpdateVo smsTemplateAuditUpdateVo) {
        if (!this.lambdaQuery().eq(SmsTemplateDo::getPlatformTemplateId, smsTemplateAuditUpdateVo.getPlatformTemplateId()).exists()) {
            throw new BizException(500, "模板不存在");
        }
        switch (smsTemplateAuditUpdateVo.getAudits()) {
            case 0:
                smsTemplateAuditUpdateVo.setAudits(1);
                break;
            case 1:
                smsTemplateAuditUpdateVo.setAudits(2);
                break;
            case 2:
                smsTemplateAuditUpdateVo.setAudits(3);
                break;
            case -1:
                smsTemplateAuditUpdateVo.setAudits(3);
                break;
        }
        smsTemplateAuditService.lambdaUpdate()
                .eq(SmsTemplateAuditDo::getPlatformTemplateId, smsTemplateAuditUpdateVo.getPlatformTemplateId())
                .set(SmsTemplateAuditDo::getStatus, smsTemplateAuditUpdateVo.getAudits())
                .update();

    }

    @Override
    public void reportTemplate(Long id) {
        SmsTemplateDo smsTemplateDo = this.getById(id);
        if (smsTemplateDo == null)
            throw new BizException(500, "模板不存在");
        if (smsTemplateAuditService.lambdaQuery()
                .eq(SmsTemplateAuditDo::getSmsTemplateId, id)
                .ne(SmsTemplateAuditDo::getStatus, AuditStatus.WAIT_POST)
                .exists())
            throw new BizException(500, "请勿重复送审模板");
        String signature = this.getSignatureById(smsTemplateDo.getSignatureId());
        this.reportTemplate(smsTemplateDo.getId(), signature, smsTemplateDo.getAccountId(), smsTemplateDo.getContent(), smsTemplateDo.getTemplateType());
    }

    @Override
    @Transactional
    public void deleteTemplate(SmsTemplateCommonVo smsTemplateCommonVo) {
        for (Long templateId : smsTemplateCommonVo.getTemplateIds()) {
            //查询该用户是否有此模板权限
            checkSmsTemplateAuth(templateId, false);
            SmsTemplateDo smsTemplateDo = getById(templateId);
            RobotGroupSendPlanIdReq req = new RobotGroupSendPlanIdReq();
            req.setId(templateId);
            req.setType(4);
            if (robotGroupSendPlansApi.checkPlansUserTemplate(req)) {
                throw new BizException(500, smsTemplateDo.getTemplateName() + "模板有计划正在使用此模板，无法删除");
            }
            this.removeById(templateId);
        }
    }

    @Override
    public List<SmsTemplateCheckVo> templateDeleteCheck(SmsTemplateCommonVo smsTemplateCommonVo) {
        List<SmsTemplateCheckVo> smsTemplateCheckVoList = new ArrayList<>();
        for (Long templateId : smsTemplateCommonVo.getTemplateIds()) {
            //查询该用户是否有此模板权限
            checkSmsTemplateAuth(templateId, false);
            RobotGroupSendPlanIdReq req = new RobotGroupSendPlanIdReq();
            req.setId(templateId);
            req.setType(4);
            SmsTemplateCheckVo smsTemplateCheckVo = new SmsTemplateCheckVo();
            smsTemplateCheckVo.setId(templateId);
            if (robotGroupSendPlansApi.checkPlansUserTemplate(req)) {
                smsTemplateCheckVo.setResult(1);
            } else {
                smsTemplateCheckVo.setResult(0);
            }
            smsTemplateCheckVoList.add(smsTemplateCheckVo);
        }
        return smsTemplateCheckVoList;
    }

    @Override
    public List<SmsHaveTemplateAccountVo> getHaveTemplateAccountsByUserId(String customerId) {
        List<SmsHaveTemplateAccountVo> smsHaveTemplateAccountVoList = this.baseMapper.getHaveTemplateAccountsByUserId(customerId);
        SmsHaveTemplateAccountVo smsHaveTemplateAccountVo = smsHaveTemplateAccountVoList.get(0);
        if (smsHaveTemplateAccountVo == null) {
            return null;
        }
        return smsHaveTemplateAccountVoList;
    }

    @Override
    public void refreshAuditStatus(SmsTemplateCommonVo smsTemplateCommonVo) {
        if (smsTemplateCommonVo.getTemplateIds() != null && !smsTemplateCommonVo.getTemplateIds().isEmpty()) {
            StringBuffer message = new StringBuffer();
            Boolean result = false;
            int i = 0;
            for (Long templateId : smsTemplateCommonVo.getTemplateIds()) {
                i++;
                if (templateId == null) {
                    throw new BizException(500, "模板Id为空");
                }
                SmsTemplateDo smsTemplateDo = this.getById(templateId);
                if (Objects.isNull(smsTemplateDo)) {
                    throw new BizException("模板不能为空");
                }
                if (!SessionContextUtil.getLoginUser().getUserId().equals(smsTemplateDo.getCustomerId())) {
                    throw new BizException("不能刷新别人的模板");
                }
                CspSmsAccountDo smsAccountDetailResp = this.getCustomerSmsAccountByAccountId(smsTemplateDo.getAccountId());
                if (smsAccountDetailResp == null || smsAccountDetailResp.getStatus() == null || !smsAccountDetailResp.getStatus().equals(1)) {
                    if (i == smsTemplateCommonVo.getTemplateIds().size()) {
                        message.append("'" + smsTemplateDo.getTemplateName() + "'");
                    } else {
                        message.append("'" + smsTemplateDo.getTemplateName() + "',");
                    }
                    result = true;
                    continue;
                }
                SmsTemplateReq smsTemplateReq = new SmsTemplateReq();
                smsTemplateReq.setAppId(smsAccountDetailResp.getAppId());
                smsTemplateReq.setSecretKey(RsaUtil.decryptByPrivateKey(keyPairConfig.getPrivateKey(), smsAccountDetailResp.getAppSecret()));

                Map<String, String> queryTemplateMap = new HashMap<String, String>();
                queryTemplateMap.put("templateId", smsTemplateDo.getPlatformTemplateId());
                queryTemplateMap.put("requestTime", String.valueOf(System.currentTimeMillis()));
                queryTemplateMap.put("requestValidPeriod", "30");
                smsTemplateReq.setTemplateMap(queryTemplateMap);
                SmsTemplateAuditStatus smsTemplateAuditStatus = smsPlatformApi.queryTemplateStatus(smsTemplateReq);
                if (smsTemplateAuditStatus != null) {
                    SmsTemplateAuditUpdateVo smsTemplateAuditUpdateVo = new SmsTemplateAuditUpdateVo();
                    smsTemplateAuditUpdateVo.setPlatformTemplateId(smsTemplateAuditStatus.getTemplateId());
                    smsTemplateAuditUpdateVo.setAudits(smsTemplateAuditStatus.getStatus());
                    this.updateAuditStatus(smsTemplateAuditUpdateVo);
                }
            }
            if (result) {
                if (smsTemplateCommonVo.getTemplateIds().size() == 1) {
                    throw new BizException(500, "该短信账号已删除或禁用，请重新选择");
                } else {
                    throw new BizException(500, message + "模板账号已被删除或者禁用，其余模板已刷新");
                }

            }
        } else {
            throw new BizException(500, "必须传入模板Id");
        }


    }

    @Override
    public Boolean testSending(SmsTemplateTestSendVo smsTemplateTestSendVo) {
        /**
         * 迭代五 黑名单中的手机号不能进行测试发送
         */
        ContactBackListPageReq req = new ContactBackListPageReq();
        req.setCreator(SessionContextUtil.getUser().getUserId());
        List<String> blackList = contactBackListService.getAllList(req).stream().map(ContactBackListResp::getPhoneNum).collect(Collectors.toList());
        boolean isblack = blackList.stream().anyMatch(black -> org.apache.commons.lang3.StringUtils.equals(black, smsTemplateTestSendVo.getPhone()));
        if (isblack) {
            throw new BizException(500, "黑名单号码，不能发送");
        }
        SmsTemplateSendVo smsTemplateSendVo = new SmsTemplateSendVo();
        BeanUtils.copyProperties(smsTemplateTestSendVo, smsTemplateSendVo);
        List<SmsDeveloperSendPhoneVo> smsDeveloperSendPhoneVoList = new ArrayList<>();
        SmsDeveloperSendPhoneVo smsDeveloperSendPhoneVo = new SmsDeveloperSendPhoneVo();
        smsDeveloperSendPhoneVo.setPhone(smsTemplateTestSendVo.getPhone());
        smsDeveloperSendPhoneVo.setDeveloperSenId(System.currentTimeMillis() + "");
        smsDeveloperSendPhoneVoList.add(smsDeveloperSendPhoneVo);
        smsTemplateSendVo.setSmsDeveloperSendPhoneVoList(smsDeveloperSendPhoneVoList);

        List<SmsTemplateVariable> smsTemplateSendVariableList = this.sending(smsTemplateSendVo);
        if (!smsTemplateSendVariableList.isEmpty()) {

            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<SmsTemplateVariable> sending(SmsTemplateSendVo smsTemplateSendVo) {
        SmsTemplateDo smsTemplateDo;
        //调用类型
        Integer callType = 0;
        //platformTemplateId为空，说明是自定义模板
        if (StringUtils.isEmpty(smsTemplateSendVo.getPlatformTemplateId())) {
            //查询该用户是否有此模板权限
            smsTemplateDo = this.getById(smsTemplateSendVo.getTemplateId());
            if (smsTemplateAuditService.lambdaQuery()
                    .eq(SmsTemplateAuditDo::getSmsTemplateId, smsTemplateSendVo.getTemplateId())
                    .ne(SmsTemplateAuditDo::getStatus, AuditStatus.PASS)
                    .exists() || StringUtils.isBlank(smsTemplateDo.getPlatformTemplateId())) {
                throw new BizException(500, "模板没有通过审核");
            }
            callType = 3;
        } else {
            smsTemplateDo = this.lambdaQuery().eq(SmsTemplateDo::getPlatformTemplateId, smsTemplateSendVo.getPlatformTemplateId()).one();
            callType = 4;
        }
        CspSmsAccountDo smsAccountDetailResp = cspSmsAccountDao.selectOne(
                Wrappers.<CspSmsAccountDo>lambdaQuery()
                        .eq(CspSmsAccountDo::getAccountId, smsTemplateDo.getAccountId())
        );
        if (smsAccountDetailResp == null || smsAccountDetailResp.getStatus() == null || !smsAccountDetailResp.getStatus().equals(1)) {
            throw new BizException(500, "该短信账号已删除或禁用，请重新选择");
        }

        List<SmsTemplateVariable> smsTemplateSendVariableList = new ArrayList<>();
        List<SmsDeveloperSendPhoneVo> smsDeveloperSendPhoneVoList = smsTemplateSendVo.getSmsDeveloperSendPhoneVoList();
        int sendNum = (int) Math.ceil(smsDeveloperSendPhoneVoList.size() / 500.0);
        for (int i = 1; i <= sendNum; i++) {
            List<SmsDeveloperSendPhoneVo> smsDeveloperSendPhoneVos = smsDeveloperSendPhoneVoList.stream().skip((i - 1) * 500L).limit(500).collect(Collectors.toList());
            List<TemplateSmsIdAndMobile> mobiles = smsDeveloperSendPhoneVos.stream()
                    .map(phoneVo -> new TemplateSmsIdAndMobile(phoneVo.getPhone(), phoneVo.getDeveloperSenId()))
                    .collect(Collectors.toList());
            SmsSendParam smsSendParam = new SmsSendParam()
                    .setAccountId(smsTemplateDo.getAccountId())
                    .setTemplateId(smsTemplateDo.getId())
                    .setVariableStr(smsTemplateSendVo.getVariable())
                    .setMobiles(mobiles)
                    .setResourceType(callType);
            SmsMessageResponse messageData = smsPlatformApi.send(smsSendParam);
            for (SmsResponse smsResponse : messageData.getData()) {
                SmsTemplateVariable variable = new SmsTemplateVariable();
                variable.setSmsId(smsResponse.getSmsId());
                variable.setCustomSmsId(smsResponse.getCustomSmsId());
                variable.setMobile(smsResponse.getMobile());
                variable.setContent(messageData.getTemplateReplaceModuleInformation());
                smsTemplateSendVariableList.add(variable);
            }
        }
        return smsTemplateSendVariableList;
    }

    /**
     * @return 返回用户可用的模板列表
     */
    @Override
    public List<SmsTemplateSimpleVo> findEffectiveTemplate(SmsTemplateEffectiveVo smsTemplateEffectiveVo) {
        List<SmsTemplateSimpleVo> smsTemplateSimpleVoList = new ArrayList<>();
        List<SmsTemplateDo> smsTemplateDoList = this.lambdaQuery()
                .eq(SmsTemplateDo::getCustomerId, SessionContextUtil.getUser().getUserId())
                .eq(SmsTemplateDo::getTemplateType, smsTemplateEffectiveVo.getTemplateType())
                .in(CollectionUtils.isNotEmpty(smsTemplateEffectiveVo.getAccountIds()), SmsTemplateDo::getAccountId, smsTemplateEffectiveVo.getAccountIds())
                .like(StringUtils.isNotBlank(smsTemplateEffectiveVo.getTemplateName()), SmsTemplateDo::getTemplateName, smsTemplateEffectiveVo.getTemplateName())
                .select(SmsTemplateDo::getId, SmsTemplateDo::getTemplateName, SmsTemplateDo::getCreateTime,SmsTemplateDo::getContent).list();
        if (!smsTemplateDoList.isEmpty()) {
            smsTemplateDoList.forEach(smsTemplateDo -> {
                SmsTemplateAuditDo smsTemplateAuditDo = smsTemplateAuditService.lambdaQuery().eq(SmsTemplateAuditDo::getSmsTemplateId, smsTemplateDo.getId()).one();
                SmsTemplateSimpleVo smsTemplateSimpleVo = new SmsTemplateSimpleVo();
                BeanUtils.copyProperties(smsTemplateDo, smsTemplateSimpleVo);
                smsTemplateSimpleVo.setAudit(smsTemplateAuditDo.getStatus().getValue());
                smsTemplateSimpleVo.setApproveTime(smsTemplateAuditDo.getUpdateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                if (SmsAuditStatus.PASS.getValue().equals(smsTemplateAuditDo.getStatus().getValue())) {
                    smsTemplateSimpleVoList.add(smsTemplateSimpleVo);
                }
            });
        }
        List<SmsTemplateSimpleVo> smsTemplateSimpleVos = smsTemplateSimpleVoList.stream().sorted(Comparator.comparing(SmsTemplateSimpleVo::getApproveTime).reversed()).collect(Collectors.toList());
        return smsTemplateSimpleVos;
    }

    @Override
    public List<Long> existShortUrl(List<String> shortUrls) {
        List<Long> smsTemplateIds = CollectionUtil.newArrayList();
        for (String shortUrl : shortUrls) {
            List<SmsTemplateDo> list = this.lambdaQuery()
                    .eq(SmsTemplateDo::getCustomerId, SessionContextUtil.getUser().getUserId())
                    .like(SmsTemplateDo::getContent, shortUrl)
                    .select(SmsTemplateDo::getId).list();
            if (!list.isEmpty()) {
                smsTemplateIds.addAll(list.stream().map(SmsTemplateDo::getId).collect(Collectors.toList()));
            }
        }
        return smsTemplateIds;
    }
}
