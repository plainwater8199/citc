package com.citc.nce.authcenter.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.authcenter.admin.dao.AdminUserDao;
import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.auth.service.AdminAuthService;
import com.citc.nce.authcenter.auth.service.AuthService;
import com.citc.nce.authcenter.auth.service.LoginRecordService;
import com.citc.nce.authcenter.auth.vo.CertificateInfo;
import com.citc.nce.authcenter.auth.vo.MerchantInfo;
import com.citc.nce.authcenter.auth.vo.req.*;
import com.citc.nce.authcenter.auth.vo.resp.*;
import com.citc.nce.authcenter.captch.service.CaptchaService;
import com.citc.nce.authcenter.captcha.vo.req.CaptchaCheckReq;
import com.citc.nce.authcenter.captcha.vo.resp.CaptchaCheckResp;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.constant.CaptchaType;
import com.citc.nce.authcenter.constant.CheckUniqueTypeEnum;
import com.citc.nce.authcenter.csp.domain.CspTrumpetUser;
import com.citc.nce.authcenter.csp.multitenant.entity.Csp;
import com.citc.nce.authcenter.csp.multitenant.service.CspCustomerService;
import com.citc.nce.authcenter.csp.multitenant.service.CspService;
import com.citc.nce.authcenter.csp.trumpet.service.ICspTrumpetUserService;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.authcenter.identification.dao.ApprovalLogDao;
import com.citc.nce.authcenter.identification.dao.UserEnterpriseIdentificationDao;
import com.citc.nce.authcenter.identification.dao.UserPlatformPermissionsDao;
import com.citc.nce.authcenter.identification.entity.*;
import com.citc.nce.authcenter.identification.service.IdentificationService;
import com.citc.nce.authcenter.identification.vo.resp.WebEnterpriseIdentificationResp;
import com.citc.nce.authcenter.legalaffairs.service.LegalAffairsService;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalFileNewestResp;
import com.citc.nce.authcenter.user.dao.UserDao;
import com.citc.nce.authcenter.user.dao.UserMapper;
import com.citc.nce.authcenter.user.entity.UserDo;
import com.citc.nce.authcenter.user.service.UserService;
import com.citc.nce.authcenter.userLoginRecord.dao.UserLoginRecordDao;
import com.citc.nce.authcenter.userLoginRecord.entity.UserLoginRecordDo;
import com.citc.nce.authcenter.utils.AuthUtils;
import com.citc.nce.authcenter.utils.MapStringUtil;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.web.utils.UserTokenUtil;
import com.citc.nce.misc.constant.NumCode;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.citc.nce.authcenter.constant.AuthCenterError.USER_AUTH_STATUS_INVALID;


/**
 * 代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 * 代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 * 代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 * 代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 * 代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 * 代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 * 代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserService userService;
    @Resource
    LoginRecordService loginRecordService;
    /**
     * csp 得小号
     */
    @Resource
    private ICspTrumpetUserService trumpetUserService;

    @Resource
    private CaptchaService captchaService;

    @Resource
    private IdentificationService identificationService;

    @Resource
    private CaptchaService verificationService;
    @Resource
    private UserDao userDao;
    @Resource
    private UserPlatformPermissionsDao userPlatformPermissionsDao;
    @Resource
    private ApprovalLogDao approvalLogDao;
    @Resource
    private UserEnterpriseIdentificationDao userEnterpriseIdentificationDao;
    @Resource
    private UserLoginRecordDao userLoginRecordDao;
    @Resource
    private LegalAffairsService legalAffairsService;
    @Autowired
    private CspCustomerService cspCustomerService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private AdminAuthApi adminAuthApi;

    @Autowired
    private CspService cspService;

    @Autowired
    private AdminAuthService adminAuthService;
    @Autowired
    private UserMapper userMapper;


    private final String REDIS_CSP_USER_PW_UPDATE = "CSP:USER-UPDATE-PW";


    @Override
    @Transactional()
    public RegisterResp register(RegisterReq req) {
        RegisterResp resp = new RegisterResp();
        //1、检验验证码
        CaptchaCheckReq checkReq = new CaptchaCheckReq();
        checkReq.setType(CaptchaType.SMS.getCode()).setCaptchaKey(req.getCaptchaKey()).setCode(req.getCode()).setAccount(req.getPhone()).setIsAdminAuth(false);
        CaptchaCheckResp checkResp = verificationService.checkCaptcha(checkReq);
        if (checkResp != null && checkResp.getResult()) {
            //2、查看用户信息是否唯一
            checkUserInfoIsUnique(req.getName(), req.getPhone(), req.getMail());
            //3、用户注册
            UserDo userDo = saveForRegister(req);
            //4、初始化用户的认证信息
            identificationService.initializeUserIdentification(userDo.getUserId());
            BeanUtils.copyProperties(userDo, resp);
        }
        return resp;
    }

    /**
     * 保存用户---注册
     *
     * @param req 请求信息
     * @return 响应信息
     */
    @Override
    public UserDo saveForRegister(RegisterReq req) {
        String mail = req.getMail().trim();
        String name = req.getName().trim();
        String phone = req.getPhone().trim();
        String uuid = getUserId();

        UserDo userDo = new UserDo();
        userDo.setUserId(uuid);
        userDo.setDeleted(0);
        userDo.setMail(mail);
        userDo.setPhone(phone);
        userDo.setCreator(uuid);
        userDo.setUpdater(uuid);
        userDo.setCreateTime(new Date());
        userDo.setName(name);
        userDo.setEmailActivated(0);
        return userService.saveUser(userDo);
    }


    private String getUserId() {
        String uuid = AuthUtils.randomID(10);
        UserDo userDo = userService.findByUserId(uuid);
        if (userDo == null) {
            return uuid;
        } else {
            return getUserId();
        }
    }

    /**
     * 校验用户的名字和手机号是否唯一
     *
     * @param name  用户名
     * @param phone 手机号
     */
    @Override
    public Boolean checkUserInfoIsUnique(String name, String phone, String email) {
        UserDo userDo = userService.findUserByNameOrPhoneOrEmail(name, phone, email);
        if (userDo != null) {
            if (!Strings.isNullOrEmpty(name) && name.equals(userDo.getName())) {
                throw new BizException(AuthCenterError.USER_NAME_REGISTERED);
            }
            if (!Strings.isNullOrEmpty(phone) && phone.equals(userDo.getPhone())) {
                throw new BizException(AuthCenterError.PHONE_REGISTERED);
            }
            if (!Strings.isNullOrEmpty(email) && email.equals(userDo.getMail())) {
                throw new BizException(AuthCenterError.EMAIL_REGISTERED);
            }
            return false;
        }

        if (Objects.nonNull(trumpetUserService.getByAccountName(name))) {
            throw new BizException(AuthCenterError.USER_NAME_REGISTERED);
        }
        if (Objects.nonNull(trumpetUserService.getByPhone(name))) {
            throw new BizException(AuthCenterError.PHONE_REGISTERED);
        }
        return true;
    }

    @Override
    public LoginResp login(LoginReq req) {
        LoginResp resp = new LoginResp();
        UserDo userDo;
        //手机号码登录
        //1、检验验证码
        if (Boolean.FALSE.equals(verificationService.checkCaptcha(new CaptchaCheckReq().setType(CaptchaType.DYZ_SMS.getCode())
                .setCaptchaKey(req.getCaptchaKey())
                .setCode(req.getCode())
                .setAccount(req.getAccount())
                .setIsAdminAuth(false)).getResult())) {
            throw new BizException(AuthCenterError.CAPTCHA_FAILED);
        }
        //查询用户
        userDo = userService.findByPhone(req.getAccount());
        if (Objects.isNull(userDo)) {
            CspTrumpetUser account = trumpetUserService.getByPhone(req.getAccount());
            if (Objects.isNull(account)) {
                log.info("用户不存在");
                throw new BizException(AuthCenterError.ACCOUNT_USER_ABSENT);
            }
            userDo = convertCspTrumpetUser(account);
        }


        if (Objects.isNull(userDo)) {
            log.info("用户不存在");
            throw new BizException(AuthCenterError.ACCOUNT_PWD_WRONG);
        }

        //0、校验法务问题勾选
        Map<String, Integer> legalVersion = getLegalVersion(req, userDo);


        //2、组织响应报文
        makeUpLoginResponse(userDo, resp, req.getPlatformType());
        //3、用户平台权限校验
        checkUserPlatformPermission(resp);
        //4、查询用户的企业认证结果
        obtainUserEnterpriseIdent(resp);

        resp.setCertificateList(identificationService.getUserCertificateByUserId(userDo.getUserId()));

        //5、生成token
        StpUtil.login(Objects.isNull(userDo.getCtuId()) ? userDo.getUserId() : userDo.getCtuId(), req.getDeviceType());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        BaseUser baseUser = new BaseUser();
        BeanUtils.copyProperties(resp, baseUser);
        baseUser.setCspId(cspService.obtainCspId(baseUser.getUserId()));
        baseUser.setCtuId(userDo.getCtuId());//设置小号id
        baseUser.setTempStorePerm(Integer.valueOf(1).equals(userDo.getTempStorePerm()));
        StpUtil.getTokenSession().set(UserTokenUtil.SESSION_USER_KEY, baseUser);

        resp.setTempStorePerm(baseUser.getTempStorePerm());
        //删除SCP强制登录记录
        deleteSCPPWIsUpdate(resp.getUserId());
        resp.setToken(tokenInfo.getTokenValue());

        //8、用户信息写入登录记录表
        loginRecordService.insertLoginRecord(userDo, legalVersion, resp.getPlatformType());
        return resp;
    }

    /**
     * 把小号的csp账号查询出来返回
     *
     * @param account 小号
     * @return csp账号
     */
    private UserDo convertCspTrumpetUser(CspTrumpetUser account) {
        //查询csp数据
        Csp csp = cspService.getCspByCspId(account.getCspId());
        if (Objects.isNull(csp)) {
            throw new BizException(AuthCenterError.ACCOUNT_USER_ABSENT);
        }
        //查询csp是否被禁止登陆
        if (!cspService.getCspStatus(csp.getCspId())) {
            throw new BizException(AuthCenterError.ACCOUNT_IS_FORBIDDEN);
        }
        //汪鹏说自己查询csp账号  csp账号勾过协议就算数
        UserDo userDo = userService.userInfoDetailByUserId(csp.getUserId());
        if (Objects.nonNull(userDo)) {
            userDo.setCtuId(account.getCtuId());
        }
        return userDo;
    }

    private void deleteSCPPWIsUpdate(String userId) {
        if (!Strings.isNullOrEmpty(userId)) {
            Integer result = (Integer) redisTemplate.opsForValue().get(REDIS_CSP_USER_PW_UPDATE + userId);
            if (result != null && result == 1) {
                redisTemplate.delete(REDIS_CSP_USER_PW_UPDATE + userId);
            }
        }
    }


    private Map<String, Integer> getLegalVersion(LoginReq req, UserDo userDo) {
        List<LegalFileNewestResp> newestRespList = legalAffairsService.newestList();
        //最新要勾选文件信息
        Map<String, Integer> confirmMap = newestRespList.stream().filter(x -> NumCode.ONE.getCode() == x.getIsConfirm()).collect(Collectors.toMap(LegalFileNewestResp::getFileName, LegalFileNewestResp::getFileVersion));
        if (MapUtil.isEmpty(confirmMap)) {
            return null;
        }
        //判断用户协议是否勾选
        if (ObjectUtil.isNull(req.getAgreement()) || NumCode.ONE.getCode() != req.getAgreement()) {
            //获取当前用户最新登陆信息
            LambdaQueryWrapper<UserLoginRecordDo> queryWrapper = Wrappers.<UserLoginRecordDo>lambdaQuery()
                    .eq(UserLoginRecordDo::getUserId, userDo.getUserId())
                    .orderByDesc(UserLoginRecordDo::getCreateTime)
                    .last("limit 1");
            UserLoginRecordDo userLoginRecord = userLoginRecordDao.selectOne(queryWrapper);
            if (Objects.isNull(userLoginRecord) || StrUtil.isBlank(userLoginRecord.getVersionInfo())) {
                throw new BizException(AuthCenterError.USER_AGREEMENT);
            }
            Map<String, Object> recordMap = MapStringUtil.getStringToMap(userLoginRecord.getVersionInfo());
            if (recordMap.size() < confirmMap.size()) {
                throw new BizException(AuthCenterError.USER_AGREEMENT);
            }
            confirmMap.forEach((key, value) -> {
                // 登陆记录不包含需要确认项
                if (!recordMap.containsKey(key)) {
                    throw new BizException(AuthCenterError.USER_AGREEMENT);
                }
                // 登陆记录与需要确认项版本不匹配
                if (!Objects.equals(String.valueOf(recordMap.get(key)), value.toString())) {
                    throw new BizException(AuthCenterError.USER_AGREEMENT);
                }
            });
        }
        return confirmMap;
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

    /**
     * 校验用户的平台权限
     *
     * @param resp 响应信息
     */
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
     * 组装响应报文
     *
     * @param userDo       用户基本信息
     * @param resp         响应报文
     * @param platformType 平台类型
     */
    private void makeUpLoginResponse(UserDo userDo, LoginResp resp, Integer platformType) {
        BeanUtils.copyProperties(userDo, resp);
        resp.setPlatformType(platformType);
        resp.setUserName(userDo.getName());
        resp.setRegisterTime(userDo.getCreateTime());
    }

    @Override
    public void logout(LogoutReq req) {
        BaseUser user = SessionContextUtil.getUser();
        if (user.getCtuId()!=null){
            StpUtil.logout(user.getCtuId(), req.getDeviceType());
        }else{
            StpUtil.logout(SessionContextUtil.getUserId(), req.getDeviceType());
        }
    }

    @Override
    @Transactional()
    public QueryUserInfoDetailResp userInfoDetail(QueryUserInfoDetailReq req) {
        String userId = SessionContextUtil.getUser().getUserId();
        UserDo userDo = userDao.selectOne(UserDo::getUserId, userId);
        if (null == userDo) return null;
        QueryUserInfoDetailResp webUserLoginResp = new QueryUserInfoDetailResp();
        BeanUtils.copyProperties(userDo, webUserLoginResp);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        webUserLoginResp.setToken(tokenInfo.getTokenValue());
        webUserLoginResp.setUserName(userDo.getName());
        webUserLoginResp.setTempStorePerm(Integer.valueOf(1).equals(userDo.getTempStorePerm()));
        webUserLoginResp.setRegisterTime(userDo.getCreateTime());
        UserPlatformPermissionsDo userPlatformPermissionsDo = userPlatformPermissionsDao.selectOne(UserPlatformPermissionsDo::getProtal, req.getProtalType(), UserPlatformPermissionsDo::getUserId, userDo.getUserId());
        if (null != userPlatformPermissionsDo) {
            webUserLoginResp.setUserStatus(userPlatformPermissionsDo.getUserStatus());
            webUserLoginResp.setApprovalStatus(userPlatformPermissionsDo.getApprovalStatus());
            if (NumCode.TWO.getCode() == userPlatformPermissionsDo.getApprovalStatus()) {
                //审核不通过时候展示审核备注
                LambdaQueryWrapperX<ApprovalLogDo> queryWrapper = new LambdaQueryWrapperX<>();
                queryWrapper.eq(ApprovalLogDo::getApprovalLogId, userPlatformPermissionsDo.getApprovalLogId())
                        .orderByDesc(ApprovalLogDo::getHandleTime);
                List<ApprovalLogDo> approvalLogList = approvalLogDao.selectList(queryWrapper);
                if (CollectionUtils.isNotEmpty(approvalLogList))
                    webUserLoginResp.setRemark(approvalLogList.get(0).getRemark());
            }
        } else {
            webUserLoginResp.setUserStatus(0);
            webUserLoginResp.setApprovalStatus(0);
        }
        //3、获取用户标签信息
        webUserLoginResp.setCertificateList(identificationService.getUserCertificateByUserId(userDo.getUserId()));

        UserEnterpriseIdentificationDo userEnterpriseIdentification = userEnterpriseIdentificationDao.selectOne(UserEnterpriseIdentificationDo::getUserId, userId);
        webUserLoginResp.setAuditRemark(userEnterpriseIdentification == null ? "" : userEnterpriseIdentification.getAuditRemark());
        //增加用户使用权限
        String permissions = cspCustomerService.getUserPermission(userId);
        if (StringUtils.isEmpty(permissions)) {

            webUserLoginResp.setPermissions("1,2");
        } else {
            webUserLoginResp.setPermissions(permissions);
        }

        // 添加渠道
        webUserLoginResp.setChannel(adminAuthService.getCspUserChannelByUserId(userId));
        return webUserLoginResp;
    }

    @Override
    @Transactional
    public ModifyUserPhoneOrEmailResp modifyUserPhoneOrEmail(ModifyUserPhoneOrEmailReq req) {
        String userId = SessionContextUtil.getUser().getUserId();
        if (StringUtils.isNotEmpty(req.getUserId())) {
            userId = req.getUserId();
        }
        UserDo userDo = userDao.selectOne(UserDo::getUserId, userId);
        if (null == userDo) throw new BizException(AuthCenterError.ACCOUNT_USER_ABSENT);
        ensureAuthState(userDo.getAuthStatus());
        //类型：手机号修改
        if (req.getAccountType().equals(CheckUniqueTypeEnum.PHONE.getType())) {
            List<UserDo> userDos = userDao.selectList(UserDo::getPhone, req.getNewAccount());
            if ((CollectionUtils.isNotEmpty(userDos) && userDos.size() > 1) || (CollectionUtils.isNotEmpty(userDos) && userDos.size() == 1 && !userDo.getUserId().equalsIgnoreCase(userDos.get(0).getUserId()))) {
                throw new BizException(AuthCenterError.USER_PHONE_REPEAT);
            }
            userDo.setPhone(req.getNewAccount());
            userDao.updateById(userDo); //目前未分库分表 主键是自增唯一的
        }
        //类型：邮箱修改
        if (req.getAccountType().equals(CheckUniqueTypeEnum.EMAIL.getType())) {
            List<UserDo> userDos = userDao.selectList(UserDo::getMail, req.getNewAccount());
            if ((CollectionUtils.isNotEmpty(userDos) && userDos.size() > 1) || (CollectionUtils.isNotEmpty(userDos) && userDos.size() == 1 && !userDo.getUserId().equalsIgnoreCase(userDos.get(0).getUserId()))) {
                throw new BizException(AuthCenterError.USER_EMAIL_REPEAT);
            }
            userDo.setMail(req.getNewAccount());
            userDao.updateById(userDo); //目前未分库分表 主键是自增唯一的
        }

        ModifyUserPhoneOrEmailResp resp = new ModifyUserPhoneOrEmailResp();
        resp.setExecResult(true);
        return resp;
    }


    @Override
    @Transactional
    public SetUseImgOrNameResp setUserImgOrName(SetUseImgOrNameVReq req) {
        SetUseImgOrNameResp resp = new SetUseImgOrNameResp();
        String userId = req.getUserId();
        String name = req.getName();
        String userImgUuid = req.getUserImg();
        if (StrUtil.isBlank(name) && StrUtil.isBlank(userImgUuid)) {
            throw new BizException(AuthCenterError.PARAMETER_BAD);
        }
        UserDo user = userService.findByUserId(userId);
        QueryWrapper<UserDo> queryWrapper = new QueryWrapper<>();
        UpdateWrapper<UserDo> up = new UpdateWrapper<>();
        up.eq("user_id", userId);
        if (!Strings.isNullOrEmpty(name)) {
            updateUserName(queryWrapper, up, user, name);
        }
        if (!StringUtils.isEmpty(userImgUuid)) {
            up.set("user_img_uuid", userImgUuid);
        }
        userDao.update(null, up);
        resp.setExecResult(true);
        return resp;
    }

    @Transactional
    public void updateUserName(QueryWrapper<UserDo> queryWrapper, UpdateWrapper<UserDo> updateWrapper, UserDo user, String name) {
        queryWrapper.ne("user_id", user.getUserId()).eq("name", name);
        List<UserDo> userDos = userDao.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(userDos) && userDos.size() > 1)
            throw new BizException(AuthCenterError.USER_ACCOUNT_REPEAT);
        updateWrapper.set("name", name);
    }


    @Override
    public CheckRegisteredResp checkRegistered(CheckRegisteredReq req) {
        CheckRegisteredResp resp = new CheckRegisteredResp();
        Integer checkType = req.getCheckType();
        String checkValue = req.getCheckValue();
        if (StringUtils.isEmpty(CheckUniqueTypeEnum.getTitle(checkType)))
            throw new BizException(AuthCenterError.ILLEGAL_PARAM);
        //是手机校验，但是手机号格式不对
        if (CheckUniqueTypeEnum.PHONE.getType() == checkType && !Validator.isMobile(checkValue))
            throw new BizException(AuthCenterError.PHONE_FORMAT_ERROR);
        // 如果是添加客户时
        if (Integer.valueOf(1).equals(req.getCaseType())) {
            resp.setResult(!cspCustomerService.checkUnique(req.getCheckValue(), req.getCheckType()));
        } else {
            resp.setResult(!checkUnique(req.getCheckValue(), req.getCheckType(), req.getUserId()));
        }
        return resp;
    }

    @Override
    public CheckBindEmailResp checkBindEmail() {
        String userId = SessionContextUtil.getUser().getUserId();
        CheckBindEmailResp resp = new CheckBindEmailResp();
        QueryWrapper<UserDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("user_id", userId);
        UserDo userDo = userDao.selectOne(queryWrapper);
        resp.setResult(userDo != null && StringUtils.isNotBlank(userDo.getMail()));
        return resp;
    }

    @Override
    public CheckLoadNameResp checkLoadNameExist(CheckLoadNameReq req) {
        CheckLoadNameResp resp = new CheckLoadNameResp();
        String value = req.getCheckValue();
        List<UserInfoVo> userInfoVos = cspCustomerService.checkLoadNameExist(value);
        resp.setResult(CollectionUtils.isNotEmpty(userInfoVos) && !userInfoVos.isEmpty());
        return resp;
    }

    @Override
    public ActivateEmailResp activateEmail(ActivateEmailReq req) {
        captchaService.checkCaptcha(req.getCaptchaCheckReq());
        String userId = req.getUserId();
        BaseUser baseUser = SessionContextUtil.getUser();
        if (baseUser != null) {
            userId = baseUser.getUserId();
        }
        log.info("----userId----:" + userId);
        LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(UserDo::getUserId, userId);
        UserDo userDo = userDao.selectOne(queryWrapper);
        if (null == userDo) throw new BizException(AuthCenterError.ACCOUNT_USER_ABSENT);
        LambdaQueryWrapperX<UserDo> emailWrapper = new LambdaQueryWrapperX<>();
        emailWrapper.eq(UserDo::getMail, req.getMail());
        List<UserDo> userList = userDao.selectList(emailWrapper);
        if (CollectionUtils.isNotEmpty(userList) && userList.size() > 1)
            throw new BizException(AuthCenterError.USER_EMAIL_REPEAT);
        userDo.setEmailActivated(NumCode.ONE.getCode())
                .setMail(req.getMail());
        LambdaUpdateWrapper<UserDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserDo::getUserId, userId);
        if (NumCode.ONE.getCode() != userDao.update(userDo, updateWrapper))
            throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
        ActivateEmailResp resp = new ActivateEmailResp();
        resp.setResult(true);
        return resp;
    }

    @Override
    public CheckUserInfoResp checkUserInfo(CheckUserInfoReq req) {
        CheckUserInfoResp resp = new CheckUserInfoResp();
        LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(UserDo::getUserId, SessionContextUtil.getUser().getUserId())
                .and(i -> i.eq(UserDo::getMail, req.getCheckValue()).or().eq(UserDo::getPhone, req.getCheckValue()));
        resp.setResult(0 != userDao.selectCount(queryWrapper));
        return resp;
    }

    @Override
    public QueryMerchantInfoResp queryMerchantInfo(QueryMerchantInfoReq req) {
        QueryMerchantInfoResp resp = new QueryMerchantInfoResp();
        String userId = SessionContextUtil.getUser().getUserId();
        if (!Strings.isNullOrEmpty(userId)) {
            //获取商户的基本信息
            MerchantInfo merchantInfo = getMerchantInfo(userId);

            resp.setMerchantInfo(merchantInfo);
            //resp.setCommonResult(new CommonResult(true, "交互成功！"));
        } else {
            throw BizException.build(AuthCenterError.ACCOUNT_USER_ABSENT);
        }
        return resp;
    }

    @Override
    public UpdateMerchantInfoResp updateMerchantInfo(UpdateMerchantInfoReq req) {
        UpdateMerchantInfoResp resp = new UpdateMerchantInfoResp();
        String userId = SessionContextUtil.getUser().getUserId();
        String spTel = req.getSpTel();
        String spEmail = req.getSpEmail();
        String spLogo = req.getSpLogo();
        if (!Strings.isNullOrEmpty(userId)) {
            if (!Strings.isNullOrEmpty(spTel) && !Strings.isNullOrEmpty(spEmail)) {
                UserDo userDo = new UserDo();
                userDo.setSpTel(spTel);
                userDo.setSpEmail(spEmail);
                userDo.setSpLogo(spLogo);
                //更新用户基本信息
                userService.updateUserInfo(userId, userDo);
                MerchantInfo merchantInfo = getMerchantInfo(userId);
                resp.setMerchantInfo(merchantInfo);
            } else {
                throw BizException.build(AuthCenterError.MERCHANT_INFO_IS_NULL);
            }
        } else {
            throw BizException.build(AuthCenterError.ACCOUNT_USER_ABSENT);
        }
        return resp;
    }


    @Override
    public ApplyPlatformPermissionResp applyPlatformPermission(ApplyPlatformPermissionReq req) {
        ApplyPlatformPermissionResp resp = new ApplyPlatformPermissionResp();
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
        resp.setResult(true);
        return resp;

    }

    /**
     * 根据商户UserID查询商户的基本信息
     *
     * @param userId 商户userId
     * @return 商户基本信息
     */
    private MerchantInfo getMerchantInfo(String userId) {
        MerchantInfo merchantInfo = new MerchantInfo();
        if (!Strings.isNullOrEmpty(userId)) {
            //1、查询商户的基本信息
            UserDo userDo = userService.userInfoDetailByUserId(userId);
            WebEnterpriseIdentificationResp userEnterpriseIdentificationInfo = identificationService.getIdentificationInfo(userId);
            //2、查询商户的资历信息
            List<CertificateOptionsDo> userCertificateOptionsDoList = identificationService.queryUserCertificateByUserId(userId);
            List<CertificateInfo> certificateInfoList = new ArrayList<>();
            if (!userCertificateOptionsDoList.isEmpty()) {
                //3、查询认证名称
                List<UserCertificateDo> userCertificateDoList = identificationService.getUserCertificate();
                Map<Long, String> certificateNameMap = userCertificateDoList.stream().collect(Collectors.toMap(UserCertificateDo::getId, UserCertificateDo::getCertificateName));
                CertificateInfo certificateInfo;
                for (CertificateOptionsDo item : userCertificateOptionsDoList) {
                    certificateInfo = new CertificateInfo();
                    certificateInfo.setCertificateId(item.getCertificateId());
                    certificateInfo.setCertificateName(certificateNameMap.get(Long.valueOf(item.getCertificateId() + "")));//优化
                    certificateInfo.setCertificateApplyStatus(item.getCertificateApplyStatus());
                    certificateInfoList.add(certificateInfo);
                }
            }
            if (userDo != null) {
                merchantInfo.setName(userEnterpriseIdentificationInfo.getEnterpriseName());
                merchantInfo.setSpTel(userDo.getSpTel());
                merchantInfo.setSpEmail(userDo.getSpEmail());
                merchantInfo.setSpLogo(userDo.getSpLogo());
                merchantInfo.setEnterpriseAuthStatus(userDo.getEnterpriseAuthStatus());
            }
            merchantInfo.setUserId(userId);
            merchantInfo.setCertificateInfoList(certificateInfoList);
        }
        return merchantInfo;

    }

    private Boolean checkUnique(String value, Integer type, String userId) {
        //1.检查用户表账户名称是否重复
        QueryWrapper<UserDo> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userId)) {
            queryWrapper.ne("user_id", userId);
        }
        switch (type) {
            case 1:
                queryWrapper.eq(CheckUniqueTypeEnum.EMAIL.getTitle(), value);
                break;
            case 2:
                queryWrapper.and(query -> query.eq(CheckUniqueTypeEnum.NAME.getTitle(), value).or().eq(CheckUniqueTypeEnum.PHONE.getTitle(), value));
                break;
            case 3:
                queryWrapper.eq(CheckUniqueTypeEnum.PHONE.getTitle(), value);
                break;
            case 4:
                queryWrapper.eq(CheckUniqueTypeEnum.MAIL.getTitle(), value).eq("email_activated", NumCode.ONE.getCode());
                break;
            default:
                return false;
        }
        Long userCount = userDao.selectCount(queryWrapper);
        if (userCount > 0) {
            return false;
        }

        if (StringUtils.isNotBlank(userId)) {
            //2.检查企业用户表账户名称是否重复
            QueryWrapper<UserEnterpriseIdentificationDo> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("enterprise_account_name", value);
            queryWrapper1.ne("user_id", userId);
            Long enterpriseCount = userEnterpriseIdentificationDao.selectCount(queryWrapper1);
            if (enterpriseCount > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkCspUserPWIsUpdate(String userId) {
        if (!Strings.isNullOrEmpty(userId)) {
            Integer result = (Integer) redisTemplate.opsForValue().get(REDIS_CSP_USER_PW_UPDATE + userId);
            if (result != null && result == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateCspLoginStatus(String userId) {
        if (!Strings.isNullOrEmpty(userId)) {
            redisTemplate.opsForValue().set(REDIS_CSP_USER_PW_UPDATE + userId, 1, 24, TimeUnit.HOURS);
        }
    }

    @Override
    @Transactional
    public ModifyUserPhoneOrEmailResp modifyUserPhoneOrEmailV(ModifyUserPhoneOrEmailVReq req) {
        ModifyUserPhoneOrEmailResp resp = new ModifyUserPhoneOrEmailResp();
        CaptchaCheckResp checkCaptcha = captchaService.checkCaptcha(req.getCaptchaInfo());
        resp.setCheckResult(checkCaptcha.getResult());
        if (checkCaptcha.getResult() != null && checkCaptcha.getResult()) {
            ModifyUserPhoneOrEmailReq reqNew = new ModifyUserPhoneOrEmailReq();
            BeanUtils.copyProperties(req, reqNew);
            reqNew.setNewAccount(req.getCaptchaInfo().getAccount());
            resp = modifyUserPhoneOrEmail(reqNew);
        }
        return resp;
    }


    @Override
    public UserInfoDetailResp userInfoDetailByUserId(String userId) {
        UserDo userDo = userDao.selectOne(UserDo::getUserId, userId);
        if (null == userDo)
            return null;
        UserInfoDetailResp resp = new UserInfoDetailResp();
        BeanUtils.copyProperties(userDo, resp);
        List<StatusAndProtalResp> userStatusList = getUserStatusAndProtal(userId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        resp.setRegisterTime(userDo.getCreateTime())
                .setUserStatusList(userStatusList)
                .setToken(tokenInfo.getTokenValue());

        QueryWrapper<UserEnterpriseIdentificationDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", NumCode.ZERO.getCode()).eq("user_id", userId);
        UserEnterpriseIdentificationDo userEnterpriseIdentificationDo = userEnterpriseIdentificationDao.selectOne(queryWrapper);
        resp.setEnterpriseName(userEnterpriseIdentificationDo.getEnterpriseName());
        return resp;
    }

    private List<StatusAndProtalResp> getUserStatusAndProtal(String userId) {
        LambdaQueryWrapperX<UserPlatformPermissionsDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(UserPlatformPermissionsDo::getUserId, userId);
        List<UserPlatformPermissionsDo> list = userPlatformPermissionsDao.selectList(queryWrapper);
        return list.stream().map(e -> JSON.parseObject(JSONObject.toJSONString(e)).toJavaObject(StatusAndProtalResp.class)).collect(Collectors.toList());
    }

    @Override
    public void recordUnRuleNum(UserDetailReq req) {
        QueryWrapper<UserDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", req.getId());
        queryWrapper.eq("deleted", NumCode.ZERO.getCode());
        UserDo exist = userDao.selectOne(queryWrapper);

        if (exist != null) {
            UserDo userDo = new UserDo();
            userDo.setId(exist.getId());
            userDo.setUnruleNum(exist.getUnruleNum() + 1);
            userDao.updateById(userDo);
        }

        UpdateUserViolationReq updateUserViolationReq = new UpdateUserViolationReq();
        updateUserViolationReq.setUserId(req.getId());
        updateUserViolationReq.setViolationType(0);
        updateUserViolationReq.setPlate(1);
        updateUserViolationReq.setIsCancel(false);
        adminAuthApi.updateUserViolation(updateUserViolationReq);
    }

    @Override
    public UserInfo getUserBaseInfoByUserId(String userId) {
        UserInfo userInfo = null;
        if (!Strings.isNullOrEmpty(userId)) {
            userInfo = new UserInfo();
            UserDo userDo = userDao.selectOne(UserDo::getUserId, userId);
            if (userDo != null) {
                BeanUtils.copyProperties(userDo, userInfo);
            }
        }
        return userInfo;
    }

    @Override
    public List<UserInfo> getUserBaseInfoByUserIds(GetUsersInfoReq req) {
        List<UserInfo> userInfos = null;
        List<String> userIds = req.getUserIds();
        if (userIds != null && !userIds.isEmpty()) {
            LambdaQueryWrapperX<UserDo> apiMainQueryWrapper = new LambdaQueryWrapperX<>();
            apiMainQueryWrapper.in(UserDo::getUserId, userIds);
            List<UserDo> userDos = userDao.selectList(apiMainQueryWrapper);
            if (userDos != null && !userDos.isEmpty()) {
                userInfos = new ArrayList<>();
                UserInfo userInfo;
                for (UserDo item : userDos) {
                    userInfo = new UserInfo();
                    BeanUtils.copyProperties(item, userInfo);
                    userInfos.add(userInfo);
                }
            }
        }
        return userInfos;
    }

    @Override
    public List<UserInfo> queryUserInfoByKey(String key) {
        List<UserInfo> userInfos = new ArrayList<>();
        if (!Strings.isNullOrEmpty(key)) {
            LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.likeIfPresent(UserDo::getName, key);
            queryWrapper.eq(UserDo::getDeleted, 0);
            List<UserDo> userDos = userDao.selectList(queryWrapper);
            if (userDos != null && !userDos.isEmpty()) {
                UserInfo userInfo;
                for (UserDo item : userDos) {
                    userInfo = new UserInfo();
                    BeanUtils.copyProperties(item, userInfo);
                    userInfos.add(userInfo);
                }
            }
        }
        return userInfos;
    }

    @Override
    public List<UserResp> getUserByName(String name) {
        QueryWrapper<UserDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", name);
        queryWrapper.eq("deleted", NumCode.ZERO.getCode());
        List<UserDo> userDoList = userDao.selectList(queryWrapper);
        List<UserResp> userRespList = new ArrayList<>();
        userDoList.forEach(item -> {
            UserResp userResp = new UserResp();
            BeanUtils.copyProperties(item, userResp);
            userRespList.add(userResp);
        });
        return userRespList;
    }

    @Override
    public List<UserInfo> getEnterpriseInfoByUserIds(GetUsersInfoReq req) {
        List<String> userIds = req.getUserIds();
        LambdaQueryWrapperX<UserEnterpriseIdentificationDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(UserEnterpriseIdentificationDo::getDeleted, 0);
        if (userIds != null && !userIds.isEmpty()) {
            queryWrapper.in(UserEnterpriseIdentificationDo::getUserId, userIds);
        }
        List<UserEnterpriseIdentificationDo> enterpriseIdentificationDos = userEnterpriseIdentificationDao.selectList(queryWrapper);
        List<UserInfo> userInfos = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(enterpriseIdentificationDos)) {
            UserInfo userInfo;
            for (UserEnterpriseIdentificationDo item : enterpriseIdentificationDos) {
                userInfo = new UserInfo();
                userInfo.setUserId(item.getUserId());
                userInfo.setEnterpriseName(item.getEnterpriseName());
                userInfos.add(userInfo);
            }
        }

        return userInfos;
    }

    @Override
    public int checkIsAgreement(UserDo userDo) {
        List<LegalFileNewestResp> newestRespList = legalAffairsService.newestList();
        //最新要勾选文件信息
        Map<String, Integer> confirmMap = newestRespList.stream().filter(x -> NumCode.ONE.getCode() == x.getIsConfirm()).collect(Collectors.toMap(LegalFileNewestResp::getFileName, LegalFileNewestResp::getFileVersion));
        if (MapUtil.isEmpty(confirmMap)) {
            return 0;
        }
        //获取当前用户最新登陆信息
        LambdaQueryWrapper<UserLoginRecordDo> queryWrapper = Wrappers.<UserLoginRecordDo>lambdaQuery()
                .eq(UserLoginRecordDo::getUserId, userDo.getUserId())
                .orderByDesc(UserLoginRecordDo::getCreateTime)
                .last("limit 1");
        UserLoginRecordDo userLoginRecord = userLoginRecordDao.selectOne(queryWrapper);
        if (Objects.isNull(userLoginRecord) || StrUtil.isBlank(userLoginRecord.getVersionInfo())) {
            return 1;
        }
        Map<String, Object> recordMap = MapStringUtil.getStringToMap(userLoginRecord.getVersionInfo());
        if (recordMap.size() < confirmMap.size()) {
            return 1;
        }

        for (Map.Entry<String, Integer> entry : confirmMap.entrySet()) {
            if (!recordMap.containsKey(entry.getKey())) {// 登陆记录不包含需要确认项
                return 1;
            }
            if (!Objects.equals(String.valueOf(recordMap.get(entry.getKey())), entry.getValue().toString())) {// 登陆记录与需要确认项版本不匹配
                return 1;
            }
        }
        return 0;
    }

    @Override
    public Boolean inUserDB(String userId) {
        UserDo userDo = userService.findByUserId(userId);
        return userDo != null;
    }

    private static void ensureAuthState(Integer state) {
        if (!Objects.equals(state, NumCode.THREE.getCode()))
            throw new BizException(USER_AUTH_STATUS_INVALID);
    }
}
