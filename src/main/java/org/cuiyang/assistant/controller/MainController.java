package org.cuiyang.assistant.controller;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.cuiyang.assistant.util.ConfigUtils;
import org.cuiyang.assistant.util.ResourceUtils;

import java.net.URL;
import java.util.ResourceBundle;

import static org.cuiyang.assistant.constant.ConfigConstant.*;

/**
 * MainController
 *
 * @author cy48576
 */
public class MainController extends BaseController implements Initializable {

    /** 逆向控制器 */
    public ReverseController reverseController;
    /** 表单控制器 */
    public FormController formController;

    /** 场景 */
    public Scene scene;

    /** tab容器 */
    public Pane tabContainer;
    /** 按钮容器 */
    public Pane menuContainer;
    /** 日志输出 */
    public TextArea logOut;
    public VBox logOutParent;
    /** 分割面板 */
    public SplitPane splitPane;
    /** 日志开关图标 */
    public ImageView logImageView;
    /** 主题开关 */
    public ImageView themeImageView;
    /** tab索引 */
    private int tabIndex;
    /** 主题 */
    private String theme;

    /**
     * 初始化
     * @param scene 场景
     */
    public void init(Scene scene) {
        this.scene = scene;
        this.theme = ConfigUtils.get(THEME, "dark");
        scene.getStylesheets().add(ResourceUtils.getResource(String.format("view/css/%s.css", theme)).toExternalForm());
    }

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
        boolean show = splitPane.getItems().size() == 1;
        showLogOut(show);
    }

    /**
     * 隐藏日志输出
     */
    public void showLogOut(boolean show) {
        if (show && splitPane.getItems().size() == 1) {
            splitPane.getItems().add(logOutParent);
            mainController.splitPane.setDividerPositions(0.8);
            logImageView.setImage(new Image("/view/image/log-open.png"));
            splitPane.getStyleClass().remove("no-divider");
        } else if (!show && splitPane.getItems().size() == 2) {
            splitPane.getItems().remove(logOutParent);
            mainController.splitPane.setDividerPositions(1);
            logImageView.setImage(new Image("/view/image/log-close.png"));
            splitPane.getStyleClass().add("no-divider");
        }
        ConfigUtils.setAndSave(SHOW_LOG_OUT, String.valueOf(show));
    }

    /**
     * 切换主题
     */
    public void switchTheme() {
        ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.remove(ResourceUtils.getResource(String.format("view/css/%s.css", theme)).toExternalForm());
        if (theme.equals("dark")) {
            // 切换成亮色
            themeImageView.setImage(new Image("/view/image/theme-light.png"));
            theme = "light";
        } else {
            // 切换成暗色
            themeImageView.setImage(new Image("/view/image/theme-dark.png"));
            theme = "dark";
        }
        stylesheets.add(ResourceUtils.getResource(String.format("view/css/%s.css", theme)).toExternalForm());
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.mainController = this;
        reverseController.mainController = this;
        formController.mainController = this;

        show(Integer.parseInt(ConfigUtils.get(TAB_INDEX, "0")));
        showLogOut(Boolean.parseBoolean(ConfigUtils.get(SHOW_LOG_OUT, "false")));
    }

}
