package com.citc.nce.filter;

import com.citc.nce.configure.ExamineConfigure;
import com.citc.nce.configure.InterfaceExcludesConfigure;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class Config {

    @Resource
    AllFilter allFilter;
    @Resource
    private ExamineConfigure examineConfigure;


    @Bean
    public FilterRegistrationBean registrationProjectFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(allFilter);
        List<String> list = StringUtils.hasLength(examineConfigure.getUrl()) ?
                Arrays.asList(examineConfigure.getUrl().split(","))
                : new ArrayList<>();
        for (String path : list) {
            registration.addUrlPatterns(path);
        }
        return registration;
    }
}
