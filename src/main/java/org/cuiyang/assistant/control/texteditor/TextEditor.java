package org.cuiyang.assistant.control.texteditor;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * TextEditor
 *
 * @author cy48576
 */
public class TextEditor extends VBox implements Initializable {

    /** 文本域 */
    public TextArea textArea;
    /** 搜索区域 */
    public Pane search;
    /** 搜索框 */
    public TextField keyTextField;

    /** 搜索内容 */
    private String key;
    /** 搜索索引 */
    private int index = -1;

    public TextEditor() {
        loadFxml();
    }

    /**
     * 初始化
     */
    private void loadFxml() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TextEditor.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ignore) {
        }
    }

    /**
     * 向下搜索
     */
    public void searchNext() {
        if (StringUtils.isEmpty(keyTextField.getText())) {
            textArea.deselect();
            return;
        }
        int index = textArea.getText().indexOf(keyTextField.getText(), this.index + 1);
        if (index < 0) {
            index = textArea.getText().indexOf(keyTextField.getText(), 0);
            if (index < 0) {
                return;
            }
        }
        this.index = index;
        textArea.selectRange(index, index + keyTextField.getText().length());
    }

    /**
     * 向上搜索
     */
    public void searchLast() {
        if (StringUtils.isEmpty(keyTextField.getText())) {
            textArea.deselect();
            return;
        }
        int index = textArea.getText().lastIndexOf(keyTextField.getText(), this.index - 1);
        if (index < 0) {
            index = textArea.getText().lastIndexOf(keyTextField.getText(), this.textArea.getText().length());
            if (index < 0) {
                return;
            }
        }
        this.index = index;
        textArea.selectRange(index, index + keyTextField.getText().length());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setOnKeyReleased(event -> {
            if (event.isControlDown() && event.getCode().equals(KeyCode.F)) {
                search.setManaged(!search.isManaged());
                search.setVisible(!search.isVisible());
                if (search.isVisible()) {
                    keyTextField.requestFocus();
                } else {
                    textArea.requestFocus();
                }
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                if (!search.isVisible()) {
                    textArea.requestFocus();
                    return;
                }
                search.setManaged(false);
                search.setVisible(false);
                textArea.requestFocus();
            }
        });
        this.keyTextField.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (event.isControlDown()) {
                    searchLast();
                } else {
                    searchNext();
                }
            } else {
                if (!this.keyTextField.getText().equals(this.key)) {
                    this.key = this.keyTextField.getText();
                    this.index = -1;
                    searchNext();
                }
            }
        });
    }
}
