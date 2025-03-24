package com.citc.nce.common.core.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/4/25 14:28
 * @Version: 1.0
 * @Description:
 */
@Data
public class EsPageResult<T> implements Serializable {

    private List<T> list;

    private Long total;

    private Map<String, Integer> types;

    private List<TagInfo> tags;

    public EsPageResult() {
    }

    public EsPageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public EsPageResult(List<T> list, Integer total) {
        this.list = list;
        this.total = (long) total;
    }

    public EsPageResult(Long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    public static <T> EsPageResult<T> empty() {
        return new EsPageResult<>(0L);
    }

    public static <T> EsPageResult<T> empty(Long total) {
        return new EsPageResult<>(total);
    }


}
