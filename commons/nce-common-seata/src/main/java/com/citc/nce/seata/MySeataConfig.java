package com.citc.nce.seata;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author bydud
 * @since 2024/3/5
 */
public class MySeataConfig {

    @Bean
    @ConditionalOnMissingBean(SpringUtil.class)
    public SpringUtil createSpringUtil() {
        return new SpringUtil();
    }

    @Bean
    public ShardingJdbcNodesAutoConfigurator createShardingJdbcNodesAutoConfigurator() {
        return new ShardingJdbcNodesAutoConfigurator();
    }
}
