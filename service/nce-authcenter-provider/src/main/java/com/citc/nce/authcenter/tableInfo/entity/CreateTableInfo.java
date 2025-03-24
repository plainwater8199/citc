package com.citc.nce.authcenter.tableInfo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 *
 * @author bydud
 * @since 2024/4/17
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CreateTableInfo {
    private String tableName;
    private String sql;
}
