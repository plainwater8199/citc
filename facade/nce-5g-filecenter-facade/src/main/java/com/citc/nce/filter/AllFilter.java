package com.citc.nce.filter;


import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.configure.BaiduSensitiveCheckConfigure;
import com.citc.nce.filter.pojo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(BaiduSensitiveCheckConfigure.class)
public class AllFilter implements Filter {

    @Resource
    BaiduService baiduService;


    private final BaiduSensitiveCheckConfigure baiduSensitiveCheckConfigure;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        RequestWrapper requestWrapper = new RequestWrapper(httpServletRequest);
        //审核开关
        if (!baiduSensitiveCheckConfigure.getIsExamine()) {
            filterChain.doFilter(requestWrapper, servletResponse);
            return;
        }

        String bodyString = requestWrapper.getBodyString();
        JSONObject jsonObject = baiduService.textCensorUrl(bodyString);
        log.info("送审文本");
        if (jsonObject.getInteger("conclusionType") == 1) {
            filterChain.doFilter(requestWrapper, servletResponse);
        } else {
            servletResponse.setCharacterEncoding("utf-8");
            servletResponse.getWriter().println(JSONUtil.toJsonStr(new Result(81001152, "请求失败，包含敏感信息或不符合相关规定")));
        }
    }

    @Override
    public void destroy() {

    }
}
