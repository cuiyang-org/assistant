package org.cuiyang.assistant.controller;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * MainController
 *
 * @author cy48576
 */
public class MainController {

    /** tab容器 */
    public Pane tabContainer;
    /** 按钮容器 */
    public Pane menuContainer;
    /** json */
    public HBox json;
    /** tab索引 */
    private int tabIndex;

    /**
     * 切换tab
     */
    public void switchTab(MouseEvent mouseEvent) {
        Node source = (Node) mouseEvent.getSource();
        ObservableList<Node> children = menuContainer.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (source == children.get(i)) {
                this.tabIndex = i;
                show(i);
                break;
            }
        }
    }

    /**
     * 切换到下一个tab
     */
    public void switchNextTab() {
        if (++this.tabIndex >= menuContainer.getChildren().size()) {
            this.tabIndex = 0;
        }
        show(this.tabIndex);
    }

    /**
     * 显示对应的tab
     */
    private void show(int index) {
        ObservableList<Node> tabChildren = tabContainer.getChildren();
        for (int i = 0; i < tabChildren.size(); i++) {
            tabChildren.get(i).setVisible(i == index);
        }

        ObservableList<Node> menuChildren = menuContainer.getChildren();
        for (int i = 0; i < menuChildren.size(); i++) {
            if (i == index) {
                menuChildren.get(index).getStyleClass().add("selected");
            } else {
                menuChildren.get(i).getStyleClass().remove("selected");
            }
        }
    }

}
