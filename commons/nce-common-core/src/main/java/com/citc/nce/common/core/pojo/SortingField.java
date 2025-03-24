package com.citc.nce.common.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/4/25 14:23
 * @Version: 1.0
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortingField implements Serializable {

    public enum ORDER {
        ORDER_ASC, ORDER_DESC;
    }

    /**
     * 字段
     */
    private String field;
    /**
     * 顺序
     */
    private ORDER order;


}
