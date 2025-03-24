package com.citc.nce.config;


import cn.hutool.json.JSONObject;
import com.citc.nce.authcenter.auth.AuthApi;
import com.citc.nce.authcenter.auth.vo.req.QueryUserInfoDetailReq;
import com.citc.nce.authcenter.auth.vo.resp.QueryUserInfoDetailResp;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.web.utils.UserTokenUtil;
import com.citc.nce.configure.UrlWhitelistConfigure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

@Slf4j
public class CheckAuthUriInterceptor implements HandlerInterceptor {

    @Resource
    private AuthApi authApi;
    @Resource
    private UrlWhitelistConfigure urlWhitelistConfigure;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
        BaseUser baseUser = SessionContextUtil.getUser();
        if (null == baseUser || baseUser.isAuthStatus()) return true;

        if (Boolean.TRUE.equals(authApi.inUserDB(baseUser.getUserId()))) {
            log.info("从数据库中检测CSP用是否审核通过==============={}", baseUser.getUserId());
            String requestURI = req.getRequestURI().replace("//","/");
            QueryUserInfoDetailReq userInfoDetailReq = new QueryUserInfoDetailReq();
            userInfoDetailReq.setProtalType(3);
            QueryUserInfoDetailResp userInfoDetail = authApi.userInfoDetail(userInfoDetailReq);
            Integer enterpriseAuthStatus = userInfoDetail.getEnterpriseAuthStatus();
            //如果认证失败，则用户可调用的接口
            if (enterpriseAuthStatus != 3) {
                String authUrl = urlWhitelistConfigure.getAuth();
                checkoutUrl(requestURI, authUrl, resp);
            }else{
                if (userInfoDetail.getApprovalStatus() != 3) {
                    String applyUrl = urlWhitelistConfigure.getApply();
                    checkoutUrl(requestURI, applyUrl, resp);
                }
            }
            if (3 == enterpriseAuthStatus && 3 == userInfoDetail.getApprovalStatus()) {
                baseUser.setAuthStatus(true);
                UserTokenUtil.refreshTokenUser(req, baseUser);
            }
        }

        return true;
    }

    private void checkoutUrl(String uri, String urls, HttpServletResponse resp) {
        boolean isOk = Arrays.stream(urls.split(","))
                .anyMatch(uri::startsWith);
        if (!isOk) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("code", 500);
            jsonObject.set("msg", "该用户无访问此接口的权限");
            returnJson(resp, jsonObject);
        }
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
