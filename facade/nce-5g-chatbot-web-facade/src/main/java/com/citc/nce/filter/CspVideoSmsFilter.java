package com.citc.nce.filter;


import cn.hutool.json.JSONUtil;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.Objects;

@WebFilter(urlPatterns = {"/csp/videoSms/account/queryList",
        "/csp/videoSms/account/save",
        "/csp/videoSms/account/edit",
        "/csp/videoSms/account/updateStatus",
        "/csp/videoSms/account/delete",
        "/chatbot/videoSms/account/queryListChatbot",
        "/csp/orderRecharge/*",
        "/mediaSms/template/*",
        "/im/message/media/selectAll"})
@Slf4j
public class CspVideoSmsFilter implements Filter {

    @Resource
    CspCustomerApi cspCustomerApi;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        BaseUser user = SessionContextUtil.getUser();
        if (Objects.isNull(user)){
            servletResponse.getWriter().println(JSONUtil.toJsonStr(new Result(401,"请先登录")));
            return;
        }
        //查询用户对应的权限
        String permissions = cspCustomerApi.getUserPermission(user.getUserId());
        if (StringUtils.isEmpty(permissions)|| permissions.contains("3")){
            filterChain.doFilter(servletRequest,servletResponse);
        }else {
            servletResponse.setCharacterEncoding("utf8");
            servletResponse.getWriter().println(JSONUtil.toJsonStr(new Result(81001122,"当前用户权限已失效，请刷新重试")));
        }
    }

    @Override
    public void destroy() {

    }
}
