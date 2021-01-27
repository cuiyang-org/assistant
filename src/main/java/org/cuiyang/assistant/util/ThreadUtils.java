package org.cuiyang.assistant.util;

import lombok.SneakyThrows;

/**
 * 线程工具类
 */
public class ThreadUtils {

    /**
     * 异步执行
     */
    public static Thread run(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
        return thread;
    }

    @SneakyThrows
    public static void sleep(long millis) {
        Thread.sleep(millis);
    }
}
