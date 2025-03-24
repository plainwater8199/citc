package com.citc.nce.authcenter.auth.service.impl;

import cn.hutool.core.map.MapUtil;
import com.citc.nce.authcenter.auth.service.LoginRecordService;
import com.citc.nce.authcenter.identification.entity.UserEnterpriseIdentificationDo;
import com.citc.nce.authcenter.user.entity.UserDo;
import com.citc.nce.authcenter.userLoginRecord.entity.UserLoginRecordDo;
import com.citc.nce.authcenter.utils.MapStringUtil;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.citc.nce.authcenter.userLoginRecord.dao.UserLoginRecordDao;
import com.citc.nce.authcenter.identification.dao.UserEnterpriseIdentificationDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yy
 * @date 2024-08-02 09:27:14
 */
@Component
public class LoginRecordServiceImpl implements LoginRecordService {

    @Resource
    UserLoginRecordDao userLoginRecordDao;

    @Resource
    UserEnterpriseIdentificationDao userEnterpriseIdentificationDao;
    public void insertLoginRecord(UserDo user, Map<String, Integer> version, Integer platformType) {
        UserLoginRecordDo recordDo = new UserLoginRecordDo();
        BeanUtils.copyProperties(user, recordDo);
        // 获取登录用户所属的CSP用户Id
        // CSP用户创建企业，这里取企业的创建者字段使用
        LambdaQueryWrapperX<UserEnterpriseIdentificationDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(UserEnterpriseIdentificationDo::getUserId, recordDo.getUserId())
                .eq(UserEnterpriseIdentificationDo::getDeleted, 0);
        // 理论上一个用户只能绑定一家企业
        UserEnterpriseIdentificationDo identificationDo = userEnterpriseIdentificationDao.selectOne(queryWrapperX);
        if (null != identificationDo && StringUtils.isNotEmpty(identificationDo.getCreator())) {
            recordDo.setCspAccount(identificationDo.getCreator());
        }
        // 查询一个用户一天内是否多次登录
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String today = sdf.format(date);
        LambdaQueryWrapperX<UserLoginRecordDo> lambdaQueryWrapperX = new LambdaQueryWrapperX<UserLoginRecordDo>();
        lambdaQueryWrapperX.eq(UserLoginRecordDo::getUserId, recordDo.getUserId())
                .ge(UserLoginRecordDo::getCreateTime, today)
                .le(UserLoginRecordDo::getCreateTime, date).orderByAsc(UserLoginRecordDo::getCreateTime);
        List<UserLoginRecordDo> recordDos = userLoginRecordDao.selectList(lambdaQueryWrapperX);
        // 一天内多次登录时，后面的登录时间都存第一次登录的时间
        if (CollectionUtils.isNotEmpty(recordDos)) {
            recordDo.setDailyFirstLoginTime(recordDos.get(0).getDailyFirstLoginTime());
        }
        recordDo.setId(null);
        recordDo.setVersionInfo(MapUtil.isNotEmpty(version) ? MapStringUtil.getMapToString(version) : "");
        recordDo.setCreator(user.getUserId());
        recordDo.setUpdater(user.getUserId());
        recordDo.setName(user.getName());
        recordDo.setCreateTime(null);
        recordDo.setUpdateTime(null);
        if (null != platformType) {
            recordDo.setPlatformType(platformType);
        }
        userLoginRecordDao.insert(recordDo);
    }
}
