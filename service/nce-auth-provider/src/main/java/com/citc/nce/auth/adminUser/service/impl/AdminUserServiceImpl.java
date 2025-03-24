package com.citc.nce.auth.adminUser.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.citc.nce.auth.adminUser.dao.*;
import com.citc.nce.auth.adminUser.entity.*;
import com.citc.nce.auth.adminUser.service.AdminUserService;
import com.citc.nce.auth.adminUser.vo.req.*;
import com.citc.nce.auth.adminUser.vo.resp.*;
import com.citc.nce.auth.certificate.dao.UserCertificateOptionsDao;
import com.citc.nce.auth.certificate.entity.UserCertificateOptionsDo;
import com.citc.nce.auth.certificate.service.CertificateOptionsService;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.constant.CountNum;
import com.citc.nce.auth.identification.dao.IdentificationAuditRecordDao;
import com.citc.nce.auth.identification.dao.UserEnterpriseIdentificationDao;
import com.citc.nce.auth.identification.dao.UserPersonIdentificationDao;
import com.citc.nce.auth.identification.entity.IdentificationAuditRecordDo;
import com.citc.nce.auth.identification.entity.UserEnterpriseIdentificationDo;
import com.citc.nce.auth.identification.entity.UserPersonIdentificationDo;
import com.citc.nce.auth.user.dao.ApprovalLogDao;
import com.citc.nce.auth.user.dao.UserDao;
import com.citc.nce.auth.user.dao.UserMapper;
import com.citc.nce.auth.user.dao.UserPlatformPermissionsDao;
import com.citc.nce.auth.user.entity.ApprovalLogDo;
import com.citc.nce.auth.user.entity.UserDo;
import com.citc.nce.auth.user.entity.UserPlatformPermissionsDo;
import com.citc.nce.auth.user.service.UserService;
import com.citc.nce.auth.user.vo.req.*;
import com.citc.nce.auth.user.vo.resp.UserIdAndNameResp;
import com.citc.nce.auth.user.vo.resp.UserInfoResp;
import com.citc.nce.auth.user.vo.resp.UserResp;
import com.citc.nce.auth.usermessage.service.IUserMsgDetailService;
import com.citc.nce.auth.usermessage.vo.req.MsgReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.web.utils.UserTokenUtil;
import com.citc.nce.misc.constant.*;
import com.citc.nce.misc.msg.req.MsgTemplateReq;
import com.citc.nce.misc.record.ProcessingRecordApi;
import com.citc.nce.misc.record.req.BusinessIdsReq;
import com.citc.nce.misc.record.req.ProcessingRecordReq;
import com.citc.nce.misc.record.resp.ProcessingRecordResp;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {
    @Resource
    private AdminUserDao adminUserDao;
    @Resource
    private UserDao userDao;
    @Resource
    private UserCertificateOptionsDao userCertificateOptionsDao;
    @Resource
    private UserPersonIdentificationDao userPersonIdentificationDao;
    @Resource
    private UserEnterpriseIdentificationDao userEnterpriseIdentificationDao;
    @Resource
    private IdentificationAuditRecordDao identificationAuditRecordDao;
    @Resource
    private UserService userService;
    @Resource
    private CertificateOptionsService certificateOptionsService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ApprovalLogDao approvalLogDao;
    @Resource
    private UserPlatformPermissionsDao userPlatformPermissionsDao;
    @Resource
    private IUserMsgDetailService iUserMsgDetailService;
    @Resource
    private AdminRoleDao adminRoleDao;
    @Resource
    private AdminUserMapper adminUserMapper;
    @Resource
    private AdminUserRoleDao adminUserRoleDao;
    @Resource
    private AdminRoleMenuDao adminRoleMenuDao;
    @Resource
    private AdminMenuDao adminMenuDao;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private ProcessingRecordApi processingRecordApi;


    /**
     * admin 端修改 client端用户信息
     *
     * @param req
     */
    @Override
    @Transactional
    public void updateClientUserInfo(UpdateClientUserReq req) {
        String userId = req.getUserId();
        String accountName = req.getAccountName();
        String email = req.getEmail();
        String phone = req.getPhone();
        UserDo userDo = null;
        UserDo user = userDao.selectOne("user_id", userId);
        if (StringUtils.isNotBlank(accountName)) {
            //设置时，对phone email  账号名 进行是否重复校验
            QueryWrapper<UserDo> nameCheckwrapper = new QueryWrapper<>();
            nameCheckwrapper.ne("user_id", userId).eq("name", accountName);
            userDo = userDao.selectOne(nameCheckwrapper);
            if (null != userDo)
                throw new BizException(AuthError.USER_ACCOUNT_REPEAT);
            user.setName(accountName);
        }

        if (StringUtils.isNotBlank(email)) {
            QueryWrapper<UserDo> emailCheckwrapper = new QueryWrapper<>();
            emailCheckwrapper.ne("user_id", userId).eq("mail", email);
            userDo = userDao.selectOne(emailCheckwrapper);
            if (null != userDo)
                throw new BizException(AuthError.USER_EMAIL_REPEAT);
            user.setMail(email).setEmailActivated(NumCode.ONE.getCode());
        }


        if (StringUtils.isNotBlank(phone)) {
            QueryWrapper<UserDo> phoneCheckwrapper = new QueryWrapper<>();
            phoneCheckwrapper.ne("user_id", userId).eq("phone", phone);
            userDo = userDao.selectOne(phoneCheckwrapper);
            if (null != userDo)
                throw new BizException(AuthError.USER_PHONE_REPEAT);
            user.setPhone(phone);
        }
        if (StringUtils.isNotBlank(req.getUserImgUuid()))
            user.setUserImgUuid(req.getUserImgUuid());
        userDao.updateById(user);

        //添加处理记录
        addProcessingRecord(userId,ProcessingContentEnum.BJYHXX.getName(),BusinessTypeEnum.YHGL_TY.getCode());
    }


    /**
     * Admin端 查看client端 userId 用户认证信息
     */
    @Override
    public ClientUserIdentificationResp getClientUserIdentifications(ClientUserIdentificationReq req) {
        LambdaQueryWrapperX<UserPersonIdentificationDo> personWrapper = new LambdaQueryWrapperX<>();
        personWrapper.eq(UserPersonIdentificationDo::getUserId, req.getUserId());
        UserPersonIdentificationDo personIdentificationDo = userPersonIdentificationDao.selectOne(personWrapper);
        //个人认证
        UserPersonIdentificationResp person = new UserPersonIdentificationResp();
        if (null != personIdentificationDo) {
            BeanUtils.copyProperties(personIdentificationDo, person);
            person.setCertificateId(QualificationType.REAL_NAME_USER.getCode());
        }
        //企业认证
        LambdaQueryWrapperX<UserEnterpriseIdentificationDo> enterpriseWrapper = new LambdaQueryWrapperX<>();
        enterpriseWrapper.eq(UserEnterpriseIdentificationDo::getUserId, req.getUserId());
        UserEnterpriseIdentificationDo enterpriseIdentificationDo = userEnterpriseIdentificationDao.selectOne(enterpriseWrapper);
        UserEnterpriseIdentificationResp enterprise = new UserEnterpriseIdentificationResp();
        if (null != enterpriseIdentificationDo) {
            BeanUtils.copyProperties(enterpriseIdentificationDo, enterprise);
            String zone = new StringBuilder()
                    .append(enterpriseIdentificationDo.getProvince()).append("-")
                    .append(enterpriseIdentificationDo.getCity()).append("-")
                    .append(enterpriseIdentificationDo.getArea()).toString();
            enterprise.setZone(zone).setCertificateId(QualificationType.BUSINESS_USER.getCode());
        }
        return new ClientUserIdentificationResp().setEnterpriseIdentificatio(enterprise).setPersonIdentificationDo(person);
    }


    /**
     * 管理端  审核用户认证
     *
     * @param req
     */
    @Override
    @Transactional()
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
            if(!req.getAuthStatus().equals(NumCode.THREE.getCode())){
                processingContent = ProcessingContentEnum.SMRZSHWTG.getName();
            }
            addProcessingRecord2(req.getClientUserId(),processingContent,BusinessTypeEnum.YHGL_TY.getCode(),req.getAuditRemark());
        }
        //审核企业
        if (NumCode.TWO.getCode() == req.getFlag()) {
            if (!String.valueOf(QualificationType.BUSINESS_USER.getCode()).equals(String.valueOf(req.getIdentificationId()))) {
                throw new BizException(AuthError.AUDIT_AND_QUALIFICATION_TRANSFER);
            }
            UserEnterpriseIdentificationDo enterpriseDo = new UserEnterpriseIdentificationDo()
                    .setAuditRemark(req.getAuditRemark())
                    .setEnterpriseAuthStatus(req.getAuthStatus())
                    .setEnterpriseAuthTime(new Date())
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
                            .setPersonAuthTime(new Date())
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
            if(!req.getAuthStatus().equals(NumCode.THREE.getCode())){
                processingContent = ProcessingContentEnum.QYRZSHWTG.getName();
            }
            addProcessingRecord2(req.getClientUserId(),processingContent,BusinessTypeEnum.YHGL_TY.getCode(),req.getAuditRemark());
        }
        //刷新用户状态
        userService.updateUserAuthStatus(req.getClientUserId());
    }

    @Override
    public Boolean checkLoadNameExist(String value) {
        LambdaQueryWrapperX<AdminUserDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(AdminUserDo::getAccountName, value).or().eq(AdminUserDo::getPhone, value);
        return null != adminUserDao.selectOne(queryWrapper);
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
        iUserMsgDetailService.addMsgIntoUser(JSON.parseObject(JSONObject.toJSONString(msgTemplateReq), MsgReq.class).setRemark(req.getAuditRemark()).setUserId(req.getClientUserId()));
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

    /**
     * 管理端dashboard用户统计
     *
     * @return DashboardUserStatisticsResp
     */
    @Override
    public DashboardUserStatisticsResp getDashboardUserStatistics() {
        //查询用户认证数据
        LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(UserDo::getAuthStatus, NumCode.ONE.getCode());
        Long unauthCount = userDao.selectCount(queryWrapper);
        //待处理的用户标签申请个数
        CountNum pendingReviewNum = certificateOptionsService.getPendingReviewNum();
        return new DashboardUserStatisticsResp().setUnauthCount(unauthCount).setUnAuditCount(pendingReviewNum.getNum());
    }


    /**
     * 后台管理用户登陆
     *
     * @param loginReq
     * @return AdminUserLoginResp
     */
    @Override
    @Transactional()
    public AdminUserLoginResp adminUserWebLogin(AdminLoginReq loginReq) {
        AdminUserDo adminUserDo = adminUserDao.selectOne(AdminUserDo::getPhone, loginReq.getPhone());
        if (null == adminUserDo) throw new BizException(AuthError.ACCOUNT_USER_ABSENT);
        if (NumCode.ONE.getCode() != adminUserDo.getUserStatus()) throw new BizException(AuthError.HAVE_NO_PERMISSION);
        StpUtil.login(adminUserDo.getUserId());
        AdminUserLoginResp resp = new AdminUserLoginResp();
        BeanUtils.copyProperties(adminUserDo, resp);
        resp.setUserName(adminUserDo.getFullName());
        //查询用户角色code
        List<AdminMenuDo> data = adminUserMapper.selectMenuByUserId(adminUserDo.getUserId());
        Map<String, List<String>> url = userMenu(data);
        resp.setUrl(url);
        //根据userId查询用户角色信息
        List<String> roleList = adminUserMapper.selectUserRoleByUserId(adminUserDo.getUserId());
        resp.setRoleList(roleList);
        redisTemplate.opsForValue().set("USER_ID:" + adminUserDo.getUserId(), url);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        resp.setToken(tokenInfo.getTokenValue());
        StpUtil.getTokenSession().set(UserTokenUtil.SESSION_USER_KEY, resp);
        return resp;
    }

    public Map<String, List<String>> userMenu(List<AdminMenuDo> data) {
        if (CollectionUtils.isEmpty(data)) return null;
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        List<String> systemList = data.stream().filter(menu -> NumCode.ZERO.getCode() == menu.getLevel()).map(i -> i.getMenuCode()).collect(Collectors.toList());
        systemList.forEach(i -> {
            List<String> list = new ArrayList<>();
            result.put(i, getUrl(data, i, list));
        });
        return result;
    }


    public List<String> getUrl(List<AdminMenuDo> ms, String code, List<String> urlList) {
        ms.forEach(i -> {
            if (code.equalsIgnoreCase(i.getMenuParentCode())) {
                urlList.add(i.getMenuUrl());
                getUrl(ms, i.getMenuCode(), urlList);
            }
        });
        return urlList;
    }

    /**
     * 通过账号查找用户信息，账号可能为用户名  or  手机号
     *
     * @param account
     * @return
     */
    @Override
    public AdminUserInfoResp getAdminUserInfoByAccount(String account) {
        QueryWrapper<AdminUserDo> queryWrapper = new QueryWrapper();
        queryWrapper.eq("deleted", NumCode.ZERO.getCode());
        queryWrapper.and(i -> i.eq("account_name", account).or().eq("phone", account));
        AdminUserDo adminUserDo = adminUserDao.selectOne(queryWrapper);
        if (null == adminUserDo)
            throw new BizException(AuthError.ACCOUNT_USER_ABSENT);
        AdminUserInfoResp userInfoResp = new AdminUserInfoResp();
        BeanUtils.copyProperties(adminUserDo, userInfoResp);
        return userInfoResp;

    }

    @Override
    public PageResult getManageUser(ManageUserReq req) {
        if (NumCode.ZERO.getCode() == req.getProtal()) {//查询统一用户管理
            req.setPageNo((req.getPageNo() - 1) * req.getPageSize());
            List<UserResp> data = userMapper.getUniteManageUserList(req);
            Long num = userMapper.getUniteManageUserCount(req);

            //判断是否有处理记录
            List<String> userIds = new ArrayList<>();
            data.forEach(item->{
                userIds.add(item.getUserId());
            });
            if(CollectionUtils.isNotEmpty(userIds)){
                BusinessIdsReq businessIdsReq = new BusinessIdsReq();
                businessIdsReq.setBusinessIds(userIds);
                businessIdsReq.setBusinessType(0);
                List<ProcessingRecordResp> processingRecordRespList = processingRecordApi.findProcessingRecordListByIds(businessIdsReq);

                for (UserResp userResp: data){
                    for (ProcessingRecordResp processingRecordResp: processingRecordRespList){
                        if(userResp.getUserId().equals(processingRecordResp.getBusinessId())){
                            userResp.setHasRecords(true);
                        }
                    }
                }
            }

            PageResult<UserResp> pageResult = new PageResult<>();
            pageResult.setList(data);
            pageResult.setTotal(num);
            return pageResult;
        } else if (NumCode.ONE.getCode() == req.getProtal()) {//查询核能商城用户管理
            UserPageListDBVO userPageListDBVO = new UserPageListDBVO();
            BeanUtils.copyProperties(req, userPageListDBVO);
            List<Integer> unruleNums = req.getUnruleNums();
            List<Integer> list = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(unruleNums)) {
                if (unruleNums.contains(0)) { //0 ---0次
                    list.add(0);
                }
                if (unruleNums.contains(1)) {// 1--- 1~3次
                    list.addAll(Arrays.asList(1, 2, 3));
                }
                if (unruleNums.contains(2)) {// 2--- 4~10次
                    list.addAll(Arrays.asList(4, 5, 6, 7, 8, 9, 10));
                }
                if (unruleNums.contains(3)) {// 3--- 10次以上
                    userPageListDBVO.setTenFlag(1);
                }
            }
            userPageListDBVO.setPageNo((req.getPageNo() - 1) * req.getPageSize());
            userPageListDBVO.setUnruleNums(null);
            //前端传全部资质的时候list里面为空得处理
            if (CollectionUtils.isNotEmpty(userPageListDBVO.getUserCertificate()) && userPageListDBVO.getUserCertificate().contains(null))
                userPageListDBVO.setUserCertificate(null);
            if (CollectionUtils.isNotEmpty(list))
                userPageListDBVO.setUnruleNums(list);
            List<UserPageListResp> data = userMapper.getEnergyMallUserList(userPageListDBVO);

            //判断是否有处理记录
            List<String> userIds = new ArrayList<>();
            data.forEach(item->{
                userIds.add(item.getUserId());
            });
            if(CollectionUtils.isNotEmpty(userIds)){
                BusinessIdsReq businessIdsReq = new BusinessIdsReq();
                businessIdsReq.setBusinessIds(userIds);
                businessIdsReq.setBusinessType(1);
                List<ProcessingRecordResp> processingRecordRespList = processingRecordApi.findProcessingRecordListByIds(businessIdsReq);

                for (UserPageListResp userPageListResp: data){
                    for (ProcessingRecordResp processingRecordResp: processingRecordRespList){
                        if(userPageListResp.getUserId().equals(processingRecordResp.getBusinessId())){
                            userPageListResp.setHasRecords(true);
                        }
                    }
                }
            }



            PageResult<UserPageListResp> pageResult = new PageResult<>();
            if (req.getIsNotExport()) {
                Long num = userMapper.getEnergyMallUserCount(userPageListDBVO);
                pageResult.setTotal(num);
            }
            pageResult.setList(data);
            return pageResult;
        } else if (NumCode.TWO.getCode() == req.getProtal() || NumCode.THREE.getCode() == req.getProtal()) {
            //查询chatbot或硬核桃用户管理
            req.setPageNo((req.getPageNo() - 1) * req.getPageSize());
            List<UserResp> data = userMapper.getChatBotAndHardWalnutsUserList(req);
            Long num = userMapper.getChatBotAndHardWalnutsUserCount(req);
            PageResult<UserResp> pageResult = new PageResult<>();
            pageResult.setList(data);
            pageResult.setTotal(num);
            return pageResult;
        } else {
            throw new BizException(AuthError.PARAMETER_BAD);
        }
    }

    @Override
    public void enableOrDisableUser(EnableOrDisableReq req) {
        UserPlatformPermissionsDo permission = new UserPlatformPermissionsDo();
        BeanUtils.copyProperties(req, permission);
        LambdaUpdateWrapper<UserPlatformPermissionsDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserPlatformPermissionsDo::getUserId, req.getUserId())
                .eq(UserPlatformPermissionsDo::getProtal, req.getProtal());
        if (NumCode.ONE.getCode() != userPlatformPermissionsDao.update(permission, updateWrapper))
            throw new BizException(AuthError.Execute_SQL_UPDATE);

        //添加处理记录
        String processingContent = ProcessingContentEnum.JYYH.getName();
        if(req.getUserStatus().equals(NumCode.ONE.getCode())){
            processingContent = ProcessingContentEnum.QYYH.getName();
        }
        addProcessingRecord(req.getUserId(),processingContent,BusinessTypeEnum.YHGL_HN.getCode());
    }

    @Override
    public List<UserIdAndNameResp> getUserInfo(UserIdReq req) {
        return userMapper.selectUserInfoByUserId(req);
    }

    @Override
    public List<UserInfoResp> getUserInfoList(KeyWordReq req) {
        LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.likeIfPresent(UserDo::getName, req.getKeyWord());
        List<UserDo> list = userDao.selectList(queryWrapper);
        List<UserInfoResp> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            resultList = list.stream().map(l -> {
                UserInfoResp resp = new UserInfoResp();
                BeanUtils.copyProperties(l, resp);
                return resp;
            }).collect(Collectors.toList());
        }
        return resultList;
    }

    @Override
    public Map<String, UserInfoResp> getUserInfoByUserId(CodeListReq req) {
        LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.inIfPresent(UserDo::getUserId, req.getCodeList());
        List<UserDo> list = userDao.selectList(queryWrapper);
        Map<String, UserInfoResp> resultMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(l -> {
                UserInfoResp resp = new UserInfoResp();
                BeanUtils.copyProperties(l, resp);
                resultMap.put(l.getUserId(), resp);
            });
        }
        return resultMap;
    }

    @Override
    public AdminUserInfoResp getAdminUserInfoByUserId(String adminUserId) {
        AdminUserInfoResp resp = new AdminUserInfoResp();
        if (!Strings.isNullOrEmpty(adminUserId)) {
            QueryWrapper<AdminUserDo> queryWrapper = new QueryWrapper();
            queryWrapper.eq("deleted", NumCode.ZERO.getCode());
            queryWrapper.eq("user_id", adminUserId);
            AdminUserDo adminUserDo = adminUserDao.selectOne(queryWrapper);
            if (adminUserDo != null) {
                BeanUtils.copyProperties(adminUserDo, resp);
            }
        }
        return resp;


    }

    @Override
    public List<AdminRoleResp> getUserRoleList() {
        LambdaQueryWrapperX<AdminRoleDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.orderByDesc(AdminRoleDo::getCreateTime);
        List<AdminRoleResp> result = new ArrayList<>();
        List<AdminRoleDo> adminRoleList = adminRoleDao.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(adminRoleList)) {
            result = JSONObject.parseArray(JSON.toJSONString(adminRoleList), AdminRoleResp.class);
        }
        return result;
    }

    @Override
    public PageResult getOperatorList(OperatorReq req) {
        List<UserAndRoleResp> data = adminUserMapper.selectOperatorList(req);
        Integer toIndex = req.getPageNo() * req.getPageSize();
        Integer from = (req.getPageNo() - 1) * req.getPageSize();
        List<UserAndRoleResp> resultList = data.subList(data.size() > from ? from : data.size(), data.size() > toIndex ? toIndex : data.size());
        PageResult result = new PageResult();
        result.setTotal((long) data.size());
        result.setList(resultList);
        return result;
    }

    @Override
    public void enableOrDisableAdminUser(DisableOrEnableReq req) {
        LambdaUpdateWrapper<AdminUserDo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AdminUserDo::getUserId, req.getCode());
        AdminUserDo adminUserDo = new AdminUserDo().setUserStatus(req.getStatus());
        if (NumCode.ONE.getCode() != adminUserDao.update(adminUserDo, wrapper))
            throw new BizException(AuthError.Execute_SQL_UPDATE);
        List<AdminMenuDo> data = adminUserMapper.selectMenuByUserId(req.getCode());
        Map<String, List<String>> url = userMenu(data);
        redisTemplate.opsForValue().set("USER_ID:" + req.getCode(), url);
    }

    @Override
    public List<AdminUserResp> getMerberByRoleId(CodeReq req) {
        if (StringUtils.isBlank(req.getCode())) {
            List<AdminUserDo> adminUserDos = adminUserDao.selectList();
            return JSONObject.parseArray(JSON.toJSONString(adminUserDos), AdminUserResp.class);
        }
        List<AdminUserRoleDo> list = adminUserRoleDao.selectList(AdminUserRoleDo::getRoleId, req.getCode());
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> collect = list.stream().map(i -> i.getUserId()).collect(Collectors.toList());
            LambdaQueryWrapperX<AdminUserDo> userWrapper = new LambdaQueryWrapperX<>();
            userWrapper.inIfPresent(AdminUserDo::getUserId, collect);
            List<AdminUserDo> adminUserDos = adminUserDao.selectList(userWrapper);
            return JSONObject.parseArray(JSON.toJSONString(adminUserDos), AdminUserResp.class);
        }
        return new ArrayList<>();
    }

    @Override
    public List<RoleAndMenuResp> getMenuByRoleId(CodeReq req) {
        LambdaQueryWrapperX<AdminMenuDo> wrapper = new LambdaQueryWrapperX<>();
        List<AdminMenuDo> menuDoList = adminMenuDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(menuDoList)) {
            List<RoleAndMenuResp> result = JSONObject.parseArray(JSON.toJSONString(menuDoList), RoleAndMenuResp.class);
            LambdaQueryWrapperX<AdminRoleMenuDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(AdminRoleMenuDo::getRoleId, req.getCode());
            List<AdminRoleMenuDo> roleMenuList = adminRoleMenuDao.selectList(queryWrapper);
            List<String> menuList = roleMenuList.stream().map(AdminRoleMenuDo::getMenuCode).collect(Collectors.toList());
            List<RoleAndMenuResp> collect = result.stream().filter(i -> Objects.equals(0, i.getLevel())).map(item -> {
                RoleAndMenuResp resp = new RoleAndMenuResp();
                BeanUtils.copyProperties(item, resp);
                if (menuList.contains(item.getMenuCode())) resp.setChoose(true);
                resp.setChildren(this.children(item, result, menuList));
                return resp;
            }).collect(Collectors.toList());
            return collect;
        }
        return new ArrayList<>();
    }

    /**
     * 查询所有子节点
     *
     * @param resp     资源对象
     * @param respList 资源集合
     * @return
     */
    private List<RoleAndMenuResp> children(RoleAndMenuResp resp, List<RoleAndMenuResp> respList, List<String> menuList) {
        List<RoleAndMenuResp> collect = respList.stream().filter(i -> {
            return i.getMenuParentCode().equals(resp.getMenuCode());
        }).map(j -> {
            // 子节点
            RoleAndMenuResp result = new RoleAndMenuResp();
            BeanUtils.copyProperties(j, result);
            if (menuList.contains(j.getMenuCode())) result.setChoose(true);
            result.setChildren(children(j, respList, menuList));
            return result;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    @Transactional()
    public void roleConfigurationMenu(RoleAndMenuReq req) {
        //查询受影响的用户 用于刷新redis数据
        List<AdminUserRoleDo> adminUserRoleList = adminUserRoleDao.selectList(AdminUserRoleDo::getRoleId, req.getRoleId());
        //根据角色查询所有菜单资源
        List<AdminRoleMenuDo> roleList = adminRoleMenuDao.selectList(AdminRoleMenuDo::getRoleId, req.getRoleId());
        if (CollectionUtils.isNotEmpty(roleList)) {
            List<String> oldMenuCodeList = roleList.stream().map(AdminRoleMenuDo::getMenuCode).collect(Collectors.toList());
            List<String> newMenuCodeList = req.getMenuCodeList();
            //求差集(删除)
            List<String> removeList = oldMenuCodeList.stream().filter(i -> !newMenuCodeList.contains(i)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(removeList)) {
                if (adminUserMapper.deleteRoleMenu(removeList, req.getRoleId()) == NumCode.ZERO.getCode())
                    throw new BizException(AuthError.Execute_SQL_DELETE);
            }
            List<String> saveList = newMenuCodeList.stream().filter(i -> !oldMenuCodeList.contains(i)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(saveList)) {
                List<AdminRoleMenuDo> list = new ArrayList<>();
                saveList.forEach(i -> list.add(new AdminRoleMenuDo().setRoleId(req.getRoleId()).setMenuCode(i).setDeletedTime(0L).setDeleted(NumCode.ZERO.getCode())));
                if (list.size() != adminRoleMenuDao.insertBatch(list))
                    throw new BizException(AuthError.Execute_SQL_SAVE);
            }
        } else {
            if (CollectionUtils.isNotEmpty(req.getMenuCodeList())) {
                List<AdminRoleMenuDo> list = new ArrayList<>();
                req.getMenuCodeList().forEach(i -> list.add(
                        new AdminRoleMenuDo()
                                .setRoleId(req.getRoleId())
                                .setMenuCode(i)
                                .setDeleted(NumCode.ZERO.getCode())
                                .setDeletedTime(0L)
                ));
                if (list.size() != adminRoleMenuDao.insertBatch(list))
                    throw new BizException(AuthError.Execute_SQL_SAVE);
            }
        }
        //刷新redis存的用户权限
        if (CollectionUtils.isNotEmpty(adminUserRoleList)) {
            adminUserRoleList.forEach(i -> {
                List<AdminMenuDo> data = adminUserMapper.selectMenuByUserId(i.getUserId());
                Map<String, List<String>> url = userMenu(data);
                redisTemplate.opsForValue().set("USER_ID:" + i.getUserId(), url);
            });
        }
    }

    @Override
    public List<AdminUserResp> findWorkOrderPermissionUsers() {
        List<AdminUserDo> adminUserDoList = adminUserMapper.findWorkOrderPermissionUsers();
        List<AdminUserResp> adminUserRespList = new ArrayList<>();
        adminUserDoList.forEach(item->{
            AdminUserResp resp = new AdminUserResp();
            BeanUtils.copyProperties(item,resp);
            adminUserRespList.add(resp);
        });
        return adminUserRespList;
    }

    @Override
    public AdminUserSumResp getPlatformApplicationReviewSum() {
        PlatformApplicationReviewReq req=new PlatformApplicationReviewReq();
        req.setApprovalStatus(1);
        AdminUserSumResp adminUserSumResp=new AdminUserSumResp();
        Long num = userMapper.getPlatformApplicationReviewCount(req);
        adminUserSumResp.setUserSum(num);
        return adminUserSumResp;
    }

    @Override
    public AdminUserResp getAdminUserByUserId(UserIdReq req) {
        QueryWrapper<AdminUserDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted",NumCode.ZERO.getCode());
        queryWrapper.eq("user_id",req.getUserId());
        AdminUserDo adminUserDo = adminUserDao.selectOne(queryWrapper);
        AdminUserResp resp = new AdminUserResp();
        if(adminUserDo!=null){
            BeanUtils.copyProperties(adminUserDo,resp);
        }
        return resp;
    }

    @Override
    public boolean checkIsAdmin(String userId) {
        QueryWrapper<AdminUserDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted",NumCode.ZERO.getCode());
        queryWrapper.eq("user_id",userId);
        AdminUserDo adminUserDo = adminUserDao.selectOne(queryWrapper);
        return adminUserDo != null;
    }

    @Override
    public PageResult getPlatformApplicationReview(PlatformApplicationReviewReq req) {
        req.setPageNo((req.getPageNo() - 1) * req.getPageSize());
        List<UserResp> data = userMapper.getgetPlatformApplicationReviewList(req);
        Long num = userMapper.getPlatformApplicationReviewCount(req);
        PageResult<UserResp> pageResult = new PageResult<>();
        pageResult.setList(data);
        pageResult.setTotal(num);
        return pageResult;
    }


    @Transactional()
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

    @Transactional()
    @Override
    public void reviewPlatformApplication(ReviewReq req) {
        UserCertificateOptionsDo userCertificateOptionsDo = new UserCertificateOptionsDo();
        UserPlatformPermissionsDo permission = new UserPlatformPermissionsDo();
        permission.setApprovalStatus(req.getApprovalStatus());
        if (NumCode.THREE.getCode() == req.getApprovalStatus()){
            permission.setUserStatus(NumCode.ONE.getCode());
            userCertificateOptionsDo.setCertificateId(QualificationType.CSP_USER.getCode());
            userCertificateOptionsDo.setUserId(req.getUserId());
            userCertificateOptionsDo.setCertificateApplyStatus(NumCode.THREE.getCode());
            userCertificateOptionsDo.setApplyTime(new Date());
            userCertificateOptionsDo.setApprovalTime(new Date());
            userCertificateOptionsDo.setCertificateStatus(NumCode.ZERO.getCode());
            userCertificateOptionsDao.insert(userCertificateOptionsDo);
        } else{
            permission.setUserStatus(NumCode.ZERO.getCode());
        }

        LambdaUpdateWrapper<UserPlatformPermissionsDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserPlatformPermissionsDo::getUserId, req.getUserId())
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

    @Override
    public List<ApprovalLogResp> getReviewLog(UuidReq req) {
        LambdaQueryWrapperX<ApprovalLogDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(ApprovalLogDo::getApprovalLogId, req.getUuid())
                .orderByDesc(ApprovalLogDo::getHandleTime);
        List<ApprovalLogDo> data = approvalLogDao.selectList(queryWrapper);
        List<ApprovalLogResp> list = new ArrayList<>();
        data.forEach(i -> {
            ApprovalLogResp resp = new ApprovalLogResp();
            BeanUtils.copyProperties(i, resp);
            list.add(resp);
        });
        return list;
    }

    private void addProcessingRecord(String businessId,String processingContent,Integer businessType){
        BaseUser baseUser = SessionContextUtil.getUser();
        //添加处理记录
        ProcessingRecordReq processingRecordReq = new ProcessingRecordReq();
        processingRecordReq.setBusinessId(businessId);
        processingRecordReq.setBusinessType(businessType);
        processingRecordReq.setProcessingContent(processingContent);
        processingRecordReq.setProcessingUserId(baseUser.getUserId());
        processingRecordApi.addRecord(processingRecordReq);
    }

    private void addProcessingRecord2(String businessId,String processingContent,Integer businessType,String remark){
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

}
