package com.citc.nce.auth.certificate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.citc.nce.auth.adminUser.vo.req.OnOrOffClientUserCertificateReq;
import com.citc.nce.auth.certificate.dao.CertificateOptionsDao;
import com.citc.nce.auth.certificate.dao.UserTagLogDao;
import com.citc.nce.auth.certificate.entity.CertificateOptionsDo;
import com.citc.nce.auth.certificate.entity.UserTagLogDo;
import com.citc.nce.auth.certificate.service.CertificateOptionsService;
import com.citc.nce.auth.certificate.vo.req.UserTagLogByCertificateOptionsIdReq;
import com.citc.nce.auth.certificate.vo.resp.UserTagLogResp;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.constant.CountNum;
import com.citc.nce.auth.identification.dao.IdentificationAuditRecordDao;
import com.citc.nce.auth.identification.entity.IdentificationAuditRecordDo;
import com.citc.nce.auth.ticket.dao.QualificationsApplyDao;
import com.citc.nce.auth.ticket.entity.QualificationsApplyDo;
import com.citc.nce.auth.user.dao.UserDao;
import com.citc.nce.auth.user.entity.UserDo;
import com.citc.nce.auth.user.service.UserService;
import com.citc.nce.auth.usermessage.service.IUserMsgDetailService;
import com.citc.nce.auth.usermessage.vo.req.MsgReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.misc.constant.BusinessTypeEnum;
import com.citc.nce.misc.constant.MsgCode;
import com.citc.nce.misc.constant.NumCode;
import com.citc.nce.misc.constant.QualificationType;
import com.citc.nce.misc.msg.req.MsgTemplateReq;
import com.citc.nce.misc.record.ProcessingRecordApi;
import com.citc.nce.misc.record.req.ProcessingRecordReq;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/07/14
 * @Version 1.0
 * @Description:
 */
@Service
@Slf4j
public class CertificateOptionsServiceImpl implements CertificateOptionsService {

    @Resource
    private CertificateOptionsDao certificateOptionsDao;

    @Resource
    private UserTagLogDao userTagLogDao;

    @Resource
    private QualificationsApplyDao qualificationsApplyDao;

    @Resource
    private UserDao userDao;

    @Resource
    private IdentificationAuditRecordDao identificationAuditRecordDao;
    @Resource
    private UserService userService;
    @Resource
    private IUserMsgDetailService iUserMsgDetailService;
    @Resource
    private ProcessingRecordApi processingRecordApi;


    @Override
    public CountNum getPendingReviewNum() {
        LambdaQueryWrapperX<CertificateOptionsDo> queryWrapper = new LambdaQueryWrapperX<CertificateOptionsDo>();
        queryWrapper.eq(CertificateOptionsDo::getCertificateApplyStatus, NumCode.ONE.getCode())
                .in(CertificateOptionsDo::getCertificateId, QualificationType.ABILITY_SUPPLIER.getCode(), QualificationType.SOLUTION_PROVIDER.getCode());
        Long count = certificateOptionsDao.selectCount(queryWrapper);
        return new CountNum().setNum(count);
    }

    /**
     * 启 停client 用户资质
     *
     * @param req
     */
    @Override
    @Transactional()
    public void onOrOffClientUserCertificate(OnOrOffClientUserCertificateReq req) {
        if (NumCode.ZERO.getCode() == req.getCertificateStatus()) {//开启
            turnOnUserCertificate(req);
        } else if (NumCode.ONE.getCode() == req.getCertificateStatus()) {//关闭
            turnOffUserCertificate(req);
        } else {
            throw new BizException(AuthError.PARAMETER_BAD);
        }

        BaseUser baseUser = SessionContextUtil.getUser();
        String processingContent = "";
        Integer businessType = BusinessTypeEnum.NLTGSGL.getCode();

        //添加处理记录
        ProcessingRecordReq processingRecordReq = new ProcessingRecordReq();
        processingRecordReq.setBusinessId(req.getClientUserId());
        processingRecordReq.setBusinessType(businessType);
        processingRecordReq.setProcessingContent(processingContent);
        processingRecordReq.setProcessingUserId(baseUser.getUserId());
        processingRecordReq.setRemark(req.getRemark());
        processingRecordApi.addRecord(processingRecordReq);
    }

    @Override
    public List<UserTagLogResp> getUserTagLogByCertificateOptionsId(UserTagLogByCertificateOptionsIdReq req) {
        LambdaQueryWrapperX<UserTagLogDo> wrapper = new LambdaQueryWrapperX<UserTagLogDo>();
        wrapper.eq(UserTagLogDo::getCertificateOptionsId, req.getCertificateOptionsId())
                .orderByDesc(UserTagLogDo::getHandleTime);
        List<UserTagLogDo> logList = userTagLogDao.selectList(wrapper);
        List<UserTagLogResp> dataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(logList)) {
            logList.forEach(i -> {
                UserTagLogResp resp = new UserTagLogResp();
                BeanUtils.copyProperties(i, resp);
                dataList.add(resp);
            });
        }
        return dataList;
    }

    @Override
    public List<CertificateOptionsDo> queryUserCertificateByUserId(String userId) {
        List<CertificateOptionsDo> userCertificateOptionsDoList = new ArrayList<>();
        if (!Strings.isNullOrEmpty(userId)) {
            LambdaQueryWrapperX<CertificateOptionsDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(CertificateOptionsDo::getUserId, userId)
                    .eq(CertificateOptionsDo::getCertificateApplyStatus, NumCode.THREE.getCode())
                    .eq(CertificateOptionsDo::getCertificateStatus, NumCode.ZERO.getCode())
                    .eq(CertificateOptionsDo::getDeleted, NumCode.ZERO.getCode());
            userCertificateOptionsDoList = certificateOptionsDao.selectList(queryWrapper);
        }
        return userCertificateOptionsDoList;
    }

    public void turnOnUserCertificate(OnOrOffClientUserCertificateReq req) {
        //查询用户是否有该资质
        LambdaQueryWrapperX<CertificateOptionsDo> queryWrapper = new LambdaQueryWrapperX<CertificateOptionsDo>();
        queryWrapper.eq(CertificateOptionsDo::getUserId, req.getClientUserId())
                .eq(CertificateOptionsDo::getCertificateId, req.getCertificateOptionsCode());
        CertificateOptionsDo optionsDo = certificateOptionsDao.selectOne(queryWrapper);
        UserTagLogDo tagLog = new UserTagLogDo();
        //根据用户userId查询用户信息
        LambdaQueryWrapperX<UserDo> userWrapper = new LambdaQueryWrapperX<UserDo>();
        userWrapper.eq(UserDo::getUserId, req.getClientUserId())
                .eq(UserDo::getDeleted, NumCode.ZERO.getCode());
        UserDo userDo = userDao.selectOne(userWrapper);
        if (null == userDo) {
            throw new BizException(AuthError.ACCOUNT_USER_ABSENT);
        }
        if (null == optionsDo) {//给该用户新增资质(2022-08-30修改业务逻辑:能力提供商不生成工单任务)
            //往资质工单表创建一条数据
            QualificationsApplyDo applyDo = new QualificationsApplyDo();
            if (String.valueOf(QualificationType.SOLUTION_PROVIDER.getCode()).equalsIgnoreCase(String.valueOf(req.getCertificateOptionsCode()))) {
                applyDo.setQualificationName(req.getCertificateOptionsCode())
                        .setProcessingState(NumCode.ONE.getCode())
                        .setUserName(userDo.getName())
                        .setRemark(req.getRemark())
                        .setUserId(req.getClientUserId())
                        .setEmail(Strings.isNullOrEmpty(userDo.getMail()) ? "" : userDo.getMail())
                        .setSubmitTime(new Date());
                if (NumCode.ONE.getCode() != qualificationsApplyDao.insert(applyDo)) {
                    log.error("insert ticket_qualifications_apply error message is === " + AuthError.Execute_SQL_SAVE);
                    throw new BizException(AuthError.Execute_SQL_SAVE);
                }
            }
            //往用户账号资质信息表创建一条数据
            CertificateOptionsDo certificateOptionsDo = new CertificateOptionsDo()
                    .setCertificateId(req.getCertificateOptionsCode())
                    .setUserId(req.getClientUserId())
                    .setCertificateApplyStatus(NumCode.THREE.getCode())
                    .setApplyTime(new Date())
                    .setApprovalTime(new Date())
                    .setCertificateStatus(NumCode.ZERO.getCode())
                    .setBusinessId(applyDo.getId())
                    .setRemark(req.getRemark());
            if (NumCode.ONE.getCode() != certificateOptionsDao.insert(certificateOptionsDo)) {
                log.error("insert user_certificate_options error message is === " + AuthError.Execute_SQL_SAVE);
                throw new BizException(AuthError.Execute_SQL_SAVE);
            }
            //往用户标签处理日志表创建一条数据
            tagLog.setCertificateOptionsId(certificateOptionsDo.getId());
        } else {//以前有申请过资质
            //将原有的资质状态刷为审核通过并开启标签
            LambdaUpdateWrapper<CertificateOptionsDo> optionsUpdate = new LambdaUpdateWrapper<CertificateOptionsDo>();
            optionsUpdate.eq(CertificateOptionsDo::getId, optionsDo.getId());
            optionsDo.setApprovalTime(new Date())
                    .setCertificateStatus(NumCode.ZERO.getCode())
                    .setRemark(req.getRemark())
                    .setDeleted(NumCode.ZERO.getCode())
                    .setDeletedTime(0L);
            if (String.valueOf(QualificationType.SOLUTION_PROVIDER.getCode()).equalsIgnoreCase(String.valueOf(req.getCertificateOptionsCode()))) {
                if (NumCode.ONE.getCode() == optionsDo.getCertificateApplyStatus()) {
                    //待审核将数据将原有的工单申请刷为已处理
                    LambdaUpdateWrapper<QualificationsApplyDo> updateApply = new LambdaUpdateWrapper<QualificationsApplyDo>();
                    updateApply.eq(QualificationsApplyDo::getId, optionsDo.getBusinessId());
                    QualificationsApplyDo applyDo = new QualificationsApplyDo()
                            .setProcessingState(NumCode.ONE.getCode())
                            .setRemark(req.getRemark());
                    if (NumCode.ONE.getCode() != qualificationsApplyDao.update(applyDo, updateApply)) {
                        log.error("update ticket_qualifications_apply error message is === " + AuthError.Execute_SQL_UPDATE);
                        throw new BizException(AuthError.Execute_SQL_UPDATE);
                    }
                } else {//已删除或者审核不通过的情况
                    //新增工单申请表并且为已处理(往资质工单表创建一条数据)
                    QualificationsApplyDo applyDo = new QualificationsApplyDo()
                            .setQualificationName(req.getCertificateOptionsCode())
                            .setProcessingState(NumCode.ONE.getCode())
                            .setUserName(userDo.getName())
                            .setRemark(req.getRemark())
                            .setUserId(req.getClientUserId())
                            .setEmail(userDo.getMail())
                            .setSubmitTime(new Date());

                    optionsDo.setBusinessId(applyDo.getId());
                }
            }
            optionsDo.setCertificateApplyStatus(NumCode.THREE.getCode());
            if (NumCode.ONE.getCode() != certificateOptionsDao.update(optionsDo, optionsUpdate)) {
                log.error("update user_certificate_options error message is === " + AuthError.Execute_SQL_UPDATE);
                throw new BizException(AuthError.Execute_SQL_UPDATE);
            }
            tagLog.setCertificateOptionsId(optionsDo.getId());
        }
        //往审核记录表identification_audit_record表插入数据
        IdentificationAuditRecordDo identifierAuditRecordDo = new IdentificationAuditRecordDo()
                .setIdentificationId(req.getCertificateOptionsCode())
                .setUserId(req.getClientUserId())
                .setAuditStatus(NumCode.THREE.getCode())
                .setAuditRemark(req.getRemark())
                .setReviewer(SessionContextUtil.getUser().getUserName());
        if (NumCode.ONE.getCode() != identificationAuditRecordDao.insert(identifierAuditRecordDo)) {
            log.error("insert identification_audit_record error message is === " + AuthError.Execute_SQL_SAVE);
            throw new BizException(AuthError.Execute_SQL_SAVE);
        }
        //往标签操作记录表user_tag_log插入一条数据
        tagLog.setHandleTime(new Date())
                .setUserId(req.getClientUserId())
                .setAdminUserId(SessionContextUtil.getUser().getUserId())
                .setAdminUserName(SessionContextUtil.getUser().getUserName())
                .setRemark(req.getRemark());
        if (NumCode.ONE.getCode() != userTagLogDao.insert(tagLog)) {
            throw new BizException(AuthError.Execute_SQL_SAVE);
        }
        //往用户消息详情表创建一条数据
        MsgTemplateReq msgTemplateReq = new MsgTemplateReq();
        if (String.valueOf(QualificationType.ABILITY_SUPPLIER.getCode()).equals(String.valueOf(req.getCertificateOptionsCode()))) {
            msgTemplateReq.setTempldateCode(MsgCode.ABILITY_SUPPLIER_PASS.getCode());
        } else if (String.valueOf(QualificationType.SOLUTION_PROVIDER.getCode()).equals(String.valueOf(req.getCertificateOptionsCode()))) {
            msgTemplateReq.setTempldateCode(MsgCode.SOLUTION_PROVIDER_PASS.getCode());
        }
        iUserMsgDetailService.addMsgIntoUser(JSON.parseObject(JSONObject.toJSONString(msgTemplateReq), MsgReq.class).setRemark(req.getRemark()).setUserId(req.getClientUserId()));
        //更新用户状态
        userService.updateUserAuthStatus(req.getClientUserId());
    }

    public void turnOffUserCertificate(OnOrOffClientUserCertificateReq req) {
        //查询用户资质
        LambdaQueryWrapperX<CertificateOptionsDo> queryWrapper = new LambdaQueryWrapperX<CertificateOptionsDo>();
        queryWrapper.eq(CertificateOptionsDo::getUserId, req.getClientUserId())
                .eq(CertificateOptionsDo::getCertificateId, req.getCertificateOptionsCode());
        CertificateOptionsDo optionsDo = certificateOptionsDao.selectOne(queryWrapper);
        //根据用户userId查询用户信息
        LambdaQueryWrapperX<UserDo> userWrapper = new LambdaQueryWrapperX<UserDo>();
        userWrapper.eq(UserDo::getUserId, req.getClientUserId())
                .eq(UserDo::getDeleted, NumCode.ZERO.getCode());
        UserDo userDo = userDao.selectOne(userWrapper);
        QualificationsApplyDo applyDo = new QualificationsApplyDo();
        if (String.valueOf(QualificationType.SOLUTION_PROVIDER.getCode()).equalsIgnoreCase(String.valueOf(req.getCertificateOptionsCode()))) {
            //往资质工单表创建一条数据
            applyDo.setQualificationName(req.getCertificateOptionsCode())
                    .setProcessingState(NumCode.ONE.getCode())
                    .setUserName(userDo.getName())
                    .setRemark(req.getRemark())
                    .setUserId(req.getClientUserId())
                    .setEmail(userDo.getMail())
                    .setSubmitTime(new Date());
            if (NumCode.ONE.getCode() != qualificationsApplyDao.insert(applyDo)) {
                log.error("insert ticket_qualifications_apply error message is === " + AuthError.Execute_SQL_SAVE);
                throw new BizException(AuthError.Execute_SQL_SAVE);
            }
        }
        //用户资质信息表刷新状态
        LambdaUpdateWrapper<CertificateOptionsDo> optionsUpdate = new LambdaUpdateWrapper<CertificateOptionsDo>();
        optionsUpdate.eq(CertificateOptionsDo::getId, optionsDo.getId());
        optionsDo.setCertificateApplyStatus(NumCode.TWO.getCode())
                .setApprovalTime(new Date())
                .setCertificateStatus(NumCode.ONE.getCode())
                .setRemark(req.getRemark())
                .setDeleted(NumCode.ZERO.getCode())
                .setDeletedTime(0L)
                .setBusinessId(Objects.isNull(applyDo.getId()) ? null : applyDo.getId());
        if (NumCode.ONE.getCode() != certificateOptionsDao.update(optionsDo, optionsUpdate)) {
            log.error("update user_certificate_options error message is === " + AuthError.Execute_SQL_UPDATE);
            throw new BizException(AuthError.Execute_SQL_UPDATE);
        }
        //往审核记录表identification_audit_record表插入数据
        IdentificationAuditRecordDo identifierAuditRecordDo = new IdentificationAuditRecordDo()
                .setIdentificationId(req.getCertificateOptionsCode())
                .setUserId(req.getClientUserId())
                .setAuditStatus(NumCode.THREE.getCode())
                .setAuditRemark(req.getRemark())
                .setReviewer(SessionContextUtil.getUser().getUserName());
        if (NumCode.ONE.getCode() != identificationAuditRecordDao.insert(identifierAuditRecordDo)) {
            log.error("insert identification_audit_record error message is === " + AuthError.Execute_SQL_SAVE);
            throw new BizException(AuthError.Execute_SQL_SAVE);
        }
        //往标签操作记录表user_tag_log插入一条数据
        UserTagLogDo tagLog = new UserTagLogDo()
                .setCertificateOptionsId(optionsDo.getId())
                .setHandleTime(new Date())
                .setUserId(req.getClientUserId())
                .setAdminUserId(SessionContextUtil.getUser().getUserId())
                .setAdminUserName(SessionContextUtil.getUser().getUserName())
                .setRemark(req.getRemark());
        if (NumCode.ONE.getCode() != userTagLogDao.insert(tagLog)) {
            throw new BizException(AuthError.Execute_SQL_SAVE);
        }
        //往用户消息详情表创建一条数据
        MsgTemplateReq msgTemplateReq = new MsgTemplateReq();
        if (String.valueOf(QualificationType.ABILITY_SUPPLIER.getCode()).equals(String.valueOf(req.getCertificateOptionsCode()))) {
            msgTemplateReq.setTempldateCode(MsgCode.ABILITY_SUPPLIER_NOT_PASS.getCode());
        } else if (String.valueOf(QualificationType.SOLUTION_PROVIDER.getCode()).equals(String.valueOf(req.getCertificateOptionsCode()))) {
            msgTemplateReq.setTempldateCode(MsgCode.SOLUTION_PROVIDER_NOT_PASS.getCode());
        }
        iUserMsgDetailService.addMsgIntoUser(JSON.parseObject(JSONObject.toJSONString(msgTemplateReq), MsgReq.class).setRemark(req.getRemark()).setUserId(req.getClientUserId()));
        //更新用户状态
        userService.updateUserAuthStatus(req.getClientUserId());
    }
}
