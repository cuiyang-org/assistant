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
 * Cookie 控制器
 *
 * @author cy48576
 */
public class CookieController extends BaseController implements Initializable, EditorFileOperation {

    /** cookie文本框 */
    public SearchCodeEditor editor;
    /** cookie树 */
    public TreeView<String> cookieTreeView;

    /**
     * cookie格式化
     */
    public void cookieFormat() {
        try {
            this.editor.setWrapText(false);
            String text = this.editor.getText();
            text = text.replaceAll(";\\s*", ";\n");
            this.editor.setText(text);
        } catch (Exception ignore) {
        }
    }

    /**
     * cookie去格式化
     */
    public void cookieSimple() {
        try {
            this.editor.setWrapText(true);
            String text = this.editor.getText();
            text = text.replaceAll(";\\s*", "; ");
            this.editor.setText(text);
        } catch (Exception ignore) {
        }
    }

    /**
     * cookie折叠
     */
    public void cookieFold() {
        if (cookieTreeView == null || cookieTreeView.getRoot() == null) {
            return;
        }
        expanded(cookieTreeView.getRoot(), false);
        cookieTreeView.getRoot().setExpanded(true);
    }

    /**
     * cookie展开
     */
    public void cookieExpanded() {
        if (cookieTreeView == null || cookieTreeView.getRoot() == null) {
            return;
        }
        expanded(cookieTreeView.getRoot(), true);
        cookieTreeView.getRoot().setExpanded(true);
    }

    /**
     * cookie折叠/展开
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
                cookieTreeView.setRoot(null);
                return;
            }
            try {
                KeyValueTreeItem root = new KeyValueTreeItem("Cookie", null);
                buildCookieTreeItem(newValue, root);
                cookieTreeView.setRoot(root);
                cookieTreeView.setShowRoot(false);
            } catch (Exception e) {
                cookieTreeView.setRoot(null);
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
    private void buildCookieTreeItem(String cookies, KeyValueTreeItem parent) {
        for (String cookie : cookies.split(";")) {
            if (!cookie.contains("&")) {
                String[] kv = cookie.split("=");
                KeyValueTreeItem item = new KeyValueTreeItem(kv[0].trim(), (kv.length >= 2 ? kv[1].trim() : ""));
                item.setExpanded(true);
                parent.getChildren().add(item);
            } else {
                cookie = cookie.replaceAll("&", "=");
                String[] kvs = cookie.split("=");
                KeyValueTreeItem item = new KeyValueTreeItem(kvs[0].trim(), null);
                for (int i = 1; i < kvs.length; i += 2) {
                    String key = kvs[i].trim();
                    String value = "";
                    if (i + 1 < kvs.length) {
                        value = kvs[i + 1].trim();
                    }
                    KeyValueTreeItem item2 = new KeyValueTreeItem(key, value);
                    item2.setExpanded(true);
                    item.getChildren().add(item2);
                }
                item.setExpanded(true);
                parent.getChildren().add(item);
            }
        }
    }

    @Override
    public boolean isCloseable() {
        return StringUtils.isBlank(editor.getText());
    }

    @Override
    public FileTypeEnum fileType() {
        return FileTypeEnum.COOKIE;
    }
}
