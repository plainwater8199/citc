package com.citc.nce.tenant.robot.dao;

import com.citc.nce.tenant.robot.entity.TemporaryStatisticsDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * @Author: yangchuang
 * @Date: 2022/10/28 17:50
 * @Version: 1.0
 * @Description:
 */
@Mapper
public interface TemporaryStatistics1Dao extends BaseMapperX<TemporaryStatisticsDo> {

    void createTable(@Param("tableName") String tableName);

    void dropTable(@Param("tableName") String tableName);
}
