package com.citc.nce.config;


import cn.hutool.json.JSONObject;
import com.citc.nce.authcenter.auth.AuthApi;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class CspUserStatusCheckInterceptor implements HandlerInterceptor {

    @Resource
    private AuthApi authApi;
    @Autowired
    private CspApi cspApi;
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
        BaseUser baseUser = SessionContextUtil.getUser();
        if(baseUser != null){
            boolean isCsp = cspApi.isCspInUser(baseUser.getUserId());
            if(!isCsp){//如果在user表中，但是不是csp，表示是新注册的用户
                return true;
            }
            String cspId = baseUser.getCspId();
            boolean result = cspApi.getCspStatus(cspId);
            if(!result){
                JSONObject jsonObject = new JSONObject();
                jsonObject.set("code", 401);
                jsonObject.set("msg", "该用户被禁用");
                returnJson(resp,jsonObject);
            }
            boolean isUpdate = authApi.checkCspUserPWIsUpdate(cspId);
            if(isUpdate){
                JSONObject jsonObject = new JSONObject();
                jsonObject.set("code", 401);
                jsonObject.set("msg", "信息发生更改,请重新登录!");
                returnJson(resp,jsonObject);
            }
        }
        return true;
    }

    private void returnJson(ServletResponse response, JSONObject json) {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
