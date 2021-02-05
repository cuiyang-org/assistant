package org.cuiyang.assistant.controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import lombok.SneakyThrows;
import org.cuiyang.assistant.control.InputDialog;
import org.cuiyang.assistant.file.FileOperation;
import org.cuiyang.assistant.util.AlertUtils;
import org.cuiyang.assistant.util.ResourceUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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
    /** 日志开关图标 */
    public ImageView logImageView;
    /** tab pan */
    public TabPane tabPane;

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
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab != null) {
            BaseController controller = (BaseController) tab.getUserData();
            controller.logOut.showLogOut(!controller.logOut.isShowLogOut());
        }
    }

    /**
     * 隐藏日志输出
     */
    public void showLogOut(boolean show) {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab != null) {
            BaseController controller = (BaseController) tab.getUserData();
            controller.logOut.showLogOut(show);
        }
    }

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
            case "ADB":
                resource = "view/adb.fxml";
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
        tab.setOnCloseRequest(event -> {
            this.closeTab(tab);
            event.consume();
        });
        tab.setClosable(true);
        tab.setContent(node);
        tab.setText(" " + text + " ");
        // 判断是否需要打开文件
        FileOperation fileOperation;
        if (controller instanceof FileOperation) {
            fileOperation = (FileOperation) controller;
            if (file != null) {
                fileOperation.openFile(file);
            }
            tab.setContextMenu(tabContextMenu(tab, true));
        } else {
            tab.setContextMenu(tabContextMenu(tab, false));
        }

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().selectLast();
    }

    /**
     * tab 右键菜单
     */
    private ContextMenu tabContextMenu(Tab tab, boolean isFile) {
        ContextMenu menu = new ContextMenu();

        MenuItem rename = new MenuItem("重命名");
        menu.getItems().add(rename);
        rename.setOnAction(event -> this.rename(tab));

        MenuItem close = new MenuItem("关闭");
        close.setAccelerator(invalidCtrl(KeyCode.W));
        menu.getItems().add(close);
        close.setOnAction(event -> this.closeTab(tab));

        MenuItem closeOther = new MenuItem("关闭其它");
        menu.getItems().add(closeOther);
        closeOther.setOnAction(event -> this.closeOtherTab(tab));

        MenuItem closeAll = new MenuItem("关闭全部");
        menu.getItems().add(closeAll);
        closeAll.setOnAction(event -> this.closeAllTab());

        menu.getItems().add(new SeparatorMenuItem());
        MenuItem moveLeft = new MenuItem("左移");
        moveLeft.setAccelerator(invalidCtrlAlt(KeyCode.LEFT));
        moveLeft.setOnAction(event -> this.moveLeftTab(tab));
        menu.getItems().add(moveLeft);
        MenuItem moveRight = new MenuItem("右移");
        moveRight.setAccelerator(invalidCtrlAlt(KeyCode.RIGHT));
        moveRight.setOnAction(event -> this.moveRightTab(tab));
        menu.getItems().add(moveRight);

        if (isFile) {
            menu.getItems().add(new SeparatorMenuItem());
            MenuItem open = new MenuItem("打开");
            open.setAccelerator(invalidCtrlShift(KeyCode.O));
            menu.getItems().add(open);
            open.setOnAction(event -> this.openFile(tab));

            MenuItem save = new MenuItem("保存");
            save.setAccelerator(invalidCtrl(KeyCode.S));
            menu.getItems().add(save);
            save.setOnAction(event -> this.saveFile(tab));

            MenuItem saveAs = new MenuItem("另存为");
            saveAs.setAccelerator(invalidCtrlShift(KeyCode.S));
            menu.getItems().add(saveAs);
            saveAs.setOnAction(event -> this.saveAsFile(tab));

            menu.getItems().add(new SeparatorMenuItem());
            MenuItem openInDir = new MenuItem("打开文件所在目录");
            openInDir.setAccelerator(invalidCtrlAltShift(KeyCode.Z));
            openInDir.setOnAction(event -> this.openFileLocation(tab));
            menu.getItems().add(openInDir);
        }
        return menu;
    }

    /**
     * 重命名
     */
    public void rename(Tab tab) {
        if (tab == null && (tab = tabPane.getSelectionModel().getSelectedItem()) == null) {
            return;
        }
        if (tab.getUserData() instanceof FileOperation && ((FileOperation) tab.getUserData()).file() != null) {
            AlertUtils.info("已保存的文件不支持重命名");
            return;
        }
        InputDialog dialog = new InputDialog(tab.getText());
        dialog.setTitle("重命名");
        Optional<String> optional = dialog.showAndWait();
        optional.ifPresent(tab::setText);
    }

    /**
     * 关闭tab
     */
    public void closeTab(Tab tab) {
        if (tab == null && (tab = tabPane.getSelectionModel().getSelectedItem()) == null) {
            return;
        }
        BaseController controller = (BaseController) tab.getUserData();
        if (!(controller).isCloseable()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.getDialogPane().getStylesheets().add(getThemeResource());
            alert.setTitle("提示");
            alert.setHeaderText("你确定要关闭吗？");
            alert.showAndWait();
            ButtonType result = alert.getResult();
            if (result == ButtonType.OK) {
                if (controller.close()) {
                    tabPane.getTabs().remove(tab);
                }
            }
        } else {
            if (controller.close()) {
                tabPane.getTabs().remove(tab);
            }
        }
    }

    /**
     * 关闭所有tab
     */
    public void closeAllTab() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(getThemeResource());
        alert.setTitle("提示");
        alert.setHeaderText("你确定要关闭全部标签页吗？");
        alert.showAndWait();
        ButtonType result = alert.getResult();
        if (result == ButtonType.OK) {
            tabPane.getTabs().removeIf(tab -> ((BaseController) tab.getUserData()).close());
        }
    }

    /**
     * 关闭其他tab
     */
    public void closeOtherTab(Tab tab) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(getThemeResource());
        alert.setTitle("提示");
        alert.setHeaderText("你确定要关闭其它标签页吗？");
        alert.showAndWait();
        ButtonType result = alert.getResult();
        if (result == ButtonType.OK) {
            tabPane.getTabs().removeIf(tab1 -> !tab1.equals(tab) && ((BaseController) tab1.getUserData()).close());
        }
    }

    /**
     * 左移tab
     */
    public void moveLeftTab(Tab tab) {
        if (tab == null && (tab = tabPane.getSelectionModel().getSelectedItem()) == null) {
            return;
        }
        int index = tabPane.getTabs().indexOf(tab);
        if (index > 0) {
            tabPane.getTabs().remove(tab);
            tabPane.getTabs().add(index - 1, tab);
            tabPane.getSelectionModel().select(tab);
            tabPane.requestFocus();
        }
    }

    /**
     * 右移tab
     */
    public void moveRightTab(Tab tab) {
        if (tab == null && (tab = tabPane.getSelectionModel().getSelectedItem()) == null) {
            return;
        }
        int index = tabPane.getTabs().indexOf(tab);
        if (index < tabPane.getTabs().size() - 1) {
            tabPane.getTabs().remove(tab);
            tabPane.getTabs().add(index + 1, tab);
            tabPane.getSelectionModel().select(tab);
            tabPane.requestFocus();
        }
    }

    /**
     * 打开文件
     */
    public void openFile(Tab tab) {
        if (tab == null && (tab = tabPane.getSelectionModel().getSelectedItem()) == null) {
            return;
        }
        if (!(tab.getUserData() instanceof FileOperation)) {
            return;
        }
        ((FileOperation) tab.getUserData()).openFile();
    }

    /**
     * 保存文件
     */
    public void saveFile(Tab tab) {
        if (tab == null && (tab = tabPane.getSelectionModel().getSelectedItem()) == null) {
            return;
        }
        if (!(tab.getUserData() instanceof FileOperation)) {
            return;
        }
        ((FileOperation) tab.getUserData()).save();
    }

    /**
     * 另存为
     */
    public void saveAsFile(Tab tab) {
        if (tab == null && (tab = tabPane.getSelectionModel().getSelectedItem()) == null) {
            return;
        }
        if (!(tab.getUserData() instanceof FileOperation)) {
            return;
        }
        ((FileOperation) tab.getUserData()).saveAs();
    }

    /**
     * 打开文件位置
     */
    public void openFileLocation(Tab tab) {
        if (tab == null && (tab = tabPane.getSelectionModel().getSelectedItem()) == null) {
            return;
        }
        if (!(tab.getUserData() instanceof FileOperation)) {
            return;
        }
        try {
            File file = ((FileOperation) tab.getUserData()).file();
            if (file != null) {
                Desktop.getDesktop().open(file.getParentFile());
            } else {
                AlertUtils.info("文件未保存");
            }
        } catch (IOException e) {
            AlertUtils.exception(e);
        }
    }

    @Override
    public boolean isCloseable() {
        return true;
    }
}
