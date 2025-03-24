package com.citc.nce.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageParam;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author bydud
 * @date 2022/3/9 12:49
 */
public class PageSupport<T> {

    /**
     * 封装分页对象
     */
    public static <T> Page<T> getPage(Class<T> tClass, PageParam param) {
        if (Objects.isNull(param.getPageNo()) || Objects.isNull(param.getPageSize())) {
            throw new BizException(500, "分页参数不能为空");
        }
        return Page.of(param.getPageNo(), param.getPageSize());
    }

    /**
     * 封装分页对象
     */
    public static <T> Page<T> getPage(Class<T> tClass, Long pageNum, Long pageSize) {
        if (Objects.isNull(pageNum) || Objects.isNull(pageSize)) {
            throw new BizException(500, "分页参数不能为空");
        }
        return Page.of(pageNum, pageSize);
    }

    /**
     * 对List进行分页处理
     *
     * @param list     需要分页的List
     * @param pageSize 单页条数
     * @param pageNum  页码
     */
    public static <T> List<T> subPageList(List<T> list, int pageSize, int pageNum) {
        if (list == null || list.isEmpty()) {
            return list;
        }
        int offset = (pageNum - 1) * pageSize;
        int size = Math.min(pageNum * pageSize, list.size());
        if (offset > size) {
            return Collections.emptyList();
        }
        return list.subList(offset, size);
    }
}
