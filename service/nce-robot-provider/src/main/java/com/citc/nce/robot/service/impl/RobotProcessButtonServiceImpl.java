package com.citc.nce.robot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citc.nce.robot.dao.RobotProcessButtonDao;
import com.citc.nce.robot.entity.RobotProcessButtonDo;
import com.citc.nce.robot.service.RobotProcessButtonService;
import com.citc.nce.robot.vo.RobotProcessButtonResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/6 10:03
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
public class RobotProcessButtonServiceImpl implements RobotProcessButtonService {
    @Resource
    private RobotProcessButtonDao robotProcessButtonDao;

    @Override
    public void deleteRobotProcessButtonDoByNodeId(Long nodeId, String userId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("deleted", 1);
        map.put("deleteTime", DateUtil.date());
        map.put("creator", userId);
        robotProcessButtonDao.deleteRobotProcessButtonDoByNodeId(map);
    }

    @Override
    public void saveRobotProcessButtonDoList(List<RobotProcessButtonDo> robotProcessButtonDos) {
       robotProcessButtonDao.insertBatch(robotProcessButtonDos);
    }

    @Override
    public List<RobotProcessButtonDo> getRobotProcessButtonList(Long id) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("node_id", id);
        wrapper.orderByDesc("create_time");
        List<RobotProcessButtonDo> robotProcessButtonDos = robotProcessButtonDao.selectList(wrapper);
        return robotProcessButtonDos;
    }

    @Override
    public RobotProcessButtonResp getButtonByUuid(String uuid) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("uuid", uuid);
        RobotProcessButtonDo robotProcessButtonDo = robotProcessButtonDao.selectOne(wrapper);
        if(robotProcessButtonDo!=null){
            RobotProcessButtonResp robotProcessButtonResp = BeanUtil.copyProperties(robotProcessButtonDo, RobotProcessButtonResp.class);
            return robotProcessButtonResp;
        }
        return null;
    }
}
