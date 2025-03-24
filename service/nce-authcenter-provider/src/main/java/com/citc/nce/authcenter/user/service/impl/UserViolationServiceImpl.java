package com.citc.nce.authcenter.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.authcenter.user.dao.UserViolationDao;
import com.citc.nce.authcenter.user.entity.UserViolationDo;
import com.citc.nce.authcenter.user.service.UserViolationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserViolationServiceImpl implements UserViolationService {
    @Resource
    private UserViolationDao userViolationDao;
    @Override
    public List<UserViolationDo> selectListByInfo(String userId, Integer plate, Integer violationType) {
        LambdaQueryWrapper<UserViolationDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserViolationDo::getUserId,userId);
        queryWrapper.eq(UserViolationDo::getPlate,plate);
        queryWrapper.eq(UserViolationDo::getDeleted,0);
        if(violationType != -1){
            queryWrapper.eq(UserViolationDo::getViolationType,violationType);
        }
        return userViolationDao.selectList(queryWrapper);
    }
}
