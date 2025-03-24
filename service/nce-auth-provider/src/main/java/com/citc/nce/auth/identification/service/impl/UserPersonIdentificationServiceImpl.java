package com.citc.nce.auth.identification.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.citc.nce.auth.certificate.dao.CertificateOptionsDao;
import com.citc.nce.auth.certificate.entity.CertificateOptionsDo;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.identification.dao.IdentificationAuditRecordDao;
import com.citc.nce.auth.identification.dao.UserPersonIdentificationDao;
import com.citc.nce.auth.identification.entity.IdentificationAuditRecordDo;
import com.citc.nce.auth.identification.entity.UserPersonIdentificationDo;
import com.citc.nce.auth.identification.service.UserPersonIdentificationService;
import com.citc.nce.auth.identification.vo.req.PersonIdentificationReq;
import com.citc.nce.auth.identification.vo.resp.WebPersonIdentificationResp;
import com.citc.nce.auth.user.service.UserService;
import com.citc.nce.auth.utils.ValidateIdCardUtils;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.misc.constant.NumCode;
import com.citc.nce.misc.constant.QualificationType;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @authoer:ldy
 * @createDate:2022/7/14 22:58
 * @description:
 */
@Slf4j
@Service
public class UserPersonIdentificationServiceImpl implements UserPersonIdentificationService {
    @Resource
    private UserPersonIdentificationDao userPersonIdentificationDao;
    @Resource
    private UserService userService;
    @Resource
    private IdentificationAuditRecordDao identificationAuditRecordDao;
    @Resource
    private CertificateOptionsDao certificateOptionsDao;

    /**
     * 个人认证申请
     *
     * @param req
     */
    @Override
    @Transactional()
    public void personIdentificationApply(PersonIdentificationReq req) {
        if (!ValidateIdCardUtils.validateCard(req.getIdCard()))
            throw new BizException(AuthError.INVALID_ID_CARD_NUMBER);
        UserPersonIdentificationDo dbDo = userPersonIdentificationDao.selectOne(UserPersonIdentificationDo::getUserId, req.getUserId());
        UserPersonIdentificationDo identificationDo = new UserPersonIdentificationDo();
        BeanUtils.copyProperties(req, identificationDo);
        identificationDo.setPersonAuthTime(new Date())
                .setAuditRemark("认证审核中")
                .setPersonAuthStatus(NumCode.ONE.getCode());
        if (Objects.isNull(dbDo)) {
            userPersonIdentificationDao.insert(identificationDo);
        }
        else if(!dbDo.getPersonAuthStatus().equals(NumCode.THREE.getCode())){
            identificationDo.setId(dbDo.getId());
            userPersonIdentificationDao.updateById(identificationDo);
        }
        IdentificationAuditRecordDo identifierAuditRecordDo = new IdentificationAuditRecordDo()
                .setUpdateTime(new Date())
                .setCreateTime(new Date())
                .setAuditRemark("认证审核中")
                .setUserId(req.getUserId())
                .setAuditStatus(NumCode.ONE.getCode())
                .setIdentificationId(QualificationType.REAL_NAME_USER.getCode());
        //查询该用户是否有对应资质(如果有则刷新一下申请时间  如果没有则直接插入数据)
        LambdaQueryWrapperX<CertificateOptionsDo> lqw = new LambdaQueryWrapperX<CertificateOptionsDo>();
        lqw.eq(CertificateOptionsDo::getUserId, req.getUserId())
                .eq(CertificateOptionsDo::getCertificateId, QualificationType.REAL_NAME_USER.getCode());
        if (null == certificateOptionsDao.selectOne(lqw)) {
            //往用户账号资质信息user_certificate_options表插入数据
            CertificateOptionsDo certificateOptionsDo = new CertificateOptionsDo()
                    .setBusinessId(identificationDo.getId())
                    .setCertificateId(QualificationType.REAL_NAME_USER.getCode())
                    .setUserId(req.getUserId())
                    .setCertificateApplyStatus(NumCode.ONE.getCode())
                    .setApplyTime(new Date())
                    .setCertificateStatus(NumCode.ZERO.getCode())
                    .setCreateTime(new Date())
                    .setUpdateTime(new Date());
            if (NumCode.ONE.getCode() != certificateOptionsDao.insert(certificateOptionsDo)) {
                log.error("personIdentificationApply insert user_certificate_options error message is === " + AuthError.Execute_SQL_SAVE);
                throw new BizException(AuthError.Execute_SQL_SAVE);
            }
        } else {
            //更新用户资质表信息
            CertificateOptionsDo certificateOptionsDo = new CertificateOptionsDo()
                    .setBusinessId(identificationDo.getId())
                    .setCertificateApplyStatus(NumCode.ONE.getCode())
                    .setApplyTime(new Date())
                    .setCertificateStatus(NumCode.ZERO.getCode())
                    .setUpdateTime(new Date());
            LambdaUpdateWrapper<CertificateOptionsDo> updateOptionsWrapper = new LambdaUpdateWrapper<>();
            updateOptionsWrapper.eq(CertificateOptionsDo::getUserId, req.getUserId())
                    .eq(CertificateOptionsDo::getCertificateId, QualificationType.REAL_NAME_USER.getCode());
            if (NumCode.ONE.getCode() != certificateOptionsDao.update(certificateOptionsDo, updateOptionsWrapper)) {
                log.error("personIdentificationApply update user_certificate_options error message is === " + AuthError.Execute_SQL_UPDATE);
                throw new BizException(AuthError.Execute_SQL_UPDATE);
            }
            identifierAuditRecordDo.setAuditRemark("重新编辑再次申请认证");
        }
        //往日志记录identification_audit_record表插入数据
        if(dbDo!=null && !dbDo.getPersonAuthStatus().equals(NumCode.THREE.getCode())){
            if (NumCode.ONE.getCode() != identificationAuditRecordDao.insert(identifierAuditRecordDo)) {
                log.error("personIdentificationApply insert identification_audit_record error message is === " + AuthError.Execute_SQL_SAVE);
                throw new BizException(AuthError.Execute_SQL_SAVE);
            }
        }
        if(!identificationDo.getPersonAuthStatus().equals(NumCode.THREE.getCode())){
            userService.updateUserAuthStatus(req.getUserId());
        }
    }

    @Override
    public WebPersonIdentificationResp getPersonIdentification(String userId) {
        UserPersonIdentificationDo userPersonIdentificationDo = userPersonIdentificationDao.selectOne(UserPersonIdentificationDo::getUserId, userId);
        if (null != userPersonIdentificationDo) {
            WebPersonIdentificationResp resp = new WebPersonIdentificationResp();
            BeanUtils.copyProperties(userPersonIdentificationDo, resp);
            return resp;
        }
        return null;
    }
}
