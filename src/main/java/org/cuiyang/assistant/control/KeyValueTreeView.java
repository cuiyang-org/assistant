package org.cuiyang.assistant.control;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import org.cuiyang.assistant.util.ClipBoardUtils;

import static org.cuiyang.assistant.util.KeyEventUtils.ctrl;
import static org.cuiyang.assistant.util.KeyEventUtils.ctrlShift;

public class KeyValueTreeView extends TreeView<String> {

    private String separator = ":";

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public KeyValueTreeView() {
        this.init();
    }

    public KeyValueTreeView(TreeItem<String> root) {
        super(root);
        this.init();
    }

    public void copy() {
        KeyValueTreeItem treeItem = (KeyValueTreeItem) this.getTreeItem(this.getSelectionModel().getSelectedIndex());
        if (treeItem != null) {
            ClipBoardUtils.setSysClipboardText(treeItem.getKey() + separator + treeItem.getValue2());
        }
    }

    public void copyKey() {
        KeyValueTreeItem treeItem = (KeyValueTreeItem) this.getTreeItem(this.getSelectionModel().getSelectedIndex());
        if (treeItem != null) {
            ClipBoardUtils.setSysClipboardText(treeItem.getKey());
        }
    }

    public void copyValue() {
        KeyValueTreeItem treeItem = (KeyValueTreeItem) this.getTreeItem(this.getSelectionModel().getSelectedIndex());
        if (treeItem != null) {
            ClipBoardUtils.setSysClipboardText(String.valueOf(treeItem.getValue2()));
        }
    }

    private void init() {
        MenuItem copy = new MenuItem("复制");
        copy.setOnAction(e -> this.copy());
        MenuItem copyName = new MenuItem("复制名字");
        copyName.setOnAction(e -> this.copyKey());
        MenuItem copyValue = new MenuItem("复制值");
        copyValue.setOnAction(e -> this.copyValue());
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(copy);
        contextMenu.getItems().add(copyValue);
        contextMenu.getItems().add(copyName);
        this.setContextMenu(contextMenu);

        this.contextMenuProperty().addListener(e -> {
            this.getContextMenu().getItems().add(0, copyName);
            this.getContextMenu().getItems().add(0, copyValue);
            this.getContextMenu().getItems().add(0, copy);
        });

        this.setOnKeyPressed(event -> {
            if (ctrl(event, KeyCode.C)) {
                this.copyKey();
            } else if (ctrlShift(event, KeyCode.C)) {
                this.copyValue();
            }
        });
    }
}
