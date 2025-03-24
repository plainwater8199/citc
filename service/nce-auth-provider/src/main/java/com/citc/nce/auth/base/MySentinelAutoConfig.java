package com.citc.nce.auth.base;

import com.alibaba.cloud.circuitbreaker.sentinel.SentinelCircuitBreakerFactory;
import com.alibaba.csp.sentinel.SphU;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @authoer:ldy
 * @createDate:2022/7/11 0:49
 * @description:
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SphU.class})
@ConditionalOnProperty(name = "spring.cloud.circuitbreaker.sentinel.enabled",
        havingValue = "true", matchIfMissing = true)
public class MySentinelAutoConfig {

    @Autowired(required = false)
    private List<Customizer<SentinelCircuitBreakerFactory>> customizers = new ArrayList<>();

    @Bean
    @ConditionalOnMissingBean(CircuitBreakerFactory.class)
    public CircuitBreakerFactory sentinelCircuitBreakerFactory() {
        SentinelCircuitBreakerFactory factory = new SentinelCircuitBreakerFactory();
        customizers.forEach(customizer -> customizer.customize(factory));
        return factory;
    }
}
