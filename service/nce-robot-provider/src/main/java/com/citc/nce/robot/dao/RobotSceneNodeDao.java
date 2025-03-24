package com.citc.nce.robot.dao;

import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.robot.entity.RobotSceneNodeDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/7 15:11
 * @Version: 1.0
 * @Description:
 */
public interface RobotSceneNodeDao extends BaseMapperX<RobotSceneNodeDo> {
    @Update("UPDATE robot_scene_node  SET deleted_time=#{deleteTime},deleted=#{deleted} WHERE id=#{id} ")
    int delRobotSceneNode(HashMap<String, Object> map);
    @Select("<script> " + "Select id, scene_name, scene_value, creator, creator_old, create_time, updater, updater_old, update_time, deleted, deleted_time, accounts, account_change_time  " +
            "FROM robot_scene_node " +
            "where 1 = 1 <if test = 'sceneIds != null and sceneIds.size() > 0'>and id in  <foreach item='item' index='index' collection='sceneIds' open='(' separator=',' close=')'> #{item} </foreach></if>"+"</script>")
    List<RobotSceneNodeDo> selectByIds(@Param("sceneIds")List<Long> sceneIds);

    Long getSceneNum(HashMap<String, Object> map);

    Long getProcessNum(HashMap<String, Object> map);
}
