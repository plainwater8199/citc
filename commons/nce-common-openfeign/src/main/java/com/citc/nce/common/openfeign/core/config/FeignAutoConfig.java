package com.citc.nce.common.openfeign.core.config;

import com.citc.nce.common.openfeign.core.FeignErrorDecoder;
import com.citc.nce.common.openfeign.core.FeignHeaderInterceptor;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/30 14:20
 * @Version: 1.0
 * @Description:
 */
public class FeignAutoConfig {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public Encoder feignDecoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new FeignErrorDecoder();
    }

    @Bean
    public FeignHeaderInterceptor saasFeignHeaderInterceptor() {
        return new FeignHeaderInterceptor();
    }
}
