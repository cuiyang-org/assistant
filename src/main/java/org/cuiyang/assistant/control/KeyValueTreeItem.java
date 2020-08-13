package org.cuiyang.assistant.control;

import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;

public class KeyValueTreeItem extends TreeItem<String> {
    private int row;
    private String key;
    private Object value;

    public KeyValueTreeItem(String key, Object value) {
        this.key = key;
        this.value = value;
        Label graphic = getGraphic(key);
        this.setGraphic(graphic);
        this.setValue(String.valueOf(value));
        this.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (!this.isLeaf() && newValue) {
                this.setValue("");
            } else {
                this.setValue(String.valueOf(value));
            }
        });
    }

    /**
     * TreeItem 图标
     */
    private Label getGraphic(String key) {
        Label keyLabel = new Label(key + ":");
        keyLabel.getStyleClass().add("key");
        return keyLabel;
    }

    public String getKey() {
        return key;
    }

    public Object getValue2() {
        return value;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
