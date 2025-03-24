package com.citc.nce.tenant.robot.dao;



import com.citc.nce.tenant.robot.entity.AimSentDataDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AimSentData1Dao extends BaseMapperX<AimSentDataDo> {
    void createTable(@Param("tableName") String tableName);

    void dropTable(@Param("tableName") String tableName);

    void clearTable(@Param("tableName") String tableName);
}
