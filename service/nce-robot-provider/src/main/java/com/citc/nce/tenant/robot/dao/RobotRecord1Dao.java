package com.citc.nce.tenant.robot.dao;

import com.citc.nce.tenant.robot.entity.RobotRecordDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: yangchuang
 * @Date: 2022/10/16 14:56
 * @Version: 1.0
 * @Description:
 */
@Mapper
public interface RobotRecord1Dao extends BaseMapperX<RobotRecordDo> {

    void createTable(@Param("tableName") String tableName);

    void dropTable(@Param("tableName") String tableName);

    void clearTable(@Param("tableName") String tableName);
}
