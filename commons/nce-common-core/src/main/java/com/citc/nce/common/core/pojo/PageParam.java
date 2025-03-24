package com.citc.nce.common.core.pojo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/4/25 14:23
 * @Version: 1.0
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageParam {
    private static final Integer PAGE_NO = 1;
    private static final Integer PAGE_SIZE = 10;

    private Integer pageNo = PAGE_NO;

    private Integer pageSize = PAGE_SIZE;

    /**
     * 是否count查询
     */
    private boolean count = true;

}
