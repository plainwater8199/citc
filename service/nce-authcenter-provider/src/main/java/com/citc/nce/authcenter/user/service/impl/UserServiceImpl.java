package com.citc.nce.authcenter.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.constant.CheckUniqueTypeEnum;
import com.citc.nce.authcenter.identification.dao.UserCertificateOptionsDao;
import com.citc.nce.authcenter.identification.dao.UserEnterpriseIdentificationDao;
import com.citc.nce.authcenter.identification.dao.UserPersonIdentificationDao;
import com.citc.nce.authcenter.identification.entity.UserCertificateOptionsDo;
import com.citc.nce.authcenter.identification.entity.UserEnterpriseIdentificationDo;
import com.citc.nce.authcenter.identification.entity.UserPersonIdentificationDo;
import com.citc.nce.authcenter.user.dao.UserDao;
import com.citc.nce.authcenter.user.entity.UserDo;
import com.citc.nce.authcenter.user.service.UserService;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.misc.constant.NumCode;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;
    @Resource
    private UserEnterpriseIdentificationDao userEnterpriseIdentificationDao;

    @Resource
    private UserPersonIdentificationDao userPersonIdentificationDao;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserCertificateOptionsDao userCertificateOptionsDao;

    @Override
    public UserDo saveUser(UserDo userDo) {
        userDao.insert(userDo);
        return userDo;
    }

    @Override
    public UserDo findUserByNameOrPhoneOrEmail(String name, String phone, String email) {
        if (!Strings.isNullOrEmpty(name) || !Strings.isNullOrEmpty(phone) || !Strings.isNullOrEmpty(email)) {
            LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(UserDo::getDeleted, 0);
            if (!Strings.isNullOrEmpty(phone)) {
                queryWrapper.eq(UserDo::getPhone, phone);
                if (!Strings.isNullOrEmpty(name)) {
                    queryWrapper.or().eq(UserDo::getName, name);
                }
                if (!Strings.isNullOrEmpty(email)) {
                    queryWrapper.or().eq(UserDo::getMail, email);
                }
            } else if (!Strings.isNullOrEmpty(name)) {
                queryWrapper.eq(UserDo::getName, name);
                if (!Strings.isNullOrEmpty(email)) {
                    queryWrapper.or().eq(UserDo::getMail, email);
                }
            } else {
                queryWrapper.eq(UserDo::getMail, email);
            }
            List<UserDo> userDos = userDao.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(userDos)) {
                return userDos.get(0);
            }
        }
        return null;
    }


    @Override
    public UserDo findByPhone(String phone) {
        if (!Strings.isNullOrEmpty(phone)) {
            LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(UserDo::getDeleted, 0);
            queryWrapper.eq(UserDo::getPhone, phone);
            List<UserDo> userDos = userDao.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(userDos)) {
                return userDos.get(0);
            }
        }
        return null;
    }

    @Override
    public void updateUserName(String name, String userId) {
        LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(UserDo::getUserId, userId);
        UserDo user = userDao.selectOne(queryWrapper);
        if (name.equals(user.getName())) return;//如果企业名称就是自己名称就不用刷新
        if (checkUnique(name, CheckUniqueTypeEnum.NAME.getType(), null)) {
            LambdaUpdateWrapper<UserDo> updateUserWrapper = new LambdaUpdateWrapper<UserDo>();
            updateUserWrapper.eq(UserDo::getUserId, userId);
            UserDo userDo = new UserDo().setName(name);
            if (NumCode.ONE.getCode() != userDao.update(userDo, updateUserWrapper)) {
                log.error("updateUserName error message is === " + AuthCenterError.Execute_SQL_UPDATE);
                throw new BizException(AuthCenterError.Execute_SQL_UPDATE);
            }
        } else {
            log.error("updateUserName error message is === " + AuthCenterError.BUSINESS_USER_ACCOUNT_REPEAT);
            throw new BizException(AuthCenterError.BUSINESS_USER_ACCOUNT_REPEAT);
        }
    }

    @Override
    public UserDo userInfoDetailByUserId(String userId) {
        return userDao.selectOne(UserDo::getUserId, userId);
    }

    @Override
    public void updateUserInfo(String userId, UserDo userDo) {
        String name = userDo.getName();
        String phone = userDo.getPhone();
        String mail = userDo.getMail();
        if (checkUnique(name, CheckUniqueTypeEnum.NAME.getType(), null) && checkUnique(phone, CheckUniqueTypeEnum.PHONE.getType(), null) && checkUnique(mail, CheckUniqueTypeEnum.MAIL.getType(), null)) {
            LambdaUpdateWrapper<UserDo> updateUserWrapper = new LambdaUpdateWrapper<>();
            updateUserWrapper.eq(UserDo::getUserId, userId);
            if (NumCode.ONE.getCode() == userDao.update(userDo, updateUserWrapper)) {
                //commonResult = new CommonResult(true, "更新成功");
            } else {
                throw BizException.build(AuthCenterError.Execute_SQL_UPDATE);
            }
        } else {
            throw BizException.build(AuthCenterError.USER_BASE_INFO_IS_REPEAT);
        }
    }

    @Override
    public List<String> findAllActiveUser() {
        List<String> userIds = new ArrayList<>();
        LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(UserDo::getDeleted, 0);
        List<UserDo> userDos = userDao.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(userDos)) {
            userDos.forEach(i -> userIds.add(i.getUserId()));
        }
        return userIds;
    }

    @Override
    public Map<String, String> findUserNameByIds(List<String> userIds) {
        Map<String, String> userNameMap = new HashMap<>();
        List<UserDo> userDos = findUserByIds(userIds);
        if (CollectionUtils.isNotEmpty(userDos)) {
            userDos.forEach(i -> userNameMap.put(i.getUserId(), i.getName()));
        }
        return userNameMap;
    }

    @Override
    public List<UserDo> findUserByIds(List<String> userIds) {
        List<UserDo> userDos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userIds)) {
            LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.in(UserDo::getUserId, userIds);
            userDos = userDao.selectList(queryWrapper);
        }
        return userDos;
    }

    @Override
    public UserDo findByUserId(String uuid) {
        if (!Strings.isNullOrEmpty(uuid)) {
            LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(UserDo::getUserId, uuid);
            List<UserDo> userDos = userDao.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(userDos)) {
                return userDos.get(0);
            }
        }
        return null;
    }


    @Override
    public void userStatusSyn() {

        int personCount = 0;
        int enterCount = 0;
        List<UserCertificateOptionsDo> userCertificateOptionsDoNews = new ArrayList<>();
        UserCertificateOptionsDo certificateOptionsDo;
        LambdaQueryWrapper<UserPersonIdentificationDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPersonIdentificationDo::getDeleted, 0);
        List<UserPersonIdentificationDo> userPersonIdentificationDos = userPersonIdentificationDao.selectList(queryWrapper);

        LambdaQueryWrapper<UserEnterpriseIdentificationDo> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(UserEnterpriseIdentificationDo::getDeleted, 0);
        List<UserEnterpriseIdentificationDo> userEnterpriseIdentificationDos = userEnterpriseIdentificationDao.selectList(queryWrapper1);

        LambdaQueryWrapper<UserCertificateOptionsDo> userCertificateOptionsDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userCertificateOptionsDoLambdaQueryWrapper.eq(UserCertificateOptionsDo::getDeleted, 0);
        List<UserCertificateOptionsDo> userCertificateOptionsDos = userCertificateOptionsDao.selectList(userCertificateOptionsDoLambdaQueryWrapper);

        List<String> personS = new ArrayList<>();
        List<String> enterS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userCertificateOptionsDos)) {
            for (UserCertificateOptionsDo item : userCertificateOptionsDos) {
                if (item.getCertificateId() == 10001) {
                    enterS.add(item.getUserId());
                } else if (item.getCertificateId() == 10002) {
                    personS.add(item.getUserId());
                }
            }
        }
        if (!CollectionUtils.isEmpty(userPersonIdentificationDos)) {
            for (UserPersonIdentificationDo item : userPersonIdentificationDos) {
                if (!personS.contains(item.getUserId())) {
                    certificateOptionsDo = new UserCertificateOptionsDo();
                    certificateOptionsDo.setCertificateId(10002);
                    certificateOptionsDo.setUserId(item.getUserId());
                    certificateOptionsDo.setCertificateApplyStatus(item.getPersonAuthStatus());
                    certificateOptionsDo.setApplyTime(item.getCreateTime());
                    certificateOptionsDo.setApprovalTime(item.getPersonAuthAuditTime());
                    certificateOptionsDo.setCertificateStatus(0);
                    certificateOptionsDo.setCreator(item.getUserId());
                    certificateOptionsDo.setCreateTime(item.getPersonAuthTime());
                    certificateOptionsDo.setDeleted(0);
                    certificateOptionsDo.setDeletedTime((new Date()).getTime());
                    userCertificateOptionsDoNews.add(certificateOptionsDo);
                    personCount++;
                }
            }
        }

        if (!CollectionUtils.isEmpty(userEnterpriseIdentificationDos)) {
            for (UserEnterpriseIdentificationDo item : userEnterpriseIdentificationDos) {
                if (!enterS.contains(item.getUserId())) {
                    certificateOptionsDo = new UserCertificateOptionsDo();
                    certificateOptionsDo.setCertificateId(10001);
                    certificateOptionsDo.setUserId(item.getUserId());
                    certificateOptionsDo.setCertificateApplyStatus(item.getEnterpriseAuthStatus());
                    certificateOptionsDo.setApplyTime(item.getCreateTime());
                    certificateOptionsDo.setApprovalTime(item.getEnterpriseAuthAuditTime());
                    certificateOptionsDo.setCertificateStatus(0);
                    certificateOptionsDo.setCreator(item.getUserId());
                    certificateOptionsDo.setCreateTime(item.getEnterpriseAuthTime());
                    certificateOptionsDo.setDeleted(0);
                    certificateOptionsDo.setDeletedTime((new Date()).getTime());
                    userCertificateOptionsDoNews.add(certificateOptionsDo);
                    enterCount++;
                }
            }
        }
        if (!CollectionUtils.isEmpty(userCertificateOptionsDoNews) && userCertificateOptionsDoNews.size() > 0) {
            System.out.println("-------------personCount-----------:" + personCount);
            System.out.println("-------------enterCount-----------:" + enterCount);
            userCertificateOptionsDao.insertBatch(userCertificateOptionsDoNews);
        }
    }

    @Override
    public UserDo findByEmail(String email) {
        if (!Strings.isNullOrEmpty(email)) {
            LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(UserDo::getDeleted, 0);
            queryWrapper.eq(UserDo::getMail, email);
            List<UserDo> userDos = userDao.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(userDos)) {
                return userDos.get(0);
            }
        }
        return null;
    }


    @Override
    public UserDo findByAccount(String account) {
        if (!Strings.isNullOrEmpty(account)) {
            LambdaQueryWrapperX<UserDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(UserDo::getDeleted, 0);
            queryWrapper.eq(UserDo::getName, account);
            List<UserDo> userDos = userDao.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(userDos)) {
                return userDos.get(0);
            } else {
                queryWrapper = new LambdaQueryWrapperX<>();
                queryWrapper.eq(UserDo::getDeleted, 0);
                queryWrapper.eq(UserDo::getPhone, account);
                userDos = userDao.selectList(queryWrapper);
                if (CollectionUtils.isNotEmpty(userDos)) {
                    return userDos.get(0);
                }
            }
        }
        return null;
    }

    /**
     * 用户名/邮箱/手机号是否通过唯一校验
     *
     * @param value
     * @param type  1 mail  2 name 3 phone
     * @return 通过唯一校验 true
     */

    public Boolean checkUnique(String value, Integer type, String userId) {
        //1.检查用户表账户名称是否重复
        QueryWrapper<UserDo> queryWrapper = new QueryWrapper();
        if (StringUtils.isNotBlank(userId)) {
            queryWrapper.ne("user_id", userId);
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
}
