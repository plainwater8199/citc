package com.citc.nce.tenant.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TableInfoMapper {

    List<Map<String, Object>> getTableColumns(@Param("tableName") String tableName);
}
