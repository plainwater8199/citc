package com.citc.nce.misc.tableInfo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Objects;

/**
 *
 * @author bydud
 * @since 2024/3/27
 */
@Data
public class TableInfo {
    @TableField(value = "column_name")
    private String columnName;
    @TableField(value = "data_type")
    private String dataType;
    @TableField(value = "column_key")
    private String columnKey;
    @TableField(value = "is_nullable")
    private String isNullable;
    @TableField(value = "character_maximum_length")
    private Long characterMaximumLength;
    @TableField(value = "numeric_precision")
    private Long numericPrecision;
    @TableField(value = "column_comment")
    private String columnComment;

    public String getType() {
        return this.dataType.split("\\(")[0];
    }

    public Long getLength() {
        if (Objects.nonNull(characterMaximumLength)) {
            return characterMaximumLength;
        }
        if (Objects.nonNull(numericPrecision)) {
            return numericPrecision;
        }
        return 0L;
    }

    public String isNull() {
        if ("NO".equals(isNullable)) return "否";
        return "是";
    }
    public String isUnique() {
        if ("UNI".equals(columnKey)) return "是";
        return "否";
    }
}
