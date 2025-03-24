package com.citc.nce.auth.csp.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.adminUser.dao.AdminUserDao;
import com.citc.nce.auth.adminUser.entity.AdminUserDo;
import com.citc.nce.auth.adminUser.service.AdminUserService;
import com.citc.nce.auth.adminUser.vo.req.AuditIdentificationReq;
import com.citc.nce.auth.adminUser.vo.req.ReviewCspReq;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.csp.csp.dao.CspDao;
import com.citc.nce.auth.csp.csp.entity.CspDo;
import com.citc.nce.auth.csp.customer.dao.CspCustomerDao;
import com.citc.nce.auth.csp.customer.entity.CspCustomerDo;
import com.citc.nce.auth.csp.customer.service.CspCustomerService;
import com.citc.nce.auth.csp.customer.vo.*;
import com.citc.nce.auth.identification.service.UserEnterpriseIdentificationService;
import com.citc.nce.auth.identification.vo.req.EnterpriseIdentificationReq;
import com.citc.nce.auth.identification.vo.req.IdentificationPlatformPermissionReq;
import com.citc.nce.auth.identification.vo.resp.WebEnterpriseIdentificationResp;
import com.citc.nce.auth.user.service.UserService;
import com.citc.nce.auth.user.vo.resp.UserInfo;
import com.citc.nce.authcenter.auth.AuthApi;
import com.citc.nce.authcenter.auth.vo.req.CheckUserInfoIsUniqueReq;
import com.citc.nce.authcenter.auth.vo.req.DyzUserReq;
import com.citc.nce.authcenter.auth.vo.req.RegisterReq;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.misc.constant.QualificationType;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * <p>csp-客户管理</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:51
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CspCspCustomerServiceImpl implements CspCustomerService {
    @Autowired
    private CspCustomerDao cspCustomerDao;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private UserEnterpriseIdentificationService userEnterpriseIdentificationService;
    @Autowired
    private AuthApi authApi;
    @Autowired
    private AdminUserDao adminUserDao;
    @Autowired
    private CspDao cspDao;
    @Autowired
    private CspCustomerApi customerApi;


    private static final Integer CSP_PORTAL = 3;
    private static final String CSP_CRATE = "CSP CRATE CUSTOMER";
    private static final String CSP_ACTIVE = "CSP ACTIVE CUSTOMER";
    private static final String CSP_FORBIDDEN = "CSP FORBIDDEN CUSTOMER";

    @Override
    public PageResult<CustomerResp> queryList(CustomerReq queryReq) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(CustomerSaveReq req) {
        String userId = req.getUserId();
        if (StringUtils.isEmpty(userId)) {
            userId = SessionContextUtil.getUser().getUserId();
            req.setUserId(userId);
        }
        // 创建账号
        CheckUserInfoIsUniqueReq checkUserInfoIsUniqueReq = new CheckUserInfoIsUniqueReq();
        checkUserInfoIsUniqueReq.setName(req.getEnterpriseAccountName());
        checkUserInfoIsUniqueReq.setPhone(req.getPhone());
        authApi.checkUserInfoIsUnique(checkUserInfoIsUniqueReq);
        RegisterReq registerReq = new RegisterReq();
        BeanUtils.copyProperties(req, registerReq);
        registerReq.setName(req.getEnterpriseAccountName());
        String register = userService.saveForRegister(registerReq).getUserId();
//        // 为新创建的账号写入CSP关联关系
//        AccountRelationshipDo relationshipDo = new AccountRelationshipDo();
//        relationshipDo.setUserId(register);
//        relationshipDo.setCspUserId(userId);
//        accountRelationshipDao.insert(relationshipDo);

        // 创建关联企业
        EnterpriseIdentificationReq enterpriseIdentificationReq = new EnterpriseIdentificationReq();
        BeanUtils.copyProperties(req, enterpriseIdentificationReq);
        enterpriseIdentificationReq.setUserId(register);
        // 首先验证企业名称是否唯一
        Boolean isUniuqe = userEnterpriseIdentificationService.checkEnterpriseAccountNameUnique(enterpriseIdentificationReq.getEnterpriseAccountName());
        if (isUniuqe) {
            userEnterpriseIdentificationService.enterpriseIdentificationApply(enterpriseIdentificationReq);
        } else {
            throw new BizException(AuthError.BUSINESS_USER_ACCOUNT_REPEAT);
        }
        // 保存后默认通过企业资质审核
        AuditIdentificationReq auditIdentificationReq = new AuditIdentificationReq();
        auditIdentificationReq.setIdentificationId(QualificationType.BUSINESS_USER.getCode());
        // 企业
        auditIdentificationReq.setFlag(2);
        // 认证通过
        auditIdentificationReq.setAuthStatus(3);
        auditIdentificationReq.setClientUserId(register);
        adminUserService.auditIdentification(auditIdentificationReq);

        // 为了数据统计，存数据到客户表
//        WebEnterpriseIdentificationResp identificationByUserId = userEnterpriseIdentificationService.getEnterpriseIdentificationByUserId(register);
        CspCustomerDo cspCustomerDo = new CspCustomerDo();
        BeanUtils.copyProperties(req, cspCustomerDo);
        cspCustomerDo.setCspId(userId);
        cspCustomerDo.setCustomerId(register);
        cspCustomerDao.insert(cspCustomerDo);

        // 用户申请使用权限
        IdentificationPlatformPermissionReq identificationPlatformPermissionReq = new IdentificationPlatformPermissionReq();
        identificationPlatformPermissionReq.setUserId(register);
        identificationPlatformPermissionReq.setProtal(req.getProtal());
        userEnterpriseIdentificationService.applyPlatformPermission(identificationPlatformPermissionReq);
        ReviewCspReq reviewReq = new ReviewCspReq();
        reviewReq.setUserId(register);
        reviewReq.setUpdateTargetUserId(register);
        reviewReq.setProtal(CSP_PORTAL);
        // 用户审核默认通过
        reviewReq.setApprovalStatus(1);
        reviewReq.setApprovalLogId(CSP_ACTIVE);
        reviewReq.setRemark(CSP_ACTIVE);
        adminUserService.reviewPlatformForCsp(reviewReq);
        AdminUserDo userPhone = adminUserDao.selectOne(AdminUserDo::getPhone, req.getPhone());
        if (Objects.isNull(userPhone)) {
            DyzUserReq phoneReq = new DyzUserReq();
            phoneReq.setUserName(register).setPhone(req.getPhone()).setEmail(req.getMail()).setIsAdmin(false);
            authApi.addDyzUser(phoneReq);
        }
        return 0;
    }

    @Override
    public CustomerGetDetailResp getDetailByUserId(String userId) {
        CustomerGetDetailResp res = new CustomerGetDetailResp();
        WebEnterpriseIdentificationResp identificationInfo = userEnterpriseIdentificationService.getIdentificationInfo(userId);
        BeanUtils.copyProperties(identificationInfo, res);
        UserInfo info = userService.getUserBaseInfoByUserId(userId);
        res.setMail(info.getMail());
        res.setPhone(info.getPhone());
        //用户权限
        LambdaQueryWrapperX<CspCustomerDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(CspCustomerDo::getCustomerId, userId);
        CspCustomerDo cspCustomerDo = cspCustomerDao.selectOne(queryWrapperX);
        res.setPermissions(cspCustomerDo.getPermissions());
        if (StringUtils.isEmpty(cspCustomerDo.getPermissions())) {
            res.setPermissions("1,2");
        }
        return res;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCspActive(CustomerActiveUpdateReq req) {
        String userId = SessionContextUtil.getUser().getUserId();

        UpdateWrapper<CspCustomerDo> update = new UpdateWrapper<>();
        update.eq("enterprise_id", req.getEnterpriseId())
                .eq("csp_user_id", userId)
                .eq("deleted", 0)
                .set("csp_active", req.getCspActive());
        cspCustomerDao.update(null, update);

        return 0;
    }

    @Override
    public List<CustomerProvinceResp> queryUserProvince() {
        return null;
    }


    @Override
    public String queryCspId(String userId) {
        CspDo cspDo = cspDao.selectOne(Wrappers.<CspDo>query().eq("user_id", userId));
        if (cspDo != null)
            return cspDo.getCspId();
        List<UserInfoVo> customers = customerApi.getByCustomerIds(Collections.singletonList(userId));
        if (CollectionUtils.isNotEmpty(customers))
            return customers.get(0).getCspId();
        return null;
    }
}
