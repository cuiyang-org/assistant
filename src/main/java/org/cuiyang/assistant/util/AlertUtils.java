package org.cuiyang.assistant.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * alert工具类
 *
 * @author cuiyang
 */
public class AlertUtils {

    public static void alert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);
            alert.showAndWait();
        });
    }

    public static void info(String title, String headerText, String contentText) {
        alert(Alert.AlertType.INFORMATION, title, headerText, contentText);
    }

    public static void info(String content) {
        alert(Alert.AlertType.INFORMATION, "提示", content, null);
    }

    public static void info(String title, String content) {
        alert(Alert.AlertType.INFORMATION, title, content, null);
    }

    public static void error(String title, String content) {
        alert(Alert.AlertType.ERROR, title, content, null);
    }

    public static void error(String content) {
        alert(Alert.AlertType.ERROR, "提示", content, null);
    }
}
