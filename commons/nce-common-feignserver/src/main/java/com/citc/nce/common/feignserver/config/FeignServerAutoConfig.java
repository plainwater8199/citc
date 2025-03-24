package com.citc.nce.common.feignserver.config;

import com.citc.nce.common.feignserver.core.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/23 15:40
 * @Version: 1.0
 * @Description:
 */
public class FeignServerAutoConfig {

    @Bean
    public GlobalExceptionHandler feiServerGlobalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

}
