package org.cuiyang.assistant.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.constant.FileTypeEnum;
import org.cuiyang.assistant.control.KeyValueTreeItem;
import org.cuiyang.assistant.control.searchcodeeditor.SearchCodeEditor;
import org.cuiyang.assistant.file.EditorFileOperation;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static org.cuiyang.assistant.control.searchcodeeditor.SearchCodeEditor.FILE_EVENT;

/**
 * Http 控制器
 *
 * @author cy48576
 */
public class HttpController extends BaseController implements Initializable, EditorFileOperation {

    /** 文本框 */
    public SearchCodeEditor editor;
    /** 树 */
    public TreeView<String> treeView;

    /**
     * 生成Java代码
     */
    public void genHeader() {
        String text = this.editor.getText();
        if (StringUtils.isEmpty(text)) {
            log("请求内容为空");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Map<String, String> headers = new HashMap<>();").append("\r\n");
        String[] split = text.split("\\n");
        for (String line : split) {
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            String[] split2 = line.split(":");
            if (split2.length != 2) {
                continue;
            }
            sb.append(String.format("headers.put(\"%s\", \"%s\");", split2[0].trim(), split2[1].trim())).append("\r\n");
        }
        log(sb.toString());
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
                KeyValueTreeItem root = new KeyValueTreeItem("Http", null);
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
    private void buildTreeItem(String text, KeyValueTreeItem parent) {
        for (String line : text.split("\\n")) {
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            String[] kv = line.split(":");
            KeyValueTreeItem item = new KeyValueTreeItem(kv[0].trim(), (kv.length >= 2 ? kv[1].trim() : ""));
            item.setExpanded(true);
            parent.getChildren().add(item);
        }
    }

    @Override
    public boolean isCloseable() {
        return StringUtils.isBlank(editor.getText());
    }

    @Override
    public FileTypeEnum fileType() {
        return FileTypeEnum.HTTP;
    }
}
