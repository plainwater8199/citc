package com.citc.nce.readingLetter.config;

import com.citc.nce.common.facadeserver.util.FacadeRequestUtil;
import com.citc.nce.readingLetter.configure.CorsConfigure;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 请求校验
 *1、非法地址校验
 *2、cros校验
 *3、请求方法校验
 */
@Component
@Log4j2
@RequiredArgsConstructor
@EnableConfigurationProperties(CorsConfigure.class)
public class CustomCorsFilter implements Filter {
    @Resource
    private final FacadeRequestUtil facadeRequestUtil;

    private final CorsConfigure corsConfigure;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();
            if(!facadeRequestUtil.checkIllegalURI(requestURI)){//请求地址校验
                facadeRequestUtil.returnIllegalURIResponseJson(response);
                return;
            }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        facadeRequestUtil.namcespace="com.citc.nce.readingLetter";
        facadeRequestUtil.getAllURI();
    }
}
