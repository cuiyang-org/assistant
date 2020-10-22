package org.cuiyang.assistant.control.searchcodeeditor;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.control.CodeEditor;
import org.cuiyang.assistant.util.CommonUtils;
import org.fxmisc.richtext.Selection;
import org.fxmisc.richtext.SelectionImpl;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
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
    /** 匹配数量 */
    @FXML
    private Label matchNum;

    /** 搜索内容 */
    private String key;
    /** 搜索索引 */
    private int index = -1;
    /** 搜索索引列表 */
    private List<Integer> indexList;
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
    }

    public void appendText(String text) {
        codeEditor.appendText(text);
    }

    public void clear() {
        codeEditor.clear();
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

    public void move(int row) {
        codeEditor.requestFollowCaret();
        codeEditor.moveTo(row, 0);
    }

    /**
     * 向下搜索
     */
    public void searchNext() {
        if (CollectionUtils.isEmpty(indexList)) {
            matchNum.setText("0/0");
            selection.deselect();
            return;
        }
        if (index < indexList.size() - 1) {
            index++;
        } else {
            index = 0;
        }
        select(index);
    }

    /**
     * 向上搜索
     */
    public void searchLast() {
        if (CollectionUtils.isEmpty(indexList)) {
            matchNum.setText("0/0");
            selection.deselect();
            return;
        }
        if (index > 0) {
            index--;
        } else {
            index = indexList.size() - 1;
        }
        select(index);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 打开搜索框
        this.setOnKeyPressed(event -> {
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode().equals(KeyCode.F)) {
                search.setManaged(true);
                search.setVisible(true);
                String selectedText = codeEditor.getSelectedText();
                if (StringUtils.isNotEmpty(selectedText)) {
                    keyTextField.setText(selectedText);
                }
                keyTextField.requestFocus();
                select(index);
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                search.setManaged(false);
                search.setVisible(false);
                codeEditor.requestFocus();
                selection.deselect();
            }
        });
        // 回车查找下一个
        this.keyTextField.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (event.isControlDown()) {
                    searchLast();
                } else {
                    searchNext();
                }
            }
        });
        // 选择器
        selection = new SelectionImpl<>("another selection", codeEditor,
                path -> {
                    path.setStrokeWidth(0);
                    path.setFill(Color.valueOf("#214283"));
                }
        );
        codeEditor.addSelection(selection);
        // 查询
        keyTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            indexList = CommonUtils.indexOf(codeEditor.getText(), newValue);
            index = -1;
            searchNext();
        });
        codeEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            indexList = CommonUtils.indexOf(newValue, keyTextField.getText());
            index = -1;
            searchNext();
        });
    }

    /**
     * 选择
     */
    private void select(int index) {
        if (index < 0 || indexList == null || index >= indexList.size()) {
            return;
        }
        int start = indexList.get(index);
        selection.selectRange(start, start + keyTextField.getText().length());
        codeEditor.requestFollowCaret();
        codeEditor.moveTo(start);
        matchNum.setText(index + 1 + "/" + indexList.size());
    }
}
