package com.citc.nce.filter;


import cn.hutool.json.JSONUtil;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.util.SessionContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(urlPatterns = "/robot/*")
@Slf4j
public class RobotFilter implements Filter {

    @Resource
    CspCustomerApi customerApi;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            String userId = SessionContextUtil.getLoginUser().getUserId();
            //查询用户对应的权限
            String permissions = customerApi.getUserPermission(userId);
            if (StringUtils.isEmpty(permissions) || permissions.contains("2")) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                servletResponse.setCharacterEncoding("utf8");
                servletResponse.getWriter().println(JSONUtil.toJsonStr(new Result(81001122, "当前用户权限已失效，请刷新重试")));
            }
        } catch (BizException bizException) {
            servletResponse.setCharacterEncoding("utf8");
            servletResponse.getWriter().println(JSONUtil.toJsonStr(new Result(GlobalErrorCode.NOT_LOGIN_ERROR)));
        }

    }

    @Override
    public void destroy() {

    }
}
