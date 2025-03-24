package com.citc.nce.authcenter.identification.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.identification.vo.req.PersonIdentificationReq;
import com.citc.nce.authcenter.identification.dao.IdentificationAuditRecordDao;
import com.citc.nce.authcenter.identification.dao.UserCertificateOptionsDao;
import com.citc.nce.authcenter.identification.dao.UserPersonIdentificationDao;
import com.citc.nce.authcenter.identification.entity.IdentificationAuditRecordDo;
import com.citc.nce.authcenter.identification.entity.UserCertificateOptionsDo;
import com.citc.nce.authcenter.identification.entity.UserPersonIdentificationDo;
import com.citc.nce.authcenter.identification.service.UserPersonIdentificationService;
import com.citc.nce.authcenter.utils.ValidateIdCardUtils;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.misc.constant.NumCode;
import com.citc.nce.misc.constant.QualificationType;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

/**
 * @author jiancheng
 */
@Service
@Slf4j
public class UserPersonIdentificationServiceImpl extends ServiceImpl<UserPersonIdentificationDao, UserPersonIdentificationDo> implements UserPersonIdentificationService {
    @Autowired
    private UserCertificateOptionsDao certificateOptionsDao;

    @Autowired
    private IdentificationAuditRecordDao identificationAuditRecordDao;


    /**
     * 个人认证申请
     *
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void personIdentificationApply(PersonIdentificationReq req) {
        if (!ValidateIdCardUtils.validateCard(req.getIdCard()))
            throw new BizException(AuthError.INVALID_ID_CARD_NUMBER);
        //查询是否已经提交过实名认证
        UserPersonIdentificationDo dbDo = baseMapper.selectOne(UserPersonIdentificationDo::getUserId, req.getUserId());

        UserPersonIdentificationDo identificationDo = new UserPersonIdentificationDo();
        BeanUtils.copyProperties(req, identificationDo);
        identificationDo.setPersonAuthTime(new Date())
                .setAuditRemark("认证审核中")
                .setPersonAuthStatus(NumCode.ONE.getCode());

        if (Objects.isNull(dbDo)) {
            //如果没有提交过实名认证则新增
            this.save(identificationDo);
        } else if (!dbDo.getPersonAuthStatus().equals(NumCode.THREE.getCode())) {
            //如果已经提交过但是未审核通过，则使用新的信息重新提交
            identificationDo.setId(dbDo.getId());
            this.updateById(identificationDo);
        }

        //查询该用户是否有对应资质(如果有则刷新一下申请时间  如果没有则直接插入数据)
        boolean reapply = false;
        LambdaQueryWrapperX<UserCertificateOptionsDo> lqw = new LambdaQueryWrapperX<>();
        lqw.eq(UserCertificateOptionsDo::getUserId, req.getUserId())
                .eq(UserCertificateOptionsDo::getCertificateId, QualificationType.REAL_NAME_USER.getCode());
        if (null == certificateOptionsDao.selectOne(lqw)) {
            //往用户账号资质信息user_certificate_options表插入数据
            UserCertificateOptionsDo certificateOptionsDo = new UserCertificateOptionsDo()
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
            UserCertificateOptionsDo certificateOptionsDo = new UserCertificateOptionsDo()
                    .setBusinessId(identificationDo.getId())
                    .setCertificateApplyStatus(NumCode.ONE.getCode())
                    .setApplyTime(new Date())
                    .setCertificateStatus(NumCode.ZERO.getCode())
                    .setUpdateTime(new Date());
            LambdaUpdateWrapper<UserCertificateOptionsDo> updateOptionsWrapper = new LambdaUpdateWrapper<>();
            updateOptionsWrapper.eq(UserCertificateOptionsDo::getUserId, req.getUserId())
                    .eq(UserCertificateOptionsDo::getCertificateId, QualificationType.REAL_NAME_USER.getCode());
            if (NumCode.ONE.getCode() != certificateOptionsDao.update(certificateOptionsDo, updateOptionsWrapper)) {
                log.error("personIdentificationApply update user_certificate_options error message is === " + AuthError.Execute_SQL_UPDATE);
                throw new BizException(AuthError.Execute_SQL_UPDATE);
            }
            reapply = true;
        }
        //往日志记录identification_audit_record表插入数据
        if (dbDo != null && !dbDo.getPersonAuthStatus().equals(NumCode.THREE.getCode())) {
            IdentificationAuditRecordDo identifierAuditRecordDo = new IdentificationAuditRecordDo()
                    .setUpdateTime(new Date())
                    .setCreateTime(new Date())
                    .setAuditRemark(reapply ? "重新编辑再次申请认证" : "认证审核中")
                    .setUserId(req.getUserId())
                    .setAuditStatus(NumCode.ONE.getCode())
                    .setIdentificationId(QualificationType.REAL_NAME_USER.getCode());

            if (NumCode.ONE.getCode() != identificationAuditRecordDao.insert(identifierAuditRecordDo)) {
                log.error("personIdentificationApply insert identification_audit_record error message is === " + AuthError.Execute_SQL_SAVE);
                throw new BizException(AuthError.Execute_SQL_SAVE);
            }
        }
    }
}
