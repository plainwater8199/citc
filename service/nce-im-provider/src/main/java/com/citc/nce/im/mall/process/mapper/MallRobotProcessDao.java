package com.citc.nce.im.mall.process.mapper;

import com.citc.nce.im.mall.process.entity.MallRobotProcessDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/7 16:20
 * @Version: 1.0
 * @Description:
 */
public interface MallRobotProcessDao extends BaseMapperX<MallRobotProcessDo> {
    @Select("<script> " + "Select id, process_id, template_id, process_name, process_value, derail, creator, create_time, updater, update_time, deleted, deleted_time, modified_time, release_time  " +
            "FROM mall_robot_process " +
            "where id in <foreach item='item' index='index' collection='processIds' open='(' separator=',' close=')'> #{item} </foreach>" + "</script>")
    List<MallRobotProcessDo> selectByIds(@Param("processIds") List<Long> processIds);
}
