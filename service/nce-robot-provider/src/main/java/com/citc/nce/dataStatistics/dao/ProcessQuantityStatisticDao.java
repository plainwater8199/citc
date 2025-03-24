package com.citc.nce.dataStatistics.dao;

import com.citc.nce.dataStatistics.entity.ProcessQuantityStatisticDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProcessQuantityStatisticDao extends BaseMapperX<ProcessQuantityStatisticDo> {
    void createTable(@Param("tableName") String tableName);
}
