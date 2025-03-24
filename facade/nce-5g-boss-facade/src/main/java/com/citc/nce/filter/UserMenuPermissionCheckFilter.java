package com.citc.nce.filter;


import cn.hutool.json.JSONObject;
import com.citc.nce.annotation.BossAuth;
import com.citc.nce.common.CommonConstant;
import com.citc.nce.common.Constants;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.configure.H5InterfaceConfigure;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 管理平台分别管理几个不同的平台，而每位管理员都有属于自己的平台权限。
 * 该过滤器用于对用户的权限进行检查。
 */
@RestControllerAdvice
@Slf4j
@Order(5)
public class UserMenuPermissionCheckFilter implements Filter {

    private final H5InterfaceConfigure h5InterfaceConfigure;

    @Resource
    private RedisService redisService;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    public UserMenuPermissionCheckFilter(H5InterfaceConfigure h5InterfaceConfigure) {
        this.h5InterfaceConfigure = h5InterfaceConfigure;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {


        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String token = httpServletRequest.getHeader(Constants.HttpHeader.TOKEN);
            if (httpServletRequest.getRequestURI().endsWith("/admin/user/login") || httpServletRequest.getRequestURI().endsWith("/admin/user/logout") || httpServletRequest.getRequestURI().endsWith("/admin/user/queryCurrentCommunityAdminBaseInfo") || httpServletRequest.getRequestURI().endsWith("/admin/user/queryAdminAuthList")) {
                chain.doFilter(request, response);
                return;
            }

            if (!Strings.isNullOrEmpty(token)) {
                String uri = httpServletRequest.getHeader("5g-uri");
                String platform = httpServletRequest.getHeader("platform");
//                List<String> H5_INTERFACE = Arrays.asList(h5InterfaceConfigure.getApis().split(","));
//                List<String> H5_WEB=Arrays.asList(h5InterfaceConfigure.getUris().split(","));

                /*
                 *H5 部分接口访问控制
                 */
                if(httpServletRequest.getRequestURI().startsWith("/h5/") || httpServletRequest.getRequestURI().startsWith("//h5/")){
                    chain.doFilter(request, response);
                    return;
                }

                if(!Strings.isNullOrEmpty(platform) && !Strings.isNullOrEmpty(uri)){
                    boolean checkURI = checkURIForInterface(httpServletRequest,uri);//校验uri是否被篡改
                    if (checkURI) {
                        log.info("---------------------platform-----------------------:" + platform);
                        Map<String, List<String>> menuMap = redisService.getCacheObject("USER_ID:" + SessionContextUtil.getLoginUser().getUserId());
                        log.info("menuMap {}", menuMap);
                        if (null == menuMap || menuMap.isEmpty() || null == menuMap.get(platform) || !menuMap.get(platform).contains(uri)) {
                            returnJson(response, 9999, "权限限制，不允许被访问");
                            return;
                        }
                    }else{
                        returnJson(response, 9999, "权限限制，不允许被访问");
                        return;
                    }
                }else{
                    returnJson(response, 9999, "权限限制，不允许被访问");
                    return;
                }
            }
            chain.doFilter(request, response);
        } catch (BizException e) {
            e.printStackTrace();
            // 传递异常信息
            request.setAttribute("filterError", e);
            // 指定处理该请求的处理器
            request.getRequestDispatcher(CommonConstant.ERROR_CONTROLLER_PATH).forward(request, response);
        }
    }

    private boolean checkURIForInterface(HttpServletRequest httpServletRequest, String uri) {
        // 通过 HandlerMapping 获取 HandlerMethod
        try {
            log.info("check uri :{}", uri);
            HandlerMethod handlerMethod  = (HandlerMethod) Objects.requireNonNull(requestMappingHandlerMapping.getHandler(httpServletRequest)).getHandler();
            boolean isTrue = false;//是否包含
            boolean isAdd = false;//是否添加
            BossAuth methodAnnotation = handlerMethod.getMethodAnnotation(BossAuth.class);
            if(methodAnnotation != null){
                isAdd = true;
                String[] value = methodAnnotation.value();
                log.info("method value :{}", value[0]);
                isTrue =Arrays.asList(value).contains(uri);
            }
            if(!isTrue){
                Method method = handlerMethod.getMethod();
                Class<?> clazz = method.getDeclaringClass();
                // 根据接口名称获取该接口对应类上的指定注解
                if(clazz.isAnnotationPresent(BossAuth.class)){
                    isAdd = true;
                    BossAuth classAnnotation = clazz.getAnnotation(BossAuth.class);
                    String[] value = classAnnotation.value();
                    log.info("class value :{}", value[0]);
                    isTrue =Arrays.asList(value).contains(uri);
                }
            }
            if(isAdd){//如果添加了就以校验为准
                return isTrue;
            }else{//默认通过
                return true;
            }
        } catch (Exception e) {
            return true;
        }
    }

    private void returnJson(ServletResponse response, int errorCode, String errorMessage) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("code", errorCode);
        jsonObject.set("msg", errorMessage);
        try (PrintWriter writer = response.getWriter()) {
            writer.print(jsonObject);
        } catch (IOException e) {
            log.error("response error", e);
        }
    }
}
