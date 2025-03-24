package com.citc.nce.tenant.robot.dao;


import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.tenant.robot.entity.RobotMassQuantityStatisticsDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 群发数量统计表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2022-11-21
 */
@Mapper
public interface RobotMassQuantityStatistics1Dao extends BaseMapperX<RobotMassQuantityStatisticsDo> {

    void createTable(@Param("tableName") String tableName);

    void dropTable(@Param("tableName") String tableName);

    void clearTable(@Param("tableName") String tableName);
}
