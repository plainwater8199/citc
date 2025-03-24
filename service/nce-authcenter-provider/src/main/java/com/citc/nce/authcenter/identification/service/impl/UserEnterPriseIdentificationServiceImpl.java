package com.citc.nce.authcenter.identification.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.identification.vo.req.EnterpriseIdentificationReq;
import com.citc.nce.auth.identification.vo.req.PersonIdentificationReq;
import com.citc.nce.authcenter.identification.service.UserEnterPriseIdentificationService;
import com.citc.nce.authcenter.identification.dao.CertificateOptionsDao;
import com.citc.nce.authcenter.identification.dao.IdentificationAuditRecordDao;
import com.citc.nce.authcenter.identification.dao.UserEnterpriseIdentificationDao;
import com.citc.nce.authcenter.identification.entity.CertificateOptionsDo;
import com.citc.nce.authcenter.identification.entity.IdentificationAuditRecordDo;
import com.citc.nce.authcenter.identification.entity.UserEnterpriseIdentificationDo;
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
public class UserEnterPriseIdentificationServiceImpl extends ServiceImpl<UserEnterpriseIdentificationDao, UserEnterpriseIdentificationDo> implements UserEnterPriseIdentificationService {
    @Autowired
    private CertificateOptionsDao certificateOptionsDao;
    @Autowired
    private IdentificationAuditRecordDao identificationAuditRecordDao;

    /**
     * 一个csp下的企业用户名不能重复
     *
     * @param enterpriseAccountName
     * @return
     */
    @Override
    public Boolean isUniqueEnterpriseAccountName(String cspId, String enterpriseAccountName) {
        return !this.lambdaQuery()
                .likeRight(UserEnterpriseIdentificationDo::getUserId, cspId)
                .eq(UserEnterpriseIdentificationDo::getEnterpriseAccountName, enterpriseAccountName)
                .exists();
    }

    /**
     * 企业实名认证申请
     *
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void identificationApply(EnterpriseIdentificationReq req) {
        if(!isUniqueEnterpriseAccountName(req.getUserId(), req.getEnterpriseAccountName()))
            throw new BizException(AuthError.BUSINESS_USER_ACCOUNT_REPEAT);
        String customerId = req.getUserId();
        UserEnterpriseIdentificationDo userEnterpriseIdentificationDo = baseMapper.selectOne(UserEnterpriseIdentificationDo::getUserId, customerId);
        UserEnterpriseIdentificationDo identificationDo = new UserEnterpriseIdentificationDo();
        BeanUtils.copyProperties(req, identificationDo);
        identificationDo.setEnterpriseAuthTime(new Date())
                .setEnterpriseAuthStatus(NumCode.ONE.getCode())
                .setAuditRemark("认证审核中");
        if (Objects.isNull(userEnterpriseIdentificationDo)) {
            identificationDo.setDeleted(0);
            baseMapper.insert(identificationDo);
        } else {
            identificationDo.setId(userEnterpriseIdentificationDo.getId());
            baseMapper.updateById(identificationDo);
        }
        //查询该用户是否有对应资质(如果有则刷新一下申请时间  如果没有则直接插入数据)
        boolean reapply = false;
        LambdaQueryWrapperX<CertificateOptionsDo> lqw = new LambdaQueryWrapperX<CertificateOptionsDo>();
        lqw.eq(CertificateOptionsDo::getUserId, customerId)
                .eq(CertificateOptionsDo::getCertificateId, QualificationType.BUSINESS_USER.getCode());
        if (null == certificateOptionsDao.selectOne(lqw)) {
            //往用户账号资质信息user_certificate_options表插入数据
            CertificateOptionsDo certificateOptionsDo = new CertificateOptionsDo()
                    .setBusinessId(identificationDo.getId())
                    .setCertificateId(QualificationType.BUSINESS_USER.getCode())
                    .setUserId(customerId)
                    .setCertificateApplyStatus(NumCode.ONE.getCode())
                    .setApplyTime(new Date())
                    .setCertificateStatus(NumCode.ZERO.getCode())
                    .setCreateTime(new Date())
                    .setUpdateTime(new Date());
            if (NumCode.ONE.getCode() != certificateOptionsDao.insert(certificateOptionsDo)) {
                log.error("enterpriseIdentificationApply insert user_certificate_options error message is === " + AuthError.Execute_SQL_SAVE);
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
            updateOptionsWrapper.eq(CertificateOptionsDo::getUserId, customerId)
                    .eq(CertificateOptionsDo::getCertificateId, QualificationType.BUSINESS_USER.getCode());
            if (NumCode.ONE.getCode() != certificateOptionsDao.update(certificateOptionsDo, updateOptionsWrapper)) {
                log.error("enterpriseIdentificationApply update user_certificate_options error message is === " + AuthError.Execute_SQL_UPDATE);
                throw new BizException(AuthError.Execute_SQL_UPDATE);
            }
            reapply = true;
        }
        //往日志记录identification_audit_record表插入数据
        IdentificationAuditRecordDo identifierAuditRecordDo = new IdentificationAuditRecordDo()
                .setUpdateTime(new Date())
                .setCreateTime(new Date())
                .setAuditRemark(reapply ? "重新编辑再次申请认证"  : "认证审核中")
                .setUserId(customerId)
                .setAuditStatus(NumCode.ONE.getCode())
                .setIdentificationId(QualificationType.BUSINESS_USER.getCode());
        if (NumCode.ONE.getCode() != identificationAuditRecordDao.insert(identifierAuditRecordDo)) {
            log.error("enterpriseIdentificationApply insert identification_audit_record error message is === " + AuthError.Execute_SQL_SAVE);
            throw new BizException(AuthError.Execute_SQL_SAVE);
        }
    }
}
