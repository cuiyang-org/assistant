package org.cuiyang.assistant.util;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * alert工具类
 *
 * @author cuiyang
 */
public class AlertUtils {

    public static void input(String title) {
        Platform.runLater(() -> {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setSpacing(5);
            hBox.getChildren().add(new Button("取消"));
            hBox.getChildren().add(new Button("确定"));

            VBox vBox = new VBox();
            vBox.setSpacing(10);
            vBox.getChildren().add(new TextField());
            vBox.getChildren().add(hBox);
            DialogPane dialogPane = new DialogPane();
            dialogPane.setContent(vBox);
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle(title);
            alert.setDialogPane(dialogPane);
            alert.showAndWait();
        });
    }

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
