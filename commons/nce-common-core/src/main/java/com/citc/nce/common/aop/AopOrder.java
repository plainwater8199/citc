package com.citc.nce.common.aop;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/24 16:42
 * @Version: 1.0
 * @Description:
 */
public interface AopOrder {

    int ORDER_START = Integer.MIN_VALUE;
    /**
     * 日志记录切面order
     */
    int LOG_ORDER = ORDER_START;
    /**
     * 防重复请求order
     */
    int NOREPEAT_CHECK_ORDER = ORDER_START + 1;
    /**
     * 缓存order
     */
    int CACHE_ORDER = ORDER_START + 2;
}
