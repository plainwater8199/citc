package com.citc.nce.authcenter.tableInfo.service;

import com.citc.nce.authcenter.tableInfo.dao.MySqlMapper;
import com.citc.nce.authcenter.tableInfo.entity.CreateTableInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *  表结构查询
 * @author bydud
 * @since 2024/4/17
 */
@Component
public class TableInfoService {

    @Autowired
    private MySqlMapper mySqlMapper;

    public CreateTableInfo getCreateTableDDl(String tableName) {
        Map<String, String> createTable = mySqlMapper.selectCreateTable(tableName);

        return new CreateTableInfo(createTable.get("Table"), createTable.get("Create Table"));
    }

    public void executeDDlSql(String ddl) {
        mySqlMapper.executeDDlSql(ddl);
    }

    public boolean existTable(String table) {
        try {
            getCreateTableDDl(table);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
