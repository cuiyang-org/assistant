package org.cuiyang.assistant.controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import org.cuiyang.assistant.control.searchcodeeditor.SearchCodeEditor;
import org.cuiyang.assistant.file.FileOperation;
import org.cuiyang.assistant.util.AlertUtils;
import org.cuiyang.assistant.util.ResourceUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.cuiyang.assistant.util.ShortcutKeyUtils.*;
import static org.cuiyang.assistant.util.ThemeUtils.getThemeResource;

/**
 * MainController
 *
 * @author cy48576
 */
public class MainController extends BaseController implements Initializable {

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
    /** tab右键菜单 */
    private ContextMenu tabContextMenu;
    /** file tab右键菜单 */
    private ContextMenu fileTabContextMenu;

    /**
     * 打开tab
     */
    public void openTab(MouseEvent mouseEvent) {
        Label source = (Label) mouseEvent.getSource();
        String text = source.getText();
        openTab(text, null);
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
            splitPane.setDividerPositions(0.8);
            logImageView.setImage(new Image("/view/image/log-open.png"));
            splitPane.getStyleClass().remove("no-divider");
        } else if (!show && splitPane.getItems().size() == 2) {
            splitPane.getItems().remove(logOutParent);
            splitPane.setDividerPositions(1);
            logImageView.setImage(new Image("/view/image/log-close.png"));
            splitPane.getStyleClass().add("no-divider");
        }
    }

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.tabContextMenu = tabContextMenu(false);
        this.fileTabContextMenu = tabContextMenu(true);
        openTab("JSON", null);
        showLogOut(false);
    }

    /**
     * 打开tab
     */
    @SneakyThrows
    public void openTab(String text, File file) {
        String resource;
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
            case "Http":
                resource = "view/http.fxml";
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

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResourceUtils.getResource(resource));
        Parent node = fxmlLoader.load();
        BaseController controller = fxmlLoader.getController();
        Tab tab = new Tab();
        tab.setUserData(controller);
        controller.tab = tab;
        tab.setOnSelectionChanged(event -> {
            if (controller instanceof FileOperation) {
                controller.setTitle(((FileOperation) controller).file());
            } else {
                controller.setTitle(null);
            }
        });
        tab.setClosable(true);
        tab.setContent(node);
        tab.setText(text);
        // 判断是否需要打开文件
        FileOperation fileOperation;
        if (controller instanceof FileOperation) {
            fileOperation = (FileOperation) controller;
            if (file != null) {
                fileOperation.openFile(file);
            }
            tab.setContextMenu(fileTabContextMenu);
        } else {
            tab.setContextMenu(tabContextMenu);
        }

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().selectLast();
    }

    /**
     * tab 右键菜单
     */
    private ContextMenu tabContextMenu(boolean isFile) {
        ContextMenu menu = new ContextMenu();
        MenuItem close = new MenuItem("关闭");
        close.setAccelerator(ctrl(KeyCode.W));
        menu.getItems().add(close);
        close.setOnAction(event -> {
            if (tabPane.getTabs().size() > 0 && !((BaseController) tabPane.getSelectionModel().getSelectedItem().getUserData()).isCloseable()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.getDialogPane().getStylesheets().add(getThemeResource());
                alert.setTitle("提示");
                alert.setHeaderText("你确定要关闭吗？");
                alert.showAndWait();
                ButtonType result = alert.getResult();
                if (result == ButtonType.OK) {
                    tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());
                }
            } else {
                tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());
            }
        });

        MenuItem closeOther = new MenuItem("关闭其它");
        menu.getItems().add(closeOther);
        closeOther.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.getDialogPane().getStylesheets().add(getThemeResource());
            alert.setTitle("提示");
            alert.setHeaderText("你确定要关闭其它标签页吗？");
            alert.showAndWait();
            ButtonType result = alert.getResult();
            if (result == ButtonType.OK) {
                tabPane.getTabs().removeIf(tab1 -> !tab1.equals(tabPane.getSelectionModel().getSelectedItem()));
            }
        });

        MenuItem closeAll = new MenuItem("关闭全部");
        menu.getItems().add(closeAll);
        closeAll.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.getDialogPane().getStylesheets().add(getThemeResource());
            alert.setTitle("提示");
            alert.setHeaderText("你确定要关闭全部标签页吗？");
            alert.showAndWait();
            ButtonType result = alert.getResult();
            if (result == ButtonType.OK) {
                tabPane.getTabs().removeIf(tab1 -> true);
            }
        });

        menu.getItems().add(new SeparatorMenuItem());
        MenuItem moveLeft = new MenuItem("左移");
        moveLeft.setAccelerator(ctrlAlt(KeyCode.LEFT));
        moveLeft.setOnAction(event -> {
            int index = tabPane.getSelectionModel().getSelectedIndex();
            if (index > 0) {
                Tab tab = tabPane.getTabs().remove(index);
                tabPane.getTabs().add(index - 1, tab);
                tabPane.getSelectionModel().select(tab);
                tabPane.requestFocus();
            }
        });
        menu.getItems().add(moveLeft);
        MenuItem moveRight = new MenuItem("右移");
        moveRight.setAccelerator(ctrlAlt(KeyCode.RIGHT));
        moveRight.setOnAction(event -> {
            int index = tabPane.getSelectionModel().getSelectedIndex();
            if (index < tabPane.getTabs().size() - 1) {
                Tab tab = tabPane.getTabs().remove(index);
                tabPane.getTabs().add(index + 1, tab);
                tabPane.getSelectionModel().select(tab);
                tabPane.requestFocus();
            }
        });
        menu.getItems().add(moveRight);

        if (isFile) {
            menu.getItems().add(new SeparatorMenuItem());
            MenuItem open = new MenuItem("打开");
            open.setAccelerator(ctrlShift(KeyCode.O));
            menu.getItems().add(open);
            open.setOnAction(event -> ((FileOperation) tabPane.getSelectionModel().getSelectedItem().getUserData()).openFile());

            MenuItem save = new MenuItem("保存");
            save.setAccelerator(ctrl(KeyCode.S));
            menu.getItems().add(save);
            save.setOnAction(event -> ((FileOperation) tabPane.getSelectionModel().getSelectedItem().getUserData()).save());

            MenuItem saveAs = new MenuItem("另存为");
            saveAs.setAccelerator(ctrlShift(KeyCode.S));
            menu.getItems().add(saveAs);
            saveAs.setOnAction(event -> ((FileOperation) tabPane.getSelectionModel().getSelectedItem().getUserData()).saveAs());

            menu.getItems().add(new SeparatorMenuItem());
            MenuItem openInDir = new MenuItem("打开文件所在目录");
            openInDir.setAccelerator(ctrlAltShift(KeyCode.Z));
            openInDir.setOnAction(event -> {
                try {
                    File file = ((FileOperation) tabPane.getSelectionModel().getSelectedItem().getUserData()).file();
                    if (file != null) {
                        Desktop.getDesktop().open(file.getParentFile());
                    } else {
                        AlertUtils.info("文件未保存");
                    }
                } catch (IOException e) {
                    AlertUtils.exception(e);
                }
            });
            menu.getItems().add(openInDir);
        }
        return menu;
    }

    @Override
    public boolean isCloseable() {
        return true;
    }
}
