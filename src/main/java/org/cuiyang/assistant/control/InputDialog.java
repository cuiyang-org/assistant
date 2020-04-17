package org.cuiyang.assistant.control;

import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import static org.cuiyang.assistant.util.ThemeUtils.getThemeResource;

/**
 * 输入对话框
 *
 * @author cuiyang
 */
public class InputDialog extends Dialog<String> {

    /** 后缀 */
    private String suffix;
    /** 默认值 */
    private String defaultValue;
    /** 文本框 */
    private TextField textField;

    public InputDialog() {
        this("");
    }

    public InputDialog(@NamedArg("defaultValue") String defaultValue) {
        this.defaultValue = defaultValue;
        this.textField = new TextField(defaultValue);

        VBox content = new VBox();
        content.setPrefWidth(300);
        content.getChildren().add(textField);
        DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.getStylesheets().add(getThemeResource());
        dialogPane.setContent(content);
        this.setDialogPane(dialogPane);

        Platform.runLater(textField::requestFocus);

        setResultConverter((dialogButton) -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonBar.ButtonData.OK_DONE ? (
                this.suffix == null ? textField.getText() : textField.getText() + this.suffix
            ) : null;
        });
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
