package com.citc.nce.tenant.robot.dao;


import com.citc.nce.tenant.robot.entity.ConversationalQuantityStatisticDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ConversationalQuantityStatistic1Dao extends BaseMapperX<ConversationalQuantityStatisticDo> {
    void createTable(@Param("tableName") String tableName);

    void dropTable(@Param("tableName") String tableName);

    void clearTable(@Param("tableName") String tableName);
}
