package com.citc.nce.filter;

import com.citc.nce.configure.ExamineConfigure;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Configuration
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
        List<String> list = Arrays.asList(examineConfigure.getUrl().split(","));
        for (String path : list) {
            registration.addUrlPatterns(path);
        }
        return registration;
    }
}
