package org.cuiyang.assistant.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * 控制器基类
 *
 * @author cuiyang
 */
public abstract class BaseController {
    public MainController mainController;

    /**
     * 输出日志
     */
    public void log(String str) {
        mainController.logOut.appendText(str);
        mainController.logOut.appendText("\r\n");
    }

    /**
     * 输出异常日志
     */
    public void log(Throwable t) {
        mainController.logOut.appendText(ExceptionUtils.getStackTrace(t));
        mainController.logOut.appendText("\r\n");
    }

    /**
     * 输出日志
     */
    public void log(String str, Throwable t) {
        if (StringUtils.isNotEmpty(str)) {
            log(str);
        }
        if (t != null) {
            log(t);
        }
    }
}
