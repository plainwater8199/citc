package com.citc.nce.common.thread;

import cn.hutool.core.thread.ExecutorBuilder;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.threadpool.TtlExecutors;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/25 9:27
 * @Version: 1.0
 * @Description: 包装原始的runnable和callable，用于上下文传递
 * 所有返回的ExecutorService都支持租户上下文传递
 */
public class ThreadTaskUtils {

    public static final int DEFAULT_WORK_QUEUE_CAPACITY = 1024;

    public static void main(String[] args) {
        ThreadLocal<String> objectThreadLocal = newThreadLocal();
        objectThreadLocal.set("aaa");
        System.out.println(objectThreadLocal.get());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + ":" + objectThreadLocal.get());
            }
        };
        ThreadTaskUtils.execute(runnable);
    }

    public static <T> ThreadLocal<T> newThreadLocal() {
        return new TransmittableThreadLocal<T>();
    }

    public static void sleepMilliSeconds(int time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    /**
     * 提供全局的默认任务执行线程池
     */
    private static ExecutorService GlobalThreadPool = newFixedThreadPool(Runtime.getRuntime().availableProcessors());


    /**
     * @param runnable
     */
    public static void submit(Runnable runnable) {
        GlobalThreadPool.submit(getRunnableWrap(runnable));
    }


    public static void execute(Runnable runnable) {
        GlobalThreadPool.execute(getRunnableWrap(runnable));
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        Future<T> f = GlobalThreadPool.submit(getCallableWrap(callable));
        return f;
    }

    public static Runnable get(Runnable runnable) {
        return TtlRunnable.get(getRunnableWrap(runnable));
    }

    public static <T> Callable<T> get(Callable<T> callable) {
        return TtlCallable.get(getCallableWrap(callable));
    }

    private static <T> Callable<T> getCallableWrap(Callable<T> callable) {
        return isCallableWrap(callable) ? callable : new CallableWrap(callable);
    }

    private static Runnable getRunnableWrap(Runnable runnable) {
        return isRunnableWrap(runnable) ? runnable : new RunnableWrap(runnable);
    }

    /**
     * 返回固定线程数据的线程池，工作队列默认采用LinkedBlockingQueue，最大1024
     *
     * @param nThreads
     * @return
     */
    public static ExecutorService newFixedThreadPool(int nThreads) {
        ThreadPoolExecutor threadPoolExecutor = ExecutorBuilder.create()
                .setCorePoolSize(nThreads)
                .setMaxPoolSize(nThreads)
                .setKeepAliveTime(0L, TimeUnit.MILLISECONDS)
                .setWorkQueue(new LinkedBlockingQueue(1024))
                .build();
        ExecutorService ttlExecutorService = TtlExecutors.getTtlExecutorService(threadPoolExecutor);
        return ttlExecutorService;
    }

    /**
     * @param nThreads
     * @param workQueue 任务队列,使用时需要防止内存溢出
     * @return
     */
    public static ExecutorService newFixedThreadPool(int nThreads, BlockingQueue workQueue) {
        ThreadPoolExecutor threadPoolExecutor = ExecutorBuilder.create()
                .setCorePoolSize(nThreads)
                .setMaxPoolSize(nThreads)
                .setKeepAliveTime(0L, TimeUnit.MILLISECONDS)
                .setWorkQueue(workQueue)
                .setThreadFactory(Executors.defaultThreadFactory())
                .setHandler(new ThreadPoolExecutor.AbortPolicy())
                .build();
        ExecutorService ttlExecutorService = TtlExecutors.getTtlExecutorService(threadPoolExecutor);
        return ttlExecutorService;
    }

    /**
     * @param coreThreads   核心线程数
     * @param maxThreads    最大线程数
     * @param keepAliveTime 线程空闲时间
     * @return
     */
    public static ExecutorService newDynamicThreadPool(int coreThreads, int maxThreads, int keepAliveTime) {
        ThreadPoolExecutor threadPoolExecutor = ExecutorBuilder.create()
                .setCorePoolSize(coreThreads)
                .setMaxPoolSize(maxThreads)
                .setKeepAliveTime(keepAliveTime, TimeUnit.MILLISECONDS)
                .setWorkQueue(new LinkedBlockingQueue(DEFAULT_WORK_QUEUE_CAPACITY))
                .build();
        ExecutorService ttlExecutorService = TtlExecutors.getTtlExecutorService(threadPoolExecutor);
        return ttlExecutorService;
    }

    /**
     * 提供原始的构造方式，谨慎使用
     *
     * @param builder
     * @return
     */
    public static ExecutorService newFixedThreadPool(ExecutorBuilder builder) {
        ThreadPoolExecutor threadPoolExecutor = builder.build();
        ExecutorService ttlExecutorService = TtlExecutors.getTtlExecutorService(threadPoolExecutor);
        return ttlExecutorService;
    }

    static class RunnableWrap implements Runnable {
        private Map<String, String> mdcMap;
        private Runnable run;

        public RunnableWrap(Runnable runnable) {
            mdcMap = MDC.getCopyOfContextMap();
            this.run = runnable;
        }


        @Override
        public void run() {
            if (mdcMap != null) {
                Set<Map.Entry<String, String>> entries = mdcMap.entrySet();
                for (Map.Entry<String, String> e : entries) {
                    MDC.put(e.getKey(), e.getValue());
                }
                mdcMap = null;
            }
            run.run();
        }
    }

    public static boolean isRunnableWrap(Runnable o) {
        return o instanceof RunnableWrap;
    }

    public static boolean isCallableWrap(Callable o) {
        return o instanceof CallableWrap;
    }

    static class CallableWrap<T> implements Callable<T> {
        private Map<String, String> mdcMap;
        private Callable<T> callable;

        public CallableWrap(Callable<T> callable) {
            mdcMap = MDC.getCopyOfContextMap();
            this.callable = callable;
        }


        @Override
        public T call() throws Exception {
            if (mdcMap != null) {
                Set<Map.Entry<String, String>> entries = mdcMap.entrySet();
                for (Map.Entry<String, String> e : entries) {
                    MDC.put(e.getKey(), e.getValue());
                }
                mdcMap = null;
            }
            return callable.call();
        }
    }

}
