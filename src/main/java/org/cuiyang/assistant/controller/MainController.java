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

    /** json 控制器 */
    public JsonController jsonController;

    /** tab容器 */
    public Pane container;
    /** json */
    public HBox json;

    public void init() {
        jsonController.init();
    }

    /**
     * 切换tab
     */
    public void switchTab(MouseEvent mouseEvent) {
        Node source = (Node) mouseEvent.getSource();
        Pane parent = (Pane) source.getParent();
        ObservableList<Node> children = parent.getChildren();
        for (int i = 0; i < children.size(); i++) {
            children.get(i).getStyleClass().remove("selected");
            if (source == children.get(i)) {
                show(i);
            }
        }
        source.getStyleClass().add("selected");
    }

    /**
     * 显示对应的tab
     */
    private void show(int index) {
        ObservableList<Node> children = container.getChildren();
        for (int i = 0; i < children.size(); i++) {
            children.get(i).setVisible(i == index);
        }
    }

}
