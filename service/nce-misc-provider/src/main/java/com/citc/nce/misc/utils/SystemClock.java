package com.citc.nce.misc.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/24 20:18
 * @Version 1.0
 * @Description:高并发下获取当前系统时间戳
 */
public class SystemClock {
    private static final String THREAD_NAME = "system.clock";
    private static final SystemClock MILLIS_CLOCK = new SystemClock(1);
    private final long precision;
    private final AtomicLong now;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, THREAD_NAME);
        thread.setDaemon(true);
        return thread;
    });

    private SystemClock(long precision) {
        this.precision = precision;
        now = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdating();
    }

    public static SystemClock millisClock() {
        return MILLIS_CLOCK;
    }

    private void scheduleClockUpdating() {
        scheduler.scheduleAtFixedRate(() ->
                        now.set(System.currentTimeMillis()),
                precision, precision, TimeUnit.MILLISECONDS);
    }

    public long now() {
        return now.get();
    }
}
