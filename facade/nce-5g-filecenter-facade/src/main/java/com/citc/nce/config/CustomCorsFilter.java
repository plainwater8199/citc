package com.citc.nce.config;


import com.citc.nce.common.facadeserver.util.FacadeRequestUtil;
import com.citc.nce.configure.CorsConfigure;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

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
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        if (StringUtils.equals("1", String.valueOf(corsConfigure.getIsEnabled()))) {//是否开启
            if(facadeRequestUtil.checkIllegalURI(requestURI)){//请求地址校验
                String[] split = corsConfigure.getAllowOrigin().split(",");
                List<String> stringList = Arrays.asList(split);
                if (StringUtils.isNotEmpty(req.getHeader("origin"))) {//cors校验
                    if (!stringList.stream().anyMatch(a -> a.equals(req.getHeader("origin")))) {
                        log.info("corsConfigure allowOrigin: " + corsConfigure.getAllowOrigin());
                        log.info("origin : " + req.getHeader("origin"));
                        facadeRequestUtil.returnIllegalOriginResponseJson(response,corsConfigure.getAllowOrigin());
                        return;
                    }
                }
                split = corsConfigure.getAllowMethod().split(",");
                stringList = Arrays.asList(split);
                if (!stringList.stream().anyMatch(a -> a.equals(req.getMethod()))) {//请求方法校验
                    facadeRequestUtil.returnIllegalMethodResponseJson(response,corsConfigure.getAllowMethod());
                    return;
                }
            }else{
                facadeRequestUtil.returnIllegalURIResponseJson(response);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        facadeRequestUtil.getAllURI();
    }
}
