package org.cuiyang.assistant.control.searchcodeeditor;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.control.CodeEditor;
import org.fxmisc.richtext.Selection;
import org.fxmisc.richtext.SelectionImpl;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * 可搜索的代码编辑器
 *
 * @author cy48576
 */
public class SearchCodeEditor extends VBox implements Initializable {

    /** 文本域 */
    @FXML
    private CodeEditor codeEditor;
    /** 搜索区域 */
    @FXML
    private Pane search;
    /** 搜索框 */
    @FXML
    private TextField keyTextField;

    /** 搜索内容 */
    private String key;
    /** 搜索索引 */
    private int index = -1;
    /** 选择 */
    private Selection<Collection<String>, String, Collection<String>> selection;

    public SearchCodeEditor() {
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
        selection = new SelectionImpl<>("another selection", codeEditor,
                path -> {
                    path.setStrokeWidth(0);
                    path.setFill(Color.valueOf("#214283"));
                }
        );
        codeEditor.addSelection(selection);
    }

    public void setText(String text) {
        codeEditor.setText(text);
    }

    public String getText() {
        return codeEditor.getText();
    }

    public void setType(CodeEditor.Type type) {
        codeEditor.setType(type);
    }

    public ObservableValue<String> textProperty() {
        return codeEditor.textProperty();
    }

    public void setWrapText(boolean value) {
        codeEditor.setWrapText(value);
    }

    /**
     * 向下搜索
     */
    public void searchNext() {
        if (StringUtils.isEmpty(keyTextField.getText())) {
            selection.deselect();
            return;
        }
        int index = codeEditor.getText().indexOf(keyTextField.getText(), this.index + 1);
        if (index < 0) {
            index = codeEditor.getText().indexOf(keyTextField.getText());
            if (index < 0) {
                return;
            }
        }
        this.index = index;
        selection.selectRange(index, index + keyTextField.getText().length());
        codeEditor.requestFollowCaret();
        codeEditor.moveTo(index);
    }

    /**
     * 向上搜索
     */
    public void searchLast() {
        if (StringUtils.isEmpty(keyTextField.getText())) {
            selection.deselect();
            return;
        }
        int index = codeEditor.getText().lastIndexOf(keyTextField.getText(), this.index - 1);
        if (index < 0) {
            index = codeEditor.getText().lastIndexOf(keyTextField.getText());
            if (index < 0) {
                return;
            }
        }
        this.index = index;
        selection.selectRange(index, index + keyTextField.getText().length());
        codeEditor.requestFollowCaret();
        codeEditor.moveTo(index);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setOnKeyReleased(event -> {
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode().equals(KeyCode.F)) {
                search.setManaged(true);
                search.setVisible(true);
                String selectedText = codeEditor.getSelectedText();
                if (StringUtils.isNotEmpty(selectedText)) {
                    keyTextField.setText(selectedText);
                }
                keyTextField.requestFocus();
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                search.setManaged(false);
                search.setVisible(false);
                codeEditor.requestFocus();
                selection.deselect();
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
