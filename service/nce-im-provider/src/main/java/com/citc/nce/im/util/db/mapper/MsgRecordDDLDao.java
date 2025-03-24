package com.citc.nce.im.util.db.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

/**
 * @author jcrenc
 * @since 2025/1/15 9:57
 */
@Mapper
public interface MsgRecordDDLDao {
    @Select("SHOW INDEX FROM ${tableName}")
    List<Map<String, Object>> getTableIndexes(@Param("tableName") String tableName);

    @Update("ALTER TABLE ${tableName} DROP INDEX ${indexName}")
    void dropIndex(@Param("tableName")String tableName, @Param("indexName")String indexName);


    @Update("ALTER TABLE ${tableName} ADD INDEX ${indexName} (`${index}`)")
    void addIndex(@Param("tableName") String tableName, @Param("indexName") String indexName, @Param("index")String index);

}
