package com.citc.nce.common.web.core.trace.filter;


import com.citc.nce.common.trace.TraceContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/25 14:47
 * @Version: 1.0
 * @Description:
 */
public class TraceFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String traceId = httpServletRequest.getHeader(TraceContext.getTraceKey());
            TraceContext.trace(traceId);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            TraceContext.remove();
        }
    }
}
