package com.citc.nce.common.facadeserver.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.google.common.base.Strings;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import javax.annotation.Resource;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@Log4j2
public class FacadeRequestUtil {

    private static Set<String> urls = new HashSet<>();

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Resource
    ApplicationContext applicationContext;
    public String namcespace="";
    /**
     * 获取该聚合层中所有的请求地址，（自定义地址）
     */
    public void getAllURI() {
        namcespace= StrUtil.emptyToDefault(namcespace,"com.citc.nce");
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();
        for(Map.Entry<RequestMappingInfo, HandlerMethod> entry: handlerMethods.entrySet()){
            RequestMappingInfo key = entry.getKey();
            HandlerMethod value = entry.getValue();
            if(value.toString().startsWith(namcespace)){
                if(key != null){
                    PathPatternsRequestCondition pathPatternsCondition = key.getPathPatternsCondition();
                    if(pathPatternsCondition != null){
                        Set<PathPattern> patterns = pathPatternsCondition.getPatterns();
                        for(PathPattern item : patterns){
                            urls.add(item.getPatternString());
                        }
                    }
                    PatternsRequestCondition patternsCondition = key.getPatternsCondition();
                    if(patternsCondition != null){
                        Set<String> patterns = patternsCondition.getPatterns();
                        if(!CollectionUtils.isEmpty(patterns)){
                            urls.addAll(patterns);
                        }
                    }
                }
            }
        }
        // ws里面的接口
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(ServerEndpoint.class);
        for (Object bean : beansWithAnnotation.values()) {
            urls.add(bean.getClass().getAnnotation(ServerEndpoint.class).value());
        }
        //spring ws 心跳检测接口
        urls.add("/actuator/health/readiness");
        urls.add("/actuator/health/liveness");

    }

    /**
     * 校验是否未非法地址
     * @param requestURI 请求地址
     * @return 是否非法
     */
    public boolean checkIllegalURI(String requestURI) {
        for(String urlPath : urls){
            if(pathMatcher.match(urlPath,requestURI)){
                return true;
            }
        }
        return false;
    }

    /**
     * 错误响应--非法地址访问
     * @param response 响应
     */
    public void returnIllegalURIResponseJson(ServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("code", 410);
        jsonObject.set("msg", "非法访问");
        returnJson(response,jsonObject,null,null);
    }
    /**
     * 错误响应--非法方法访问
     * @param response 响应
     */
    public void returnIllegalMethodResponseJson(ServletResponse response,String allowMethods) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("code", 408);
        jsonObject.set("msg", "请求方法拦截");
        returnJson(response,jsonObject, "Access-Control-Allow-Methods", allowMethods);
    }
    /**
     * 错误响应--非法cors访问
     * @param response 响应
     */
    public void returnIllegalOriginResponseJson(ServletResponse response, String allowOrigin) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("code", 409);
        jsonObject.set("msg", "cors拦截");
        returnJson(response,jsonObject, "Access-control-Allow-Origin", allowOrigin);
    }

    private void returnJson(ServletResponse response, JSONObject json, String headerPre, String headerFix) {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if(Strings.isNullOrEmpty(headerPre) && Strings.isNullOrEmpty(headerFix)){
            httpServletResponse.setHeader(headerPre, headerFix);
        }
        try {
            writer = httpServletResponse.getWriter();
            writer.print(json);

        } catch (IOException e) {
            log.error("response error", e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
