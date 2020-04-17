package org.cuiyang.assistant.controller;

import javafx.collections.ObservableList;
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
import org.cuiyang.assistant.constant.FileTypeEnum;
import org.cuiyang.assistant.control.InputDialog;
import org.cuiyang.assistant.util.ConfigUtils;
import org.cuiyang.assistant.util.ResourceUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.cuiyang.assistant.constant.ConfigConstant.SHOW_LOG_OUT;
import static org.cuiyang.assistant.constant.ConfigConstant.THEME;
import static org.cuiyang.assistant.util.ThemeUtils.*;

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
        this.theme = getTheme();
        this.tabPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.W && (event.isControlDown() || event.isMetaDown())) {
                this.tabPane.getTabs().remove(this.tabPane.getSelectionModel().getSelectedItem());
            }
        });
        scene.getStylesheets().add(getThemeResource());
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
            case "Form":
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
        stylesheets.remove(getThemeResource());
        if (theme.equals(DARK)) {
            // 切换成亮色
            themeImageView.setImage(new Image("/view/image/theme-light.png"));
            theme = LIGHT;
        } else {
            // 切换成暗色
            themeImageView.setImage(new Image("/view/image/theme-dark.png"));
            theme = DARK;
        }
        stylesheets.add(getThemeResource(theme));
        ConfigUtils.setAndSave(THEME, theme);
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

        FileTypeEnum fileType = FileTypeEnum.parseOfDesc(tab.getText());
        MenuItem rename = new MenuItem("重命名");
        menu.getItems().add(rename);
        rename.setOnAction(event -> {
            InputDialog dialog = new InputDialog(tab.getText());
            dialog.setSuffix(fileType != null ? fileType.getSuffix() : null);
            dialog.setTitle("重命名");
            Optional<String> optional = dialog.showAndWait();
            optional.ifPresent(tab::setText);
        });
        return menu;
    }
}
