package org.cuiyang.assistant.util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import static org.cuiyang.assistant.util.ThemeUtils.getThemeResource;

/**
 * alert工具类
 *
 * @author cuiyang
 */
@Slf4j
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
            alert.getDialogPane().getStylesheets().add(getThemeResource());
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

    public static void exception(Throwable throwable) {
        log.error("运行异常", throwable);
        Platform.runLater(() -> {
            ScrollPane scrollPane = new ScrollPane(new Label(ExceptionUtils.getStackTrace(throwable)));
            scrollPane.setMaxHeight(800);

            DialogPane dialogPane = new DialogPane();
            dialogPane.setPadding(new Insets(10));
            dialogPane.setContent(new VBox(scrollPane));
            dialogPane.getStylesheets().add(getThemeResource());
            dialogPane.getButtonTypes().add(ButtonType.OK);

            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("运行异常");
            alert.setDialogPane(dialogPane);
            alert.showAndWait();
            alert.close();
        });
    }
}
