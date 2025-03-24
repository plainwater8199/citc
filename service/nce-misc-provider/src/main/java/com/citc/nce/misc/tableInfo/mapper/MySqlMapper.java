package com.citc.nce.misc.tableInfo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 *
 * @author bydud
 * @since 2024/3/27
 */
@Mapper
public interface MySqlMapper {
    Map<String, String> selectCreateTable(@Param("tableName") String tableName);

    void executeDDlSql(@Param("ddl") String ddl);
}
