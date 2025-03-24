package com.citc.nce.authcenter.csp.multitenant.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.adminUser.vo.req.AuditIdentificationReq;
import com.citc.nce.auth.adminUser.vo.req.ReviewCspReq;
import com.citc.nce.auth.identification.vo.req.EnterpriseIdentificationReq;
import com.citc.nce.auth.identification.vo.req.IdentificationPlatformPermissionReq;
import com.citc.nce.auth.identification.vo.req.PersonIdentificationReq;
import com.citc.nce.auth.meal.CspMealContractApi;
import com.citc.nce.auth.postpay.CustomerPostpayConfigApi;
import com.citc.nce.auth.prepayment.PrepaymentApi;
import com.citc.nce.authcenter.admin.service.AdminUserService;
import com.citc.nce.authcenter.auth.vo.UserInfo;
import com.citc.nce.authcenter.auth.vo.req.*;
import com.citc.nce.authcenter.auth.vo.resp.ActivateEmailResp;
import com.citc.nce.authcenter.auth.vo.resp.LoginResp;
import com.citc.nce.authcenter.auth.vo.resp.ModifyUserPhoneOrEmailResp;
import com.citc.nce.authcenter.auth.vo.resp.QueryUserInfoDetailResp;
import com.citc.nce.authcenter.auth.vo.resp.SetUseImgOrNameResp;
import com.citc.nce.authcenter.captch.service.CaptchaService;
import com.citc.nce.authcenter.captcha.vo.req.CaptchaCheckReq;
import com.citc.nce.authcenter.captcha.vo.resp.CaptchaCheckResp;
import com.citc.nce.authcenter.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.authcenter.configure.ThirdLoginConfigure;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.constant.CaptchaType;
import com.citc.nce.authcenter.csp.multitenant.dao.*;
import com.citc.nce.authcenter.csp.multitenant.entity.*;
import com.citc.nce.authcenter.csp.multitenant.service.CspCreateTableService;
import com.citc.nce.authcenter.csp.multitenant.service.CspCustomerService;
import com.citc.nce.authcenter.csp.multitenant.service.CspService;
import com.citc.nce.authcenter.csp.multitenant.utils.CspUtils;
import com.citc.nce.authcenter.csp.vo.CspAgentLoginReq;
import com.citc.nce.authcenter.csp.vo.CustomerAddReq;
import com.citc.nce.authcenter.csp.vo.CustomerDetailResp;
import com.citc.nce.authcenter.csp.vo.CustomerProvinceResp;
import com.citc.nce.authcenter.csp.vo.CustomerSearchReq;
import com.citc.nce.authcenter.csp.vo.CustomerSearchResp;
import com.citc.nce.authcenter.csp.vo.CustomerUpdateReq;
import com.citc.nce.authcenter.csp.vo.ReduceBalanceResp;
import com.citc.nce.authcenter.csp.vo.UserEnterpriseInfoVo;
import com.citc.nce.authcenter.csp.vo.UserInfoForDropdownVo;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.authcenter.csp.vo.resp.CspInfoResp;
import com.citc.nce.authcenter.identification.dao.CertificateOptionsDao;
import com.citc.nce.authcenter.identification.entity.ApprovalLogDo;
import com.citc.nce.authcenter.identification.entity.CertificateOptionsDo;
import com.citc.nce.authcenter.identification.entity.UserEnterpriseIdentificationDo;
import com.citc.nce.authcenter.identification.entity.UserPlatformPermissionsDo;
import com.citc.nce.authcenter.identification.service.IdentificationService;
import com.citc.nce.authcenter.identification.service.UserEnterPriseIdentificationService;
import com.citc.nce.authcenter.identification.service.UserPersonIdentificationService;
import com.citc.nce.authcenter.permission.enums.Permission;
import com.citc.nce.authcenter.tenantdata.user.dao.Csp1Dao;
import com.citc.nce.authcenter.tenantdata.user.dao.CspCustomer1Dao;
import com.citc.nce.authcenter.tenantdata.user.entity.CspCustomerDo;
import com.citc.nce.authcenter.tenantdata.user.entity.CspDo;
import com.citc.nce.authcenter.user.dao.UserMapper;
import com.citc.nce.authcenter.user.entity.UserDo;
import com.citc.nce.authcenter.user.service.UserService;
import com.citc.nce.authcenter.utils.AuthUtils;
import com.citc.nce.common.core.enums.CustomerPayType;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.common.util.DateUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.web.utils.UserTokenUtil;
import com.citc.nce.filecenter.platform.vo.UpReceiveData;
import com.citc.nce.misc.constant.NumCode;
import com.citc.nce.misc.constant.QualificationType;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.readingLetter.ReadingLetterParseRecordApi;
import com.citc.nce.tenant.MsgRecordApi;
import com.citc.nce.tenant.vo.req.RefreshActualNodesReq;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shardingsphere.transaction.annotation.ShardingSphereTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author jiancheng
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CspCustomerServiceImpl extends ServiceImpl<CspCustomerMapper, CspCustomer> implements CspCustomerService {

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private IdentificationService identificationService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CspCustomerLoginRecordMapper cspCustomerLoginRecordMapper;

    @Autowired
    private ThirdLoginRelationsMapper thirdLoginRelationsMapper;

    @Autowired
    private CspCreateTableService cspCreateTableService;

    @Autowired
    private CspMapper cspMapper;

    @Autowired
    private CspCustomer1Dao cspCustomer1Dao;

    @Autowired
    private Csp1Dao csp1Dao;

    @Autowired
    private UserEnterPriseIdentificationService enterPriseIdentificationService;

    @Autowired
    private CertificateOptionsDao certificateOptionsDao;

    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;
    @Autowired
    private ThirdLoginConfigure thirdLoginConfigure;
    @Autowired
    private UserPersonIdentificationService personIdentificationService;
    @Resource
    private CspMealContractApi mealContractApi;
    @Autowired
    private CspCustomerChatbotAccountMapper cspCustomerChatbotAccountMapper;
    @Autowired
    private CspService cspService;
    @Autowired
    private CspMealContractApi cspMealContractApi;
    @Autowired
    private CustomerPostpayConfigApi postpayConfigApi;
    @Autowired
    private PrepaymentApi prepaymentApi;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MsgRecordApi msgRecordApi;
    @Resource
    private CspCustomerMapper cspCustomerMapper;
    @Resource
    ReadingLetterParseRecordApi readingLetterParseRecordApi;
    @Resource
    private final RedissonClient redissonClient;
    private static final Integer CSP_PORTAL = 3;
    private static final String CSP_ACTIVE = "CSP ACTIVE CUSTOMER";
    private static final String lock_balance_redis_key_prefix = "customer-balance-%s";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResp cusLogin(String cspId, LoginReq req) {
        //1、检验验证码
        CaptchaCheckReq checkReq = new CaptchaCheckReq()
                .setType(CaptchaType.DYZ_SMS.getCode())
                .setCaptchaKey(req.getCaptchaKey())
                .setCode(req.getCode())
                .setAccount(req.getAccount())
                .setIsAdminAuth(false);
        CaptchaCheckResp resp = captchaService.checkCaptcha(checkReq);
        if (Boolean.FALSE.equals(resp.getResult())) {
            throw new BizException(AuthCenterError.CAPTCHA_FAILED);
        }

        //检查csp是否被禁用
        if (!cspService.getCspStatus(cspId)) {
            log.info("csp 客户登录 但是csp被禁用 cspId:{}", cspId);
            throw new BizException(AuthCenterError.ACCOUNT_IS_FORBIDDEN);
        }
        List<CspCustomer> customers = this.lambdaQuery()
                .eq(CspCustomer::getPhone,req.getAccount())
                .eq(CspCustomer::getCspId,cspId)
                .orderByDesc(CspCustomer::getCreateTime)
                .list();
        if (CollectionUtils.isEmpty(customers)) {
            log.info("csp客户不存在");
            throw new BizException(AuthCenterError.ACCOUNT_USER_ABSENT);
        }
        CspCustomer customer = customers.get(0);
        if (!Boolean.TRUE.equals(customer.getCustomerActive())) {
            throw new BizException(500, "该账号未启用,请联系你的csp");
        }

        //判断客户是否使用结束
        Date outOfTime = customer.getOutOfTime();
        if (Objects.nonNull(outOfTime) && new Date().after(outOfTime)) {
            throw new BizException("试用结束，请联系CSP");
        }

        LoginResp loginResp = new LoginResp();
        BeanUtils.copyProperties(customer, loginResp);
        String customerId = customer.getCustomerId();
        loginResp.setUserId(customerId);
        Integer platformType = req.getPlatformType();
        loginResp.setPlatformType(platformType);
        loginResp.setUserName(customer.getName());
        loginResp.setRegisterTime(customer.getCreateTime());
        //查询平台权限
        this.checkUserPlatformPermission(loginResp);
        //查询企业认证情况
        this.obtainUserEnterpriseIdent(loginResp);
        loginResp.setCertificateList(identificationService.getUserCertificateByUserId(customerId));

        //5、生成token
        StpUtil.login(customerId, req.getDeviceType());
        log.info("customer: {} 登录成功", customerId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        BaseUser baseUser = new BaseUser();
        BeanUtils.copyProperties(loginResp, baseUser);
        baseUser.setIsCustomer(true);
        baseUser.setCspId(cspId);

        baseUser.setTempStorePerm(Integer.valueOf(1).equals(userMapper.getTempStorePerm(customerId)));
        StpUtil.getTokenSession().set(UserTokenUtil.SESSION_USER_KEY, baseUser);
        loginResp.setToken(tokenInfo.getTokenValue());
        //删除SCP强制登录记录
        deleteSCPPWIsUpdate(customerId);
        CspCustomerLoginRecord cspCustomerLoginRecord = new CspCustomerLoginRecord();
        cspCustomerLoginRecord.setCustomerId(customerId);
        cspCustomerLoginRecord.setName(customer.getName());
        //用户信息写入登录记录表
        cspCustomerLoginRecordMapper.insert(cspCustomerLoginRecord);
        loginResp.setIsCustomer(true);
        return loginResp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResp thirdLogin(thirdLoginReq req) {
        // 判断来源
        if (req.getSource() != 1) {
            throw new BizException("错误来源，请重试！");
        }

        // 检查token, 获取用户名
        Map<String,Object> params = new HashMap<>();
        String url = thirdLoginConfigure.getCheckTokenUrl() + "?token=" + req.getToken();
        log.info("AICC检查URL为：{}", url);
        String result = HttpUtil.post(url, params);
        log.info("向AICC检查token，结果为: {}", result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String userName = jsonObject.getString("user_name");
        if (StringUtils.isEmpty(userName)) {
            throw new BizException("未获取到用户名，请检查配置");
        }

        // 查询用户名所属CSPID
        LambdaQueryWrapper<ThirdLoginRelations> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ThirdLoginRelations::getSource, req.getSource());
        wrapper.eq(ThirdLoginRelations::getUserName, userName);
        ThirdLoginRelations info = thirdLoginRelationsMapper.selectOne(wrapper);
        if (info == null) {
            throw new BizException("未查询到关联关系，请检查配置");
        }
        String cspId = info.getCspId();
        String customerId = info.getCustomerId();

        // 查询账号
        CspCustomer customer = baseMapper.queryCspCustomerByCustomerId("csp_customer_" + cspId, customerId);
        if (customer == null) {
            throw new BizException(AuthCenterError.USER_ACCOUNT_NO_EXIST);
        }
        if (!Boolean.TRUE.equals(customer.getCustomerActive())) {
            throw new BizException("当前账号已被禁用");
        }

        //判断客户是否使用结束
        Date outOfTime = customer.getOutOfTime();
        if (Objects.nonNull(outOfTime) && new Date().after(outOfTime)) {
            throw new BizException("试用结束");
        }

        // 初始化默认值
        Integer platFormType = 3;
        String device = "pc";
        LoginResp loginResp = new LoginResp();
        BeanUtils.copyProperties(customer, loginResp);
        loginResp.setUserId(customerId);
        loginResp.setPlatformType(platFormType); // 默认chatbot
        loginResp.setUserName(customer.getName());
        loginResp.setRegisterTime(customer.getCreateTime());
        //查询平台权限
        this.checkUserPlatformPermission(loginResp);
        //查询企业认证情况
        this.obtainUserEnterpriseIdent(loginResp);
        loginResp.setCertificateList(identificationService.getUserCertificateByUserId(customerId));

        // 生成token
        StpUtil.login(customerId, device);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        BaseUser baseUser = new BaseUser();
        BeanUtils.copyProperties(loginResp, baseUser);
        baseUser.setIsCustomer(true);
        baseUser.setCspId(cspId);

        baseUser.setTempStorePerm(Integer.valueOf(1).equals(userMapper.getTempStorePerm(customerId)));
        StpUtil.getTokenSession().set(UserTokenUtil.SESSION_USER_KEY, baseUser);
        loginResp.setToken(tokenInfo.getTokenValue());
        //删除SCP强制登录记录
        deleteSCPPWIsUpdate(customerId);
        CspCustomerLoginRecord cspCustomerLoginRecord = new CspCustomerLoginRecord();
        cspCustomerLoginRecord.setCustomerId(customerId);
        cspCustomerLoginRecord.setName(customer.getName());
        //用户信息写入登录记录表
        cspCustomerLoginRecordMapper.insert(cspCustomerLoginRecord);
        loginResp.setIsCustomer(true);
        return loginResp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResp thirdAuth(thirdAuthReq req) {
        // 通过code获取token
        String token;
        Map<String,Object> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("code", req.getCode());
        params.put("redirect_uri", thirdLoginConfigure.getRedirectUrl());
        params.put("client_id", thirdLoginConfigure.getClientId());
        params.put("client_secret", thirdLoginConfigure.getClientSecret());
        String url = thirdLoginConfigure.getGetTokenUrl();
        log.info("AICC获取token, URL为：{}", url);
        log.info("AICC获取token, 参数为：{}", params);
        String result = HttpUtil.post(url, params);
        log.info("向AICC获取token，结果为: {}", result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        token = jsonObject.getString("access_token");
        if (StringUtils.isEmpty(token)) {
            throw new BizException("未获取access_token，请重试！");
        }
        // 登录
        thirdLoginReq loginReq = new thirdLoginReq();
        loginReq.setSource(req.getSource());
        loginReq.setToken(token);
        return this.thirdLogin(loginReq);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.BASE)
    public void addCustomer(CustomerAddReq req) {
        //获取csp id
        String cspId = SessionContextUtil.verifyCspLogin();
        //检查发票信息是否设置
/*        InvoiceInfoCspVo infoCspVo = invoiceInfoCspApi.getByVoCspId(cspId);
        if (Objects.isNull(infoCspVo)) {
            throw new BizException("您尚未完成发票信息设置，请前往发票信息设置中完成");
        }*/
        //检查csp是否饱和
        checkMealSaturation(cspId);
        try {
            cspCreateTableService.createCspTable(cspId);
            try {
                //新增ReadingLetterParseRecord表并且刷新callback service
                List<String> cspIdList = cspService.queryAllCspId();

                RefreshActualNodesReq refreshActualNodesReq = new RefreshActualNodesReq();
                refreshActualNodesReq.setCspIdSet(new HashSet<>(cspIdList));
                readingLetterParseRecordApi.refreshActualNodes(refreshActualNodesReq);
            } catch (Exception e) {
                log.info("新增ReadingLetterParseRecord表并且刷新callback service失败");
            }

            //1. 客户信息唯一性校验【企业用户名作为用户名】
            this.verifyCustomerUnique(cspId, req.getEnterpriseAccountName(), req.getPhone());

            //2. 插入customer
            CspCustomer customer = new CspCustomer();
            BeanUtils.copyProperties(req, customer);
            customer.setCspId(cspId);
            String customerId = customer.getCspId() + AuthUtils.randomID(5);
            customer.setCustomerId(customerId);

            if (Objects.nonNull(req.getOutOfTime())) {
                customer.setOutOfTime(DateUtil.endOfDay(DateUtil.offset(new Date(), DateField.DAY_OF_YEAR, 30)));
            }
            this.save(customer);
            //如果是后付费客户，校验配置是否完整并保存 v2.4.0计费方式取消付费方式配置
//            if (customer.getPayType() == CustomerPayType.POSTPAY) {
//                postpayConfigApi.config(req.getPostpayConfig().setCustomerId(customerId));
//            } else {
//                prepaymentApi.config(req.getPrepaymentConfig().setCustomerId(customerId));
//            }
            req.setUserId(customerId); //将userId设置成customerId，相关业务都通过customerId关联

            //3. 个人实名认证申请
            PersonIdentificationReq personIdentificationReq = new PersonIdentificationReq();
            BeanUtils.copyProperties(req, personIdentificationReq);
            personIdentificationService.personIdentificationApply(personIdentificationReq);

            //4. 企业资质认证申请
            EnterpriseIdentificationReq enterpriseIdentificationReq = new EnterpriseIdentificationReq();
            BeanUtils.copyProperties(req, enterpriseIdentificationReq);
            enterPriseIdentificationService.identificationApply(enterpriseIdentificationReq);

            //5. 默认通过企业资质申请
            AuditIdentificationReq auditIdentificationReq = new AuditIdentificationReq();
            auditIdentificationReq.setIdentificationId(QualificationType.BUSINESS_USER.getCode());
            auditIdentificationReq.setFlag(2);
            auditIdentificationReq.setAuthStatus(3);
            auditIdentificationReq.setClientUserId(customerId);
            adminUserService.auditIdentification(auditIdentificationReq);

            //6. 刷新客户资质到客户表
            this.updateAuthStatus(customerId, customer.getId());

            //7. 客户申请平台使用权限【user_platform_permissions】
            IdentificationPlatformPermissionReq identificationPlatformPermissionReq = new IdentificationPlatformPermissionReq();
            identificationPlatformPermissionReq.setUserId(customerId);
            identificationPlatformPermissionReq.setProtal(req.getProtal());
            adminUserService.applyPlatformPermission(identificationPlatformPermissionReq);

            //8. 通过用户平台权限申请
            ReviewCspReq reviewReq = new ReviewCspReq();
            reviewReq.setUserId(cspId);
            reviewReq.setUpdateTargetUserId(customerId);
            reviewReq.setProtal(CSP_PORTAL);
            reviewReq.setApprovalStatus(1);
            reviewReq.setApprovalLogId(CSP_ACTIVE);
            reviewReq.setRemark(CSP_ACTIVE);
            adminUserService.reviewPlatformForCsp(reviewReq);

        } catch (Throwable throwable) {
            log.error("添加用户失败", throwable);
            throw new BizException(500, "添加用户失败:" + throwable.getMessage());
        }
    }

    //检查csp是否饱和
    private void checkMealSaturation(String cspId) {
        if (!cspService.getCspStatus(cspId)) {
            throw new BizException("csp被禁用");
        }

        Long customerCount = countActiveCustomerByCspId(cspId);
        Long mealNumMun = cspMealContractApi.countCurrentMealNumMunByCspId(cspId);
        if (customerCount >= mealNumMun) {
            throw new BizException("您的客户数量已经饱和，不可进行该操作");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAuthStatus(String customerId, Long id) {
        //查询用户标签最新状态刷到user表中去
        CspCustomer customer = new CspCustomer().setCustomerId(customerId).setId(id);

        List<CertificateOptionsDo> certificateOptionsDos = certificateOptionsDao.selectList(
                Wrappers.<CertificateOptionsDo>lambdaQuery()
                        .eq(CertificateOptionsDo::getUserId, customerId)
                        .orderByDesc(CertificateOptionsDo::getUpdateTime)
        );
        if (CollectionUtils.isNotEmpty(certificateOptionsDos)) {
            customer.setAuthStatus(certificateOptionsDos.get(0).getCertificateApplyStatus());
            certificateOptionsDos.forEach(i -> {
                if (QualificationType.BUSINESS_USER.getCode().equals(i.getCertificateId())) {
                    customer.setEnterpriseAuthStatus(i.getCertificateApplyStatus());
                }
                if (QualificationType.REAL_NAME_USER.getCode().equals(i.getCertificateId())) {
                    customer.setPersonAuthStatus(i.getCertificateApplyStatus());
                }
            });
        }
        this.lambdaUpdate()
                .eq(CspCustomer::getCustomerId, customerId)
                .eq(CspCustomer::getId, id)
                .set(customer.getEnterpriseAuthStatus() != null, CspCustomer::getEnterpriseAuthStatus, customer.getEnterpriseAuthStatus())
                .set(customer.getPersonAuthStatus() != null, CspCustomer::getPersonAuthStatus, customer.getPersonAuthStatus())
                .update();
    }

    @Override
    public PageResult<CustomerSearchResp> queryList(CustomerSearchReq queryReq) {
        String enterpriseAccountName = queryReq.getEnterpriseAccountName();
        Integer customerActive = queryReq.getCustomerActive();
        String userId = SessionContextUtil.getUser().getUserId();
        Page<CustomerSearchResp> page = new Page<>(queryReq.getPageNo(), queryReq.getPageSize());
        // 超管可以查看所有
        if (StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            baseMapper.queryCspCustomerList(enterpriseAccountName, customerActive, queryReq.getPhone(), null, new Date(), page);
        } else {
            baseMapper.queryCspCustomerList(enterpriseAccountName, customerActive, queryReq.getPhone(), SessionContextUtil.verifyCspLogin(), new Date(), page);
        }
        changeCustomerStatus(page);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    public List<UserInfoForDropdownVo> queryCustomerOfCSPForDropdown(Integer payType) {
        String cspId = SessionContextUtil.getUser().getUserId();
        if (ObjUtil.isNull(payType)) {
            payType = CustomerPayType.PREPAY.getCode();
        }
        return cspCustomerMapper.queryCustomerOfCSPForDropdown(cspId, payType);
    }

    /**
     * 查询时修改状态
     */
    private static void changeCustomerStatus(Page<CustomerSearchResp> page) {
        Date date = new Date();
        for (CustomerSearchResp record : page.getRecords()) {
            //数据库原始状态
            Integer status = record.getCustomerActive();
            //有过期时间的用户
            Date outOfTime = record.getOutOfTime();
            if (Objects.nonNull(outOfTime)) {
                record.setCustomerActive(date.before(outOfTime) ? 2 : 3);
            }
            //如果用户被禁用那么将其设置为禁用而不是 如果过期则不修改状态（过期状态3 > 禁用0 >试用2 >正常1）
            if (status == 0 && record.getCustomerActive() != 3) {
                record.setCustomerActive(0);
            }
        }
    }

    @Override
    public List<CustomerProvinceResp> getCustomerDistribution() {
        return baseMapper.getCustomerDistribution(SessionContextUtil.verifyCspLogin());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCspActive(String customerId, Integer customerActive) {
        if (Objects.isNull(customerActive)) {
            throw new BizException("状态不能为空");
        }
        checkLoginCspCustomer(customerId);
        String cspId = SessionContextUtil.verifyCspLogin();
        //检查csp是否饱和
        if (customerActive != 0) {
            checkMealSaturation(cspId);
        }
        this.lambdaUpdate()
                .eq(CspCustomer::getCustomerId, customerId)
                .set(CspCustomer::getCustomerActive, customerActive)
                .update();
        //如果是禁用，把客户下线
        if (customerActive == 0)
            StpUtil.logout(customerId);
        return 0;
    }

    @Override
    public CustomerDetailResp getCustomerDetail(String customerId) {
        CustomerDetailResp detail = baseMapper.getCustomerDetail(customerId);
        detail.setAuditRemark(detail.getEnterpriseAuditRemark() != null ? detail.getEnterpriseAuditRemark() : detail.getPersonAuditRemark());
        detail.setPostpayConfig(postpayConfigApi.queryCustomerPostpayConfig(customerId));
        detail.setPrepaymentConfig(prepaymentApi.queryCustomerPrepaymentConfig(customerId));
        return detail;
    }


    //确保操作客户是当前csp的客户
    private static void checkLoginCspCustomer(String customerId) {
        String cspId = SessionContextUtil.verifyCspLogin();
        if (!customerId.substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.BASE)
    public int updateCustomer(CustomerUpdateReq req) {
        checkLoginCspCustomer(req.getUserId());
        checkoutPermissions(req.getPermissions());
        CspCustomer customer = lambdaQuery()
                .eq(CspCustomer::getCustomerId, req.getUserId())
                .one();
        if (customer == null)
            throw new BizException(500, "客户不存在");
        String reqName = req.getName();
        if (lambdaQuery()
                .ne(CspCustomer::getCustomerId, customer.getCustomerId())
                .eq(CspCustomer::getCspId, customer.getCspId())
                .eq(CspCustomer::getName, reqName).exists())
            throw new BizException(500, "账号名重复");
        String newPhone = req.getPhone();
        if (lambdaQuery()
                .ne(CspCustomer::getCustomerId, customer.getCustomerId())
                .eq(CspCustomer::getCspId, customer.getCspId())
                .eq(CspCustomer::getPhone, newPhone).exists())
            throw new BizException(500, "手机号重复");
        String newMail = req.getMail();
        if (lambdaQuery()
                .ne(CspCustomer::getCustomerId, customer.getCustomerId())
                .eq(CspCustomer::getCspId, customer.getCspId())
                .eq(CspCustomer::getMail, newMail).exists())
            throw new BizException(500, "邮箱重复");


        //校验权限和支付配置的修改是否合法（如果产生对应的消息记录则不能再修改）
        checkPermissionAndPayConfigUpdateIsLegal(req, customer);

        lambdaUpdate()
                .eq(CspCustomer::getCustomerId, req.getUserId())
                .set(CspCustomer::getPhone, newPhone)
                .set(CspCustomer::getMail, newMail)
                .set(req.getName() != null, CspCustomer::getName, req.getName())
                .set(req.getPermissions() != null, CspCustomer::getPermissions, req.getPermissions())
                .update();


        //如果req.getName()不为空,  那么尝试修改csp_customer_chatbot_account中的account_name字段
        if (StringUtils.isNotBlank(req.getName())) {
            CspCustomerChatbotAccount cspCustomerChatbotAccount = new CspCustomerChatbotAccount();
            cspCustomerChatbotAccount.setCustomerName(req.getName());
            cspCustomerChatbotAccountMapper.update(cspCustomerChatbotAccount,
                    new LambdaQueryWrapperX<CspCustomerChatbotAccount>().eq(CspCustomerChatbotAccount::getCustomerId, req.getUserId()));
        }

        return 0;
    }

    private void checkPermissionAndPayConfigUpdateIsLegal(CustomerUpdateReq req, CspCustomer customer) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
        Map<MsgTypeEnum, List<MessageResourceType>> sentMsgMap = msgRecordApi.querySendMsgTypeListBetween(customer.getCustomerId(), start, end);
        Set<MsgTypeEnum> sentMsgTypes = sentMsgMap.keySet();
        Set<MessageResourceType> sentMessageSources = sentMsgMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
        if (req.getPermissions() != null) {

            List<Permission> oldPermissions = Permission.convert(customer.getPermissions());
            List<Permission> newPermissions = Permission.convert(req.getPermissions());
            log.info("oldPermissions:{}, newPermissions:{}", oldPermissions, newPermissions);
            oldPermissions.removeAll(newPermissions);
            //校验撤销的客户权限对应的消息类型是否产生计费
            List<Permission> revokedPermissions = oldPermissions;
            log.info("revokedPermissions:{}", revokedPermissions);
            for (Permission permission : revokedPermissions) {
                switch (permission) {
                    case MASS: {
                        throw new BizException("不能取消5G消息权限!");
                    }
                    case _5G_MESSAGE: {
                        if (sentMsgTypes.contains(MsgTypeEnum.M5G_MSG))
                            throw new BizException(500, String.format("该客户当前账期 %s 已经产生消息记录，不能撤销此权限！", permission.getDesc()));
                        break;
                    }
                    case MEDIA_SMS: {
                        if (sentMsgTypes.contains(MsgTypeEnum.MEDIA_MSG))
                            throw new BizException(500, String.format("该客户当前账期 %s 已经产生消息记录，不能撤销此权限！", permission.getDesc()));
                        break;
                    }
                    case SMS: {
                        if (sentMsgTypes.contains(MsgTypeEnum.SHORT_MSG))
                            throw new BizException(500, String.format("该客户当前账期 %s 已经产生消息记录，不能撤销此权限！", permission.getDesc()));
                        break;
                    }
                    case ROBOT: {
                        if (sentMessageSources.contains(MessageResourceType.CHATBOT))
                            throw new BizException(500, String.format("该客户当前账期 %s 已经产生消息记录，不能撤销此权限！", permission.getDesc()));
                        break;
                    }
                    default:
                        throw new BizException("unknown permission: " + permission);
                }
            }
        }

        // v2.4.0计费方式取消付费方式配置
//        if (customer.getPayType() == CustomerPayType.POSTPAY) {
//            CustomerPostpayConfigVo postpayConfig = req.getPostpayConfig();
//            if (postpayConfig != null) {
//                if (postpayConfig.getSmsPrice() != null)
//                    Assert.isTrue(!sentMsgTypes.contains(MsgTypeEnum.SHORT_MSG), String.format("该客户当前账期 %s 已经产生消息记录，不能修改此价格！", MsgTypeEnum.SHORT_MSG.getDesc()));
//                if (postpayConfig.getVideoPrice() != null)
//                    Assert.isTrue(!sentMsgTypes.contains(MsgTypeEnum.MEDIA_MSG), String.format("该客户当前账期 %s 已经产生消息记录，不能修改此价格！", MsgTypeEnum.MEDIA_MSG.getDesc()));
//                if (postpayConfig.getFifthConfigMap() != null)
//                    Assert.isTrue(!sentMsgTypes.contains(MsgTypeEnum.M5G_MSG), String.format("该客户当前账期 %s 已经产生消息记录，不能修改此价格！", MsgTypeEnum.M5G_MSG.getDesc()));
//                postpayConfigApi.config(postpayConfig.setCustomerId(customer.getCustomerId()));
//            }
//        } else {
//            CustomerPrepaymentConfigVo prepaymentConfig = req.getPrepaymentConfig();
//            if (prepaymentConfig != null) {
//                if (prepaymentConfig.getFallbackPrice() != null)
//                    Assert.isTrue(!sentMsgTypes.contains(MsgTypeEnum.M5G_MSG), String.format("该客户当前账期 %s 已经产生消息记录，不能修改此价格！", MsgTypeEnum.M5G_MSG.getDesc()));
//                prepaymentApi.config(prepaymentConfig.setCustomerId(customer.getCustomerId()));
//            }
//        }
    }

    private void checkoutPermissions(String permissions) {
        List<String> permissionAlls = Arrays.asList("1", "2", "3", "4", "5", "6");
        String[] split = permissions.split(",");
        for (String item : split) {
            if (!permissionAlls.contains(item)) {
                throw new BizException("权限列表错误！！");
            }
        }
    }

    @Override
    public String getUserPermission(String customerId) {
        return lambdaQuery()
                .eq(CspCustomer::getCustomerId, customerId)
                .eq(CspCustomer::getCustomerActive, true)
                .select(CspCustomer::getPermissions)
                .oneOpt()
                .map(CspCustomer::getPermissions)
                .orElse(null);
    }

    @Override
    public CspInfoResp getCspInfo(String userId) {
        CspInfoResp resp = new CspInfoResp();
        if (!Strings.isNullOrEmpty(userId)) {
            Csp csp = cspMapper.selectOne(
                    Wrappers.<Csp>lambdaQuery()
                            .eq(Csp::getUserId, userId)
                            .select());
            if (csp != null) {
                resp.setCspId(csp.getCspId());
                resp.setSplite(1 == csp.getIsSplite());
            } else {
                LambdaQueryWrapper<CspCustomerDo> cspCustomerDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
                cspCustomerDoLambdaQueryWrapper.eq(CspCustomerDo::getCustomerId, userId);
                List<CspCustomerDo> cspCustomerDos = cspCustomer1Dao.selectList(cspCustomerDoLambdaQueryWrapper);
                if (CollectionUtils.isNotEmpty(cspCustomerDos)) {
                    CspCustomerDo cspCustomerDo = cspCustomerDos.get(0);
                    String cspId = cspCustomerDo.getCspId();
                    Csp csp2 = cspMapper.selectOne(
                            Wrappers.<Csp>lambdaQuery()
                                    .eq(Csp::getCspId, cspId)
                                    .select());
                    if (csp2 != null) {
                        resp.setCspId(csp2.getCspId());
                        resp.setSplite(1 == csp2.getIsSplite());
                    }
                }
            }
        }
        return resp;
    }

    @Override
    public QueryUserInfoDetailResp getUserInfoDetail(QueryUserInfoDetailReq req) {
        QueryUserInfoDetailResp detail = new QueryUserInfoDetailResp();
        detail.setIsCustomer(true);
        BaseUser user = SessionContextUtil.getUser();
        String customerId = user.getUserId();
        CspCustomer customer = this.lambdaQuery().eq(CspCustomer::getCustomerId, customerId).one();
        if (customer == null)
            return null;
        BeanUtils.copyProperties(customer, detail);
        detail.setToken(StpUtil.getTokenInfo().getTokenValue());
        detail.setUserName(customer.getName());
        detail.setRegisterTime(customer.getCreateTime());
        detail.setUserId(customerId);
        detail.setLoginAddress(String.format("/user/login/%s", CspUtils.encodeCspId(customer.getCspId())));
        UserPlatformPermissionsDo platformPermission = identificationService.findByPlatAndUserId(req.getProtalType(), customerId);
        if (platformPermission != null) {
            detail.setUserStatus(platformPermission.getUserStatus());
            detail.setApprovalStatus(platformPermission.getApprovalStatus());
            if (2 == platformPermission.getApprovalStatus()) {
                identificationService.findApprovalById(platformPermission.getApprovalLogId())
                        .stream()
                        .findFirst()
                        .ifPresent(approvalLog -> detail.setRemark(approvalLog.getRemark()));
            }
        } else {
            detail.setUserStatus(0);
            detail.setApprovalStatus(0);
        }
        if (detail.getPayType() == CustomerPayType.PREPAY) {
            detail.setPrepaymentConfig(prepaymentApi.queryCustomerPrepaymentConfig(customerId));
        } else {
            detail.setPostpayConfig(postpayConfigApi.queryCustomerPostpayConfig(customerId));
        }
        detail.setCertificateList(identificationService.getUserCertificateByUserId(customerId));
        detail.setAuditRemark(Optional.ofNullable(identificationService.findUserEnterpriseIdent(customerId)).map(UserEnterpriseIdentificationDo::getAuditRemark).orElse(""));
        if (StringUtils.isEmpty(detail.getPermissions()))
            detail.setPermissions("1,2");

        // 代登录
        detail.setIsAgentLogin(user.getIsAgentLogin());
        // 如果是代登录，设置用户名称为CSP的用户名
        if (user.getIsAgentLogin()) {
            UserDo userDo = userService.findByUserId(customer.getCspId());
            detail.setName(userDo.getName());
            detail.setUserName(userDo.getName());
        }
        return detail;
    }

    @Override
    public List<String> queryCustomerIdsByCspId(String cspId) {
        return lambdaQuery().eq(CspCustomer::getCspId, cspId).select(CspCustomer::getCustomerId).list().stream().map(CspCustomer::getCustomerId).collect(Collectors.toList());
    }

    @Override
    public SetUseImgOrNameResp setCusImgOrName(SetUseImgOrNameVReq req) {
        SetUseImgOrNameResp resp = new SetUseImgOrNameResp();
        String customerId = SessionContextUtil.getUser().getUserId();
        String name = req.getName();
        String userImgUuid = req.getUserImg();
        if (StrUtil.isBlank(name) && StrUtil.isBlank(userImgUuid)) {
            throw new BizException(AuthCenterError.PARAMETER_BAD);
        }

        CspCustomer customer = lambdaQuery().eq(CspCustomer::getCustomerId, customerId).oneOpt().orElseThrow(() -> new BizException(500, "客户不存在"));
        if (!Strings.isNullOrEmpty(name)) {
            if (lambdaQuery()
                    .ne(CspCustomer::getCustomerId, customer.getCustomerId())
                    .eq(CspCustomer::getCspId, customer.getCspId())
                    .eq(CspCustomer::getName, name).exists()) {
                throw new BizException(500, "账号名重复");
            }
        }

        resp.setExecResult(
                lambdaUpdate()
                        .eq(CspCustomer::getCustomerId, customerId)
                        .set(StringUtils.isNotEmpty(name), CspCustomer::getName, name)
                        .set(StringUtils.isNotEmpty(userImgUuid), CspCustomer::getUserImgUuid, userImgUuid)
                        .update(new CspCustomer())
        );

        //如果req.getName()不为空,  那么尝试修改csp_customer_chatbot_account中的account_name字段
        if (StringUtils.isNotBlank(req.getName())) {
            CspCustomerChatbotAccount cspCustomerChatbotAccount = new CspCustomerChatbotAccount();
            cspCustomerChatbotAccount.setCustomerName(req.getName());
            cspCustomerChatbotAccountMapper.update(cspCustomerChatbotAccount,
                    new LambdaQueryWrapperX<CspCustomerChatbotAccount>().eq(CspCustomerChatbotAccount::getCustomerId, customerId));
        }

        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActivateEmailResp activateEmail(ActivateEmailReq req) {
        BaseUser baseUser = SessionContextUtil.getUser();
        String userId = (baseUser != null) ? baseUser.getUserId() : req.getUserId();
        captchaService.checkCaptcha(req.getCaptchaCheckReq());
        if (this.lambdaUpdate()
                .eq(CspCustomer::getCustomerId, userId)
                .eq(CspCustomer::getMail, req.getMail())
                .set(CspCustomer::getEmailActivated, 1)
                .update(new CspCustomer())) {
            return new ActivateEmailResp();
        }
        throw new BizException(500, "激活邮箱失败");
    }

    @Override
    public UserInfoVo queryById(String customerId) {
        return lambdaQuery()
                .eq(CspCustomer::getCustomerId, customerId)
                .select(
                        CspCustomer::getId,
                        CspCustomer::getCspId,
                        CspCustomer::getCustomerId,
                        CspCustomer::getName,
                        CspCustomer::getPhone,
                        CspCustomer::getMail,
                        CspCustomer::getCustomerActive,
                        CspCustomer::getPayType,
                        CspCustomer::getPermissions,
                        CspCustomer::getBalance
                )
                .oneOpt()
                .map(customer -> {
                    UserInfoVo userInfoVo = new UserInfoVo();
                    BeanUtils.copyProperties(customer, userInfoVo);
                    return userInfoVo;
                })
                .orElse(null);
    }

    Boolean updateRecharge(String customerId, Long balance) {
        return this.lambdaUpdate()
                .eq(CspCustomer::getCustomerId, customerId)
                .set(CspCustomer::getBalance, balance)
                .update(new CspCustomer());
    }

    @Override
    public Boolean recharge(String customerId, Long chargeAmount) {
        RLock rLock = redissonClient.getLock(String.format(lock_balance_redis_key_prefix, customerId));
        try {
            String userId = SessionContextUtil.verifyCspLogin();
            log.info("充值,CSP:{},customerId:{},amount:{}", userId, customerId, chargeAmount);
            CspCustomer customer = getByCustomerId(customerId);
            if (Objects.isNull(customer)) {
                log.error("充值时客户不存在");
                throw new BizException("操作失败");
            }
            if (customer.getPayType() != CustomerPayType.PREPAY) {
                throw new BizException("后付费客户不能充值");
            }

            if (Objects.isNull(chargeAmount) || chargeAmount <= 0) {
                throw new BizException("金额非法");
            }
            customer.setBalance((customer.getBalance() == null ? 0 : customer.getBalance()) + chargeAmount);
            log.info("开始更新余额");
            return updateRecharge(customerId, customer.getBalance());
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread())
                rLock.unlock();
        }
    }

    @Override
    public Long getBalance(String customerId) {
        CspCustomer cspCustomer = getByCustomerId(customerId);
        if (Objects.isNull(cspCustomer)) {
            throw new BizException(AuthCenterError.PARAM_USER_ID_ERROR);
        }
        return cspCustomer.getBalance();
    }

    @Override
    public Long addBalance(String customerId, Long addMoney) {
        
        RLock rLock = redissonClient.getLock(String.format(lock_balance_redis_key_prefix, customerId));
        try {
            rLock.lock();
            CspCustomer cspCustomer = getByCustomerId(customerId);
            if (Objects.isNull(cspCustomer)) {
                throw new BizException(AuthCenterError.PARAM_USER_ID_ERROR);
            }
            cspCustomer.setBalance(cspCustomer.getBalance() + addMoney);
            updateRecharge(customerId, cspCustomer.getBalance());
            return cspCustomer.getBalance();
        } catch (Throwable throwable) {
            throw throwable;
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread())
                rLock.unlock();
        }
    }

    @Override
    public ReduceBalanceResp reduceBalance(String customerId, Long price, Long num, boolean isTryMax) {
        RLock rLock = redissonClient.getLock(String.format(lock_balance_redis_key_prefix, customerId));
        Long deductAmount = num * price;
        ReduceBalanceResp reduceBalanceResp = new ReduceBalanceResp();
        try {
            CspCustomer cspCustomer = getByCustomerId(customerId);
            if (Objects.isNull(cspCustomer)) {
                throw new BizException(AuthCenterError.PARAM_USER_ID_ERROR);
            }
            if (cspCustomer.getBalance() <= 0L) {
                throw new BizException(AuthCenterError.BALANCE_EMPTY);
            }
            Long balance = cspCustomer.getBalance() - deductAmount;
            Long realNum = num;
            if (balance < 0) {
                //扣费总额不满足，抛异常
                if (!isTryMax) {
                    throw new BizException(AuthCenterError.BALANCE_NOT_ENOUGH);
                } else {
                    //尽最大努力扣费
                    realNum = cspCustomer.getBalance() / price;
                    //一条记录的费用都不够
                    if (realNum == 0) {
                        log.error("用户余额不足，无法扣费 price:{} , num : {}, balance:{}", price, num, balance);
                    }
                    deductAmount = realNum * price;
                    balance = cspCustomer.getBalance() - deductAmount;
                }
            }
            reduceBalanceResp.setDeductAmount(deductAmount);
            reduceBalanceResp.setNumOfDeduct(realNum);
            cspCustomer.setBalance(balance);
            updateRecharge(customerId, cspCustomer.getBalance());
            return reduceBalanceResp;
        } catch (Throwable throwable) {
            throw throwable;
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread())
                rLock.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModifyUserPhoneOrEmailResp updatePhoneOrEmail(ModifyUserPhoneOrEmailVReq req) {
        CaptchaCheckResp captchaCheckResp = captchaService.checkCaptcha(req.getCaptchaInfo());
        Integer type = req.getAccountType();//1--EMAIL,3--PHONE
        CspCustomer customer = lambdaQuery().eq(CspCustomer::getCustomerId, SessionContextUtil.getUser().getUserId()).one();
        LambdaUpdateChainWrapper<CspCustomer> luw = lambdaUpdate()
                .eq(CspCustomer::getCustomerId, customer.getCustomerId());
        boolean updated;
        switch (type) {
            case 1: {
                if (lambdaQuery()
                        .eq(CspCustomer::getCspId, customer.getCspId())
                        .eq(CspCustomer::getMail, req.getNewAccount())
                        .ne(CspCustomer::getCustomerId, customer.getCustomerId())
                        .exists()) {
                    throw new BizException(500, "该邮箱已被使用，请更换邮箱后重试");
                }
                updated = luw
                        .set(CspCustomer::getMail, req.getNewAccount())
                        .update(new CspCustomer());
                break;
            }
            case 3: {
                if (lambdaQuery()
                        .eq(CspCustomer::getCspId, customer.getCspId())
                        .eq(CspCustomer::getPhone, req.getNewAccount())
                        .ne(CspCustomer::getCustomerId, customer.getCustomerId())
                        .exists()) {
                    throw new BizException(500, "该手机号已被使用，请更换手机号后重试");
                }
                updated = luw
                        .set(CspCustomer::getPhone, req.getNewAccount())
                        .update(new CspCustomer());
                break;
            }
            default:
                throw new BizException(500, "不支持的修改类型:" + type);
        }
        ModifyUserPhoneOrEmailResp resp = new ModifyUserPhoneOrEmailResp();
        resp.setCheckResult(captchaCheckResp.getResult());
        resp.setExecResult(updated);
        return resp;
    }

    @Override
    public List<UserInfoVo> checkLoadNameExist(String value) {
        BaseUser loginUser = SessionContextUtil.getLoginUser();
        List<CspCustomer> list = lambdaQuery()
                .eq(CspCustomer::getCspId, loginUser.getCspId())
                .and(qw -> qw.eq(CspCustomer::getName, value)
                        .or()
                        .eq(CspCustomer::getPhone, value)
                )
                .select(
                        CspCustomer::getId,
                        CspCustomer::getCspId,
                        CspCustomer::getCustomerId,
                        CspCustomer::getName,
                        CspCustomer::getPhone,
                        CspCustomer::getMail
                )
                .list();
        List<UserInfoVo> userInfoVos = BeanUtil.copyToList(list, UserInfoVo.class);
        return userInfoVos;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void disableCustomerMealCsp(String cspId) {
        //查询合同套餐数量
        Long mealNum = mealContractApi.countCurrentMealNumMunByCspId(cspId);
        Long customerNum = countActiveCustomerByCspId(cspId);
        //需要禁用客户
        if (mealNum < customerNum) {
            //按照账号创建的时间倒序，即越晚创建的客户账号最先被禁用 查询出需要数量
            List<String> customerIds = lambdaQuery()
                    .select(CspCustomer::getCustomerId)
                    .eq(CspCustomer::getCspId, cspId)
                    .eq(CspCustomer::getCustomerActive, true)
                    .and(wp -> wp.isNull(CspCustomer::getOutOfTime).or().gt(CspCustomer::getOutOfTime, new Date()))
                    .orderByDesc(CspCustomer::getCreateTime)
                    .last("limit " + (customerNum - mealNum))
                    .list()
                    .stream().map(CspCustomer::getCustomerId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(customerIds)) {
                log.info("客户被禁用 {}", customerIds);
                //禁用客户
                lambdaUpdate()
                        .in(CspCustomer::getCustomerId, customerIds)
                        .set(CspCustomer::getCustomerActive, false)
                        .update();
            }
        }
        //刷新状态
        refreshCspMealStatus(cspId);
    }

    @Override
    public Long countActiveCustomerByCspId(String cspId) {
        if (StringUtils.isEmpty(cspId)) return 0L;
        return count(
                new LambdaQueryWrapper<CspCustomer>()
                        .eq(CspCustomer::getCspId, cspId)
                        .eq(CspCustomer::getCustomerActive, true)
                        .and(wp -> wp.isNull(CspCustomer::getOutOfTime).or().gt(CspCustomer::getOutOfTime, new Date()))
        );
    }

    @Override
    public Long countCustomerByCspId(String cspId) {
        if (StringUtils.isEmpty(cspId)) return 0L;
        return count(new LambdaUpdateWrapper<CspCustomer>()
                .eq(CspCustomer::getCspId, cspId));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void refreshCspMealStatus(String cspId) {
        Long mealNumMun = mealContractApi.countCurrentMealNumMunByCspId(cspId);
        Long customerNum = countActiveCustomerByCspId(cspId);
        refreshCspMealStatus(cspId, mealNumMun, customerNum);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void refreshCspMealStatus(String cspId, Long mealNumMun, Long customerNum) {
        Integer status = customerNum > mealNumMun ? 1 : 0;
        userMapper.changeCspMealStatusByCspId(cspId, status);
    }

    @Override
    public List<UserInfo> listMealStatus(String cspId, Integer mealStatus) {
        return userMapper.listMealStatus(cspId, mealStatus);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void changeCustomerToFormal(String customerId) {
        CustomerDetailResp customer = getCustomerDetail(customerId);
        if (Objects.isNull(customer)) {
            throw new BizException("客户不存在");
        }
        String cspId = customer.getCspId();
        Long mealNumMun = mealContractApi.countCurrentMealNumMunByCspId(cspId);
        Long customerNum = countActiveCustomerByCspId(cspId);
        refreshCspMealStatus(cspId, mealNumMun, customerNum);

        if (customerNum + 1 > mealNumMun) {
            throw new BizException("您的客户服务数量已经饱和，不可进行该操作");
        }
        getBaseMapper().setCustomerToFormal(customerId);
        log.info("csp转正了一个用户:{}", customerId);
    }

    @Override
    public void deleteCustomer(String customerId) {
        CspCustomer customer = getByCustomerId(customerId);
        if (Objects.isNull(customer)) return;
        if (!customer.getCustomerActive()) {
            getBaseMapper().removeByCustomerId(customer.getCustomerId(), SessionContextUtil.getUserId());
            return;
        }
        if (Objects.nonNull(customer.getOutOfTime())) {
            getBaseMapper().removeByCustomerId(customer.getCustomerId(), SessionContextUtil.getUserId());
            return;
        }
        throw new BizException("当前客户状态不能删除");
    }

    private CspCustomer getByCustomerId(String customerId) {
        return lambdaQuery().eq(CspCustomer::getCustomerId, customerId).one();
    }

    @Override
    public boolean checkUnique(String checkValue, Integer checkType) {
        BaseUser user = SessionContextUtil.getLoginUser();
        LambdaQueryWrapper<CspCustomer> queryWrapper = new LambdaQueryWrapper<CspCustomer>()
                .eq(CspCustomer::getCspId, user.getCspId())
                .ne(CspCustomer::getCustomerId, user.getUserId());
        switch (checkType) {
            case 1:
                queryWrapper.eq(CspCustomer::getMail, checkValue);
                break;
            case 2:
                queryWrapper.and(query -> query.eq(CspCustomer::getName, checkValue).or().eq(CspCustomer::getPhone, checkValue));
                break;
            case 3:
                queryWrapper.eq(CspCustomer::getPhone, checkValue);
                break;
            case 4:
                queryWrapper.eq(CspCustomer::getMail, checkValue).eq(CspCustomer::getEmailActivated, NumCode.ONE.getCode());
                break;
            default:
                return false;
        }
        Long userCount = this.baseMapper.selectCount(queryWrapper);
        if (userCount > 0) {
            return false;
        }
        return true;
    }


    @Override
    public Collection<UserEnterpriseInfoVo> getUserEnterpriseInfoByUserIds(Collection<String> userIds) {
        List<UserEnterpriseIdentificationDo> list = enterPriseIdentificationService.lambdaQuery()
                .in(UserEnterpriseIdentificationDo::getUserId, userIds)
                .list();
        return BeanUtil.copyToList(list, UserEnterpriseInfoVo.class);
    }

    @Override
    public Collection<UserEnterpriseInfoVo> getUserUserIdsLikeEnterpriseAccountName(String enterpriseAccountName) {
        List<UserEnterpriseIdentificationDo> list = enterPriseIdentificationService.lambdaQuery()
                .like(UserEnterpriseIdentificationDo::getEnterpriseAccountName, enterpriseAccountName)
                .list();
        return BeanUtil.copyToList(list, UserEnterpriseInfoVo.class);
    }

    @Override
    public CustomerDetailResp getDetailByCustomerIdForDontCheck(String customerId) {
        CustomerDetailResp detail = baseMapper.getCustomerDetail(customerId);
        detail.setAuditRemark(detail.getEnterpriseAuditRemark() != null ? detail.getEnterpriseAuditRemark() : detail.getPersonAuditRemark());
        return detail;
    }

    @Override
    public void changeEnableAgentLogin(EnableAgentLoginReq req) {
        BaseUser loginUser = SessionContextUtil.getLoginUser();
        CustomerDetailResp detail = baseMapper.getCustomerDetail(loginUser.getUserId());
        detail.setEnableAgentLogin(req.getEnableAgentLogin());
        lambdaUpdate()
                .eq(CspCustomer::getCustomerId, loginUser.getUserId())
                .set(CspCustomer::getEnableAgentLogin, req.getEnableAgentLogin())
                .update(new CspCustomer());

        // 如果将enableAgentLogin置为false的同时，需要将自动登出代登录上去的csp账号
        if (!req.getEnableAgentLogin()) {
            StpUtil.logout(loginUser.getUserId(), "agentPC");
        }
    }

    @Override
    public String agentLogin(CspAgentLoginReq req) {
        // 查询customer信息
        CustomerDetailResp detail = baseMapper.getCustomerDetail(req.getCustomerId());
        if (!Objects.equals(req.getCspId(), detail.getCspId())) {
            throw new BizException("当前客户不属于您，无法进行代登录");
        }
        if (!detail.getEnableAgentLogin()) {
            throw new BizException("当前客户没有开通代登录服务，请联系客户！");
        }
        // 查询csp信息
        UserDo userDo = userService.findByUserId(req.getCspId());

        String customerId = req.getCustomerId();
        LoginResp loginResp = new LoginResp();
        BeanUtils.copyProperties(detail, loginResp);
        loginResp.setUserId(customerId);
        loginResp.setPlatformType(req.getPlatformType());
        loginResp.setUserName(userDo.getName());
        loginResp.setName(userDo.getName());
        //查询平台权限
        this.checkUserPlatformPermission(loginResp);
        //查询企业认证情况
        this.obtainUserEnterpriseIdent(loginResp);
        loginResp.setCertificateList(identificationService.getUserCertificateByUserId(customerId));

        // 代登录
        loginResp.setIsAgentLogin(true);

        //生成token
        StpUtil.login(customerId, req.getDeviceType());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        BaseUser baseUser = new BaseUser();
        BeanUtils.copyProperties(loginResp, baseUser);
        baseUser.setIsCustomer(true);
        baseUser.setCspId(req.getCspId());

        baseUser.setTempStorePerm(Integer.valueOf(1).equals(userMapper.getTempStorePerm(customerId)));
        StpUtil.getTokenSession().set(UserTokenUtil.SESSION_USER_KEY, baseUser);
        String tokenStr = tokenInfo.getTokenValue();
        loginResp.setToken(tokenStr);
        //删除SCP强制登录记录
        deleteSCPPWIsUpdate(detail.getCustomerId());
        //用户信息写入登录记录表
        cspCustomerLoginRecordMapper.insert(new CspCustomerLoginRecord().setCustomerId(customerId));
        loginResp.setIsCustomer(true);

        // 登录信息写入redis中，前端用token来获取用户的登录信息
        redisService.setCacheObject(tokenStr, loginResp, 1L, TimeUnit.MINUTES);
        return tokenStr;
    }

    @Override
    public LoginResp getAgentLoginInfo(String token) {
        return redisService.getCacheObject(token);
    }

    private void verifyCustomerUnique(String cspId, String name, String phone) {
        if (phone.length() != 11 || !phone.startsWith("1")) {
            throw new BizException(AuthCenterError.PHONE_FORMAT_ERROR);
        }
        if (lambdaQuery().eq(CspCustomer::getCspId, cspId).eq(CspCustomer::getName, name).exists())
            throw new BizException(500, "客户名重复");
        if (lambdaQuery().eq(CspCustomer::getCspId, cspId).eq(CspCustomer::getPhone, phone).exists())
            throw new BizException(500, "客户手机号重复");
    }

    private void checkUserPlatformPermission(LoginResp resp) {
        UserPlatformPermissionsDo userPlatformPermissionsDo = identificationService.findByPlatAndUserId(resp.getPlatformType(), resp.getUserId());
        if (null != userPlatformPermissionsDo) {
            resp.setUserStatus(userPlatformPermissionsDo.getUserStatus());
            resp.setApprovalStatus(userPlatformPermissionsDo.getApprovalStatus());
            if (resp.getPlatformType() == 3 && userPlatformPermissionsDo.getApprovalStatus() == 2) {
                //审核不通过时候展示审核备注
                List<ApprovalLogDo> approvalLogList = identificationService.findApprovalById(userPlatformPermissionsDo.getApprovalLogId());
                if (CollectionUtils.isNotEmpty(approvalLogList))
                    resp.setRemark(approvalLogList.get(0).getRemark());
            }
        } else {
            resp.setUserStatus(0);
            resp.setApprovalStatus(0);
        }
    }

    /**
     * 获取用户的企业认证结果
     *
     * @param resp 响应信息
     */
    private void obtainUserEnterpriseIdent(LoginResp resp) {
        UserEnterpriseIdentificationDo userEnterpriseIdentification = identificationService.findUserEnterpriseIdent(resp.getUserId());
        resp.setAuditRemark(userEnterpriseIdentification == null ? "" : userEnterpriseIdentification.getAuditRemark());
    }

    private void deleteSCPPWIsUpdate(String userId) {
        if (!Strings.isNullOrEmpty(userId)) {
            String REDIS_CSP_USER_PW_UPDATE = "CSP:USER-UPDATE-PW";
            Integer result = (Integer) redisTemplate.opsForValue().get(REDIS_CSP_USER_PW_UPDATE + userId);
            if (result != null && result == 1) {
                redisTemplate.delete(REDIS_CSP_USER_PW_UPDATE + userId);
            }
        }
    }
}
