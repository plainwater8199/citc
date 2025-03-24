package com.citc.nce.configure;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jcrenc
 * @since 2024/12/1 11:27
 */
@ConditionalOnProperty(value = "enable-druid-monitor", havingValue = "true")
@Configuration
public class DruidConfig {

    /**
     * 注册 Druid 的监控页面
     */
    @Bean
    public ServletRegistrationBean<StatViewServlet> druidStatViewServlet() {
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");

        // 配置白名单
        servletRegistrationBean.addInitParameter("allow", "127.0.0.1"); // 允许访问的 IP,允许本机
        // 登录配置
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "citc@admin");
        return servletRegistrationBean;
    }

    /**
     * 注册 Druid 的过滤器
     */
    @Bean
    public FilterRegistrationBean<WebStatFilter> druidWebStatFilter() {
        WebStatFilter statFilter = new WebStatFilter();
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>(statFilter);
        filterRegistrationBean.addUrlPatterns("/*"); // 监控所有请求
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

}
