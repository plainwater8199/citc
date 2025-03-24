package com.citc.nce.authcenter.identification.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.identification.vo.req.QueryAllCertificateOptionsReq;
import com.citc.nce.authcenter.identification.dao.*;
import com.citc.nce.authcenter.identification.entity.*;
import com.citc.nce.authcenter.identification.service.IdentificationService;
import com.citc.nce.authcenter.identification.vo.*;
import com.citc.nce.authcenter.identification.vo.req.*;
import com.citc.nce.authcenter.identification.vo.resp.*;
import com.citc.nce.authcenter.sysmsg.dto.MsgReq;
import com.citc.nce.authcenter.sysmsg.service.UserSysMsgService;
import com.citc.nce.authcenter.user.dao.UserDao;
import com.citc.nce.authcenter.user.entity.UserDo;
import com.citc.nce.authcenter.user.service.UserService;
import com.citc.nce.authcenter.utils.ValidateIdCardUtils;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;


/**
 *
 *  代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 *  代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 *  代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 *  代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 *  代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 *  代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 *  代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 *
 */

@Service
@Slf4j
public class IdentificationServiceImpl implements IdentificationService {

    @Resource
    private UserPlatformPermissionsDao userPlatformPermissionsDao;
    @Resource
    private UserEnterpriseIdentificationDao userEnterpriseIdentificationDao;
    @Resource
    private ApprovalLogDao approvalLogDao;
    @Resource
    private UserPersonIdentificationDao userPersonIdentificationDao;
    @Resource
    private CertificateOptionsDao certificateOptionsDao;
    @Resource
    private IdentificationAuditRecordDao identificationAuditRecordDao;
    @Resource
    private UserDao userDao;
    @Resource
    private UserCertificateOptionsDao userCertificateOptionsDao;
    @Resource
    private UserCertificateDao userCertificateDao;
    @Resource
    private ProcessingRecordApi processingRecordApi;
    @Resource
    private QualificationsApplyDao qualificationsApplyDao;
    @Resource
    private UserTagLogDao userTagLogDao;
    @Resource
    private UserService userService;
    @Resource
    private UserSysMsgService userSysMsgService;

    @Override
    public void initializeUserIdentification(String userId) {
        ArrayList<UserPlatformPermissionsDo> userPlatformPermissionsDos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            UserPlatformPermissionsDo platformPermissionsDo = new UserPlatformPermissionsDo();
            platformPermissionsDo.setUserId(userId);
            if (1 == (i + 1)) platformPermissionsDo.setUserStatus(1);//用户状态(0初始化 默认未开启,1启用,2禁用)
            else platformPermissionsDo.setUserStatus(0);//用户状态(0初始化 默认未开启,1启用,2禁用)
            platformPermissionsDo.setApplyTime(new Date());
            platformPermissionsDo.setApprovalStatus(0);//申请审核状态(0初始化 1 待审核 2 审核不通过 3 审核通过)
            platformPermissionsDo.setProtal(i + 1);//平台信息(1核能商城2硬核桃3chatbot)
            //注册初始化默认日志id：1111111111111111
            platformPermissionsDo.setApprovalLogId(UUID.randomUUID().toString().replaceAll("-", " "));
            platformPermissionsDo.setDeleted(0);
            platformPermissionsDo.setDeletedTime((long) 0);
            platformPermissionsDo.setCreator(userId).setUpdater(userId);
            userPlatformPermissionsDos.add(platformPermissionsDo);
        }
        userPlatformPermissionsDao.insertBatch(userPlatformPermissionsDos);
    }

    @Override
    public UserEnterpriseIdentificationDo findUserEnterpriseIdent(String userId) {
        LambdaQueryWrapperX<UserEnterpriseIdentificationDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(UserEnterpriseIdentificationDo::getDeleted, 0);
        queryWrapper.eq(UserEnterpriseIdentificationDo::getUserId, userId);
        List<UserEnterpriseIdentificationDo> userEnterpriseIdentificationDos = userEnterpriseIdentificationDao.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(userEnterpriseIdentificationDos)) {
            return userEnterpriseIdentificationDos.get(0);
        }
        return null;
    }

    @Override
    public UserPlatformPermissionsDo findByPlatAndUserId(Integer platformType, String userId) {
        LambdaQueryWrapperX<UserPlatformPermissionsDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(UserPlatformPermissionsDo::getDeleted, 0);
        queryWrapper.eq(UserPlatformPermissionsDo::getUserId, userId);
        queryWrapper.eq(UserPlatformPermissionsDo::getProtal, platformType);
        List<UserPlatformPermissionsDo> platformPermissionsDos = userPlatformPermissionsDao.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(platformPermissionsDos)) {
            return platformPermissionsDos.get(0);
        }
        return null;
    }

    @Override
    public List<ApprovalLogDo> findApprovalById(String approvalLogId) {
        LambdaQueryWrapperX<ApprovalLogDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(ApprovalLogDo::getDeleted, 0);
        queryWrapper.eq(ApprovalLogDo::getApprovalLogId, approvalLogId)
                .orderByDesc(ApprovalLogDo::getHandleTime);
        return approvalLogDao.selectList(queryWrapper);
    }

    @Override
    public GetPersonIdentificationResp getPersonIdentification() {
        String userId = SessionContextUtil.getUser().getUserId();
        return getPersonIdentification(userId);
    }

    @Override
    public GetPersonIdentificationResp getPersonIdentificationByUserId(String userId) {
        GetPersonIdentificationResp resp = new GetPersonIdentificationResp();
        UserPersonIdentificationDo personIdentificationDo = userPersonIdentificationDao.selectOne(UserPersonIdentificationDo::getUserId, userId);
        if (null != personIdentificationDo) {
            //清理个人认证备注，以企业认证备注为主
            resp.setAuditRemark(null);
            BeanUtils.copyProperties(personIdentificationDo, resp);
        }
        return resp;
    }

    @Override
    @Transactional()
    public PersonIdentificationApplyResp personIdentificationApply(PersonIdentificationApplyReq req) {
        if (!ValidateIdCardUtils.validateCard(req.getIdCard()))
            throw new BizException(AuthCenterError.INVALID_ID_CARD_NUMBER);
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
        LambdaQueryWrapperX<CertificateOptionsDo> lqw = new LambdaQueryWrapperX<>();
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
                log.error("personIdentificationApply insert user_certificate_options error message is === " + AuthCenterError.Execute_SQL_SAVE);
                throw new BizException(AuthCenterError.Execute_SQL_SAVE);
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
                log.error("personIdentificationApply update user_certificate_options error message is === " + AuthCenterError.Execute_SQL_UPDATE);
                throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
            }
            identifierAuditRecordDo.setAuditRemark("重新编辑再次申请认证");
        }
        //往日志记录identification_audit_record表插入数据
        if(dbDo!=null && !dbDo.getPersonAuthStatus().equals(NumCode.THREE.getCode())){
            if (NumCode.ONE.getCode() != identificationAuditRecordDao.insert(identifierAuditRecordDo)) {
                log.error("personIdentificationApply insert identification_audit_record error message is === " + AuthCenterError.Execute_SQL_SAVE);
                throw new BizException(AuthCenterError.Execute_SQL_SAVE);
            }
        }
        if(!identificationDo.getPersonAuthStatus().equals(NumCode.THREE.getCode())){
            updateUserAuthStatus(req.getUserId());
        }
        PersonIdentificationApplyResp resp = new PersonIdentificationApplyResp();
        resp.setResult(true);
        return resp;
    }

    @Override
    public void updateUserAuthStatus(String userId) {
        //查询用户标签最新状态刷到user表中去
        LambdaQueryWrapper<CertificateOptionsDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CertificateOptionsDo::getUserId, userId)
                .orderByDesc(CertificateOptionsDo::getUpdateTime);
        List<CertificateOptionsDo> dataList = certificateOptionsDao.selectList(queryWrapper);
        LambdaUpdateWrapper<UserDo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(UserDo::getUserId, userId).eq(UserDo::getDeleted, NumCode.ZERO.getCode());
        UserDo userDo = new UserDo();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(dataList)) {
            userDo.setAuthStatus(dataList.get(0).getCertificateApplyStatus());
            dataList.forEach(i -> {
                if (String.valueOf(QualificationType.BUSINESS_USER.getCode()).equals(String.valueOf(i.getCertificateId()))) {
                    userDo.setEnterpriseAuthStatus(i.getCertificateApplyStatus());
                    if (NumCode.THREE.getCode() == i.getCertificateApplyStatus()) {
                        userDo.setUserType(NumCode.ONE.getCode());
                    }
                }
                if (String.valueOf(QualificationType.REAL_NAME_USER.getCode()).equals(String.valueOf(i.getCertificateId()))) {
                    userDo.setPersonAuthStatus(i.getCertificateApplyStatus());
                }
            });
        }
        if (NumCode.ONE.getCode() != userDao.update(userDo, lambdaUpdateWrapper)) {
            throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
        }
    }

    @Override
    public GetEnterpriseIdentificationResp getEnterpriseIdentificationInfo() {
        return getEnterpriseIdentificationInfoByUserId(SessionContextUtil.getUser().getUserId());
    }

    @Override
    public GetEnterpriseIdentificationResp getEnterpriseIdentificationInfoByUserId(String userId) {
        GetEnterpriseIdentificationResp resp = new GetEnterpriseIdentificationResp();
        String usedId = SessionContextUtil.getUser().getUserId();
        GetPersonIdentificationResp personIdentification = getPersonIdentification();
        if (null != personIdentification) {
            BeanUtils.copyProperties(personIdentification, resp);
        }
        UserEnterpriseIdentificationDo userEnterpriseIdentificationDo = userEnterpriseIdentificationDao.selectOne(UserEnterpriseIdentificationDo::getUserId, usedId);
        if (null != userEnterpriseIdentificationDo) {
            //清理个人认证备注，以企业认证备注为主
            resp.setAuditRemark(null);
            BeanUtils.copyProperties(userEnterpriseIdentificationDo, resp);
        }
        return resp;
    }

    @Override
    @Transactional()
    public EnterpriseIdentificationApplyResp enterpriseIdentificationApply(EnterpriseIdentificationApplyReq req) {
        if (!ValidateIdCardUtils.validateCard(req.getIdCard()))
            throw new BizException(AuthCenterError.INVALID_ID_CARD_NUMBER);
        UserDo userDo = userDao.selectOne(new LambdaQueryWrapper<UserDo>().eq(UserDo::getUserId, req.getUserId()));
        Assert.notNull(userDo, "用户不存在:" + req.getUserId());
        if (Objects.equals(userDo.getAuthStatus(), NumCode.ONE.getCode()))
            throw new BizException("申请正在审核中，请勿重复提交！");
        //刷新个人认证数据
//        PersonIdentificationApplyReq personIdentificationReq = new PersonIdentificationApplyReq();
//        BeanUtils.copyProperties(req, personIdentificationReq);
//        personIdentificationApply(personIdentificationReq);
        UserPersonIdentificationDo dbDo = userPersonIdentificationDao.selectOne(UserPersonIdentificationDo::getUserId, req.getUserId());
        UserPersonIdentificationDo userPersonIdentificationDo = new UserPersonIdentificationDo();
        BeanUtils.copyProperties(req, userPersonIdentificationDo);
        if (Objects.isNull(dbDo)) {
            userPersonIdentificationDo.setPersonAuthStatus(NumCode.ZERO.getCode());
            userPersonIdentificationDao.insert(userPersonIdentificationDo);
        } else {
            userPersonIdentificationDo.setId(dbDo.getId());
            userPersonIdentificationDo.setPersonAuthStatus(dbDo.getPersonAuthStatus());
            userPersonIdentificationDao.updateById(userPersonIdentificationDo);
        }

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
                log.error("enterpriseIdentificationApply insert user_certificate_options error message is === " + AuthCenterError.Execute_SQL_SAVE);
                throw new BizException(AuthCenterError.Execute_SQL_SAVE);
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
                log.error("enterpriseIdentificationApply update user_certificate_options error message is === " + AuthCenterError.Execute_SQL_UPDATE);
                throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
            }
            identifierAuditRecordDo.setAuditRemark("重新编辑再次申请认证");
        }
        //往日志记录identification_audit_record表插入数据
        if (NumCode.ONE.getCode() != identificationAuditRecordDao.insert(identifierAuditRecordDo)) {
            log.error("enterpriseIdentificationApply insert identification_audit_record error message is === " + AuthCenterError.Execute_SQL_SAVE);
            throw new BizException(AuthCenterError.Execute_SQL_SAVE);
        }
        //更新用户状态
        updateUserAuthStatus(req.getUserId());
        EnterpriseIdentificationApplyResp resp = new EnterpriseIdentificationApplyResp();
        resp.setResult(true);
        return resp;

    }

    @Override
    public CheckEnterpriseAccountNameUniqueResp checkEnterpriseAccountNameUnique(String enterpriseAccountName) {
        CheckEnterpriseAccountNameUniqueResp resp = new CheckEnterpriseAccountNameUniqueResp();
        if (StringUtils.isEmpty(enterpriseAccountName)){
            throw new BizException(AuthCenterError.PARAM_MISS);
        }
        QueryWrapper<UserEnterpriseIdentificationDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", NumCode.ZERO.getCode()).eq("enterprise_account_name", enterpriseAccountName);
        resp.setResult((null == userEnterpriseIdentificationDao.selectOne(queryWrapper)));
        return resp;
    }


    @Override
    public QueryCertificateOptionListResp queryCertificateOptionList(QueryCertificateOptionListReq req) {
        QueryCertificateOptionListResp resp = new QueryCertificateOptionListResp();
        String userId = SessionContextUtil.getUser().getUserId();
        List<UserCertificateItem> userCertificateItems = userCertificateOptionsDao.selectCertificateOptionsList(userId);
        resp.setUserCertificateItems(userCertificateItems);
        return resp;
    }

    @Override
    public QueryAllCertificateOptionsResp queryAllCertificateOptions(QueryAllCertificateOptionsReq req) {
        QueryAllCertificateOptionsResp resp = new QueryAllCertificateOptionsResp();
        String userId = SessionContextUtil.getUser().getUserId();
        List<UserCertificateItem> userCertificateItems = userCertificateOptionsDao.selectCertificateOptionsList(userId);
        resp.setUserCertificateItems(userCertificateItems);
        return resp;
    }

    @Override
    public CheckPermissionStatusResp checkPermissionStatus(CheckPermissionStatusReq req) {
        QueryWrapper<UserCertificateOptionsDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", NumCode.ZERO.getCode());
        queryWrapper.eq("user_id", SessionContextUtil.getUser().getUserId());
        List<UserCertificateOptionsDo> userCertificateOptionsDoList = userCertificateOptionsDao.selectList(queryWrapper);

        CheckPermissionStatusResp resp = new CheckPermissionStatusResp();
        resp.setIsSolutionProvider(false);
        resp.setIsAbilitySupplier(false);
        userCertificateOptionsDoList.forEach(item -> {
            if (item.getCertificateId().equals(QualificationType.ABILITY_SUPPLIER.getCode()) && item.getCertificateStatus().equals(NumCode.ZERO.getCode())) {
                resp.setIsAbilitySupplier(true);
            }
            if (item.getCertificateId().equals(QualificationType.SOLUTION_PROVIDER.getCode()) && item.getCertificateStatus().equals(NumCode.ZERO.getCode())) {
                resp.setIsSolutionProvider(true);
            }
        });
        return resp;
    }

    @Override
    public GetCertificateListResp getCertificateList() {
        GetCertificateListResp resp = new GetCertificateListResp();
        List<UserCertificateDo> userCertificateList = getUserCertificate();
        if (org.springframework.util.CollectionUtils.isEmpty(userCertificateList)) {
            return null;
        }
        List<CertificateItem> certificateItems = BeanUtil.copyToList(userCertificateList, CertificateItem.class);
        CertificateItem certificateItem0 = new CertificateItem();
        certificateItem0.setId(0L);
        certificateItem0.setCertificateName("全部");
        certificateItems.add(certificateItem0);
        resp.setCertificateItems(certificateItems);
        return resp;
    }

    @Override
    @Transactional()
    public OnOrOffUserCertificateResp onOrOffUserCertificate(OnOrOffUserCertificateReq req) {
        if (NumCode.ZERO.getCode() == req.getCertificateStatus()) {//开启
            turnOnUserCertificate(req);
        } else if (NumCode.ONE.getCode() == req.getCertificateStatus()) {//关闭
            turnOffUserCertificate(req);
        } else {
            throw new BizException(AuthCenterError.PARAMETER_BAD);
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

        OnOrOffUserCertificateResp resp = new OnOrOffUserCertificateResp();
        resp.setResult(true);
        return resp;
    }

    @Override
    public GetUserTagLogByCertificateOptionsIdResp getUserTagLogByCertificateOptionsId(GetUserTagLogByCertificateOptionsIdReq req) {
        GetUserTagLogByCertificateOptionsIdResp resp = new GetUserTagLogByCertificateOptionsIdResp();
        LambdaQueryWrapperX<UserTagLogDo> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(UserTagLogDo::getCertificateOptionsId, req.getCertificateOptionsId())
                .orderByDesc(UserTagLogDo::getHandleTime);
        List<UserTagLogDo> logList = userTagLogDao.selectList(wrapper);
        List<UserTagLogItem> dataList = new ArrayList<>();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(logList)) {
            logList.forEach(i -> {
                UserTagLogItem item = new UserTagLogItem();
                BeanUtils.copyProperties(i, item);
                dataList.add(item);
            });
        }
        resp.setUserTagLogItems(dataList);
        return resp;
    }

    @Override
    public GetClientUserCertificateResp getClientUserCertificate(GetClientUserCertificateReq req) {
        GetClientUserCertificateResp resp = new GetClientUserCertificateResp();
        List<UserCertificateItem> userCertificateItems = userCertificateOptionsDao.selectCertificateOptionsList(req.getClientUserId());
        resp.setUserCertificateItems(userCertificateItems);
        return resp;
    }

    @Override
    public ViewRemarkHistoryResp viewRemarkHistory(ViewRemarkHistoryReq req) {
        ViewRemarkHistoryResp resp = new ViewRemarkHistoryResp();
        LambdaQueryWrapperX<IdentificationAuditRecordDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(IdentificationAuditRecordDo::getUserId, req.getClientUserId())
                .eq(IdentificationAuditRecordDo::getIdentificationId, req.getIdentificationId())
                .gt(IdentificationAuditRecordDo::getAuditStatus, NumCode.ONE.getCode())
                .orderByDesc(IdentificationAuditRecordDo::getUpdateTime);
        List<IdentificationAuditRecordDo> list = identificationAuditRecordDao.selectList(queryWrapper);
        List<IdentificationAuditItem> identificationAuditItems = new ArrayList<>();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
            list.forEach(i ->
                    identificationAuditItems.add(new IdentificationAuditItem()
                            .setAuditTime(i.getUpdateTime())
                            .setAuditAccount(i.getReviewer())
                            .setAuditRemark(i.getAuditRemark()))
            );
        }
        resp.setIdentificationAuditItems(identificationAuditItems);
        return resp;
    }

    @Override
    @Transactional()
    public AuditIdentificationResp auditIdentification(AuditIdentificationReq req) {
        //审核用户私人
        if (NumCode.ONE.getCode() == req.getFlag()) {
            if (!String.valueOf(QualificationType.REAL_NAME_USER.getCode()).equals(String.valueOf(req.getIdentificationId()))) {
                throw new BizException(AuthCenterError.AUDIT_AND_QUALIFICATION_TRANSFER);
            }
            UserPersonIdentificationDo personDo = new UserPersonIdentificationDo()
                    .setAuditRemark(req.getAuditRemark())
                    .setPersonAuthStatus(req.getAuthStatus())
                    /**
                     * 添加个人认证审核时间
                     * 2023/3/28
                     */
                    .setPersonAuthAuditTime(new Date())
                    .setUpdater(SessionContextUtil.getUser().getUserId())
                    .setUpdateTime(new Date());
            LambdaUpdateWrapper<UserPersonIdentificationDo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserPersonIdentificationDo::getUserId, req.getClientUserId());
            if (NumCode.ONE.getCode() != userPersonIdentificationDao.update(personDo, updateWrapper)) {
                throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
            }
            updateCertificateAndInsertRecord(req);

            //添加处理记录
            String processingContent = ProcessingContentEnum.SMRZSHTG.getName();
            if (!req.getAuthStatus().equals(NumCode.THREE.getCode())) {
                processingContent = ProcessingContentEnum.SMRZSHWTG.getName();
            }
            addProcessingRecord2(req.getClientUserId(), processingContent, BusinessTypeEnum.YHGL_TY.getCode(), req.getAuditRemark());
        }
        //审核企业
        if (NumCode.TWO.getCode() == req.getFlag()) {
            if (!String.valueOf(QualificationType.BUSINESS_USER.getCode()).equals(String.valueOf(req.getIdentificationId()))) {
                throw new BizException(AuthCenterError.AUDIT_AND_QUALIFICATION_TRANSFER);
            }
            UserEnterpriseIdentificationDo enterpriseDo = new UserEnterpriseIdentificationDo()
                    .setAuditRemark(req.getAuditRemark())
                    .setEnterpriseAuthStatus(req.getAuthStatus())
                    /**
                     * 添加企业认证审核时间
                     * 2023/3/28
                     */
                    .setEnterpriseAuthAuditTime(new Date())
                    .setUpdater(SessionContextUtil.getUser().getUserId())
                    .setUpdateTime(new Date());
            LambdaUpdateWrapper<UserEnterpriseIdentificationDo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserEnterpriseIdentificationDo::getUserId, req.getClientUserId());
            if (NumCode.ONE.getCode() != userEnterpriseIdentificationDao.update(enterpriseDo, updateWrapper)) {
                throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
            }
            updateCertificateAndInsertRecord(req);
            if (NumCode.THREE.getCode() == req.getAuthStatus()) {//企业认证通过查询个人认证是否通过
                //审核通过之后将企业名称刷到user表用户名
                LambdaQueryWrapperX<UserEnterpriseIdentificationDo> wrapper = new LambdaQueryWrapperX<>();
                wrapper.eq(UserEnterpriseIdentificationDo::getUserId, req.getClientUserId())
                        .eq(UserEnterpriseIdentificationDo::getDeleted, NumCode.ZERO.getCode());
                UserEnterpriseIdentificationDo userEnterpriseIdentificationDo = userEnterpriseIdentificationDao.selectOne(wrapper);
                if (null != userEnterpriseIdentificationDo)
                    userService.updateUserName(userEnterpriseIdentificationDo.getEnterpriseAccountName(), req.getClientUserId());
                //查询实名认证信息
                LambdaQueryWrapperX<UserPersonIdentificationDo> queryWrapperY = new LambdaQueryWrapperX<>();
                queryWrapperY.eq(UserPersonIdentificationDo::getUserId, req.getClientUserId())
                        .eq(UserPersonIdentificationDo::getDeleted, NumCode.ZERO.getCode())
                        .eq(UserPersonIdentificationDo::getPersonAuthStatus, NumCode.THREE.getCode());
                UserPersonIdentificationDo userPersonIdentificationDo = userPersonIdentificationDao.selectOne(queryWrapperY);
                //12.30 注释以下代码  现业务逻辑中 若通过了企业认证 则页面优先展示企业认证 不再展示个人认证状态 所以不再需要更新个人认证状态
                //2023-01-12 打开注释
                if (null == userPersonIdentificationDo) {
                    //企业认证通过查询用户认证是否通过,个人认证未通过则允许通过
                    UserPersonIdentificationDo personDo = new UserPersonIdentificationDo()
                            .setAuditRemark(req.getAuditRemark())
                            .setPersonAuthStatus(req.getAuthStatus())
                            /**
                             * 添加个人认证审核时间
                             * 2023/3/28
                             */
                            .setPersonAuthAuditTime(new Date())
                            .setUpdater(SessionContextUtil.getUser().getUserId())
                            .setUpdateTime(new Date());
                    LambdaUpdateWrapper<UserPersonIdentificationDo> personWrapper = new LambdaUpdateWrapper<>();
                    personWrapper.eq(UserPersonIdentificationDo::getUserId, req.getClientUserId()).eq(UserPersonIdentificationDo::getDeleted, NumCode.ZERO.getCode());
                    if (NumCode.ONE.getCode() != userPersonIdentificationDao.update(personDo, personWrapper)) {
                        personDo.setUserId(req.getClientUserId());
                        personDo.setProtal(NumCode.ONE.getCode());
                        personDo.setPersonAuthTime(new Date());
                        userPersonIdentificationDao.insert(personDo);
                    }
                    req.setIdentificationId(QualificationType.REAL_NAME_USER.getCode())
                            .setFlag(NumCode.ONE.getCode());
                    updateCertificateAndInsertRecord(req);
                }
            }

            //添加处理记录
            String processingContent = ProcessingContentEnum.QYRZSHTG.getName();
            if (!req.getAuthStatus().equals(NumCode.THREE.getCode())) {
                processingContent = ProcessingContentEnum.QYRZSHWTG.getName();
            }
            addProcessingRecord2(req.getClientUserId(), processingContent, BusinessTypeEnum.YHGL_TY.getCode(), req.getAuditRemark());
        }
        //刷新用户状态
        updateUserAuthStatus(req.getClientUserId());
        AuditIdentificationResp resp = new AuditIdentificationResp();
        resp.setResult(true);
        return resp;
    }

    @Override
    public GetClientUserIdentificationResp getClientUserIdentifications(GetClientUserIdentificationReq req) {
        LambdaQueryWrapperX<UserPersonIdentificationDo> personWrapper = new LambdaQueryWrapperX<>();
        personWrapper.eq(UserPersonIdentificationDo::getUserId, req.getUserId());
        UserPersonIdentificationDo personIdentificationDo = userPersonIdentificationDao.selectOne(personWrapper);
        //个人认证
        UserPersonIdentificationItem person = new UserPersonIdentificationItem();
        if (null != personIdentificationDo) {
            BeanUtils.copyProperties(personIdentificationDo, person);
            person.setCertificateId(QualificationType.REAL_NAME_USER.getCode());
        }
        //企业认证
        LambdaQueryWrapperX<UserEnterpriseIdentificationDo> enterpriseWrapper = new LambdaQueryWrapperX<>();
        enterpriseWrapper.eq(UserEnterpriseIdentificationDo::getUserId, req.getUserId());
        UserEnterpriseIdentificationDo enterpriseIdentificationDo = userEnterpriseIdentificationDao.selectOne(enterpriseWrapper);
        UserEnterpriseIdentificationItem enterprise = new UserEnterpriseIdentificationItem();
        if (null != enterpriseIdentificationDo) {
            BeanUtils.copyProperties(enterpriseIdentificationDo, enterprise);
            String zone = enterpriseIdentificationDo.getProvince() + "-" +
                    enterpriseIdentificationDo.getCity() + "-" +
                    enterpriseIdentificationDo.getArea();
            enterprise.setZone(zone).setCertificateId(QualificationType.BUSINESS_USER.getCode());
        }
        return new GetClientUserIdentificationResp().setEnterpriseIdentificatio(enterprise).setPersonIdentificationDo(person);
    }

    @Override
    public DashboardUserStatisticsResp getDashboardUserStatistics() {
        //查询用户认证数据
        LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(UserDo::getAuthStatus, NumCode.ONE.getCode());
        Long unauthCount = userDao.selectCount(queryWrapper);
        //待处理的用户标签申请个数
        Long pendingReviewNum = getPendingReviewNum();
        return new DashboardUserStatisticsResp().setUnauthCount(unauthCount).setUnAuditCount(pendingReviewNum);
    }

    @Override
    public List<UserCertificateItem> getCertificateOptions(String userId) {
        return userCertificateOptionsDao.selectCertificateOptionsList(userId);
    }

    @Override
    public void addProcessingRecord(String userId, String name, Integer code) {
        BaseUser baseUser = SessionContextUtil.getUser();
        //添加处理记录
        ProcessingRecordReq processingRecordReq = new ProcessingRecordReq();
        processingRecordReq.setBusinessId(userId);
        processingRecordReq.setBusinessType(code);
        processingRecordReq.setProcessingContent(name);
        processingRecordReq.setProcessingUserId(baseUser.getUserId());
        processingRecordApi.addRecord(processingRecordReq);
    }

    @Override
    public WebEnterpriseIdentificationResp getIdentificationInfo(String userId) {
        WebEnterpriseIdentificationResp resp = new WebEnterpriseIdentificationResp();
        GetPersonIdentificationResp personIdentification = getPersonIdentification(userId);
        if (null != personIdentification) {
            BeanUtils.copyProperties(personIdentification, resp);
        }
        UserEnterpriseIdentificationDo userEnterpriseIdentificationDo = userEnterpriseIdentificationDao.selectOne(UserEnterpriseIdentificationDo::getUserId, userId);
        if (null != userEnterpriseIdentificationDo) {
            //清理个人认证备注，以企业认证备注为主
            resp.setAuditRemark(null);
            BeanUtils.copyProperties(userEnterpriseIdentificationDo, resp);
        }
        return resp;
    }

    private GetPersonIdentificationResp getPersonIdentification(String userId) {
        UserPersonIdentificationDo userPersonIdentificationDo = userPersonIdentificationDao.selectOne(UserPersonIdentificationDo::getUserId, userId);
        if (null != userPersonIdentificationDo) {
            GetPersonIdentificationResp resp = new GetPersonIdentificationResp();
            BeanUtils.copyProperties(userPersonIdentificationDo, resp);
            return resp;
        }
        return null;
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

    public Long getPendingReviewNum() {
        LambdaQueryWrapperX<CertificateOptionsDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(CertificateOptionsDo::getCertificateApplyStatus, NumCode.ONE.getCode())
                .in(CertificateOptionsDo::getCertificateId, QualificationType.ABILITY_SUPPLIER.getCode(), QualificationType.SOLUTION_PROVIDER.getCode());
        return certificateOptionsDao.selectCount(queryWrapper);
    }

    private void addProcessingRecord2(String businessId, String processingContent, Integer businessType, String remark) {
        BaseUser baseUser = SessionContextUtil.getUser();
        //添加处理记录
        ProcessingRecordReq processingRecordReq = new ProcessingRecordReq();
        processingRecordReq.setBusinessId(businessId);
        processingRecordReq.setBusinessType(businessType);
        processingRecordReq.setProcessingContent(processingContent);
        processingRecordReq.setProcessingUserId(baseUser.getUserId());
        processingRecordReq.setRemark(remark);
        processingRecordApi.addRecord(processingRecordReq);
    }

    public void updateCertificateAndInsertRecord(AuditIdentificationReq req) {
        UserCertificateOptionsDo optionsDo = new UserCertificateOptionsDo()
                .setCertificateApplyStatus(req.getAuthStatus())
                .setUpdater(SessionContextUtil.getUser().getUserId());
        MsgTemplateReq msgTemplateReq = new MsgTemplateReq();
        if (NumCode.THREE.getCode() == req.getAuthStatus()) {//审核通过
            req.setAuditRemark("审核通过");
            optionsDo.setApprovalTime(new Date());
            if (NumCode.ONE.getCode() == req.getFlag()) {//个人
                msgTemplateReq.setTempldateCode(MsgCode.REAL_NAME_USER_PASS.getCode());
            } else if (NumCode.TWO.getCode() == req.getFlag()) {//企业
                msgTemplateReq.setTempldateCode(MsgCode.BUSINESS_USER_PASS.getCode());
            }
        } else if (NumCode.TWO.getCode() == req.getAuthStatus()) {//审核不通过
            if (NumCode.ONE.getCode() == req.getFlag()) {//个人
                msgTemplateReq.setTempldateCode(MsgCode.REAL_NAME_USER_NOT_PASS.getCode());
            } else if (NumCode.TWO.getCode() == req.getFlag()) {//企业
                msgTemplateReq.setTempldateCode(MsgCode.BUSINESS_USER_NOT_PASS.getCode());
            }
        }
        //刷新资质状态
        LambdaUpdateWrapper<UserCertificateOptionsDo> updateOptionsWrapper = new LambdaUpdateWrapper<>();
        updateOptionsWrapper.eq(UserCertificateOptionsDo::getUserId, req.getClientUserId())
                .eq(UserCertificateOptionsDo::getCertificateId, req.getIdentificationId());
        if (NumCode.ONE.getCode() != userCertificateOptionsDao.update(optionsDo, updateOptionsWrapper)) {
            optionsDo.setUserId(req.getClientUserId());
            optionsDo.setCertificateId(req.getIdentificationId());
            optionsDo.setApplyTime(new Date());
            optionsDo.setCertificateStatus(0);
            userCertificateOptionsDao.insert(optionsDo);
        }
        //往用户消息详情表插入一条消息通知
        userSysMsgService.addMsgIntoUser(JSON.parseObject(JSONObject.toJSONString(msgTemplateReq), MsgReq.class).setRemark(req.getAuditRemark()).setUserId(req.getClientUserId()));
        //往日志记录identification_audit_record表插入数据
        IdentificationAuditRecordDo identifierAuditRecordDo = new IdentificationAuditRecordDo()
                .setAuditRemark(req.getAuditRemark())
                .setUserId(req.getClientUserId())
                .setAuditStatus(req.getAuthStatus())
                .setIdentificationId(req.getIdentificationId())
                .setReviewer(SessionContextUtil.getUser().getUserName());
        if (NumCode.ONE.getCode() != identificationAuditRecordDao.insert(identifierAuditRecordDo)) {
            throw new BizException(AuthCenterError.Execute_SQL_SAVE);
        }
    }

    public void turnOnUserCertificate(OnOrOffUserCertificateReq req) {
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
            throw new BizException(AuthCenterError.ACCOUNT_USER_ABSENT);
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
                    log.error("insert ticket_qualifications_apply error message is === " + AuthCenterError.Execute_SQL_SAVE);
                    throw new BizException(AuthCenterError.Execute_SQL_SAVE);
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
                log.error("insert user_certificate_options error message is === " + AuthCenterError.Execute_SQL_SAVE);
                throw new BizException(AuthCenterError.Execute_SQL_SAVE);
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
                        log.error("update ticket_qualifications_apply error message is === " + AuthCenterError.Execute_SQL_UPDATE);
                        throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
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
                log.error("update user_certificate_options error message is === " + AuthCenterError.Execute_SQL_UPDATE);
                throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
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
            log.error("insert identification_audit_record error message is === " + AuthCenterError.Execute_SQL_SAVE);
            throw new BizException(AuthCenterError.Execute_SQL_SAVE);
        }
        //往标签操作记录表user_tag_log插入一条数据
        tagLog.setHandleTime(new Date())
                .setUserId(req.getClientUserId())
                .setAdminUserId(SessionContextUtil.getUser().getUserId())
                .setAdminUserName(SessionContextUtil.getUser().getUserName())
                .setRemark(req.getRemark());
        if (NumCode.ONE.getCode() != userTagLogDao.insert(tagLog)) {
            throw new BizException(AuthCenterError.Execute_SQL_SAVE);
        }
        //往用户消息详情表创建一条数据
        MsgTemplateReq msgTemplateReq = new MsgTemplateReq();
        if (String.valueOf(QualificationType.ABILITY_SUPPLIER.getCode()).equals(String.valueOf(req.getCertificateOptionsCode()))) {
            msgTemplateReq.setTempldateCode(MsgCode.ABILITY_SUPPLIER_PASS.getCode());
        } else if (String.valueOf(QualificationType.SOLUTION_PROVIDER.getCode()).equals(String.valueOf(req.getCertificateOptionsCode()))) {
            msgTemplateReq.setTempldateCode(MsgCode.SOLUTION_PROVIDER_PASS.getCode());
        }
        //TODO
        userSysMsgService.addMsgIntoUser(JSON.parseObject(JSONObject.toJSONString(msgTemplateReq), MsgReq.class).setRemark(req.getRemark()).setUserId(req.getClientUserId()));
        //更新用户状态
        updateUserAuthStatus(req.getClientUserId());
    }

    public void turnOffUserCertificate(OnOrOffUserCertificateReq req) {
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
                log.error("insert ticket_qualifications_apply error message is === " + AuthCenterError.Execute_SQL_SAVE);
                throw new BizException(AuthCenterError.Execute_SQL_SAVE);
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
                .setBusinessId(applyDo.getId());
        if (NumCode.ONE.getCode() != certificateOptionsDao.update(optionsDo, optionsUpdate)) {
            log.error("update user_certificate_options error message is === " + AuthCenterError.Execute_SQL_UPDATE);
            throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
        }
        //往审核记录表identification_audit_record表插入数据
        IdentificationAuditRecordDo identifierAuditRecordDo = new IdentificationAuditRecordDo()
                .setIdentificationId(req.getCertificateOptionsCode())
                .setUserId(req.getClientUserId())
                .setAuditStatus(NumCode.THREE.getCode())
                .setAuditRemark(req.getRemark())
                .setReviewer(SessionContextUtil.getUser().getUserName());
        if (NumCode.ONE.getCode() != identificationAuditRecordDao.insert(identifierAuditRecordDo)) {
            log.error("insert identification_audit_record error message is === " + AuthCenterError.Execute_SQL_SAVE);
            throw new BizException(AuthCenterError.Execute_SQL_SAVE);
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
            throw new BizException(AuthCenterError.Execute_SQL_SAVE);
        }
        //往用户消息详情表创建一条数据
        MsgTemplateReq msgTemplateReq = new MsgTemplateReq();
        if (String.valueOf(QualificationType.ABILITY_SUPPLIER.getCode()).equals(String.valueOf(req.getCertificateOptionsCode()))) {
            msgTemplateReq.setTempldateCode(MsgCode.ABILITY_SUPPLIER_NOT_PASS.getCode());
        } else if (String.valueOf(QualificationType.SOLUTION_PROVIDER.getCode()).equals(String.valueOf(req.getCertificateOptionsCode()))) {
            msgTemplateReq.setTempldateCode(MsgCode.SOLUTION_PROVIDER_NOT_PASS.getCode());
        }
        //TODO
        userSysMsgService.addMsgIntoUser(JSON.parseObject(JSONObject.toJSONString(msgTemplateReq), MsgReq.class).setRemark(req.getRemark()).setUserId(req.getClientUserId()));
        //更新用户状态
        updateUserAuthStatus(req.getClientUserId());
    }

    @Override
    public List<UserCertificateDo> getUserCertificate() {
        LambdaQueryWrapperX<UserCertificateDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(UserCertificateDo::getProtal, NumCode.ONE.getCode());
        return userCertificateDao.selectList(queryWrapper);
    }

    @Override
    public List<String> findUsersByCertificateOptions(List<Integer> tags) {
        List<String> userIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tags)) {
            LambdaQueryWrapperX<CertificateOptionsDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(CertificateOptionsDo::getCertificateApplyStatus, 3);//审核通过
            queryWrapper.eq(CertificateOptionsDo::getCertificateStatus, 0);//开启状态
            queryWrapper.eq(CertificateOptionsDo::getDeleted, 0);//未删除
            queryWrapper.in(CertificateOptionsDo::getCertificateId, tags);
            List<CertificateOptionsDo> optionsDo = certificateOptionsDao.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(optionsDo)) {
                optionsDo.forEach(i -> userIds.add(i.getUserId()));
            }
        }
        return userIds;
    }

    @Override
    public Map<String, String> findCertificateNameMap(List<Integer> tags) {
        Map<String, String> certificateNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(tags)) {
            LambdaQueryWrapperX<UserCertificateDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.in(UserCertificateDo::getId, tags);
            List<UserCertificateDo> userCertificateDos = userCertificateDao.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(userCertificateDos)) {
                userCertificateDos.forEach(i -> certificateNameMap.put(i.getId().toString(), i.getCertificateName()));
            }
        }
        return certificateNameMap;
    }

    @Override
    public List<CertificateItem> getCertificateList(List<Integer> plats) {
        LambdaQueryWrapperX<UserCertificateDo> queryWrapper = new LambdaQueryWrapperX<>();
        if (CollectionUtils.isNotEmpty(plats)) {
            queryWrapper.in(UserCertificateDo::getProtal, plats);
        }
        queryWrapper.eq(UserCertificateDo::getDeleted, 0);
        List<UserCertificateDo> userCertificateDos = userCertificateDao.selectList(queryWrapper);
        List<CertificateItem> certificateItems = new ArrayList<>();
        CertificateItem certificateItem0 = new CertificateItem();
        certificateItem0.setId(0L);
        certificateItem0.setCertificateName("全部");
        certificateItems.add(certificateItem0);
        if (CollectionUtils.isNotEmpty(userCertificateDos)) {
            CertificateItem certificateItem;
            for (UserCertificateDo item : userCertificateDos) {
                certificateItem = new CertificateItem();
                certificateItem.setId(item.getId());
                certificateItem.setCertificateName(item.getCertificateName());
                certificateItems.add(certificateItem);
            }
        }
        return certificateItems;
    }

    @Override
    public List<String> getUserCertificateByUserId(String userId) {
        List<String> certificateList = new ArrayList<>();
        if (!Strings.isNullOrEmpty(userId)) {
            LambdaQueryWrapperX<CertificateOptionsDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(CertificateOptionsDo::getCertificateApplyStatus, 3);//审核通过
            queryWrapper.eq(CertificateOptionsDo::getCertificateStatus, 0);//开启状态
            queryWrapper.eq(CertificateOptionsDo::getDeleted, 0);//未删除
            queryWrapper.eq(CertificateOptionsDo::getUserId, userId);
            List<CertificateOptionsDo> optionsDo = certificateOptionsDao.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(optionsDo)) {
                optionsDo.forEach(i -> certificateList.add(i.getCertificateId() + ""));
            }
        }
        return certificateList;
    }

    @Override
    public AddUserCertificateResp addUserCertificate(AddUserCertificateReq req) {
        AddUserCertificateResp resp = new AddUserCertificateResp();
        LambdaQueryWrapperX<UserCertificateDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(UserCertificateDo::getId, req.getCertificateOptionsCode())
                .eq(UserCertificateDo::getDeleted, 0);
        List<UserCertificateDo> userCertificateDos = userCertificateDao.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(userCertificateDos)) {
            LambdaQueryWrapperX<UserCertificateOptionsDo> queryWrapper1 = new LambdaQueryWrapperX<>();
            queryWrapper1.eq(UserCertificateOptionsDo::getCertificateId, req.getCertificateOptionsCode())
                    .eq(UserCertificateOptionsDo::getUserId, req.getClientUserId())
                    .eq(UserCertificateOptionsDo::getDeleted, 0);
            List<UserCertificateOptionsDo> userCertificateOptionsDos = userCertificateOptionsDao.selectList(queryWrapper1);
            if (!CollectionUtils.isNotEmpty(userCertificateOptionsDos)) {
                UserCertificateOptionsDo certificateOptionsDo = new UserCertificateOptionsDo();
                certificateOptionsDo.setCertificateId(req.getCertificateOptionsCode());
                certificateOptionsDo.setUserId(req.getClientUserId());
                certificateOptionsDo.setCertificateApplyStatus(3);
                certificateOptionsDo.setApplyTime(new Date());
                certificateOptionsDo.setApprovalTime(new Date());
                certificateOptionsDo.setCertificateStatus(0);
                certificateOptionsDo.setCreator(req.getClientUserId());
                certificateOptionsDo.setCreateTime(new Date());
                certificateOptionsDo.setDeleted(0);
                certificateOptionsDo.setDeletedTime((new Date()).getTime());
                userCertificateOptionsDao.insert(certificateOptionsDo);
                resp.setId(certificateOptionsDo.getId());
            } else {
                throw new BizException(AuthCenterError.USER_CERTIFICATE_IS_EXIST);
            }
        } else {
            throw new BizException(AuthCenterError.CERTIFICATE_NOT_EXIST);
        }
        return resp;
    }

    @Override
    @Transactional
    public void updateUserCertificate(UpdateUserCertificateReq req) {
        LambdaQueryWrapperX<UserCertificateOptionsDo> queryWrapper1 = new LambdaQueryWrapperX<>();
        queryWrapper1.eq(UserCertificateOptionsDo::getCertificateId, req.getCertificateOptionsCode())
                .eq(UserCertificateOptionsDo::getUserId, req.getClientUserId())
                .eq(UserCertificateOptionsDo::getDeleted, 0);
        List<UserCertificateOptionsDo> userCertificateOptionsDos = userCertificateOptionsDao.selectList(queryWrapper1);
        if (CollectionUtils.isNotEmpty(userCertificateOptionsDos)) {
            UserCertificateOptionsDo userCertificateOptionsDo = userCertificateOptionsDos.get(0);
            userCertificateOptionsDo.setCertificateStatus(req.getCertificateStatus());
            queryWrapper1.eq(UserCertificateOptionsDo::getId, userCertificateOptionsDo.getId());
            userCertificateOptionsDao.update(userCertificateOptionsDo, queryWrapper1);
        } else {
            LambdaQueryWrapperX<UserCertificateDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(UserCertificateDo::getId, req.getCertificateOptionsCode())
                    .eq(UserCertificateDo::getDeleted, 0);
            List<UserCertificateDo> userCertificateDos = userCertificateDao.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(userCertificateDos)) {
                UserCertificateOptionsDo certificateOptionsDo = new UserCertificateOptionsDo();
                certificateOptionsDo.setCertificateId(req.getCertificateOptionsCode());
                certificateOptionsDo.setUserId(req.getClientUserId());
                certificateOptionsDo.setCertificateApplyStatus(3);
                certificateOptionsDo.setApplyTime(new Date());
                certificateOptionsDo.setApprovalTime(new Date());
                certificateOptionsDo.setCertificateStatus(req.getCertificateStatus());
                certificateOptionsDo.setCreator(req.getClientUserId());
                certificateOptionsDo.setCreateTime(new Date());
                certificateOptionsDo.setDeleted(0);
                certificateOptionsDo.setDeletedTime((new Date()).getTime());
                userCertificateOptionsDao.insert(certificateOptionsDo);
            } else {
                throw new BizException(AuthCenterError.CERTIFICATE_NOT_EXIST);
            }
        }
    }

    @Override
    public CertificateOptions queryCertificateOptionsById(QueryCertificateOptionsByIdReq req) {
        CertificateOptions certificateOptions = null;
        LambdaQueryWrapperX<CertificateOptionsDo> optionsWrapper = new LambdaQueryWrapperX<>();
        optionsWrapper.eq(CertificateOptionsDo::getUserId, req.getUserId())
                .eq(CertificateOptionsDo::getCertificateId, req.getCertificateId())
                .eq(CertificateOptionsDo::getDeleted, NumCode.ZERO.getCode());
        List<CertificateOptionsDo> optionsDos = certificateOptionsDao.selectList(optionsWrapper);
        if (CollectionUtils.isNotEmpty(optionsDos)) {
            certificateOptions = new CertificateOptions();
            BeanUtils.copyProperties(optionsDos.get(0), certificateOptions);
        }
        return certificateOptions;
    }

    @Override
    public Integer insertCertificateOptions(CertificateOptions req) {
        CertificateOptionsDo certificateOptionsDo = new CertificateOptionsDo();
        BeanUtils.copyProperties(req, certificateOptionsDo);
        return certificateOptionsDao.insert(certificateOptionsDo);
    }

    @Override
    public Integer updateCertificateOptions(CertificateOptions req) {
        CertificateOptionsDo certificateOptionsDo = new CertificateOptionsDo();
        BeanUtils.copyProperties(req, certificateOptionsDo);
        LambdaQueryWrapperX<CertificateOptionsDo> optionsWrapper = new LambdaQueryWrapperX<>();
        optionsWrapper.eq(CertificateOptionsDo::getUserId, req.getUserId())
                .eq(CertificateOptionsDo::getCertificateId, req.getCertificateId())
                .eq(CertificateOptionsDo::getDeleted, NumCode.ZERO.getCode());
        return certificateOptionsDao.update(certificateOptionsDo, optionsWrapper);
    }

    @Override
    public Integer insertIdentificationAuditRecord(InsertIdentificationAuditRecordReq req) {
        IdentificationAuditRecordDo identifierAuditRecordDo = new IdentificationAuditRecordDo();
        BeanUtils.copyProperties(req, identifierAuditRecordDo);
        return identificationAuditRecordDao.insert(identifierAuditRecordDo);
    }

    @Override
    public Integer updateCertificateOptionsByBusinessId(CertificateOptions req) {
        CertificateOptionsDo certificateOptionsDo = new CertificateOptionsDo();
        BeanUtils.copyProperties(req, certificateOptionsDo);
        LambdaUpdateWrapper<CertificateOptionsDo> updateCertificateWrapper = new LambdaUpdateWrapper<>();
        updateCertificateWrapper.eq(CertificateOptionsDo::getBusinessId, req.getBusinessId())
                .eq(CertificateOptionsDo::getUserId, req.getUserId());
        return certificateOptionsDao.update(certificateOptionsDo, updateCertificateWrapper);
    }

    @Override
    @Transactional()
    public void disposeQualificationsApply(DisposeQualificationApplyReq req) {
        //处理工单表状态 ticket_qualifications_apply
        QualificationsApplyDo applyDo = new QualificationsApplyDo();
        BeanUtils.copyProperties(req, applyDo);
        applyDo.setProcessingState(NumCode.ONE.getCode());
        if (NumCode.ONE.getCode() != qualificationsApplyDao.updateById(applyDo)) {
            log.error("disposeQualificationsApply error message is === " + AuthCenterError.Execute_SQL_UPDATE);
            throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
        }
        //处理用户账号资质信息表状态 ticket_qualifications_apply
        CertificateOptionsDo certOptionsDo = new CertificateOptionsDo()
                .setUpdateTime(new Date())
                .setBusinessId(req.getId())
                .setUpdateTime(new Date())
                .setCertificateStatus(NumCode.ZERO.getCode())
                .setCertificateApplyStatus(req.getCertificateApplyStatus());
        IdentificationAuditRecordDo identifierAuditRecordDo = new IdentificationAuditRecordDo()
                .setUpdateTime(new Date())
                .setCreateTime(new Date())
                .setAuditRemark(applyDo.getRemark())
                .setUserId(applyDo.getUserId())
                .setIdentificationId(req.getQualificationName())
                .setReviewer(SessionContextUtil.getUser().getUserName());
        MsgTemplateReq msgTemplateReq = new MsgTemplateReq();
        if (NumCode.TWO.getCode() == req.getCertificateApplyStatus()) {//审核不通过
            if (String.valueOf(QualificationType.ABILITY_SUPPLIER.getCode()).equals(String.valueOf(req.getQualificationName()))) {
                msgTemplateReq.setTempldateCode(MsgCode.ABILITY_SUPPLIER_NOT_PASS.getCode());
            } else if (String.valueOf(QualificationType.SOLUTION_PROVIDER.getCode()).equals(String.valueOf(req.getQualificationName()))) {
                msgTemplateReq.setTempldateCode(MsgCode.SOLUTION_PROVIDER_NOT_PASS.getCode());
            }
            identifierAuditRecordDo.setAuditStatus(NumCode.THREE.getCode());
        } else if (NumCode.THREE.getCode() == req.getCertificateApplyStatus()) {//审核通过
            certOptionsDo.setApprovalTime(new Date());
            if (String.valueOf(QualificationType.ABILITY_SUPPLIER.getCode()).equals(String.valueOf(req.getQualificationName()))) {
                msgTemplateReq.setTempldateCode(MsgCode.ABILITY_SUPPLIER_PASS.getCode());
            } else if (String.valueOf(QualificationType.SOLUTION_PROVIDER.getCode()).equals(String.valueOf(req.getQualificationName()))) {
                msgTemplateReq.setTempldateCode(MsgCode.SOLUTION_PROVIDER_PASS.getCode());
            }
            identifierAuditRecordDo.setAuditStatus(NumCode.TWO.getCode());
        } else {
            throw new BizException(AuthCenterError.PARAMETER_BAD);
        }
        //修改user_certificate_options资质申请状态
        LambdaUpdateWrapper<CertificateOptionsDo> updateCertificateWrapper = new LambdaUpdateWrapper<>();
        updateCertificateWrapper.eq(CertificateOptionsDo::getBusinessId, req.getId())
                .eq(CertificateOptionsDo::getUserId, req.getUserId());
        if (NumCode.ONE.getCode() != certificateOptionsDao.update(certOptionsDo, updateCertificateWrapper)) {
            log.error("disposeCertificateOptions error message is === " + AuthCenterError.Execute_SQL_UPDATE);
            throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
        }
        //往用户消息详情表插入一条消息通知
        userSysMsgService.addMsgIntoUser(JSON.parseObject(JSONObject.toJSONString(msgTemplateReq), MsgReq.class).setRemark(req.getRemark()).setUserId(req.getUserId()));
        //identification_audit_record表插入数据
        if (NumCode.ONE.getCode() != identificationAuditRecordDao.insert(identifierAuditRecordDo)) {
            log.error("submitIdentificationAuditRecord error message is === " + AuthCenterError.Execute_SQL_SAVE);
            throw new BizException(AuthCenterError.Execute_SQL_SAVE);
        }
        //刷新用户状态
        updateUserAuthStatus(applyDo.getUserId());

        BaseUser baseUser = SessionContextUtil.getUser();
        String processingContent = ProcessingContentEnum.TYSQ.getName();
        if (!req.getCertificateApplyStatus().equals(NumCode.THREE.getCode())) {
            processingContent = ProcessingContentEnum.BHSQ.getName();
        }
        //添加处理记录
        ProcessingRecordReq processingRecordReq = new ProcessingRecordReq();
        processingRecordReq.setBusinessId(req.getId().toString());
        processingRecordReq.setBusinessType(BusinessTypeEnum.ZZSQGD.getCode());
        processingRecordReq.setProcessingContent(processingContent);
        processingRecordReq.setProcessingUserId(baseUser.getUserId());
        processingRecordReq.setRemark(req.getRemark());
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
        qualificationsApplyDos.forEach(item -> {
            ids.add(String.valueOf(item.getId()));
        });
        if (com.alibaba.nacos.common.utils.CollectionUtils.isNotEmpty(ids)) {
            BusinessIdsReq businessIdsReq = new BusinessIdsReq();
            businessIdsReq.setBusinessIds(ids);
            businessIdsReq.setBusinessType(4);
            List<ProcessingRecordResp> processingRecordRespList = processingRecordApi.findProcessingRecordListByIds(businessIdsReq);

            for (QualificationsApplyDo qualificationsApplyDo : qualificationsApplyDos) {
                for (ProcessingRecordResp processingRecordResp : processingRecordRespList) {
                    if (qualificationsApplyDo.getId().toString().equals(processingRecordResp.getBusinessId())) {
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
            log.error("getQualificationsApplyInfoById error message is === " + AuthCenterError.Execute_SQL_QUERY);
            throw new BizException(AuthCenterError.Execute_SQL_QUERY);
        }
        return resp;
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
}
