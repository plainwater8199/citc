package com.citc.nce.robot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.dao.RobotProcessSettingNodeDao;
import com.citc.nce.robot.dao.RobotProcessTriggerNodeDao;
import com.citc.nce.robot.entity.RobotProcessSettingNodeDo;
import com.citc.nce.robot.entity.RobotProcessTriggerNodeDo;
import com.citc.nce.robot.service.RobotProcessTriggerNodeService;
import com.citc.nce.robot.vo.RobotProcessTriggerNodeOneReq;
import com.citc.nce.robot.vo.RobotProcessTriggerNodeReq;
import com.citc.nce.robot.vo.RobotProcessTriggerNodeResp;
import com.citc.nce.robot.vo.RobotProcessTriggerNodesResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/11 16:12
 * @Version: 1.0
 * @Description:
 */

@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
public class RobotProcessTriggerNodeServicelmpI implements RobotProcessTriggerNodeService {

    @Resource
    private RobotProcessTriggerNodeDao robotProcessTriggerNodeDao;
    @Resource
    private RobotProcessSettingNodeDao processSetting;

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;

    @Override
    public int saveRobotProcessTriggerNodeReq(RobotProcessTriggerNodeReq robotProcessTriggerNodeReq) {
        if (!processSetting.exists(new LambdaQueryWrapperX<RobotProcessSettingNodeDo>()
                .eq(BaseDo::getId, robotProcessTriggerNodeReq.getProcessId())
                .eq(BaseDo::getCreator, SessionContextUtil.getUser().getUserId()))) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }

        RobotProcessTriggerNodeDo oldRobotProcessTriggerNodeDo = robotProcessTriggerNodeDao.selectOne((Wrappers.<RobotProcessTriggerNodeDo>lambdaQuery()
                .eq(RobotProcessTriggerNodeDo::getSceneId, robotProcessTriggerNodeReq.getSceneId())
                .eq(RobotProcessTriggerNodeDo::getProcessId, robotProcessTriggerNodeReq.getProcessId())
        ));
        if (oldRobotProcessTriggerNodeDo == null) {
            RobotProcessTriggerNodeDo robotProcessTriggerNodeDo = new RobotProcessTriggerNodeDo();
            BeanUtil.copyProperties(robotProcessTriggerNodeReq, robotProcessTriggerNodeDo);
            return robotProcessTriggerNodeDao.insert(robotProcessTriggerNodeDo);
        } else {
            BeanUtil.copyProperties(robotProcessTriggerNodeReq, oldRobotProcessTriggerNodeDo);
            return robotProcessTriggerNodeDao.update(oldRobotProcessTriggerNodeDo, Wrappers.<RobotProcessTriggerNodeDo>lambdaQuery()
                    .eq(RobotProcessTriggerNodeDo::getSceneId, robotProcessTriggerNodeReq.getSceneId())
                    .eq(RobotProcessTriggerNodeDo::getProcessId, robotProcessTriggerNodeReq.getProcessId()));
        }
    }

    @Override
    public List<RobotProcessTriggerNodesResp> getRobotProcessTriggerNodes(RobotProcessTriggerNodeReq robotProcessTriggerNodeReq) {
        HashMap<String, Object> vMap = new HashMap<>();
        if (StringUtils.isNotEmpty(robotProcessTriggerNodeReq.getCreate())) {
            vMap.put("creator", robotProcessTriggerNodeReq.getCreate());
        } else {
            String userId = SessionContextUtil.getUser().getUserId();
            if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
                vMap.put("creator", userId);
            }
        }
        vMap.put("account", robotProcessTriggerNodeReq.getAccount());
        if (robotProcessTriggerNodeReq.getSceneId() == null || robotProcessTriggerNodeReq.getSceneId() == 0) {
            List<RobotProcessTriggerNodesResp> RobotProcessTriggerNodesResps = robotProcessTriggerNodeDao.getRobotProcessTriggerNodeAlls(vMap);
            return RobotProcessTriggerNodesResps;
        } else {
            vMap.put("sceneId", robotProcessTriggerNodeReq.getSceneId());
            List<RobotProcessTriggerNodesResp> RobotProcessTriggerNodesResps = robotProcessTriggerNodeDao.getRobotProcessTriggerNodes(vMap);
            return RobotProcessTriggerNodesResps;
        }
    }

    @Override
    public RobotProcessTriggerNodeResp getRobotProcessTriggerNode(RobotProcessTriggerNodeOneReq robotProcessTriggerNodeOneReq) {
        RobotProcessTriggerNodeDo oldRobotProcessTriggerNodeDo = robotProcessTriggerNodeDao.selectOne((Wrappers.<RobotProcessTriggerNodeDo>lambdaQuery()
                .eq(RobotProcessTriggerNodeDo::getSceneId, robotProcessTriggerNodeOneReq.getSceneId())
                .eq(RobotProcessTriggerNodeDo::getProcessId, robotProcessTriggerNodeOneReq.getProcessId())
        ));
        if (oldRobotProcessTriggerNodeDo != null) {
            String userId = SessionContextUtil.getUser().getUserId();
            if (!userId.equals(oldRobotProcessTriggerNodeDo.getCreator())) {
                throw new BizException("场景不是你的");
            }
            RobotProcessTriggerNodeResp robotProcessTriggerNodeResp = new RobotProcessTriggerNodeResp();
            BeanUtil.copyProperties(oldRobotProcessTriggerNodeDo, robotProcessTriggerNodeResp);
            return robotProcessTriggerNodeResp;
        }
        return null;
    }

    @Override
    public List<RobotProcessTriggerNodesResp> getRobotProcessTriggerNodesByCreate(String create, String account) {
        HashMap<String, Object> vMap = new HashMap<>();
        vMap.put("creator", create);
        vMap.put("account", account);
        List<RobotProcessTriggerNodesResp> robotProcessTriggerNodeAlls = robotProcessTriggerNodeDao.getRobotProcessTriggerNodeAlls(vMap);
        if (CollectionUtils.isNotEmpty(robotProcessTriggerNodeAlls)) {
            List<RobotProcessTriggerNodesResp> triggerNodesResps = BeanUtil.copyToList(robotProcessTriggerNodeAlls, RobotProcessTriggerNodesResp.class);
            return triggerNodesResps;
        }
        return null;
    }
}
