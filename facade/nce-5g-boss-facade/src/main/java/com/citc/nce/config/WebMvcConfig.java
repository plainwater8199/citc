package com.citc.nce.config;


import com.citc.nce.common.facadeserver.annotations.AnnotationScanner;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.configure.InterfaceExcludesConfigure;
import com.citc.nce.common.web.core.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(InterfaceExcludesConfigure.class)
public class WebMvcConfig implements WebMvcConfigurer {


    private final InterfaceExcludesConfigure interfaceExcludesConfigure;


    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    /**
     * 加载拦截器
     **/
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //定义排除swagger访问的路径配置
        String[] swaggerExcludes = new String[]{"/swagger**/**", "/webjars/**", "/doc.html/**", "/v2/**", "/error","/admin/user/login","/admin/user/logout"};
        String[] split = interfaceExcludesConfigure.getUrl().split(",");//获取配置文件中不需要token校验的接口
        List<String> dontCheckTokenInterface = new ArrayList<>();
        if(split.length >0){
            dontCheckTokenInterface.addAll(Arrays.asList(split));
        }
        List<String> methodsWithAnnotation = AnnotationScanner.getMethodsWithAnnotation(SkipToken.class);//获取通过注解不需要token校验的接口
        if(!CollectionUtils.isEmpty(methodsWithAnnotation)){
            dontCheckTokenInterface.addAll(methodsWithAnnotation);
        }

        //接口验签拦截器
        registry.addInterceptor(loginInterceptor()).addPathPatterns("/**")
                .excludePathPatterns(swaggerExcludes)
                .excludePathPatterns(dontCheckTokenInterface);
    }

}