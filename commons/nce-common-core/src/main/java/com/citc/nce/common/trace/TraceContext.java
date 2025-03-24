package com.citc.nce.common.trace;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/25 14:41
 * @Version: 1.0
 * @Description: slf4j  MDC封装，在跨线程会丢失，需要配合ThreadTaskUtils使用，不要自己创建线程任务
 * mdc线程透传无感知方案：https://juejin.cn/post/6981831233911128072
 * 改变了logback的实现，没有采用
 */
@Slf4j
public class TraceContext {

    private static String TRACE_KEY = "bizTraceId";

    public static String getTraceKey() {
        return TRACE_KEY;
    }

    public static String trace(String traceId) {
        MDC.put(TRACE_KEY, traceId);
        return traceId;
    }

    public static String getTrace() {
        return MDC.get(TRACE_KEY);
    }

    public static void remove() {
        MDC.remove(TRACE_KEY);
    }
}
