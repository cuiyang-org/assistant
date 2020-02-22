package org.cuiyang.assistant.util;

import lombok.SneakyThrows;

/**
 * 线程工具类
 */
public class ThreadUtils {

    /**
     * 异步执行
     */
    public static void run(Runnable runnable) {
        new Thread(runnable, "Asyn").start();
    }

    @SneakyThrows
    public static void sleep(long millis) {
        Thread.sleep(millis);
    }
}
