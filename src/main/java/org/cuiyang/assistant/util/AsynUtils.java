package org.cuiyang.assistant.util;

/**
 * 异步工具类
 */
public class AsynUtils {

    /**
     * 异步执行
     */
    public static void asyn(Runnable runnable) {
        new Thread(runnable, "Asyn").start();
    }
}
