package com.citc.nce.robot.dao;

import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.robot.entity.RobotProcessTriggerNodeDo;
import com.citc.nce.robot.vo.RobotProcessTriggerNodesResp;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/8 16:21
 * @Version: 1.0
 * @Description:
 */
public interface RobotProcessTriggerNodeDao  extends BaseMapperX<RobotProcessTriggerNodeDo> {

    List<RobotProcessTriggerNodesResp> getRobotProcessTriggerNodes(HashMap<String, Object> vMap);

    List<RobotProcessTriggerNodesResp> getRobotProcessTriggerNodeAlls(HashMap<String, Object> vMap);

    @Update("UPDATE robot_process_trigger_node  SET deleted_time=#{deleteTime},deleted=#{deleted} WHERE scene_id=#{id} ")
    int delRobotProcessTriggerNode(HashMap<String, Object> map);

    @Update("UPDATE robot_process_trigger_node  SET deleted_time=#{deleteTime},deleted=#{deleted} WHERE process_id=#{id} ")
    int delRobotProcessTriggerNodeProcessId(HashMap<String, Object> map);
}
