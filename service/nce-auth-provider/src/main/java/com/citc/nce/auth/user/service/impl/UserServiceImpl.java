package com.citc.nce.auth.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.citc.nce.auth.adminUser.vo.req.DeleteClientUserReq;
import com.citc.nce.auth.certificate.dao.CertificateOptionsDao;
import com.citc.nce.auth.certificate.dao.UserCertificateOptionsDao;
import com.citc.nce.auth.certificate.entity.CertificateOptionsDo;
import com.citc.nce.auth.certificate.entity.UserCertificateOptionsDo;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.constant.CheckUniqueTypeEnum;
import com.citc.nce.auth.identification.dao.UserEnterpriseIdentificationDao;
import com.citc.nce.auth.identification.entity.UserEnterpriseIdentificationDo;
import com.citc.nce.auth.user.dao.ApprovalLogDao;
import com.citc.nce.auth.user.dao.UserDao;
import com.citc.nce.auth.user.dao.UserPlatformPermissionsDao;
import com.citc.nce.auth.user.entity.UserDo;
import com.citc.nce.auth.user.entity.UserPlatformPermissionsDo;
import com.citc.nce.auth.user.service.UserService;
import com.citc.nce.auth.user.vo.resp.UserInfo;
import com.citc.nce.auth.user.vo.resp.UserResp;
import com.citc.nce.auth.utils.AuthUtils;
import com.citc.nce.authcenter.auth.vo.req.RegisterReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.misc.constant.NumCode;
import com.citc.nce.misc.constant.QualificationType;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/21 17:32
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Resource
    private CertificateOptionsDao certificateOptionsDao;

    @Resource
    private UserPlatformPermissionsDao userPlatformPermissionsDao;


    @Resource
    UserEnterpriseIdentificationDao userEnterpriseIdentificationDao;

    @Resource
    ApprovalLogDao approvalLogDao;


    @Resource
    private UserCertificateOptionsDao userCertificateOptionsDao;


    @Override
    public UserDo saveForRegister(RegisterReq req) {
        String mail =req.getMail().trim();
        String name = req.getName().trim();
        String phone = req.getPhone().trim();
        String uuid = gitUserId();

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
        userDao.insert(userDo);
        return userDo;
    }

    @Override
    public UserResp findByPhone(String phone) {
        if(!Strings.isNullOrEmpty(phone)){
            LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(UserDo::getDeleted, 0);
            queryWrapper.eq(UserDo::getPhone,phone);
            List<UserDo> userDos = userDao.selectList(queryWrapper);
            if(com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(userDos)){
                UserResp res = new UserResp();
                BeanUtils.copyProperties(userDos.get(0), res);
                return res;
            }
        }
        return null;
    }

    private String gitUserId() {
        return AuthUtils.randomID(10);
    }

    /**
     * 用户名/邮箱/手机号是否通过唯一校验
     *
     * @param value
     * @param type  1 mail  2 name 3 phone
     * @return 通过唯一校验 true
     */
    @Override
    public Boolean checkUnique(String value, Integer type, String userId) {
        //1.检查用户表账户名称是否重复
        QueryWrapper<UserDo> queryWrapper = new QueryWrapper();
        if(StringUtils.isNotBlank(userId)){
            queryWrapper.ne("user_id",userId);
        }
        switch (type) {
            case 1:
                queryWrapper.eq(CheckUniqueTypeEnum.EMAIL.getTitle(), value);
                break;
            case 2:
                queryWrapper.eq(CheckUniqueTypeEnum.NAME.getTitle(), value);
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
        if(userCount>0){
            return false;
        }

        if(StringUtils.isNotBlank(userId)){
            //2.检查企业用户表账户名称是否重复
            QueryWrapper<UserEnterpriseIdentificationDo> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("enterprise_account_name",value);
            queryWrapper1.ne("user_id",userId);
            Long enterpriseCount = userEnterpriseIdentificationDao.selectCount(queryWrapper1);
            if(enterpriseCount>0){
                return false;
            }
        }
        return true;
    }

    /**
     * 根据userId 获取用户基本信息
     *
     * @param userId
     * @return
     */
    //@Override
    public UserDo userInfoDetailByUserId(String userId) {
        return userDao.selectOne(UserDo::getUserId, userId);
    }

    /**
     * 根据用户id往user表刷新用户状态
     *
     * @param userId
     */
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
        if (CollectionUtils.isNotEmpty(dataList)) {
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
            throw new BizException(AuthError.Execute_SQL_UPDATE);
        }
    }



    @Override
    public void deleteClientUserInfo(DeleteClientUserReq req) {
        UpdateWrapper<UserDo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", req.getUserId());
        updateWrapper.set("deleted", NumCode.ONE.getCode())
                .set("updater", SessionContextUtil.getUser().getUserId());
        userDao.update(null, updateWrapper);

    }


    @Override
    public void updateUserName(String name, String userId) {
        LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(UserDo::getUserId, userId);
        UserDo user = userDao.selectOne(queryWrapper);
        if (name.equals(user.getName())) return;//如果企业名称就是自己名称就不用刷新
        if (checkUnique(name, CheckUniqueTypeEnum.NAME.getType(),null)) {
            LambdaUpdateWrapper<UserDo> updateUserWrapper = new LambdaUpdateWrapper<UserDo>();
            updateUserWrapper.eq(UserDo::getUserId, userId);
            UserDo userDo = new UserDo().setName(name);
            if (NumCode.ONE.getCode() != userDao.update(userDo, updateUserWrapper)) {
                log.error("updateUserName error message is === " + AuthError.Execute_SQL_UPDATE);
                throw new BizException(AuthError.Execute_SQL_UPDATE);
            }
        } else {
            log.error("updateUserName error message is === " + AuthError.BUSINESS_USER_ACCOUNT_REPEAT);
            throw new BizException(AuthError.BUSINESS_USER_ACCOUNT_REPEAT);
        }
    }

    @Override
    public void updateUserInfo(String userId, UserDo userDo) {
        String name = userDo.getName();
        String phone = userDo.getPhone();
        String mail = userDo.getMail();
        if (checkUnique(name, CheckUniqueTypeEnum.NAME.getType(), null) && checkUnique(phone, CheckUniqueTypeEnum.PHONE.getType(), null) && checkUnique(mail, CheckUniqueTypeEnum.MAIL.getType(), null)) {
            LambdaUpdateWrapper<UserDo> updateUserWrapper = new LambdaUpdateWrapper<UserDo>();
            updateUserWrapper.eq(UserDo::getUserId, userId);
            if (NumCode.ONE.getCode() == userDao.update(userDo, updateUserWrapper)) {
                //commonResult = new CommonResult(true, "更新成功");
            } else {
                throw BizException.build(AuthError.Execute_SQL_UPDATE);
            }
        } else {
            throw BizException.build(AuthError.USER_BASE_INFO_IS_REPEAT);
        }
    }

    public List<UserResp> getEnterpriseUserList() {
        QueryWrapper<UserDo> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("enterprise_auth_status", 3);
        queryWrapper1.eq("deleted", 0);
        List<UserDo> userDoList = userDao.selectList(queryWrapper1);

        QueryWrapper<UserPlatformPermissionsDo> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("protal", 1);
        queryWrapper2.eq("deleted", 0);
        List<UserPlatformPermissionsDo> userPlatformPermissionsDoList = userPlatformPermissionsDao.selectList(queryWrapper2);

        QueryWrapper<UserCertificateOptionsDo> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("certificate_apply_status", 3);
        queryWrapper3.eq("certificate_status", 0);
        queryWrapper3.eq("deleted", 0);
        List<UserCertificateOptionsDo> userCertificateDoList = userCertificateOptionsDao.selectList(queryWrapper3);

        QueryWrapper<UserEnterpriseIdentificationDo> queryWrapper4 = new QueryWrapper<>();
        queryWrapper4.eq("enterprise_auth_status", 3);
        queryWrapper4.eq("deleted", 0);
        List<UserEnterpriseIdentificationDo> userEnterpriseIdentificationDoList = userEnterpriseIdentificationDao.selectList(queryWrapper4);

        List<UserResp> userRespList = new ArrayList<>();
        userDoList.forEach(item -> {
            UserResp userResp = new UserResp();
            BeanUtil.copyProperties(item, userResp);
            if (StringUtils.isNotEmpty(item.getSpTel())) {
                userResp.setIsSetSp(true);
            } else {
                userResp.setIsSetSp(false);
            }
            userResp.setIsDisable(false);
            userResp.setIsProvider(false);

            for (UserPlatformPermissionsDo userPlatformPermissionsDo : userPlatformPermissionsDoList) {
                if (item.getUserId().equals(userPlatformPermissionsDo.getUserId())) {
                    if (userPlatformPermissionsDo.getUserStatus().equals(NumCode.TWO.getCode())) {
                        userResp.setIsDisable(true);
                        break;
                    }
                }
            }

            for (UserCertificateOptionsDo userCertificateOptionsDo : userCertificateDoList) {
                if (item.getUserId().equals(userCertificateOptionsDo.getUserId())) {
                    if (userCertificateOptionsDo.getCertificateId().equals(QualificationType.ABILITY_SUPPLIER.getCode())) {
                        userResp.setIsProvider(true);
                        break;
                    }
                }
            }

            for (UserEnterpriseIdentificationDo userEnterpriseIdentificationDo : userEnterpriseIdentificationDoList) {
                if (item.getUserId().equals(userEnterpriseIdentificationDo.getUserId())) {
                    userResp.setEnterpriseName(userEnterpriseIdentificationDo.getEnterpriseName());
                    break;
                }
            }
            userRespList.add(userResp);
        });


        return userRespList;
    }

    @Override
    public List<UserResp> getEnterpriseUserListForChatbot() {
        QueryWrapper<UserDo> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("enterprise_auth_status", 3);
        queryWrapper1.eq("deleted", 0);
        List<UserDo> userDoList = userDao.selectList(queryWrapper1);

        QueryWrapper<UserPlatformPermissionsDo> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("protal", 3);
        queryWrapper2.eq("deleted", 0);
        List<UserPlatformPermissionsDo> userPlatformPermissionsDoList = userPlatformPermissionsDao.selectList(queryWrapper2);

        QueryWrapper<UserCertificateOptionsDo> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("certificate_apply_status", 3);
        queryWrapper3.eq("certificate_status", 0);
        queryWrapper3.eq("deleted", 0);
        List<UserCertificateOptionsDo> userCertificateDoList = userCertificateOptionsDao.selectList(queryWrapper3);

        QueryWrapper<UserEnterpriseIdentificationDo> queryWrapper4 = new QueryWrapper<>();
        queryWrapper4.eq("enterprise_auth_status", 3);
        queryWrapper4.eq("deleted", 0);
        List<UserEnterpriseIdentificationDo> userEnterpriseIdentificationDoList = userEnterpriseIdentificationDao.selectList(queryWrapper4);

        List<UserResp> userRespList = new ArrayList<>();
        userDoList.forEach(item -> {
            UserResp userResp = new UserResp();
            BeanUtil.copyProperties(item, userResp);
            if (StringUtils.isNotEmpty(item.getSpTel())) {
                userResp.setIsSetSp(true);
            } else {
                userResp.setIsSetSp(false);
            }
            userResp.setIsDisable(false);
            userResp.setIsProvider(false);

            for (UserPlatformPermissionsDo userPlatformPermissionsDo : userPlatformPermissionsDoList) {
                if (item.getUserId().equals(userPlatformPermissionsDo.getUserId())) {
                    if (userPlatformPermissionsDo.getUserStatus().equals(NumCode.TWO.getCode())) {
                        userResp.setIsDisable(true);
                        break;
                    }
                }
            }

            for (UserCertificateOptionsDo userCertificateOptionsDo : userCertificateDoList) {
                if (item.getUserId().equals(userCertificateOptionsDo.getUserId())) {
                    if (userCertificateOptionsDo.getCertificateId().equals(QualificationType.ABILITY_SUPPLIER.getCode())) {
                        userResp.setIsProvider(true);
                        break;
                    }
                }
            }

            for (UserEnterpriseIdentificationDo userEnterpriseIdentificationDo : userEnterpriseIdentificationDoList) {
                if (item.getUserId().equals(userEnterpriseIdentificationDo.getUserId())) {
                    userResp.setEnterpriseName(userEnterpriseIdentificationDo.getEnterpriseName());
                    break;
                }
            }
            userRespList.add(userResp);
        });
        return userRespList;
    }


    @Override
    public UserInfo getUserBaseInfoByUserId(String userId) {
        UserInfo userInfo = null;
        if (!Strings.isNullOrEmpty(userId)) {
            userInfo = new UserInfo();
            UserDo userDo = userInfoDetailByUserId(userId);
            if (userDo != null) {
                BeanUtils.copyProperties(userDo, userInfo);
            }
        }
        return userInfo;
    }

}
