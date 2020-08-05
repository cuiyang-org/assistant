package org.cuiyang.assistant.controller;

import javafx.scene.control.Tab;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * 控制器基类
 *
 * @author cuiyang
 */
public abstract class BaseController {
    public static MainController mainController;
    public Tab tab;

    /**
     * 清除日志
     */
    public void clearLog() {
        mainController.logOut.clear();
    }

    /**
     * 输出日志
     */
    public void log(String str) {
        if (mainController.splitPane.getItems().size() == 1) {
            mainController.showLogOut(true);
        }
        mainController.logOut.appendText(str);
        mainController.logOut.appendText("\r\n");
    }

    /**
     * 输出异常日志
     */
    public void log(Throwable t) {
        if (mainController.splitPane.getItems().size() == 1) {
            mainController.showLogOut(true);
        }
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

    public void close() {
        mainController.closeTab(tab);
    }

    /**
     * 是否可关闭
     */
    public abstract boolean isCloseable();

}
