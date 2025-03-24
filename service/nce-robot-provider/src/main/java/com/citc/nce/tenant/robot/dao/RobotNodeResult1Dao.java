package com.citc.nce.tenant.robot.dao;

import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.tenant.robot.entity.RobotNodeResultDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2022-09-08
 */
@Mapper
public interface RobotNodeResult1Dao extends BaseMapperX<RobotNodeResultDo> {

    void createTable(@Param("tableName") String tableName);

    void dropTable(@Param("tableName") String tableName);

    void clearTable(@Param("tableName") String tableName);
}
