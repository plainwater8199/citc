package com.citc.nce.robot.dao;

import com.citc.nce.dataStatistics.vo.resp.ScenarioFlowCountResp;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.robot.entity.RobotOrderDo;
import com.citc.nce.robot.entity.RobotProcessDesDo;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.dao
 * @Author: weilanglang
 * @CreateTime: 2022-07-07  17:19
 
 * @Version: 1.0
 */
public interface RobotProcessDesDao extends BaseMapperX<RobotProcessDesDo> {

    @Select("SELECT SUM(pose_num) AS questionsNum,SUM(branch_num) AS branchNum,SUM(send_num) AS sentMessagesNum,SUM(instruction_node) AS instructionNodesNum,SUM(variable_operation) AS variableOperationsNum,SUM(sub_process) AS subprocessesNum,SUM(contact_action) AS contactOperationsNum FROM robot_process_des WHERE deleted=0 AND creator=#{userId}")
    ScenarioFlowCountResp selectRobotProcessDesCount(String userId);

    @Update("UPDATE robot_process_des  SET deleted_time=#{deleteTime},deleted=#{deleted} WHERE process_id=#{id} ")
    int delRobotProcessDescProcessId(HashMap<String, Object> map);
}
