package com.citc.nce.mybatis.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.mybatis.core.handler.DefaultFieldHandler;
import com.citc.nce.mybatis.core.method.MpExtSqlInjector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/4/25 11:21
 * @Version: 1.0
 * @Description:
 */
@Slf4j
public class MybatisAutoConfiguration {

    /**
     * 分页插件
     *
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        log.info("mybatis plus分页插件加载");
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        /*分页插件*/
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }


    /**
     * com.citc.nce.mybatis plus 自动填充字段处理
     *
     * @param
     * @return x nmk,kk
     */
    @Bean
    @ConditionalOnClass(SessionContextUtil.class)
    public MetaObjectHandler defaultMetaObjectHandler() {
        log.info("mybatis plus 自动填充字段处理加载。。。。。。");
        return new DefaultFieldHandler();
    }

    /**
     * 增加批量插入和更新方法
     *
     * @return
     */
    @Bean
    public MpExtSqlInjector mpExtSqlInjector() {
        log.info("mybatis plus批量新增和更新加载");
        return new MpExtSqlInjector();
    }

}
