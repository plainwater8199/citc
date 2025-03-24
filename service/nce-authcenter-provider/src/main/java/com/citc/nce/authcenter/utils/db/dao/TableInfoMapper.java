package com.citc.nce.authcenter.utils.db.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TableInfoMapper {

    List<Map<String, Object>> getTableColumns(@Param("tableName") String tableName);
}
