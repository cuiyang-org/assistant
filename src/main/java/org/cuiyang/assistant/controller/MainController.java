package org.cuiyang.assistant.controller;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.cuiyang.assistant.AssistantApplication;
import org.cuiyang.assistant.util.ConfigUtils;

import java.net.URL;
import java.util.ResourceBundle;

import static org.cuiyang.assistant.constant.ConfigConstant.SHOW_LOG_OUT;
import static org.cuiyang.assistant.constant.ConfigConstant.TAB_INDEX;
import static org.cuiyang.assistant.util.ThreadUtils.run;
import static org.cuiyang.assistant.util.ThreadUtils.sleep;

/**
 * MainController
 *
 * @author cy48576
 */
public class MainController extends BaseController implements Initializable {

    /** 逆向控制器 */
    public ReverseController reverseController;

    /** tab容器 */
    public Pane tabContainer;
    /** 按钮容器 */
    public Pane menuContainer;
    /** 日志输出 */
    public TextArea logOut;
    /** 分割面板 */
    public SplitPane splitPane;
    /** 日志开关图标 */
    public ImageView logImageView;
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
     * 切换日志显示
     */
    public void switchLogOut() {
        boolean show = !logOut.getParent().isVisible();
        showLogOut(show);
    }

    /**
     * 隐藏日志输出
     */
    public void showLogOut(boolean show) {
        mainController.logOut.getParent().setVisible(show);
        mainController.logOut.getParent().setManaged(show);
        if (show) {
            mainController.splitPane.setDividerPositions(0.8);
            logImageView.setImage(new Image("/view/image/log-open.png"));
        } else {
            mainController.splitPane.setDividerPositions(1);
            logImageView.setImage(new Image("/view/image/log-close.png"));
        }
        ConfigUtils.setAndSave(SHOW_LOG_OUT, String.valueOf(show));
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
        ConfigUtils.setAndSave(TAB_INDEX, String.valueOf(index));
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
        this.mainController = this;
        reverseController.mainController = this;

        show(Integer.parseInt(ConfigUtils.get(TAB_INDEX, "0")));
        run(() -> {
            sleep(1000);
            showLogOut(Boolean.parseBoolean(ConfigUtils.get(SHOW_LOG_OUT, "true")));
        });
    }
}
