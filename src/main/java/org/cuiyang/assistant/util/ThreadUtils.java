package org.cuiyang.assistant.util;

import javafx.application.Platform;
import lombok.SneakyThrows;

/**
 * 线程工具类
 */
public class ThreadUtils {

    /**
     * 异步执行
     */
    public static void run(Runnable runnable) {
        new Thread(() -> Platform.runLater(runnable), "Asyn").start();
    }

    @SneakyThrows
    public static void sleep(long millis) {
        Thread.sleep(millis);
    }
}
