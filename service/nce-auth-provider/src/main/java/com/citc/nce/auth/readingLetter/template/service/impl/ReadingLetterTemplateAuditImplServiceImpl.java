package com.citc.nce.auth.readingLetter.template.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.accountmanagement.service.AccountManagementService;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.csp.readingLetter.service.CspReadingLetterAccountService;
import com.citc.nce.auth.csp.readingLetter.vo.CspReadingLetterDetailResp;
import com.citc.nce.auth.messagetemplate.entity.TemplateDataResp;
import com.citc.nce.auth.readingLetter.apply.PlatfomManageReadingLetterService;
import com.citc.nce.auth.readingLetter.template.dao.ReadingLetterTemplateAuditDao;
import com.citc.nce.auth.readingLetter.template.dao.ReadingLetterTemplateProvedDao;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateAuditDo;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateDo;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateProvedDo;
import com.citc.nce.auth.readingLetter.template.enums.AuditStatus;
import com.citc.nce.auth.readingLetter.template.enums.SmsTypeEnum;
import com.citc.nce.auth.readingLetter.template.service.ReadingLetterTemplateAuditService;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterAuditDeleteReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateNameVo;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.common.util.SessionContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zjy
 */
@Service
@Slf4j
public class ReadingLetterTemplateAuditImplServiceImpl extends ServiceImpl<ReadingLetterTemplateAuditDao, ReadingLetterTemplateAuditDo> implements ReadingLetterTemplateAuditService {

    @Resource
    private PlatfomManageReadingLetterService platfomManageReadingLetterService;
    @Resource
    private RedisService redisService;
    @Resource
    private ReadingLetterTemplateProvedDao readingLetterTemplateProvedDao;
    @Resource
    private CspReadingLetterAccountService cspReadingLetterAccountService;
    @Resource
    AccountManagementService accountManagementService;

    @Override
    public void audit(ReadingLetterTemplateDo readingLetterTemplateDo, List<CspReadingLetterDetailResp> readingLetterAccounts, List<AccountManagementResp> fontdoAccounts) {
        //阅信+模板送审
        for (CspReadingLetterDetailResp readingLetterAccount : readingLetterAccounts) {
            //该账号的阅信+模板 如果已经送审过,跳过
            LambdaQueryWrapper<ReadingLetterTemplateAuditDo> auditQueryWrapper = new LambdaQueryWrapper<>();
            auditQueryWrapper.eq(ReadingLetterTemplateAuditDo::getTemplateId, readingLetterTemplateDo.getId())
                    .eq(ReadingLetterTemplateAuditDo::getAuditAccount, readingLetterAccount.getAccountId())
                    .eq(ReadingLetterTemplateAuditDo::getSmsType, SmsTypeEnum.READING_LETTER_PLUS.getCode());

            ReadingLetterTemplateAuditDo one = this.getOne(auditQueryWrapper);

            if (ObjectUtil.isNotNull(one)) {
                if (AuditStatus.SUCCESS.getCode().equals(one.getStatus())) {
                    log.info("阅信+模板已审核成功，跳过。模板id：" + readingLetterTemplateDo.getId() + "，账户id：" + readingLetterAccount.getAccountId());
                    continue;
                } else {
                    log.info("阅信+模板重新送审。模板id：" + readingLetterTemplateDo.getId() + "，账户id：" + readingLetterAccount.getAccountId() + "，原审核记录删除，记录id:" + one.getId() + ", 原审核状态为:" + one.getStatus());
                    this.removeById(one);
                }
            }

            //save
            ReadingLetterTemplateAuditDo readingLetterTemplateAuditDo = new ReadingLetterTemplateAuditDo();
            readingLetterTemplateAuditDo.setTemplateId(readingLetterTemplateDo.getId())
                    .setTemplateType(readingLetterTemplateDo.getTemplateType())
                    .setModuleInformation(readingLetterTemplateDo.getModuleInformation())
                    .setCustomerId(readingLetterTemplateDo.getCustomerId())
                    .setAuditAccount(readingLetterAccount.getAccountId())
                    .setStatus(AuditStatus.PENDING.getCode())
                    .setCreator(SessionContextUtil.getUser().getUserId())
                    .setOperatorCode(readingLetterAccount.getOperator())
                    //阅信+
                    .setSmsType(SmsTypeEnum.READING_LETTER_PLUS.getCode());
            ;
            this.save(readingLetterTemplateAuditDo);

            TemplateDataResp templateDataResp = new TemplateDataResp();
            try {
                //向网关发送审核请求
                templateDataResp = doAuditRequest(readingLetterAccount, readingLetterTemplateDo);
                //模板审核lock
                if (ObjectUtil.isNotNull(templateDataResp) && ObjectUtil.isNotEmpty(templateDataResp.getData())) {
                    redisService.setCacheObject(String.format(Constants.TEMPLATE_AUDIT_CACHE_REDIS_KEY, templateDataResp.getData()), templateDataResp.getData(), 300L, TimeUnit.SECONDS);
                }
                log.info("阅信+模板送审结果,通道：" + readingLetterAccount.getOperator() + "，返回内容：" + JSONObject.toJSONString(templateDataResp));
            } catch (Exception e) {
                log.error("阅信+模板送审异常,通道：" + readingLetterAccount.getOperator() + "，异常信息：" + e.getMessage());
                e.printStackTrace();
                log.error(e.getMessage());
                templateDataResp.setCode(500);
                templateDataResp.setMessage(e.getMessage());
            } finally {
                //save
                updateMessageTemplateAuditDo(templateDataResp, readingLetterTemplateAuditDo);
                //删除模板审核lock
                if (ObjectUtil.isNotNull(templateDataResp) && ObjectUtil.isNotEmpty(templateDataResp.getData())) {
                    redisService.deleteObject(String.format(Constants.TEMPLATE_AUDIT_CACHE_REDIS_KEY, templateDataResp.getData()));
                }
            }
        }
        //蜂动账号送审阅信模板
        for (AccountManagementResp fontdoAccount : fontdoAccounts) {

            //该账号的5G阅信模板 如果已经送审过,跳过
            LambdaQueryWrapper<ReadingLetterTemplateAuditDo> auditQueryWrapper = new LambdaQueryWrapper<>();
            auditQueryWrapper.eq(ReadingLetterTemplateAuditDo::getTemplateId, readingLetterTemplateDo.getId())
                    .eq(ReadingLetterTemplateAuditDo::getAuditAccount, fontdoAccount.getChatbotAccountId())
                    .eq(ReadingLetterTemplateAuditDo::getSmsType, SmsTypeEnum.FIFTH_READING_LETTER.getCode());

            ReadingLetterTemplateAuditDo one = this.getOne(auditQueryWrapper);

            if (ObjectUtil.isNotNull(one)) {
                if (AuditStatus.SUCCESS.getCode().equals(one.getStatus())) {
                    log.info("5G阅信模板已审核成功，跳过。模板id：" + readingLetterTemplateDo.getId() + "，账户id：" + fontdoAccount.getChatbotAccountId());
                    continue;
                } else {
                    log.info("5G阅信模板重新送审。模板id：" + readingLetterTemplateDo.getId() + "，账户id：" + fontdoAccount.getChatbotAccountId() + "，原审核记录删除，记录id:" + one.getId() + ", 原审核状态为:" + one.getStatus());
                    this.removeById(one);
                }
            }

            //save
            ReadingLetterTemplateAuditDo readingLetterTemplateAuditDo = new ReadingLetterTemplateAuditDo();
            readingLetterTemplateAuditDo.setTemplateId(readingLetterTemplateDo.getId())
                    .setTemplateType(readingLetterTemplateDo.getTemplateType())
                    .setModuleInformation(readingLetterTemplateDo.getModuleInformation())
                    .setCustomerId(readingLetterTemplateDo.getCustomerId())
                    .setAuditAccount(fontdoAccount.getChatbotAccountId())
                    .setStatus(AuditStatus.PENDING.getCode())
                    .setCreator(SessionContextUtil.getUser().getUserId())
                    .setOperatorCode(fontdoAccount.getAccountTypeCode())
                    //5G阅信
                    .setSmsType(SmsTypeEnum.FIFTH_READING_LETTER.getCode());
            ;
            this.save(readingLetterTemplateAuditDo);

            TemplateDataResp templateDataResp = new TemplateDataResp();
            try {
                //向网关发送审核请求
                templateDataResp = doAuditRequest(fontdoAccount, readingLetterTemplateDo);
                //模板审核lock
                if (ObjectUtil.isNotNull(templateDataResp) && ObjectUtil.isNotEmpty(templateDataResp.getData())) {
                    redisService.setCacheObject(String.format(Constants.TEMPLATE_AUDIT_CACHE_REDIS_KEY, templateDataResp.getData()), templateDataResp.getData(), 300L, TimeUnit.SECONDS);
                }
                log.info("5G阅信模板送审结果,通道：" + fontdoAccount.getAccountTypeCode() + "，返回内容：" + JSONObject.toJSONString(templateDataResp));
            } catch (Exception e) {
                log.error("5G阅信模板送审异常,通道：" + fontdoAccount.getAccountTypeCode() + "，异常信息：" + e.getMessage());
                e.printStackTrace();
                log.error(e.getMessage());
                templateDataResp.setCode(500);
                templateDataResp.setMessage(e.getMessage());
            } finally {
                //save
                updateMessageTemplateAuditDo(templateDataResp, readingLetterTemplateAuditDo);
                //删除模板审核lock
                if (ObjectUtil.isNotNull(templateDataResp) && ObjectUtil.isNotEmpty(templateDataResp.getData())) {
                    redisService.deleteObject(String.format(Constants.TEMPLATE_AUDIT_CACHE_REDIS_KEY, templateDataResp.getData()));
                }
            }
        }

    }

    @Override
    public void reAudit(ReadingLetterTemplateDo readingLetterTemplateDo, List<CspReadingLetterDetailResp> readingLetterAccounts, List<AccountManagementResp> fontdoAccounts) {

        //阅信+模板送审
        for (CspReadingLetterDetailResp readingLetterAccount : readingLetterAccounts) {

            //判断是否需要送审(如果删除成功, 那么就需要新建)
            LambdaQueryWrapper<ReadingLetterTemplateAuditDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ReadingLetterTemplateAuditDo::getTemplateId, readingLetterTemplateDo.getId())
                    .eq(ReadingLetterTemplateAuditDo::getAuditAccount, readingLetterAccount.getAccountId())
                    .eq(ReadingLetterTemplateAuditDo::getTemplateId, readingLetterTemplateDo.getId())
                    .eq(ReadingLetterTemplateAuditDo::getSmsType, SmsTypeEnum.READING_LETTER_PLUS.getCode());
            int deleteNum = getBaseMapper().delete(queryWrapper);
            if (deleteNum == 0) {
                log.info("阅信+模板送审,模板id：" + readingLetterTemplateDo.getId() + "，账户id：" + readingLetterAccount.getAccountId() + "之前没有审核记录, 新建送审");
            }

            //save
            ReadingLetterTemplateAuditDo readingLetterTemplateAuditDo = new ReadingLetterTemplateAuditDo();
            readingLetterTemplateAuditDo.setTemplateId(readingLetterTemplateDo.getId())
                    .setTemplateType(readingLetterTemplateDo.getTemplateType())
                    .setModuleInformation(readingLetterTemplateDo.getModuleInformation())
                    .setCustomerId(readingLetterTemplateDo.getCustomerId())
                    .setAuditAccount(readingLetterAccount.getAccountId())
                    .setStatus(AuditStatus.PENDING.getCode())
                    .setCreator(SessionContextUtil.getUser().getUserId())
                    .setOperatorCode(readingLetterAccount.getOperator())
                    //阅信+
                    .setSmsType(SmsTypeEnum.READING_LETTER_PLUS.getCode());
            ;
            this.save(readingLetterTemplateAuditDo);

            TemplateDataResp templateDataResp = new TemplateDataResp();
            try {
                //向网关发送审核请求
                templateDataResp = doAuditRequest(readingLetterAccount, readingLetterTemplateDo);
                //模板审核lock
                if (ObjectUtil.isNotNull(templateDataResp) && ObjectUtil.isNotEmpty(templateDataResp.getData())) {
                    redisService.setCacheObject(String.format(Constants.TEMPLATE_AUDIT_CACHE_REDIS_KEY, templateDataResp.getData()), templateDataResp.getData(), 300L, TimeUnit.SECONDS);
                }
                log.info("阅信+模板送审结果,通道：" + readingLetterAccount.getOperator() + "，返回内容：" + JSONObject.toJSONString(templateDataResp));
            } catch (Exception e) {
                log.error("阅信+模板送审异常,通道：" + readingLetterAccount.getOperator() + "，异常信息：" + e.getMessage());
                e.printStackTrace();
                log.error(e.getMessage());
                templateDataResp.setCode(500);
                templateDataResp.setMessage(e.getMessage());
            } finally {
                //save
                updateMessageTemplateAuditDo(templateDataResp, readingLetterTemplateAuditDo);
                //删除模板审核lock
                if (ObjectUtil.isNotNull(templateDataResp) && ObjectUtil.isNotEmpty(templateDataResp.getData())) {
                    redisService.deleteObject(String.format(Constants.TEMPLATE_AUDIT_CACHE_REDIS_KEY, templateDataResp.getData()));
                }
            }
        }

        //蜂动账号送审5G阅信模板
        for (AccountManagementResp fontdoAccount : fontdoAccounts) {

            //判断是否需要送审(如果删除成功, 那么就需要新建)
            LambdaQueryWrapper<ReadingLetterTemplateAuditDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ReadingLetterTemplateAuditDo::getTemplateId, readingLetterTemplateDo.getId())
                    .eq(ReadingLetterTemplateAuditDo::getAuditAccount, fontdoAccount.getChatbotAccountId())
                    .eq(ReadingLetterTemplateAuditDo::getTemplateId, readingLetterTemplateDo.getId())
                    .eq(ReadingLetterTemplateAuditDo::getSmsType, SmsTypeEnum.FIFTH_READING_LETTER.getCode());
            int deleteNum = getBaseMapper().delete(queryWrapper);
            if (deleteNum == 0) {
                //没有记录,跳过
                log.info("5G阅信模板送审,模板id：" + readingLetterTemplateDo.getId() + "，账户id：" + fontdoAccount.getChatbotAccountId() + "之前没有审核记录, 新建送审");
            }

            //save
            ReadingLetterTemplateAuditDo readingLetterTemplateAuditDo = new ReadingLetterTemplateAuditDo();
            readingLetterTemplateAuditDo.setTemplateId(readingLetterTemplateDo.getId())
                    .setTemplateType(readingLetterTemplateDo.getTemplateType())
                    .setModuleInformation(readingLetterTemplateDo.getModuleInformation())
                    .setCustomerId(readingLetterTemplateDo.getCustomerId())
                    .setAuditAccount(fontdoAccount.getChatbotAccountId())
                    .setStatus(AuditStatus.PENDING.getCode())
                    .setCreator(SessionContextUtil.getUser().getUserId())
                    .setOperatorCode(fontdoAccount.getAccountTypeCode())
                    //5G阅信
                    .setSmsType(SmsTypeEnum.FIFTH_READING_LETTER.getCode());
            ;
            this.save(readingLetterTemplateAuditDo);

            TemplateDataResp templateDataResp = new TemplateDataResp();
            try {
                //向网关发送审核请求
                templateDataResp = doAuditRequest(fontdoAccount, readingLetterTemplateDo);
                //模板审核lock
                if (ObjectUtil.isNotNull(templateDataResp) && ObjectUtil.isNotEmpty(templateDataResp.getData())) {
                    redisService.setCacheObject(String.format(Constants.TEMPLATE_AUDIT_CACHE_REDIS_KEY, templateDataResp.getData()), templateDataResp.getData(), 300L, TimeUnit.SECONDS);
                }
                log.info("5G阅信模板送审结果,通道：" + fontdoAccount.getAccountTypeCode() + "，返回内容：" + JSONObject.toJSONString(templateDataResp));
            } catch (Exception e) {
                log.error("5G阅信模板送审异常,通道：" + fontdoAccount.getAccountTypeCode() + "，异常信息：" + e.getMessage());
                e.printStackTrace();
                log.error(e.getMessage());
                templateDataResp.setCode(500);
                templateDataResp.setMessage(e.getMessage());
            } finally {
                //save
                updateMessageTemplateAuditDo(templateDataResp, readingLetterTemplateAuditDo);
                //删除模板审核lock
                if (ObjectUtil.isNotNull(templateDataResp) && ObjectUtil.isNotEmpty(templateDataResp.getData())) {
                    redisService.deleteObject(String.format(Constants.TEMPLATE_AUDIT_CACHE_REDIS_KEY, templateDataResp.getData()));
                }
            }
        }
    }

    @Override
    public void deleteAuditAndProved(Long id) {
        LambdaQueryWrapper<ReadingLetterTemplateAuditDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReadingLetterTemplateAuditDo::getTemplateId, id);
        List<ReadingLetterTemplateAuditDo> auditDos = getBaseMapper().selectList(queryWrapper);
        String customerId = SessionContextUtil.getLoginUser().getUserId();

        for (ReadingLetterTemplateAuditDo auditDo : auditDos) {
            String auditAccount = auditDo.getAuditAccount();
            AccountManagementResp accountManagementResp = new AccountManagementResp();
            if (SmsTypeEnum.FIFTH_READING_LETTER.getCode().equals(auditDo.getSmsType())) {
                accountManagementResp = accountManagementService.getAccountManagementById(auditAccount);
            } else if (SmsTypeEnum.READING_LETTER_PLUS.getCode().equals(auditDo.getSmsType())) {
                // (阅信+账号)
                CspReadingLetterDetailResp readingLetterAccounts = cspReadingLetterAccountService.selectOne(auditAccount, customerId);
                accountManagementResp.setAppId(readingLetterAccounts.getAppId());
                accountManagementResp.setAppKey(readingLetterAccounts.getAppKey());
                accountManagementResp.setAgentId(readingLetterAccounts.getAgentId());
            }
            //删除模板审核记录
            getBaseMapper().deleteById(auditDo.getId());
            if (auditDo.getStatus().equals(AuditStatus.SUCCESS.getCode())) {
                platfomManageReadingLetterService.deleteReadingLetterTemplate(auditDo.getPlatformTemplateId(), accountManagementResp);
                log.info("fontdo远程删除模板审核记录,模板id：{}, fontdo平台模板id:{}", id, auditDo.getPlatformTemplateId());
            }
        }

        LambdaQueryWrapper<ReadingLetterTemplateProvedDo> queryProvedWrapper = new LambdaQueryWrapper<>();
        queryProvedWrapper.eq(ReadingLetterTemplateProvedDo::getTemplateId, id)
                .eq(ReadingLetterTemplateProvedDo::getCustomerId, customerId);
        int delete = readingLetterTemplateProvedDao.delete(queryProvedWrapper);
        log.info("删除模板审核proved记录共{}条", delete);

    }

    @Override
    public void deleteAuditAndProvedByAccount(ReadingLetterAuditDeleteReq req) {
        LambdaQueryWrapper<ReadingLetterTemplateAuditDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReadingLetterTemplateAuditDo::getAuditAccount, req.getAccountId());
        queryWrapper.eq(ReadingLetterTemplateAuditDo::getSmsType, req.getSmsType());
        List<ReadingLetterTemplateAuditDo> auditDos = getBaseMapper().selectList(queryWrapper);

        String customerId = SessionContextUtil.getLoginUser().getUserId();

        if (CollectionUtil.isEmpty(auditDos)) {
            log.info("没有需要删除的审核记录, account:{}", req.getAccountId());
            return;
        }
        AccountManagementResp accountManagementResp = new AccountManagementResp();
        accountManagementResp.setAgentId(req.getAgentId());
        accountManagementResp.setAppId(req.getAppId());
        accountManagementResp.setAppKey(req.getAppKey());
        ArrayList<Long> templateIds = CollectionUtil.newArrayList();
        for (ReadingLetterTemplateAuditDo auditDo : auditDos) {
            //删除模板审核记录
            getBaseMapper().deleteById(auditDo.getId());
            if (auditDo.getStatus().equals(AuditStatus.SUCCESS.getCode())) {
                platfomManageReadingLetterService.deleteReadingLetterTemplate(auditDo.getPlatformTemplateId(), accountManagementResp);
                log.info("fontdo远程删除模板审核记录,, fontdo平台模板id:{}", auditDo.getPlatformTemplateId());
                templateIds.add(auditDo.getTemplateId());
            }
        }

        if (CollectionUtil.isNotEmpty(templateIds)) {
            LambdaQueryWrapper<ReadingLetterTemplateProvedDo> queryProvedWrapper = new LambdaQueryWrapper<>();
            queryProvedWrapper.in(ReadingLetterTemplateProvedDo::getTemplateId, templateIds)
                    .eq(ReadingLetterTemplateProvedDo::getCustomerId, customerId);
            int delete = readingLetterTemplateProvedDao.delete(queryProvedWrapper);
            log.info("删除模板审核proved记录共{}条", delete);
        }
    }

    @Override
    public String getPlatformTemplateIdByAccountIdAndTemplateId(String accountId, Long TemplateId) {
        LambdaQueryWrapper<ReadingLetterTemplateAuditDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReadingLetterTemplateAuditDo::getAuditAccount, accountId)
                .eq(ReadingLetterTemplateAuditDo::getTemplateId, TemplateId)
                .eq(ReadingLetterTemplateAuditDo::getSmsType, SmsTypeEnum.FIFTH_READING_LETTER.getCode())
                .eq(ReadingLetterTemplateAuditDo::getStatus, AuditStatus.SUCCESS.getCode());
        ReadingLetterTemplateAuditDo one = this.getOne(queryWrapper);
        if (ObjectUtil.isNotNull(one)) {
            return one.getPlatformTemplateId();
        }
        return null;
    }

    @Override
    public List<ReadingLetterTemplateNameVo> getTemplatesByPlatformTemplateIdList(List<String> platformTemplateIdList) {
        return getBaseMapper().getTemplatesByPlatformTemplateIdList(platformTemplateIdList);
    }

    //阅信+账号向网关发送审核请求
    private TemplateDataResp doAuditRequest(CspReadingLetterDetailResp readingLetterAccount, ReadingLetterTemplateDo readingLetterTemplateDo) {

        //适配蜂动账号
        AccountManagementResp account = new AccountManagementResp();
        account.setAppId(readingLetterAccount.getAppId());
        account.setAppKey(readingLetterAccount.getAppKey());
        account.setAgentId(readingLetterAccount.getAgentId());
        //发送创建模板请求
        return platfomManageReadingLetterService.createReadingLetterTemplate(readingLetterTemplateDo.getModuleInformation(), account);
    }

    //蜂动账号向网关发送审核请求
    private TemplateDataResp doAuditRequest(AccountManagementResp fontdoAccount, ReadingLetterTemplateDo readingLetterTemplateDo) {

        //适配蜂动账号
        AccountManagementResp account = new AccountManagementResp();
        account.setAppId(fontdoAccount.getAppId());
        account.setAppKey(fontdoAccount.getAppKey());
        account.setAgentId(fontdoAccount.getAgentId());
        //发送创建模板请求
        return platfomManageReadingLetterService.createReadingLetterTemplate(readingLetterTemplateDo.getModuleInformation(), account);
    }


    ReadingLetterTemplateAuditDo updateMessageTemplateAuditDo(TemplateDataResp<String> templateDataResp,
                                                              ReadingLetterTemplateAuditDo readingLetterTemplateAuditDo) {
        if (templateDataResp.getCode() == 200) {
            readingLetterTemplateAuditDo.setStatus(Constants.TEMPLATE_STATUS_PENDING);
            if (ObjectUtil.isNotEmpty(templateDataResp.getData())) {
                readingLetterTemplateAuditDo.setPlatformTemplateId(templateDataResp.getData());
            }
        } else {
            readingLetterTemplateAuditDo.setStatus(Constants.TEMPLATE_STATUS_FAILED);
            readingLetterTemplateAuditDo.setRemark(templateDataResp.getMessage());
        }

        this.getBaseMapper().updateById(readingLetterTemplateAuditDo);
        return readingLetterTemplateAuditDo;
    }

}
