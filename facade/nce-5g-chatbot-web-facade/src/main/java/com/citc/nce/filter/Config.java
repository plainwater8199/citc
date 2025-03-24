package com.citc.nce.filter;

import com.citc.nce.common.facadeserver.annotations.AnnotationScanner;
import com.citc.nce.common.facadeserver.annotations.Examine;
import com.citc.nce.configure.ExamineConfigure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(ExamineConfigure.class)
public class Config {

    @Resource
    AllFilter allFilter;


    private final ExamineConfigure examineConfigure;

    @Bean
    public FilterRegistrationBean registrationProjectFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(allFilter);
        List<String> examineInterfaces = new ArrayList<>();
        String[] split = examineConfigure.getUrl().split(",");//获取配置文件中不需要token校验的接口
        if(split.length >0){
            examineInterfaces.addAll(Arrays.asList(split));
        }
        List<String> methodsWithAnnotation = AnnotationScanner.getMethodsWithAnnotation(Examine.class);//获取通过注解不需要token校验的接口
        if(!CollectionUtils.isEmpty(methodsWithAnnotation)){
            examineInterfaces.addAll(methodsWithAnnotation);
        }

        for (String path : examineInterfaces) {
            registration.addUrlPatterns(path);
        }
        return registration;
    }
}
