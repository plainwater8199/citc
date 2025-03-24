package com.citc.nce.robot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.robot.bean.RobotSceneMaterialBean;
import com.citc.nce.robot.dao.RobotProcessDesDao;
import com.citc.nce.robot.dao.RobotSceneMaterialDao;
import com.citc.nce.robot.entity.RobotProcessDesDo;
import com.citc.nce.robot.entity.RobotSceneMaterialDo;
import com.citc.nce.robot.service.RobotSceneMaterialService;
import com.citc.nce.robot.vo.RobotSceneMaterialReq;
import com.citc.nce.robot.vo.RobotSceneMaterialResp;
import com.citc.nce.robot.vo.SceneMaterialReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 17:12
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
public class RobotSceneMaterialServiceImpl implements RobotSceneMaterialService {

    @Resource
    RobotSceneMaterialDao robotSceneMaterialDao;
    @Resource
    RobotProcessDesDao robotProcessDesDao;

    @Override
    @Transactional
    public int saveSceneMaterial(RobotSceneMaterialReq robotSceneMaterialReq) {
        if (robotSceneMaterialReq == null || robotSceneMaterialReq.getSceneMaterialReqList() == null) {
            return 0;
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("scene_id", robotSceneMaterialReq.getSceneId());
        queryWrapper.eq("process_id", robotSceneMaterialReq.getProcessId());
        robotSceneMaterialDao.delete(queryWrapper);
        for (SceneMaterialReq sceneMaterialReq : robotSceneMaterialReq.getSceneMaterialReqList()) {
            if (null == sceneMaterialReq.getMaterialId()) {
                break;
            }
            RobotSceneMaterialDo robotSceneMaterialDo = new RobotSceneMaterialDo();
            robotSceneMaterialDo.setSceneId(robotSceneMaterialReq.getSceneId());
            robotSceneMaterialDo.setMaterialId(sceneMaterialReq.getMaterialId());
            robotSceneMaterialDo.setMaterialType(sceneMaterialReq.getMaterialType());
            robotSceneMaterialDo.setProcessId(sceneMaterialReq.getProcessId());
            robotSceneMaterialDao.insert(robotSceneMaterialDo);
        }
        return 1;
    }

    @Override
    public RobotSceneMaterialResp getRobotSceneMaterial(RobotSceneMaterialReq robotSceneMaterialReq) {
        Long sceneId = robotSceneMaterialReq.getSceneId();
        List<RobotSceneMaterialDo> robotSceneMaterialDos = robotSceneMaterialDao.selectList(
                Wrappers.<RobotSceneMaterialDo>lambdaQuery().eq(RobotSceneMaterialDo::getSceneId, sceneId)
                        .eq(RobotSceneMaterialDo::getDeleted, 0));
        Set<Long> processIdSet = new HashSet<>();
        Map<Long, Long> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(robotSceneMaterialDos)) {
            String userId = SessionContextUtil.getUser().getUserId();
            if (!robotSceneMaterialDos.stream().map(RobotSceneMaterialDo::getCreator)
                    .distinct().allMatch(s -> s.equals(userId))) {
                throw new BizException("场景不是你的");
            }

            robotSceneMaterialDos.forEach(robotSceneMaterialDo -> processIdSet.add(robotSceneMaterialDo.getProcessId()));
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.in("process_id", processIdSet);
            queryWrapper.eq("deleted", 0);
            queryWrapper.eq("release_type", 1);
            List<RobotProcessDesDo> robotProcessDesDoList = robotProcessDesDao.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(robotProcessDesDoList)) {
                robotProcessDesDoList.forEach(robotProcessDesDo -> map.put(robotProcessDesDo.getProcessId(), robotProcessDesDo.getProcessId()));
            }
        }
        RobotSceneMaterialResp robotSceneMaterialResp = new RobotSceneMaterialResp();
        List<RobotSceneMaterialBean> list = new ArrayList<>();
        for (RobotSceneMaterialDo robotSceneMaterialDo : robotSceneMaterialDos) {
            if (null == map.get(robotSceneMaterialDo.getProcessId())) {
                break;
            }
            RobotSceneMaterialBean robotSceneMaterialBean = new RobotSceneMaterialBean();
            BeanUtils.copyProperties(robotSceneMaterialDo, robotSceneMaterialBean);
            list.add(robotSceneMaterialBean);
        }
        robotSceneMaterialResp.setRobotSceneMaterialBeanList(list);
        return robotSceneMaterialResp;
    }
}
