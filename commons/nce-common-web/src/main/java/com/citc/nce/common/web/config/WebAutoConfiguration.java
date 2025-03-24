package com.citc.nce.common.web.config;


import com.citc.nce.common.web.core.cors.filter.CorsFilter;
//import com.citc.nce.common.web.core.token.filter.TokenFilter;
import com.citc.nce.common.web.core.trace.filter.TraceFilter;
import com.citc.nce.common.web.core.user.UserFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/23 10:42
 * @Version: 1.0
 * @Description:
 */
@Slf4j
public class WebAutoConfiguration {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterFilterRegistrationBean() {
        CorsFilter corsFilter = new CorsFilter();
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(corsFilter);
        bean.setOrder(Integer.MIN_VALUE);
        bean.setName("corsFilter");
        return bean;
    }


    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilter() {
        FilterRegistrationBean<TraceFilter> bean = new FilterRegistrationBean<>(new TraceFilter());
        bean.setOrder(Integer.MIN_VALUE);
        bean.setName("traceFilter");
        return bean;
    }

    @Bean
    public FilterRegistrationBean<UserFilter> userFilter() {
        FilterRegistrationBean<UserFilter> bean = new FilterRegistrationBean<>(new UserFilter());
        bean.setOrder(Integer.MIN_VALUE);
        bean.setName("userFilter");
        return bean;
    }


//    @Bean
//    public FilterRegistrationBean<TokenFilter> tokenFilter() {
//        FilterRegistrationBean<TokenFilter> bean = new FilterRegistrationBean<>(new TokenFilter());
//        bean.setOrder(Integer.MIN_VALUE);
//        bean.setName("tokenFilter");
//        return bean;
//    }


}
