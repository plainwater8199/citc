package com.citc.nce.robot.dao;

import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.robot.entity.RobotSettingDo;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 17:28
 * @Version: 1.0
 * @Description:
 */
public interface RobotSettingDao  extends BaseMapperX<RobotSettingDo> {
    @Update("UPDATE robot_setting  SET delete_time=#{deleteTime},deleted=#{deleted} WHERE deleted=0 and creator=#{creator} ")
    void deleteRebotSettingReq(HashMap<String, Object> map);
}
