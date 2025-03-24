package com.citc.nce.common.web.core.user;

import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.web.utils.UserTokenUtil;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * @authoer: ldy
 * @createDate: 2022/7/10 1:25
 * @description:  用于将header的用户信息解析设置到SessionContextUtil中
 *
 * 客户端调用聚合层（mall-facade、boss-facade、authcenter-facade、chatbot-facade）
 * 和
 * 各个服务之间的调用时，
 * 会通过这个过滤器获取请求中的token并且将请求中的token转换成SessionContext终的user。
 *
 */
public class UserFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            //获取token并且保存用户
            UserTokenUtil.saveUser((HttpServletRequest) servletRequest);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            //移除本地缓存中的用户和token
            SessionContextUtil.remove();
        }
    }
}
