package com.citc.nce.common.facadeserver.config;

import com.citc.nce.common.facadeserver.core.FacadeGlobalExceptionHandler;
import com.citc.nce.common.facadeserver.core.GlobalResponseBodyHandler;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import java.util.TimeZone;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/23 16:00
 * @Version: 1.0
 * @Description:
 */
@Slf4j
public class FacadeServerAutoConfig {

    @Bean
    @ConditionalOnMissingBean(FacadeGlobalExceptionHandler.class)
    public FacadeGlobalExceptionHandler facadeGlobalExceptionHandler() {
        return new FacadeGlobalExceptionHandler();
    }


    @Bean
    @ConditionalOnMissingBean(GlobalResponseBodyHandler.class)
    public GlobalResponseBodyHandler facadeserverResponseBodyHandler() {
        log.info("...facadeserver responseBodyHandler...");
        return new GlobalResponseBodyHandler();
    }
}
