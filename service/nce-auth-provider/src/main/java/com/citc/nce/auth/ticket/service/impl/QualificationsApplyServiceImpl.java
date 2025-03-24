package com.citc.nce.auth.ticket.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.citc.nce.auth.certificate.dao.CertificateOptionsDao;
import com.citc.nce.auth.certificate.entity.CertificateOptionsDo;
import com.citc.nce.auth.certificate.vo.resp.CertificateOptionsResp;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.identification.dao.IdentificationAuditRecordDao;
import com.citc.nce.auth.identification.entity.IdentificationAuditRecordDo;
import com.citc.nce.auth.ticket.dao.QualificationsApplyDao;
import com.citc.nce.auth.ticket.entity.QualificationsApplyDo;
import com.citc.nce.auth.ticket.service.QualificationsApplyService;
import com.citc.nce.auth.ticket.vo.req.GetInfoByIdReq;
import com.citc.nce.auth.ticket.vo.req.GetQualificationsApplyReq;
import com.citc.nce.auth.ticket.vo.req.ProcessingStateReq;
import com.citc.nce.auth.ticket.vo.req.SubmitQualificationsApplyReq;
import com.citc.nce.auth.ticket.vo.resp.GetQualificationsApplyInfoByIdResp;
import com.citc.nce.auth.user.service.UserService;
import com.citc.nce.auth.usermessage.service.IUserMsgDetailService;
import com.citc.nce.auth.usermessage.vo.req.MsgReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.misc.constant.*;
import com.citc.nce.misc.msg.req.MsgTemplateReq;
import com.citc.nce.misc.record.ProcessingRecordApi;
import com.citc.nce.misc.record.req.BusinessIdsReq;
import com.citc.nce.misc.record.req.ProcessingRecordReq;
import com.citc.nce.misc.record.resp.ProcessingRecordResp;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: zouyili
 * @Contact: ylzouf
 * @Date: 2022/6/21 17:06
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
public class QualificationsApplyServiceImpl implements QualificationsApplyService {

    @Resource
    private QualificationsApplyDao qualificationsApplyDao;

    @Resource
    private CertificateOptionsDao certificateOptionsDao;

    @Resource
    private IdentificationAuditRecordDao identificationAuditRecordDao;

    @Resource
    private UserService userService;
    @Resource
    private IUserMsgDetailService iUserMsgDetailService;

    @Resource
    private ProcessingRecordApi processingRecordApi;


    @Transactional()
    @Override
    public void submitQualificationsApply(SubmitQualificationsApplyReq submitQualificationsApplyReq) {
        //判断是否有未走完流程的申请资质单
        LambdaQueryWrapperX<QualificationsApplyDo> qwrapper = new LambdaQueryWrapperX<QualificationsApplyDo>();
        qwrapper.eq(QualificationsApplyDo::getUserId, submitQualificationsApplyReq.getUserId())
                .eq(QualificationsApplyDo::getDeleted, NumCode.ZERO.getCode())
                .eq(QualificationsApplyDo::getProcessingState, NumCode.ZERO.getCode())
                .eq(QualificationsApplyDo::getQualificationName, submitQualificationsApplyReq.getQualificationName());
        List<QualificationsApplyDo> qualificationsList = qualificationsApplyDao.selectList(qwrapper);
        if (CollectionUtils.isNotEmpty(qualificationsList)) {//有未走完成流程的资质申请单
            log.error("error message is APPLICATION_HAS_NOT_COMPLETED");
            throw new BizException(AuthError.APPLICATION_HAS_NOT_COMPLETED);
        }
        //生成资质申请工单 ticket_qualifications_apply
        QualificationsApplyDo applyDo = new QualificationsApplyDo();
        BeanUtils.copyProperties(submitQualificationsApplyReq, applyDo);
        applyDo.setCreator(submitQualificationsApplyReq.getUserId());
        applyDo.setUpdater(submitQualificationsApplyReq.getUserId());
        applyDo.setEmail(Strings.isNullOrEmpty(submitQualificationsApplyReq.getEmail()) ? "" : submitQualificationsApplyReq.getEmail());
        applyDo.setSubmitTime(new Date())
                .setProcessingState(NumCode.ZERO.getCode());
        if (NumCode.ONE.getCode() != qualificationsApplyDao.insert(applyDo)) {
            log.error("submitQualificationsApply error message is === " + AuthError.Execute_SQL_SAVE);
            throw new BizException(AuthError.Execute_SQL_SAVE);
        }
        //判断用户资质是否曾申请过
        LambdaQueryWrapperX<CertificateOptionsDo> optionsWrapper = new LambdaQueryWrapperX<>();
        optionsWrapper.eq(CertificateOptionsDo::getUserId, submitQualificationsApplyReq.getUserId())
                .eq(CertificateOptionsDo::getCertificateId, submitQualificationsApplyReq.getQualificationName())
                .eq(CertificateOptionsDo::getDeleted, NumCode.ZERO.getCode());
        CertificateOptionsDo optionsDo = certificateOptionsDao.selectOne(optionsWrapper);
        if (null == optionsDo) {
            //用户账号资质信息表插入数据user_certificate_options
            CertificateOptionsDo certificateOptionsDo = new CertificateOptionsDo()
                    .setCertificateId(applyDo.getQualificationName())
                    .setUserId(applyDo.getUserId())
                    .setCertificateApplyStatus(NumCode.ONE.getCode())
                    .setApplyTime(new Date())
                    .setCertificateStatus(NumCode.ONE.getCode())
                    .setBusinessId(applyDo.getId())
                    .setCreator(applyDo.getUserId())
                    .setUpdater(applyDo.getUserId());
            if (NumCode.ONE.getCode() != certificateOptionsDao.insert(certificateOptionsDo)) {
                log.error("submitCertificateOptions error message is === " + AuthError.Execute_SQL_SAVE);
                throw new BizException(AuthError.Execute_SQL_SAVE);
            }
        } else {
            //重新申请刷新资质状态
            optionsDo.setCertificateApplyStatus(NumCode.ONE.getCode())
                    .setApplyTime(new Date())
                    .setCertificateStatus(NumCode.ONE.getCode())
                    .setBusinessId(applyDo.getId());
            if (NumCode.ONE.getCode() != certificateOptionsDao.update(optionsDo, optionsWrapper)) {
                log.error("submitCertificateOptions error message is === " + AuthError.Execute_SQL_UPDATE);
                throw new BizException(AuthError.Execute_SQL_UPDATE);
            }
        }
        IdentificationAuditRecordDo identifierAuditRecordDo = new IdentificationAuditRecordDo()
                .setAuditRemark(applyDo.getRemark())
                .setUserId(applyDo.getUserId())
                .setIdentificationId(submitQualificationsApplyReq.getQualificationName())
                .setAuditStatus(NumCode.ONE.getCode())
                .setCreator(applyDo.getUserId())
                .setUpdater(applyDo.getUserId());
        //identification_audit_record表插入数据
        if (NumCode.ONE.getCode() != identificationAuditRecordDao.insert(identifierAuditRecordDo)) {
            log.error("submitIdentificationAuditRecord error message is === " + AuthError.Execute_SQL_SAVE);
            throw new BizException(AuthError.Execute_SQL_SAVE);
        }
        //刷新用户状态
        userService.updateUserAuthStatus(applyDo.getUserId());
    }

    @Transactional()
    @Override
    public void disposeQualificationsApply(ProcessingStateReq processingStateReq) {
        //处理工单表状态 ticket_qualifications_apply
        QualificationsApplyDo applyDo = new QualificationsApplyDo();
        BeanUtils.copyProperties(processingStateReq, applyDo);
        applyDo.setProcessingState(NumCode.ONE.getCode());
        if (NumCode.ONE.getCode() != qualificationsApplyDao.updateById(applyDo)) {
            log.error("disposeQualificationsApply error message is === " + AuthError.Execute_SQL_UPDATE);
            throw new BizException(AuthError.Execute_SQL_UPDATE);
        }
        //处理用户账号资质信息表状态 ticket_qualifications_apply
        CertificateOptionsDo certOptionsDo = new CertificateOptionsDo()
                .setUpdateTime(new Date())
                .setBusinessId(processingStateReq.getId())
                .setUpdateTime(new Date())
                .setCertificateStatus(NumCode.ZERO.getCode())
                .setCertificateApplyStatus(processingStateReq.getCertificateApplyStatus());
        IdentificationAuditRecordDo identifierAuditRecordDo = new IdentificationAuditRecordDo()
                .setUpdateTime(new Date())
                .setCreateTime(new Date())
                .setAuditRemark(applyDo.getRemark())
                .setUserId(applyDo.getUserId())
                .setIdentificationId(processingStateReq.getQualificationName())
                .setReviewer(SessionContextUtil.getUser().getUserName());
        MsgTemplateReq msgTemplateReq = new MsgTemplateReq();
        if (NumCode.TWO.getCode() == processingStateReq.getCertificateApplyStatus()) {//审核不通过
            if (String.valueOf(QualificationType.ABILITY_SUPPLIER.getCode()).equals(String.valueOf(processingStateReq.getQualificationName()))) {
                msgTemplateReq.setTempldateCode(MsgCode.ABILITY_SUPPLIER_NOT_PASS.getCode());
            } else if (String.valueOf(QualificationType.SOLUTION_PROVIDER.getCode()).equals(String.valueOf(processingStateReq.getQualificationName()))) {
                msgTemplateReq.setTempldateCode(MsgCode.SOLUTION_PROVIDER_NOT_PASS.getCode());
            }
            identifierAuditRecordDo.setAuditStatus(NumCode.THREE.getCode());
        } else if (NumCode.THREE.getCode() == processingStateReq.getCertificateApplyStatus()) {//审核通过
            certOptionsDo.setApprovalTime(new Date());
            if (String.valueOf(QualificationType.ABILITY_SUPPLIER.getCode()).equals(String.valueOf(processingStateReq.getQualificationName()))) {
                msgTemplateReq.setTempldateCode(MsgCode.ABILITY_SUPPLIER_PASS.getCode());
            } else if (String.valueOf(QualificationType.SOLUTION_PROVIDER.getCode()).equals(String.valueOf(processingStateReq.getQualificationName()))) {
                msgTemplateReq.setTempldateCode(MsgCode.SOLUTION_PROVIDER_PASS.getCode());
            }
            identifierAuditRecordDo.setAuditStatus(NumCode.TWO.getCode());
        } else {
            throw new BizException(AuthError.PARAMETER_BAD);
        }
        //修改user_certificate_options资质申请状态
        LambdaUpdateWrapper<CertificateOptionsDo> updateCertificateWrapper = new LambdaUpdateWrapper<>();
        updateCertificateWrapper.eq(CertificateOptionsDo::getBusinessId, processingStateReq.getId())
                .eq(CertificateOptionsDo::getUserId, processingStateReq.getUserId());
        if (NumCode.ONE.getCode() != certificateOptionsDao.update(certOptionsDo, updateCertificateWrapper)) {
            log.error("disposeCertificateOptions error message is === " + AuthError.Execute_SQL_UPDATE);
            throw new BizException(AuthError.Execute_SQL_UPDATE);
        }
        //往用户消息详情表插入一条消息通知
        iUserMsgDetailService.addMsgIntoUser(JSON.parseObject(JSONObject.toJSONString(msgTemplateReq), MsgReq.class).setRemark(processingStateReq.getRemark()).setUserId(processingStateReq.getUserId()));
        //identification_audit_record表插入数据
        if (NumCode.ONE.getCode() != identificationAuditRecordDao.insert(identifierAuditRecordDo)) {
            log.error("submitIdentificationAuditRecord error message is === " + AuthError.Execute_SQL_SAVE);
            throw new BizException(AuthError.Execute_SQL_SAVE);
        }
        //刷新用户状态
        userService.updateUserAuthStatus(applyDo.getUserId());

        BaseUser baseUser = SessionContextUtil.getUser();
        String processingContent = ProcessingContentEnum.TYSQ.getName();
        if(!processingStateReq.getCertificateApplyStatus().equals(NumCode.THREE.getCode())){
            processingContent = ProcessingContentEnum.BHSQ.getName();
        }
        //添加处理记录
        ProcessingRecordReq processingRecordReq = new ProcessingRecordReq();
        processingRecordReq.setBusinessId(processingStateReq.getId().toString());
        processingRecordReq.setBusinessType(BusinessTypeEnum.ZZSQGD.getCode());
        processingRecordReq.setProcessingContent(processingContent);
        processingRecordReq.setProcessingUserId(baseUser.getUserId());
        processingRecordReq.setRemark(processingStateReq.getRemark());
        processingRecordApi.addRecord(processingRecordReq);
    }

    @Override
    public PageResult getQualificationsApply(GetQualificationsApplyReq getQualificationsApplyReq) {
        LambdaQueryWrapperX<QualificationsApplyDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eqIfPresent(QualificationsApplyDo::getProcessingState, getQualificationsApplyReq.getProcessingState())
                .likeIfPresent(QualificationsApplyDo::getUserName, getQualificationsApplyReq.getKeyWord())
                .orderByDesc(QualificationsApplyDo::getSubmitTime);
        PageParam page = new PageParam();
        page.setPageSize(getQualificationsApplyReq.getPageSize());
        page.setPageNo(getQualificationsApplyReq.getPageNo());
        PageResult<QualificationsApplyDo> pageResult = qualificationsApplyDao.selectPage(page, queryWrapper);

        List<QualificationsApplyDo> qualificationsApplyDos = pageResult.getList();
        //判断是否有处理记录
        List<String> ids = new ArrayList<>();
        qualificationsApplyDos.forEach(item->{ids.add(String.valueOf(item.getId()));});
        if(CollectionUtils.isNotEmpty(ids)){
            BusinessIdsReq businessIdsReq = new BusinessIdsReq();
            businessIdsReq.setBusinessIds(ids);
            businessIdsReq.setBusinessType(4);
            List<ProcessingRecordResp> processingRecordRespList = processingRecordApi.findProcessingRecordListByIds(businessIdsReq);

            for (QualificationsApplyDo qualificationsApplyDo: qualificationsApplyDos){
                for (ProcessingRecordResp processingRecordResp: processingRecordRespList){
                    if(qualificationsApplyDo.getId().toString().equals(processingRecordResp.getBusinessId())){
                        qualificationsApplyDo.setHasRecords(true);
                    }
                }
            }
        }

        return pageResult;
    }

    @Override
    public GetQualificationsApplyInfoByIdResp getQualificationsApplyInfoById(GetInfoByIdReq getInfoByIdReq) {
        GetQualificationsApplyInfoByIdResp resp = new GetQualificationsApplyInfoByIdResp();
        QualificationsApplyDo ticket = qualificationsApplyDao.selectById(getInfoByIdReq.getId());
        if (null != ticket && null != ticket.getId()) {
            BeanUtils.copyProperties(ticket, resp);
            LambdaQueryWrapperX<CertificateOptionsDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.eq(CertificateOptionsDo::getCertificateId, ticket.getQualificationName())
                    .eq(CertificateOptionsDo::getUserId, ticket.getUserId());
            CertificateOptionsDo certificate = certificateOptionsDao.selectOne(queryWrapperX);
            CertificateOptionsResp certificateOptionsResp = new CertificateOptionsResp();
            if (null != certificate) {
                BeanUtils.copyProperties(certificate, certificateOptionsResp);
            }
            resp.setCertificateOptionsResp(certificateOptionsResp);
        } else {
            log.error("getQualificationsApplyInfoById error message is === " + AuthError.Execute_SQL_QUERY);
            throw new BizException(AuthError.Execute_SQL_QUERY);
        }
        return resp;
    }
}