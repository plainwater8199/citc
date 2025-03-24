package com.citc.nce.filter;

import cn.hutool.json.JSONUtil;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.common.util.SessionContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;


@WebFilter(urlPatterns = {"/csp/sms/account/queryList",
        "/csp/sms/account/queryDetail",
        "/csp/sms/account/save",
        "/csp/sms/account/edit",
        "/csp/sms/account/updateStatus",
        "/csp/sms/account/delete",
        "/chatbot/sms/account/queryListChatbot",
        "/csp/sms/orderRecharge/*",
        "/sms/template",
        "/sms/template/update",
        "/sms/template/search",
        "/sms/template/{templateId}",
        "/sms/template/delete",
        "/sms/template/delete/check",
        "/sms/template/report/{templateId}",
        "/sms/template/accounts",
        "/sms/template/refresh/auditStatus",
        "/sms/template/testSending",
        "/im/message/shortMsg/selectAll"}
)
@Slf4j
public class CspSmsFilter implements Filter {

    @Resource
    CspCustomerApi cspCustomerApi;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String userId = SessionContextUtil.getUser().getUserId();
        //查询用户对应的权限
        String permissions = cspCustomerApi.getUserPermission(userId);
        if (StringUtils.isEmpty(permissions)|| permissions.contains("4")){
            chain.doFilter(request,response);
        }else {
            response.setCharacterEncoding("utf8");
            response.getWriter().println(JSONUtil.toJsonStr(new Result(81001122,"当前用户权限已失效，请刷新重试")));
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
