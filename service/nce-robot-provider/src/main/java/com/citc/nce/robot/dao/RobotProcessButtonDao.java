package com.citc.nce.robot.dao;

import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.robot.entity.RobotProcessButtonDo;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;

/**
 * @Author: yangchuang
 * @Date: 2022/7/6 10:06
 * @Version: 1.0
 * @Description:
 */
public interface RobotProcessButtonDao  extends BaseMapperX<RobotProcessButtonDo> {
    @Update("UPDATE robot_process_button  SET delete_time=#{deleteTime},deleted=#{deleted} WHERE deleted=0  and creator=#{creator} ")
    void deleteRobotProcessButtonDoByNodeId(HashMap<String, Object> map);
}
