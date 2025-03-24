package com.citc.nce.im.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.entity.RobotGroupSendPlanDescDo;
import com.citc.nce.im.entity.RobotGroupSendPlansDo;
import com.citc.nce.im.mapper.RobotGroupSendPlanDescDao;
import com.citc.nce.im.mapper.RobotGroupSendPlansDao;
import com.citc.nce.im.service.RobotGroupSendPlanDescService;
import com.citc.nce.im.util.WrapperUtil;
import com.citc.nce.robot.req.RobotGroupSendPlanDescReq;
import com.citc.nce.robot.vo.RobotGroupSendPlanDesc;
import com.citc.nce.robot.vo.RobotGroupSendPlanDescPageParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;
import java.util.stream.Collectors;

import static com.citc.nce.common.core.exception.GlobalErrorCode.USER_AUTH_ERROR;

@Service
public class RobotGroupSendPlanDescServiceImpl extends ServiceImpl<RobotGroupSendPlanDescDao,RobotGroupSendPlanDescDo> implements RobotGroupSendPlanDescService {

    @Resource
    RobotGroupSendPlanDescDao robotGroupSendPlanDescDao;
    @Resource
    RobotGroupSendPlansDao robotGroupSendPlansDao;

    @Value("${userId.superAdministrator}")
    private String superAdministrator;

    @Override
    public RobotGroupSendPlanDesc queryById(Long id) {
        RobotGroupSendPlanDesc robotGroupSendPlanDesc = new RobotGroupSendPlanDesc();
        QueryWrapper<RobotGroupSendPlanDescDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plan_id",id);
        RobotGroupSendPlanDescDo robotGroupSendPlanDescDo = robotGroupSendPlanDescDao.selectOne(queryWrapper);
        if (null != robotGroupSendPlanDescDo){
            BeanUtils.copyProperties(robotGroupSendPlanDescDo,robotGroupSendPlanDesc);
        }
        return robotGroupSendPlanDesc;
    }

    private void filterUsers(QueryWrapper queryWrapper) {
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministrator)) {
            queryWrapper.eq("creator", userId);
        }
    }

    @Override
    public PageResult queryByPage(RobotGroupSendPlanDescPageParam robotGroupSendPlanDescPageParam) {
        QueryWrapper queryWrapper = WrapperUtil.entity2Wrapper(robotGroupSendPlanDescPageParam.getRobotGroupSendPlanDesc());
        filterUsers(queryWrapper);
        return robotGroupSendPlanDescDao.selectPage(robotGroupSendPlanDescPageParam, queryWrapper);
    }

    @Override
    public void insert(RobotGroupSendPlanDescReq robotGroupSendPlanDescReq) {
        Long planId = robotGroupSendPlanDescReq.getPlanId();
        String userId = SessionContextUtil.getUser().getUserId();
        RobotGroupSendPlansDo robotGroupSendPlansDo = robotGroupSendPlansDao.selectById(planId);
        if (robotGroupSendPlansDo != null){
            if(userId.equals(robotGroupSendPlansDo.getCreator())){
                RobotGroupSendPlanDescDo robotGroupSendPlanDescDo = new RobotGroupSendPlanDescDo();
                BeanUtils.copyProperties(robotGroupSendPlanDescReq,robotGroupSendPlanDescDo);
                LambdaQueryWrapper<RobotGroupSendPlanDescDo> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(RobotGroupSendPlanDescDo::getPlanId,robotGroupSendPlanDescReq.getPlanId());
                RobotGroupSendPlanDescDo descDo = robotGroupSendPlanDescDao.selectOne(wrapper);
                if (null == descDo){
                    robotGroupSendPlanDescDao.insert(robotGroupSendPlanDescDo);
                }else {
                    robotGroupSendPlanDescDao.update(robotGroupSendPlanDescDo,wrapper);
                }
            }else{
                throw new BizException(USER_AUTH_ERROR);
            }
        }else{
            throw new BizException("计划不存在:"+planId);
        }



    }

    @Override
    public void update(RobotGroupSendPlanDescReq robotGroupSendPlanDescReq) {
        RobotGroupSendPlanDescDo robotGroupSendPlanDescDo = new RobotGroupSendPlanDescDo();
        BeanUtils.copyProperties(robotGroupSendPlanDescReq,robotGroupSendPlanDescDo);
        robotGroupSendPlanDescDao.updateById(robotGroupSendPlanDescDo);
    }

    @Override
    public void deleteById(Long id) {
        RobotGroupSendPlanDescDo robotGroupSendPlanDescDo = new RobotGroupSendPlanDescDo();
        robotGroupSendPlanDescDo.setId(id);
        robotGroupSendPlanDescDo.setPlanStatus(1);
        robotGroupSendPlanDescDao.updateById(robotGroupSendPlanDescDo);
    }

    @Override
    public boolean containList(List<String> list) {
        for (String one : list) {
            if (StrUtil.isBlank(one)) {
                continue;
            }
            List<RobotGroupSendPlanDescDo> robotGroupSendPlanDescDos = robotGroupSendPlanDescDao.selectList(new LambdaQueryWrapper<RobotGroupSendPlanDescDo>()
                    .eq(RobotGroupSendPlanDescDo::getCreator, SessionContextUtil.getUser().getUserId())
                    .like(RobotGroupSendPlanDescDo::getPlanDesc, one));
            if (CollUtil.isEmpty(robotGroupSendPlanDescDos)) {
                continue;
            }

            Long count = robotGroupSendPlansDao.selectCount(new LambdaQueryWrapper<RobotGroupSendPlansDo>()
                    .eq(RobotGroupSendPlansDo::getCreator, SessionContextUtil.getUser().getUserId())
                    //未启动或执行中
                    .lt(RobotGroupSendPlansDo::getPlanStatus, 2)
                    .in(RobotGroupSendPlansDo::getId, robotGroupSendPlanDescDos.stream().map(RobotGroupSendPlanDescDo::getPlanId).collect(Collectors.toList())));
            if (count>0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containOne(String one) {
        if (StrUtil.isBlank(one)) {
            return false;
        }
        List<RobotGroupSendPlanDescDo> robotGroupSendPlanDescDos = robotGroupSendPlanDescDao.selectList(new LambdaQueryWrapper<RobotGroupSendPlanDescDo>()
                .eq(RobotGroupSendPlanDescDo::getCreator, SessionContextUtil.getUser().getUserId())
                .like(RobotGroupSendPlanDescDo::getPlanDesc, one));
        if (CollUtil.isEmpty(robotGroupSendPlanDescDos)) {
            return false;
        }

        Long count = robotGroupSendPlansDao.selectCount(new LambdaQueryWrapper<RobotGroupSendPlansDo>()
                .eq(RobotGroupSendPlansDo::getCreator, SessionContextUtil.getUser().getUserId())
                //未启动或执行中
                .lt(RobotGroupSendPlansDo::getPlanStatus, 2)
                .in(RobotGroupSendPlansDo::getId, robotGroupSendPlanDescDos.stream().map(RobotGroupSendPlanDescDo::getPlanId).collect(Collectors.toList())));
        return count > 0;
    }
}
