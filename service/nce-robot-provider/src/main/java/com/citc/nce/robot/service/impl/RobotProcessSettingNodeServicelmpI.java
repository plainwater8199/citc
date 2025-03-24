package com.citc.nce.robot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.robot.Consts.Constants;
import com.citc.nce.robot.Consts.ProcessStatusEnum;
import com.citc.nce.robot.dao.RobotProcessSettingNodeDao;
import com.citc.nce.robot.dao.RobotProcessTriggerNodeDao;
import com.citc.nce.robot.entity.RobotProcessSettingNodeDo;
import com.citc.nce.robot.exception.RobotErrorCode;
import com.citc.nce.robot.service.RobotProcessSettingNodeService;
import com.citc.nce.robot.service.RobotProcessTreeService;
import com.citc.nce.robot.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.citc.nce.common.core.exception.GlobalErrorCode.USER_AUTH_ERROR;

/**
 * 流程管理
 *
 * @Author: yangchuang
 * @Date: 2022/7/8 11:16
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
public class RobotProcessSettingNodeServicelmpI implements RobotProcessSettingNodeService {

    @Resource
    private RobotProcessSettingNodeDao robotProcessSettingNodeDao;

    @Resource
    private RobotProcessTriggerNodeDao robotProcessTriggerNodeDao;
@Resource
private RobotProcessTreeService robotProcessTreeService;

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;

    @Override
    public PageResultResp getRobotProcessSettings(RobotProcessSettingNodePageReq req) {
        String userId = SessionContextUtil.getUser().getUserId();
        boolean isSuperAdmin = StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator());
        Page<RobotProcessSettingNodeDo> page = new Page<>(req.getPageParam().getPageNo(), req.getPageParam().getPageSize());
        page.setOrders(OrderItem.descs("process.create_time"));
        Page<RobotProcessSettingNodeResp> page1 =  robotProcessSettingNodeDao.selectProcessByScene(req.getSceneId(), isSuperAdmin ? null : userId, page);
       if(ObjectUtil.isNotEmpty(page1.getRecords()))
       {
           page1.getRecords().forEach(item->item.setStatusDesc(ProcessStatusEnum.getStatusEnum(item.getStatus()).getDesc()));
       }
        return new PageResultResp<>(page1.getRecords(), page1.getTotal(), req.getPageParam().getPageNo());
    }

    @Override
    public RobotProcessSettingNodeResp getRobotProcessOne(Long id) {
        QueryWrapper<RobotProcessSettingNodeDo> wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("id", id);
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        RobotProcessSettingNodeDo robotProcessSettingNodeDos = robotProcessSettingNodeDao.selectOne(wrapper);
        RobotProcessSettingNodeResp robotProcessSettingNodeResp=BeanUtil.copyProperties(robotProcessSettingNodeDos,RobotProcessSettingNodeResp.class);
        robotProcessSettingNodeResp.setStatusDesc(ProcessStatusEnum.getStatusEnum(robotProcessSettingNodeResp.getStatus()).getDesc());
        return robotProcessSettingNodeResp;
    }

    @Override
    public Long saveRobotProcessSetting(RobotProcessSettingNodeReq robotProcessSettingNodeReq) {
        QueryWrapper<RobotProcessSettingNodeDo> wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("scene_id", robotProcessSettingNodeReq.getSceneId());
        wrapper.eq("process_name", robotProcessSettingNodeReq.getProcessName());
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        List<RobotProcessSettingNodeDo> robotProcessSettingNodeDos = robotProcessSettingNodeDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(robotProcessSettingNodeDos)) {
            throw new BizException(RobotErrorCode.RobotProcessSettingNode_DATA_EXIST);
        }
        RobotProcessSettingNodeDo robotProcessSettingNodeDo = new RobotProcessSettingNodeDo();
        BeanUtil.copyProperties(robotProcessSettingNodeReq, robotProcessSettingNodeDo);
        robotProcessSettingNodeDo.setStatus(ProcessStatusEnum.Waiting.getCode());
        robotProcessSettingNodeDo.setSceneAccountChangeStatus(Constants.Scene_Account_Change_status_Normal);
        robotProcessSettingNodeDao.insert(robotProcessSettingNodeDo);
        return robotProcessSettingNodeDo.getId();
    }

    @Override
    public int updateRobotProcessSetting(RobotProcessSettingNodeEditReq robotProcessSettingNodeEditReq) {
        QueryWrapper<RobotProcessSettingNodeDo> wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("scene_id", robotProcessSettingNodeEditReq.getSceneId());
        wrapper.eq("process_name", robotProcessSettingNodeEditReq.getProcessName());
        wrapper.ne("id", robotProcessSettingNodeEditReq.getId());
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        List<RobotProcessSettingNodeDo> robotProcessSettingNodeDos = robotProcessSettingNodeDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(robotProcessSettingNodeDos)) {
            for (RobotProcessSettingNodeDo robotProcessSettingNodeDo : robotProcessSettingNodeDos) {
                if (!robotProcessSettingNodeDo.getId().equals(robotProcessSettingNodeEditReq.getId())) {
                    throw new BizException(RobotErrorCode.RobotProcessSettingNode_DATA_EXIST);
                }
            }
        }
        RobotProcessSettingNodeDo nodeDo = robotProcessSettingNodeDao.selectById(robotProcessSettingNodeEditReq.getId());
        if (Objects.isNull(nodeDo)){
            throw new BizException("流程不存在");
        }
        if(nodeDo.getStatus()==ProcessStatusEnum.Appending.getCode())
        {
            throw new BizException("流程发布中，不能编辑");
        }
        if (!SessionContextUtil.getLoginUser().getUserId().equals(nodeDo.getCreator())){
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }

       // RobotProcessSettingNodeDo robotProcessSettingNodeDo = new RobotProcessSettingNodeDo();
        BeanUtil.copyProperties(robotProcessSettingNodeEditReq, nodeDo);
        return robotProcessSettingNodeDao.updateById(nodeDo);
    }

    @Override
    @Transactional
    public int deleteRobotProcessSettingById(Long id) {
        try {
            RobotProcessSettingNodeDo process = robotProcessSettingNodeDao.selectById(id);
            String userId = SessionContextUtil.getUser().getUserId();
            if (!process.getCreator().equals(userId)) {
                throw new BizException("流程不是你的");
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("deleted", 1);
            map.put("deleteTime", DateUtil.date());
            //删除流程管理对应触发器
            robotProcessTriggerNodeDao.delRobotProcessTriggerNodeProcessId(map);
            robotProcessTreeService.deleteProcessDesc(map);
            return robotProcessSettingNodeDao.delRobotProcessSettingNodeId(map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<RobotProcessSettingNodeResp> getRobotProcessSettingNodes() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }

        //根据创建时间排序
        wrapper.orderByDesc("create_time");
        List<RobotProcessSettingNodeDo> robotProcessSettingNodeDos = robotProcessSettingNodeDao.selectList(wrapper);
        List<RobotProcessSettingNodeResp> robotProcessSettingNodeResps = BeanUtil.copyToList(robotProcessSettingNodeDos, RobotProcessSettingNodeResp.class);
        return robotProcessSettingNodeResps;
    }

    @Override
    public int updateRobotProcessSettingDerailById(RobotProcessSettingNodeEditDerailReq robotProcessSettingNodeEditDerailReq) {
        RobotProcessSettingNodeDo robotProcessSettingNodeDo1 = robotProcessSettingNodeDao.selectById(robotProcessSettingNodeEditDerailReq.getId());
        String userId = SessionContextUtil.getUser().getUserId();
        if(userId.equals(robotProcessSettingNodeDo1.getCreator())){
            robotProcessSettingNodeDo1.setDerail(robotProcessSettingNodeEditDerailReq.getDerail());
            return robotProcessSettingNodeDao.updateById(robotProcessSettingNodeDo1);
        }else{
            throw new BizException(USER_AUTH_ERROR);
        }

    }

    @Override
    public RobotProcessSettingNodeResp getRobotProcessSettingById(Long id) {
        RobotProcessSettingNodeDo robotProcessSettingNodeDo = robotProcessSettingNodeDao.selectOne(Wrappers.<RobotProcessSettingNodeDo>lambdaQuery()
                .eq(RobotProcessSettingNodeDo::getDeleted, 0)
                .eq(RobotProcessSettingNodeDo::getId, id));
        RobotProcessSettingNodeResp robotProcessSettingNodeResp = BeanUtil.copyProperties(robotProcessSettingNodeDo, RobotProcessSettingNodeResp.class);
        return robotProcessSettingNodeResp;
    }

}
