package org.cuiyang.assistant.controller;

import javafx.scene.control.Tab;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.cuiyang.assistant.control.LogOutPane;

import java.io.File;

import static org.cuiyang.assistant.AssistantApplication.primaryStage;
import static org.cuiyang.assistant.constant.SystemConstant.APP_NAME;

/**
 * 控制器基类
 *
 * @author cuiyang
 */
public abstract class BaseController {
    public Tab tab;
    public LogOutPane logOut;

    /**
     * 清除日志
     */
    public void clearLog() {
        logOut.clear();
    }

    /**
     * 输出日志
     */
    public void log(String str) {
        if (!logOut.isShowLogOut()) {
            logOut.showLogOut(true);
        }
        logOut.appendText(str);
        logOut.appendText("\r\n");
    }

    /**
     * 输出异常日志
     */
    public void log(Throwable t) {
        if (!logOut.isShowLogOut()) {
            logOut.showLogOut(true);
        }
        logOut.appendText(ExceptionUtils.getStackTrace(t));
        logOut.appendText("\r\n");
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

    public void setTitle(File file) {
        if (file == null) {
            primaryStage().setTitle(APP_NAME);
        } else {
            primaryStage().setTitle(APP_NAME + " - " + file.getPath());
            tab.setText(file.getName());
        }
    }

    /**
     * 关闭
     */
    public boolean close() {
        return true;
    }

    /**
     * 是否可关闭
     */
    public abstract boolean isCloseable();
}
