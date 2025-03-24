package com.citc.nce.im.config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author jcrenc
 * @since 2024/4/18 10:58
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean(name = "broadcastTaskExecutor")
    public ThreadPoolTaskExecutor broadcastThreadPoolTaskExecutor() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(availableProcessors * 2); // 核心线程数
        executor.setMaxPoolSize(availableProcessors * 4); // 最大线程数
        executor.setQueueCapacity(availableProcessors); // 调整为合适的队列容量
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 设置拒绝策略
        executor.setKeepAliveSeconds(30); // 设置线程存活时间
        executor.initialize();
        final AtomicInteger threadNumber = new AtomicInteger(1);
        executor.setThreadFactory(r -> {
            Thread thread = new Thread(r, "broadcastTaskExecutor-" + threadNumber.getAndIncrement()); // 线程名称前缀
            thread.setUncaughtExceptionHandler((t, e) -> log.error("Thread: {} threw exception: {}", t.getName(), e.getMessage(), e));
            return thread;
        });
        return executor;
    }

}
