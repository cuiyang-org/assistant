package org.cuiyang.assistant.controller;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.cuiyang.assistant.AssistantApplication;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * MainController
 *
 * @author cy48576
 */
public class MainController implements Initializable {

    /** tab容器 */
    public Pane tabContainer;
    /** 按钮容器 */
    public Pane menuContainer;
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

    /**
     * 多开
     */
    public void moreOpen() throws Exception {
        AssistantApplication application = new AssistantApplication();
        application.start(new Stage());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        show(0);
    }
}
