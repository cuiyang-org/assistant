package org.cuiyang.assistant.controller;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import org.cuiyang.assistant.util.ConfigUtils;
import org.cuiyang.assistant.util.ResourceUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.cuiyang.assistant.constant.ConfigConstant.SHOW_LOG_OUT;
import static org.cuiyang.assistant.constant.ConfigConstant.THEME;

/**
 * MainController
 *
 * @author cy48576
 */
public class MainController extends BaseController implements Initializable {

    /** 场景 */
    public Scene scene;

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
    /** tab pan */
    public TabPane tabPane;
    /** 主题 */
    private String theme;

    /**
     * 初始化
     * @param scene 场景
     */
    public void init(Scene scene) {
        this.scene = scene;
        this.theme = ConfigUtils.get(THEME, "dark");
        this.tabPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F4 && (event.isControlDown() || event.isMetaDown()) && tabPane.getTabs().size() > 1) {
                this.tabPane.getTabs().remove(this.tabPane.getSelectionModel().getSelectedItem());
            }
        });
        scene.getStylesheets().add(ResourceUtils.getResource(String.format("view/css/%s.css", theme)).toExternalForm());
    }

    /**
     * 打开tab
     */
    public void openTab(MouseEvent mouseEvent) throws IOException {
        URL resource;
        Label source = (Label) mouseEvent.getSource();
        String text = source.getText();
        switch (text) {
            case "JSON":
                resource = ResourceUtils.getResource("view/json.fxml");
                break;
            case "HTML":
                resource = ResourceUtils.getResource("view/html.fxml");
                break;
            case "XML":
                resource = ResourceUtils.getResource("view/xml.fxml");
                break;
            case "Cookie":
                resource = ResourceUtils.getResource("view/cookie.fxml");
                break;
            case "表单":
                resource = ResourceUtils.getResource("view/form.fxml");
                break;
            case "正则":
                resource = ResourceUtils.getResource("view/regex.fxml");
                break;
            case "编码转换":
                resource = ResourceUtils.getResource("view/encode.fxml");
                break;
            case "加解密":
                resource = ResourceUtils.getResource("view/encryption.fxml");
                break;
            case "ID生成器":
                resource = ResourceUtils.getResource("view/id.fxml");
                break;
            case "逆向":
                resource = ResourceUtils.getResource("view/reverse.fxml");
                break;
            default:
                throw new RuntimeException(text);
        }
        openTab(text, FXMLLoader.load(resource));
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

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainController = this;
        openTab("JSON", FXMLLoader.load(ResourceUtils.getResource("view/json.fxml")));
        showLogOut(Boolean.parseBoolean(ConfigUtils.get(SHOW_LOG_OUT, "false")));
    }

    /**
     * 打开tab
     */
    private void openTab(String text, Node node) {
        Tab tab = new Tab();
        tab.setClosable(true);
        tab.setText(text);
        tab.setContent(node);
        tab.setContextMenu(tabContextMenu(tab));
        tab.setOnCloseRequest(event -> {
            if (tabPane.getTabs().size() <= 1) {
                event.consume();
            }
        });

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().selectLast();
    }

    /**
     * tab 右键菜单
     */
    private ContextMenu tabContextMenu(Tab tab) {
        ContextMenu menu = new ContextMenu();
        MenuItem close = new MenuItem("关闭");
        menu.getItems().add(close);
        close.setOnAction(event -> tabPane.getTabs().remove(tab));

        MenuItem closeOther = new MenuItem("关闭其它");
        menu.getItems().add(closeOther);
        closeOther.setOnAction(event -> tabPane.getTabs().removeIf(tab1 -> !tab1.equals(tab)));

        MenuItem closeAll = new MenuItem("关闭全部");
        menu.getItems().add(closeAll);
        closeAll.setOnAction(event -> tabPane.getTabs().removeIf(tab1 -> true));
        return menu;
    }
}
