package com.citc.nce.common.web.core.interceptor;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.citc.nce.common.util.SessionContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 该拦截器主要拦截客户端请求聚合层的所有请求
 * 1、检查用户是否登录
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        try {
            //判断是否已登录
            StpUtil.checkLogin();
            //获取token并且保存用户
            return true;
        } catch (NotLoginException e) {
            log.warn("用户未登录，禁止访问:{}, reason: {}", requestURI, e.getMessage());
            throw new NotLoginException(e.getMessage(), e.getLoginType(), e.getType());
        } catch (Exception e) {
            throw NotLoginException.newInstance("PC", "login error");
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SessionContextUtil.remove();
    }
}
