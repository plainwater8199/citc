package com.citc.nce.auth.identification.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.citc.nce.auth.adminUser.vo.resp.UserEnterpriseIdentificationResp;
import com.citc.nce.auth.certificate.dao.CertificateOptionsDao;
import com.citc.nce.auth.certificate.entity.CertificateOptionsDo;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.identification.dao.IdentificationAuditRecordDao;
import com.citc.nce.auth.identification.dao.UserEnterpriseIdentificationDao;
import com.citc.nce.auth.identification.entity.IdentificationAuditRecordDo;
import com.citc.nce.auth.identification.entity.UserEnterpriseIdentificationDo;
import com.citc.nce.auth.identification.service.UserEnterpriseIdentificationService;
import com.citc.nce.auth.identification.service.UserPersonIdentificationService;
import com.citc.nce.auth.identification.vo.req.EnterpriseIdentificationReq;
import com.citc.nce.auth.identification.vo.req.GetEnterpriseInfoByUserIdsReq;
import com.citc.nce.auth.identification.vo.req.IdentificationPlatformPermissionReq;
import com.citc.nce.auth.identification.vo.req.PersonIdentificationReq;
import com.citc.nce.auth.identification.vo.resp.UserProvinceResp;
import com.citc.nce.auth.identification.vo.resp.WebEnterpriseIdentificationResp;
import com.citc.nce.auth.identification.vo.resp.WebPersonIdentificationResp;
import com.citc.nce.auth.user.dao.UserPlatformPermissionsDao;
import com.citc.nce.auth.user.entity.UserPlatformPermissionsDo;
import com.citc.nce.auth.user.service.UserService;
import com.citc.nce.auth.utils.ValidateIdCardUtils;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.misc.constant.NumCode;
import com.citc.nce.misc.constant.QualificationType;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @authoer:ldy
 * @createDate:2022/7/14 23:00
 * @description:
 */
@Service
@Slf4j
public class UserEnterpriseIdentificationServiceImpl implements UserEnterpriseIdentificationService {
    @Resource
    private UserEnterpriseIdentificationDao userEnterpriseIdentificationDao;
    @Resource
    private UserPersonIdentificationService userPersonIdentificationService;
    @Resource
    private UserService userService;
    @Resource
    private IdentificationAuditRecordDao identificationAuditRecordDao;
    @Resource
    private CertificateOptionsDao certificateOptionsDao;

    @Resource
    private UserPlatformPermissionsDao userPlatformPermissionsDao;

    /**
     * 企业认证申请
     *
     * @param req
     */
    @Override
    @Transactional()
    public void enterpriseIdentificationApply(EnterpriseIdentificationReq req) {
        if (!ValidateIdCardUtils.validateCard(req.getIdCard()))
            throw new BizException(AuthError.INVALID_ID_CARD_NUMBER);

        List<UserEnterpriseIdentificationDo> checkUserEnterpriseAccountNames = userEnterpriseIdentificationDao.selectList(UserEnterpriseIdentificationDo::getEnterpriseAccountName, req.getEnterpriseAccountName());
        if (!checkUserEnterpriseAccountNames.isEmpty()) {
            throw new BizException(AuthError.BUSINESS_USER_ACCOUNT_REPEAT);
        }

        //刷新个人认证数据
        PersonIdentificationReq personIdentificationReq = new PersonIdentificationReq();
        BeanUtils.copyProperties(req, personIdentificationReq);
        userPersonIdentificationService.personIdentificationApply(personIdentificationReq);

        UserEnterpriseIdentificationDo userEnterpriseIdentificationDo = userEnterpriseIdentificationDao.selectOne(UserEnterpriseIdentificationDo::getUserId, req.getUserId());
        UserEnterpriseIdentificationDo identificationDo = new UserEnterpriseIdentificationDo();
        BeanUtils.copyProperties(req, identificationDo);
        identificationDo.setEnterpriseAuthTime(new Date())
                .setEnterpriseAuthStatus(NumCode.ONE.getCode())
                .setAuditRemark("认证审核中");
        if (Objects.isNull(userEnterpriseIdentificationDo)) {
            userEnterpriseIdentificationDao.insert(identificationDo);
        } else {
            identificationDo.setId(userEnterpriseIdentificationDo.getId());
            userEnterpriseIdentificationDao.updateById(identificationDo);
        }
        IdentificationAuditRecordDo identifierAuditRecordDo = new IdentificationAuditRecordDo()
                .setUpdateTime(new Date())
                .setCreateTime(new Date())
                .setAuditRemark("认证审核中")
                .setUserId(req.getUserId())
                .setAuditStatus(NumCode.ONE.getCode())
                .setIdentificationId(QualificationType.BUSINESS_USER.getCode());
        //查询该用户是否有对应资质(如果有则刷新一下申请时间  如果没有则直接插入数据)
        LambdaQueryWrapperX<CertificateOptionsDo> lqw = new LambdaQueryWrapperX<CertificateOptionsDo>();
        lqw.eq(CertificateOptionsDo::getUserId, req.getUserId())
                .eq(CertificateOptionsDo::getCertificateId, QualificationType.BUSINESS_USER.getCode());
        if (null == certificateOptionsDao.selectOne(lqw)) {
            //往用户账号资质信息user_certificate_options表插入数据
            CertificateOptionsDo certificateOptionsDo = new CertificateOptionsDo()
                    .setBusinessId(identificationDo.getId())
                    .setCertificateId(QualificationType.BUSINESS_USER.getCode())
                    .setUserId(req.getUserId())
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
            updateOptionsWrapper.eq(CertificateOptionsDo::getUserId, req.getUserId())
                    .eq(CertificateOptionsDo::getCertificateId, QualificationType.BUSINESS_USER.getCode());
            if (NumCode.ONE.getCode() != certificateOptionsDao.update(certificateOptionsDo, updateOptionsWrapper)) {
                log.error("enterpriseIdentificationApply update user_certificate_options error message is === " + AuthError.Execute_SQL_UPDATE);
                throw new BizException(AuthError.Execute_SQL_UPDATE);
            }
            identifierAuditRecordDo.setAuditRemark("重新编辑再次申请认证");
        }
        //往日志记录identification_audit_record表插入数据
        if (NumCode.ONE.getCode() != identificationAuditRecordDao.insert(identifierAuditRecordDo)) {
            log.error("enterpriseIdentificationApply insert identification_audit_record error message is === " + AuthError.Execute_SQL_SAVE);
            throw new BizException(AuthError.Execute_SQL_SAVE);
        }
        //更新用户状态
        userService.updateUserAuthStatus(req.getUserId());
    }

    /**
     * 查看企业认证信息
     *
     * @param usedId
     * @return
     */
    @Override
    public WebEnterpriseIdentificationResp getIdentificationInfo(String usedId) {
        WebEnterpriseIdentificationResp resp = new WebEnterpriseIdentificationResp();
        WebPersonIdentificationResp personIdentification = userPersonIdentificationService.getPersonIdentification(usedId);
        if (null != personIdentification) {
            BeanUtils.copyProperties(personIdentification, resp);
        }
        UserEnterpriseIdentificationDo userEnterpriseIdentificationDo = userEnterpriseIdentificationDao.selectOne(UserEnterpriseIdentificationDo::getUserId, usedId);
        if (null != userEnterpriseIdentificationDo) {
            //清理个人认证备注，以企业认证备注为主
            resp.setAuditRemark(null);
            BeanUtils.copyProperties(userEnterpriseIdentificationDo, resp);
            return resp;
        }
        return null;
    }
    @Override
    public WebEnterpriseIdentificationResp getEnterpriseIdentificationByUserId(String usedId) {
        WebEnterpriseIdentificationResp resp = new WebEnterpriseIdentificationResp();
        UserEnterpriseIdentificationDo userEnterpriseIdentificationDo = userEnterpriseIdentificationDao.selectOne(UserEnterpriseIdentificationDo::getUserId, usedId);
        if (null != userEnterpriseIdentificationDo) {
            BeanUtils.copyProperties(userEnterpriseIdentificationDo, resp);
            return resp;
        }
        return null;
    }
    @Override
    public WebEnterpriseIdentificationResp getEnterpriseIdentificationByEnterpriseId(Long id){
        WebEnterpriseIdentificationResp resp = new WebEnterpriseIdentificationResp();
        UserEnterpriseIdentificationDo userEnterpriseIdentificationDo = userEnterpriseIdentificationDao.selectOne(UserEnterpriseIdentificationDo::getId, id);
        if (null != userEnterpriseIdentificationDo) {
            BeanUtils.copyProperties(userEnterpriseIdentificationDo, resp);
            return resp;
        }
        return null;
    }

    /**
     * 校验企业用户名是否唯一
     *
     * @param enterpriseAccountName
     * @return true 唯一，  false 不唯一
     */
    @Override
    public Boolean checkEnterpriseAccountNameUnique(String enterpriseAccountName) {
        QueryWrapper<UserEnterpriseIdentificationDo> queryWrapper = new QueryWrapper();
        queryWrapper.eq("deleted", NumCode.ZERO.getCode()).eq("enterprise_account_name", enterpriseAccountName);
        List<UserEnterpriseIdentificationDo> list = userEnterpriseIdentificationDao.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return true;
        }
        return false;

    }


    /**
     * 平台使用权限申请
     * wll   2022/08/03
     */
    @Override
    public void applyPlatformPermission(IdentificationPlatformPermissionReq req) {
        UserPlatformPermissionsDo userPlatformPermissionsDo = userPlatformPermissionsDao.selectOne(UserPlatformPermissionsDo::getUserId, req.getUserId(), UserPlatformPermissionsDo::getProtal, req.getProtal());
        if (ObjectUtil.isNull(userPlatformPermissionsDo)) {
            //没有历史数据  直接保存
            UserPlatformPermissionsDo insertDo = new UserPlatformPermissionsDo()
                    .setUserId(req.getUserId())
                    .setUserStatus(NumCode.ZERO.getCode())
                    .setApplyTime(new Date())
                    .setApprovalStatus(NumCode.ONE.getCode())
                    .setProtal(req.getProtal())
                    .setApprovalLogId(UUID.randomUUID().toString());
            userPlatformPermissionsDao.insert(insertDo);
        } else {
            LambdaUpdateWrapper<UserPlatformPermissionsDo> wrapper = new LambdaUpdateWrapper<>();
            UserPlatformPermissionsDo userDo = new UserPlatformPermissionsDo()
                    .setApplyTime(new Date())
                    .setApprovalStatus(NumCode.ONE.getCode());
            wrapper.eq(UserPlatformPermissionsDo::getUserId, req.getUserId())
                    .eq(UserPlatformPermissionsDo::getProtal, req.getProtal());
            userPlatformPermissionsDao.update(userDo, wrapper);
        }
    }

    @Override
    public Map<String, UserEnterpriseIdentificationResp> getEnterpriseInfoByUserIds(GetEnterpriseInfoByUserIdsReq req) {
        Map<String, UserEnterpriseIdentificationResp> userEnterpriseInfoMap = new HashMap<>();
        List<String> userIds = req.getUserIds();
        LambdaQueryWrapperX<UserEnterpriseIdentificationDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(UserEnterpriseIdentificationDo::getDeleted, 0);
        if (userIds != null && !userIds.isEmpty()) {
            queryWrapperX.in(UserEnterpriseIdentificationDo::getUserId, userIds);
        }
        List<UserEnterpriseIdentificationDo> userEnterpriseIdentificationDos = userEnterpriseIdentificationDao.selectList(queryWrapperX);
        if (userEnterpriseIdentificationDos != null && !userEnterpriseIdentificationDos.isEmpty()) {
            UserEnterpriseIdentificationResp resp;
            String userId;
            for (UserEnterpriseIdentificationDo item : userEnterpriseIdentificationDos) {
                resp = new UserEnterpriseIdentificationResp();
                userId = item.getUserId();
                BeanUtils.copyProperties(item, resp);
                userEnterpriseInfoMap.put(userId, resp);
            }
        }
        return userEnterpriseInfoMap;
    }

    @Override
    public List<UserEnterpriseIdentificationResp> getEnterpriseInfoByIds(List<String> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            LambdaQueryWrapperX<UserEnterpriseIdentificationDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.eq(UserEnterpriseIdentificationDo::getDeleted, 0);
            queryWrapperX.in(UserEnterpriseIdentificationDo::getUserId, ids);
            List<UserEnterpriseIdentificationDo> userEnterpriseIdentificationDoList = userEnterpriseIdentificationDao.selectList(queryWrapperX);
            if (CollectionUtils.isNotEmpty(userEnterpriseIdentificationDoList)) {
                List<UserEnterpriseIdentificationResp> userEnterpriseIdentificationResps = BeanUtil.copyToList(userEnterpriseIdentificationDoList, UserEnterpriseIdentificationResp.class);
                return userEnterpriseIdentificationResps;
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getEnterpriseIdentificationInfoByUserIds(List<String> customerIds) {
        Map<String, String> resultMap = new HashMap<>();
        LambdaQueryWrapperX<UserEnterpriseIdentificationDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.in(UserEnterpriseIdentificationDo::getUserId, customerIds);
        List<UserEnterpriseIdentificationDo> userEnterpriseIdentificationDos = userEnterpriseIdentificationDao.selectList(queryWrapperX);
        if (CollectionUtils.isNotEmpty(userEnterpriseIdentificationDos)) {
            for (UserEnterpriseIdentificationDo item : userEnterpriseIdentificationDos) {
                resultMap.put(item.getUserId(), item.getEnterpriseAccountName());
            }
        }
        return resultMap;
    }
}
