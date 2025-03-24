package com.citc.nce.robot.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.robot.entity.RobotProcessSettingNodeDo;
import com.citc.nce.robot.vo.RobotProcessSettingNodeResp;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yangchuang
 * @Date: 2022/7/7 16:20
 * @Version: 1.0
 * @Description:
 */
public interface RobotProcessSettingNodeDao extends BaseMapperX<RobotProcessSettingNodeDo> {
    @Select("SELECT scene_id AS sceneId,COUNT(*) AS nums FROM robot_process_setting_node WHERE deleted=0 and creator=#{userId} GROUP BY scene_id")
    List<Map<String, Long>> selectCountBySceneId(String userId);

    @Update("UPDATE robot_process_setting_node  SET deleted_time=#{deleteTime},deleted=#{deleted} WHERE scene_id=#{id} ")
    int delRobotProcessSettingNode(HashMap<String, Object> map);

    @Update("UPDATE robot_process_setting_node  SET deleted_time=#{deleteTime},deleted=#{deleted} WHERE id=#{id} ")
    int delRobotProcessSettingNodeId(HashMap<String, Object> map);

    @Select("SELECT scene_id AS sceneId,COUNT(*) AS nums FROM robot_process_setting_node WHERE deleted=0 GROUP BY scene_id")
    List<Map<String, Long>> selectCountBySceneIdAdmin();

    @Select("<script> " + "Select id, scene_id, process_name, process_value, derail, creator, creator_old, create_time, updater, updater_old, update_time, deleted, deleted_time, modified_time, release_time, accounts, status, related_scene_accounts, scene_account_change_status, template_id, account_change_time, audit_result  " +
            "FROM robot_process_setting_node " +
            "where id in <foreach item='item' index='index' collection='processIds' open='(' separator=',' close=')'> #{item} </foreach>" + "</script>")
    List<RobotProcessSettingNodeDo> selectByIds(@Param("processIds") List<Long> processIds);


    Page<RobotProcessSettingNodeResp> selectProcessByScene(@Param("sceneId") Long sceneId, @Param("creator") String creator, Page<RobotProcessSettingNodeDo> page);
}
