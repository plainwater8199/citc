package com.citc.nce.authcenter.admin.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.adminUser.vo.req.AuditIdentificationReq;
import com.citc.nce.auth.adminUser.vo.req.ReviewCspReq;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.identification.vo.req.IdentificationPlatformPermissionReq;
import com.citc.nce.auth.usermessage.UserMsgApi;
import com.citc.nce.auth.usermessage.vo.req.MsgReq;
import com.citc.nce.authcenter.admin.dao.AdminMenuDao;
import com.citc.nce.authcenter.admin.dao.AdminRoleMenuDao;
import com.citc.nce.authcenter.admin.dao.AdminUserDao;
import com.citc.nce.authcenter.admin.dao.AdminUserRoleDao;
import com.citc.nce.authcenter.admin.entity.AdminMenuDo;
import com.citc.nce.authcenter.admin.entity.AdminRoleMenuDo;
import com.citc.nce.authcenter.admin.entity.AdminUserDo;
import com.citc.nce.authcenter.admin.entity.AdminUserRoleDo;
import com.citc.nce.authcenter.admin.service.AdminUserService;
import com.citc.nce.authcenter.csp.multitenant.dao.CspCustomerMapper;
import com.citc.nce.authcenter.csp.multitenant.entity.CspCustomer;
import com.citc.nce.authcenter.identification.dao.*;
import com.citc.nce.authcenter.identification.entity.*;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.misc.constant.*;
import com.citc.nce.misc.msg.req.MsgTemplateReq;
import com.citc.nce.misc.record.ProcessingRecordApi;
import com.citc.nce.misc.record.req.ProcessingRecordReq;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {
    @Resource
    private AdminUserRoleDao adminUserRoleDao;
    @Resource
    private AdminRoleMenuDao adminRoleMenuDao;

    @Autowired
    private AdminMenuDao adminMenuDao;

    @Autowired
    private AdminUserDao adminUserDao;

    @Autowired
    private UserPersonIdentificationDao userPersonIdentificationDao;

    @Autowired
    private UserEnterpriseIdentificationDao userEnterpriseIdentificationDao;

    @Autowired
    private UserCertificateOptionsDao userCertificateOptionsDao;

    @Autowired
    private ProcessingRecordApi processingRecordApi;

    @Autowired
    private IdentificationAuditRecordDao identificationAuditRecordDao;

    @Autowired
    private UserMsgApi userMsgApi;

    @Autowired
    private CspCustomerMapper customerMapper;

    @Autowired
    private UserPlatformPermissionsDao userPlatformPermissionsDao;

    @Autowired
    private ApprovalLogDao approvalLogDao;

    @Override
    public List<String> findAdminRoleList(String adminUserId) {
        List<String> roleIdList = new ArrayList<>();
        LambdaQueryWrapper<AdminUserRoleDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminUserRoleDo::getUserId, adminUserId);
        queryWrapper.eq(AdminUserRoleDo::getDeleted, 0);
        List<AdminUserRoleDo> adminUserRoleDos = adminUserRoleDao.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(adminUserRoleDos)) {
            adminUserRoleDos.forEach(i -> roleIdList.add(i.getRoleId()));
        }
        return roleIdList;
    }

    @Override
    @Transactional
    public List<AdminMenuDo> findAuthUrlListByRoleIds(List<String> roleIdList) {
        List<AdminMenuDo> adminMenuDos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roleIdList)) {
            //获取权限地址code
            List<String> menuCodeList = queryMenuCodeList(roleIdList);
            if (!CollectionUtils.isEmpty(menuCodeList)) {
                //获取具体的的地址
                adminMenuDos = queryMenuInfoListByCodes(menuCodeList);
            }
        }
        return adminMenuDos;
    }

    @Override
    public AdminUserDo findByPhone(String phone) {
        if (!Strings.isNullOrEmpty(phone)) {
            LambdaQueryWrapper<AdminUserDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AdminUserDo::getDeleted, 0);
            queryWrapper.eq(AdminUserDo::getPhone, phone);
            List<AdminUserDo> adminUserDos = adminUserDao.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(adminUserDos)) {
                return adminUserDos.get(0);
            }
        }
        return null;
    }

    /**
     * 管理端  审核用户认证
     *
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditIdentification(AuditIdentificationReq req) {
        //审核用户私人
        if (NumCode.ONE.getCode() == req.getFlag()) {
            if (!String.valueOf(QualificationType.REAL_NAME_USER.getCode()).equals(String.valueOf(req.getIdentificationId()))) {
                throw new BizException(AuthError.AUDIT_AND_QUALIFICATION_TRANSFER);
            }
            UserPersonIdentificationDo personDo = new UserPersonIdentificationDo()
                    .setAuditRemark(req.getAuditRemark())
                    .setPersonAuthStatus(req.getAuthStatus())
                    .setPersonAuthTime(new Date())
                    .setUpdater(SessionContextUtil.getUser().getUserId())
                    .setUpdateTime(new Date());
            LambdaUpdateWrapper<UserPersonIdentificationDo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserPersonIdentificationDo::getUserId, req.getClientUserId());
            if (NumCode.ONE.getCode() != userPersonIdentificationDao.update(personDo, updateWrapper)) {
                throw new BizException(AuthError.Execute_SQL_UPDATE);
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
                throw new BizException(AuthError.AUDIT_AND_QUALIFICATION_TRANSFER);
            }
            UserEnterpriseIdentificationDo enterpriseDo = new UserEnterpriseIdentificationDo()
                    .setAuditRemark(req.getAuditRemark())
                    .setEnterpriseAuthStatus(req.getAuthStatus())
                    .setEnterpriseAuthAuditTime(new Date())
                    .setUpdater(SessionContextUtil.getUser().getUserId())
                    .setUpdateTime(new Date());
            LambdaUpdateWrapper<UserEnterpriseIdentificationDo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserEnterpriseIdentificationDo::getUserId, req.getClientUserId());
            if (NumCode.ONE.getCode() != userEnterpriseIdentificationDao.update(enterpriseDo, updateWrapper)) {
                throw new BizException(AuthError.Execute_SQL_UPDATE);
            }
            updateCertificateAndInsertRecord(req);
            if (NumCode.THREE.getCode() == req.getAuthStatus()) {//企业认证通过查询个人认证是否通过
                //审核通过之后将企业名称刷到user表用户名
                LambdaQueryWrapperX<UserEnterpriseIdentificationDo> wrapper = new LambdaQueryWrapperX<>();
                wrapper.eq(UserEnterpriseIdentificationDo::getUserId, req.getClientUserId())
                        .eq(UserEnterpriseIdentificationDo::getDeleted, NumCode.ZERO.getCode());
                UserEnterpriseIdentificationDo userEnterpriseIdentificationDo = userEnterpriseIdentificationDao.selectOne(wrapper);
                if (null != userEnterpriseIdentificationDo) {
                    if (customerMapper.exists(
                            Wrappers.<CspCustomer>lambdaQuery()
                                    .eq(CspCustomer::getCustomerId, req.getClientUserId())
                                    .eq(CspCustomer::getName, userEnterpriseIdentificationDo.getEnterpriseAccountName())))
                        throw new BizException(AuthError.BUSINESS_USER_ACCOUNT_REPEAT);
                    customerMapper.update(new CspCustomer()
                                    .setName(userEnterpriseIdentificationDo.getEnterpriseAccountName()),
                            Wrappers.<CspCustomer>lambdaQuery()
                                    .eq(CspCustomer::getCustomerId, req.getClientUserId())
                    );
                }
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
                            .setPersonAuthAuditTime(new Date())
                            .setUpdater(SessionContextUtil.getUser().getUserId())
                            .setUpdateTime(new Date());
                    LambdaUpdateWrapper<UserPersonIdentificationDo> personWrapper = new LambdaUpdateWrapper<>();
                    personWrapper.eq(UserPersonIdentificationDo::getUserId, req.getClientUserId()).eq(UserPersonIdentificationDo::getDeleted, NumCode.ZERO.getCode());
                    if (NumCode.ONE.getCode() != userPersonIdentificationDao.update(personDo, personWrapper)) {
                        personDo.setUserId(req.getClientUserId());
                        personDo.setProtal(NumCode.ONE.getCode());
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
    }


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
    public void superAdminMenuSyn(String adminRoleId) {
        if (!Strings.isNullOrEmpty(adminRoleId)) {
            //1、查询所有的可用菜单
            List<String> allMenus = new ArrayList<>();
            List<AdminMenuDo> adminMenuDos = adminMenuDao.selectList();
            adminMenuDos.forEach(i -> allMenus.add(i.getMenuCode().trim()));
            //2、查询所有超级管理员的菜单
            LambdaQueryWrapper<AdminRoleMenuDo> adminRoleMenuDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
            adminRoleMenuDoLambdaQueryWrapper.eq(AdminRoleMenuDo::getRoleId, adminRoleId);
            List<String> adminMenus = new ArrayList<>();
            List<AdminRoleMenuDo> adminRoleMenuDos = adminRoleMenuDao.selectList(adminRoleMenuDoLambdaQueryWrapper);
            adminRoleMenuDos.forEach(i -> adminMenus.add(i.getMenuCode().trim()));
            List<AdminRoleMenuDo> adminRoleMenuDoList = new ArrayList<>();
            for (String item : allMenus) {
                if (!adminMenus.contains(item)) {
                    AdminRoleMenuDo adminRoleMenuDo = new AdminRoleMenuDo();
                    adminRoleMenuDo.setMenuCode(item);
                    adminRoleMenuDo.setRoleId(adminRoleId);
                    adminRoleMenuDo.setCreator("system");
                    adminRoleMenuDo.setCreateTime(new Date());
                    adminRoleMenuDo.setUpdater("system");
                    adminRoleMenuDo.setUpdateTime(new Date());
                    adminRoleMenuDo.setDeleted(0);
                    adminRoleMenuDo.setDeletedTime(0L);
                    adminRoleMenuDoList.add(adminRoleMenuDo);
                }
            }
            if (!CollectionUtils.isEmpty(adminRoleMenuDoList)) {
                adminRoleMenuDao.insertBatch(adminRoleMenuDoList);
            }
        }

    }

    @Override
    public void reviewPlatformForCsp(ReviewCspReq req) {
        UserPlatformPermissionsDo permission = new UserPlatformPermissionsDo();
        // CSP 新增客户是默认通过审核的
        permission.setApprovalStatus(NumCode.THREE.getCode());
        // 用户状态则是根据传值确定
        permission.setUserStatus(req.getApprovalStatus());
        LambdaUpdateWrapper<UserPlatformPermissionsDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserPlatformPermissionsDo::getUserId, req.getUpdateTargetUserId())
                .eq(UserPlatformPermissionsDo::getProtal, req.getProtal());
        if (NumCode.ONE.getCode() != userPlatformPermissionsDao.update(permission, updateWrapper))
            throw new BizException(AuthError.Execute_SQL_SAVE);
        ApprovalLogDo log = new ApprovalLogDo()
                .setAdminUserId(SessionContextUtil.getUser().getUserId())
                .setAdminUserName(SessionContextUtil.getUser().getUserName())
                .setApprovalLogId(req.getApprovalLogId())
                .setHandleTime(new Date())
                .setRemark(req.getRemark());
        if (NumCode.ONE.getCode() != approvalLogDao.insert(log))
            throw new BizException(AuthError.Execute_SQL_SAVE);
    }

    private List<AdminMenuDo> queryMenuInfoListByCodes(List<String> menuCodeList) {
        LambdaQueryWrapper<AdminMenuDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminMenuDo::getDeleted, 0);
        queryWrapper.in(AdminMenuDo::getMenuCode, menuCodeList);
        return adminMenuDao.selectList(queryWrapper);
    }

    private List<String> queryMenuCodeList(List<String> roleIdList) {
        List<String> menuCodeList = new ArrayList<>();
        LambdaQueryWrapper<AdminRoleMenuDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminRoleMenuDo::getDeleted, 0);
        queryWrapper.in(AdminRoleMenuDo::getRoleId, roleIdList);
        List<AdminRoleMenuDo> adminRoleMenuDos = adminRoleMenuDao.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(adminRoleMenuDos)) {
            adminRoleMenuDos.forEach(i -> menuCodeList.add(i.getMenuCode()));
        }
        return menuCodeList;
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
            optionsDo.setCertificateStatus(req.getAuthStatus());
            userCertificateOptionsDao.insert(optionsDo);
        }
        //往用户消息详情表插入一条消息通知
        userMsgApi.addMsgIntoUser(JSON.parseObject(JSONObject.toJSONString(msgTemplateReq), MsgReq.class).setRemark(req.getAuditRemark()).setUserId(req.getClientUserId()));
        //往日志记录identification_audit_record表插入数据
        IdentificationAuditRecordDo identifierAuditRecordDo = new IdentificationAuditRecordDo()
                .setAuditRemark(req.getAuditRemark())
                .setUserId(req.getClientUserId())
                .setAuditStatus(req.getAuthStatus())
                .setIdentificationId(req.getIdentificationId())
                .setReviewer(SessionContextUtil.getUser().getUserName());
        if (NumCode.ONE.getCode() != identificationAuditRecordDao.insert(identifierAuditRecordDo)) {
            throw new BizException(AuthError.Execute_SQL_SAVE);
        }
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

    private String createPublicKey() {
        try {
            // 加密算法
            String algorithm = "RSA";
            //  创建密钥对生成器对象
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            // 生成密钥对
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            // 生成私钥
//            PrivateKey privateKey = keyPair.getPrivate();
            // 生成公钥
            PublicKey publicKey = keyPair.getPublic();
            // 获取私钥字节数组
//            byte[] privateKeyEncoded = privateKey.getEncoded();
            // 获取公钥字节数组
            byte[] publicKeyEncoded = publicKey.getEncoded();
            // 对公私钥进行base64编码
//            String privateKeyString = cn.hutool.core.codec.Base64.encode(privateKeyEncoded);
            return Base64.encode(publicKeyEncoded);
        } catch (Exception e) {
            log.error("生成公钥失败", e);
            return null;
        }
    }
}
