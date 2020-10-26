package org.cuiyang.assistant.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.constant.FileTypeEnum;
import org.cuiyang.assistant.control.KeyValueTreeItem;
import org.cuiyang.assistant.control.searchcodeeditor.SearchCodeEditor;
import org.cuiyang.assistant.file.EditorFileOperation;

import static org.cuiyang.assistant.control.searchcodeeditor.SearchCodeEditor.FILE_EVENT;

/**
 * 表单 控制器
 *
 * @author cy48576
 */
public class FormController extends BaseController implements Initializable, EditorFileOperation {

    /** 文本框 */
    public SearchCodeEditor editor;
    /** 树 */
    public TreeView<String> treeView;

    /**
     * 格式化
     */
    public void format() {
        try {
            this.editor.setWrapText(false);
            String text = URLDecoder.decode(this.editor.getText(), "UTF-8");
            text = text.replaceAll("&", "\n");
            this.editor.setText(text);
        } catch (Exception ignore) {
        }
    }

    /**
     * 去格式化
     */
    public void simple() {
        try {
            this.editor.setWrapText(true);
            String text = URLDecoder.decode(this.editor.getText(), "UTF-8");
            StringBuilder sb = new StringBuilder();
            String[] split = text.split("[&\\n]");
            for (String line : split) {
                String[] split2 = line.split("=");
                sb.append(split2[0]).append("=").append(split2.length >= 2 ? URLEncoder.encode(split2[1], "UTF-8") : "").append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
            this.editor.setText(sb.toString());
        } catch (Exception ignore) {
        }
    }

    /**
     * 折叠
     */
    public void fold() {
        if (treeView == null || treeView.getRoot() == null) {
            return;
        }
        expanded(treeView.getRoot(), false);
        treeView.getRoot().setExpanded(true);
    }

    /**
     * 展开
     */
    public void expanded() {
        if (treeView == null || treeView.getRoot() == null) {
            return;
        }
        expanded(treeView.getRoot(), true);
        treeView.getRoot().setExpanded(true);
    }

    /**
     * 折叠/展开
     */
    private void expanded(TreeItem<String> root, boolean expanded) {
        if (root == null) {
            return;
        }
        root.setExpanded(expanded);
        for (TreeItem<String> item : root.getChildren()) {
            expanded(item, expanded);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.editor.setFileType(fileType());
        this.editor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isBlank(newValue)) {
                treeView.setRoot(null);
                return;
            }
            try {
                KeyValueTreeItem root = new KeyValueTreeItem("Form", null);
                buildTreeItem(newValue, root);
                treeView.setRoot(root);
                treeView.setShowRoot(false);
            } catch (Exception e) {
                treeView.setRoot(null);
            }
        });
        this.editor.setSupportSave(true);
        this.editor.addEventHandler(FILE_EVENT, event -> {
            File file = this.editor.getFile();
            this.setTitle(file);
        });
    }

    /**
     * 构建cookie树
     */
    private void buildTreeItem(String cookies, KeyValueTreeItem parent) {
        for (String cookie : cookies.split("[&\\n]")) {
            String[] kv = cookie.split("=");
            KeyValueTreeItem item = new KeyValueTreeItem(kv[0].trim(), (kv.length >= 2 ? kv[1].trim() : ""));
            item.setExpanded(true);
            parent.getChildren().add(item);
        }
    }

    /**
     * 生成Java代码
     */
    public void genJava() throws UnsupportedEncodingException {
        String text = this.editor.getText();
        if (StringUtils.isEmpty(text)) {
            log("表单内容为空");
            return;
        }
        text = URLDecoder.decode(text, "UTF-8");
        StringBuilder sb = new StringBuilder();
        sb.append("Map<String, Object> form = new HashMap<>();").append("\r\n");
        String[] split = text.split("[&\\n]");
        for (String line : split) {
            String[] split2 = line.split("=");
            sb.append(String.format("form.put(\"%s\", \"%s\");", split2[0], split2.length >= 2 ? split2[1] : "")).append("\r\n");
        }
        log(sb.toString());
    }

    @Override
    public boolean isCloseable() {
        return StringUtils.isBlank(editor.getText());
    }

    @Override
    public FileTypeEnum fileType() {
        return FileTypeEnum.FORM;
    }
}
