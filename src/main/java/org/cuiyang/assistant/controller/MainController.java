package org.cuiyang.assistant.controller;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.cuiyang.assistant.control.searchcodeeditor.SearchCodeEditor;
import org.cuiyang.assistant.util.ResourceUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.cuiyang.assistant.util.KeyEventUtils.ctrl;
import static org.cuiyang.assistant.util.ThemeUtils.getThemeResource;

/**
 * MainController
 *
 * @author cy48576
 */
public class MainController extends BaseController implements Initializable {

    /** 场景 */
    public Scene scene;
    /** Stage */
    public Stage primaryStage;

    /** 按钮容器 */
    public Pane menuContainer;
    /** 日志输出 */
    public SearchCodeEditor logOut;
    public VBox logOutParent;
    /** 分割面板 */
    public SplitPane splitPane;
    /** 日志开关图标 */
    public ImageView logImageView;
    /** tab pan */
    public TabPane tabPane;

    /**
     * 初始化
     * @param scene 场景
     */
    public void init(Stage primaryStage, Scene scene) {
        this.primaryStage = primaryStage;
        this.scene = scene;
        this.tabPane.setOnKeyPressed(event -> {
            if (ctrl(event, KeyCode.W) && this.tabPane.getTabs().size() > 0) {
                // 关闭tab页
                closeTab(this.tabPane.getSelectionModel().getSelectedItem());
            } else if (event.getCode() == KeyCode.ESCAPE) {
                // 隐藏log输出
                showLogOut(false);
            }
        });
        scene.getStylesheets().add(getThemeResource());
    }

    /**
     * 打开tab
     */
    public void openTab(MouseEvent mouseEvent) throws IOException {
        String resource;
        Label source = (Label) mouseEvent.getSource();
        String text = source.getText();
        switch (text) {
            case "JSON":
                resource = "view/json.fxml";
                break;
            case "HTML":
                resource = "view/html.fxml";
                break;
            case "XML":
                resource = "view/xml.fxml";
                break;
            case "Cookie":
                resource = "view/cookie.fxml";
                break;
            case "Form":
                resource = "view/form.fxml";
                break;
            case "正则":
                resource = "view/regex.fxml";
                break;
            case "编码转换":
                resource = "view/encode.fxml";
                break;
            case "加解密":
                resource = "view/encryption.fxml";
                break;
            case "工具箱":
                resource = "view/tool.fxml";
                break;
            case "地址计算":
                resource = "view/address.fxml";
                break;
            default:
                throw new RuntimeException(text);
        }
        openTab(text, resource);
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
    }

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainController = this;
        openTab("JSON", "view/json.fxml");
        showLogOut(false);
    }

    /**
     * 打开tab
     */
    private void openTab(String text, String resource) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResourceUtils.getResource(resource));
        Parent node = fxmlLoader.load();
        BaseController controller = fxmlLoader.getController();

        Tab tab = new Tab();
        controller.tab = tab;
        tab.setOnSelectionChanged(event -> controller.setTitle(controller.title()));
        tab.setOnCloseRequest(event -> {
            if (!controller.isCloseable()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.getDialogPane().getStylesheets().add(getThemeResource());
                alert.setTitle("提示");
                alert.setHeaderText("你确定要关闭吗？");
                alert.showAndWait();
                ButtonType result = alert.getResult();
                if (result != ButtonType.OK) {
                    event.consume();
                }
            }
        });
        tab.setClosable(true);
        tab.setText(text);
        tab.setContent(node);
        tab.setContextMenu(tabContextMenu(tab));

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().selectLast();
    }

    /**
     * 关闭tab
     */
    public void closeTab(Tab tab)  {
        EventHandler<Event> eventHandler = tab.getOnCloseRequest();
        if (eventHandler != null) {
            Event event = new Event(Tab.CLOSED_EVENT);
            eventHandler.handle(event);
            if (event.isConsumed()) {
                return;
            }
        }
        this.tabPane.getTabs().remove(tab);
    }

    /**
     * tab 右键菜单
     */
    private ContextMenu tabContextMenu(Tab tab) {
        ContextMenu menu = new ContextMenu();
        MenuItem close = new MenuItem("关闭");
        menu.getItems().add(close);
        close.setOnAction(event -> closeTab(tab));

        MenuItem closeOther = new MenuItem("关闭其它");
        menu.getItems().add(closeOther);
        closeOther.setOnAction(event -> tabPane.getTabs().removeIf(tab1 -> !tab1.equals(tab)));

        MenuItem closeAll = new MenuItem("关闭全部");
        menu.getItems().add(closeAll);
        closeAll.setOnAction(event -> tabPane.getTabs().removeIf(tab1 -> true));
        return menu;
    }

    @Override
    public boolean isCloseable() {
        return true;
    }
}
