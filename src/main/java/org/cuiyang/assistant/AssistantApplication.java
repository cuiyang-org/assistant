package org.cuiyang.assistant;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import org.cuiyang.assistant.constant.FileTypeEnum;
import org.cuiyang.assistant.controller.MainController;
import org.cuiyang.assistant.util.AlertUtils;
import org.cuiyang.assistant.util.ResourceUtils;

import java.io.File;

import static org.cuiyang.assistant.constant.SystemConstant.APP_NAME;
import static org.cuiyang.assistant.util.ThemeUtils.getThemeResource;

/**
 * 开发助手
 *
 * @author cy48576
 */
public class AssistantApplication extends Application {

    /** 主窗体 */
    private static Stage PRIMARY_STAGE;
    /** 主面板控制器 */
    private static MainController MAIN_CONTROLLER;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            AlertUtils.exception(throwable);
        });

        PRIMARY_STAGE = primaryStage;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResourceUtils.getResource("view/main.fxml"));
        Parent root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        MAIN_CONTROLLER = controller;

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getThemeResource());
        // 设置事件
        setEvent(scene, controller);
        // 设置全局快捷键
        setShortcutKey(scene, controller);

        primaryStage.setScene(scene);
        primaryStage.setTitle(APP_NAME);
        primaryStage.getIcons().add(new Image(ResourceUtils.getResourceAsStream("logo.png")));
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * 主窗体
     */
    public static Stage primaryStage() {
        return PRIMARY_STAGE;
    }

    /**
     * 主面板控制器
     */
    public static MainController mainController() {
        return MAIN_CONTROLLER;
    }

    /**
     * 设置快捷键
     */
    private void setShortcutKey(Scene scene, MainController controller) {
        // 切换日志输出框
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), controller::switchLogOut);
    }

    /**
     * 设置事件监听
     */
    private void setEvent(Scene scene, MainController controller) {
        // 向控件中拖动文档时，必须先在setOnDragOver中设置TransferMode，才能正确触发setOnDragDropped方法。
        scene.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
        });
        scene.setOnDragDropped(event -> {
            if (event.getDragboard().hasFiles()) {
                File file = event.getDragboard().getFiles().get(0);
                FileTypeEnum fileType = FileTypeEnum.parseOfFile(file);
                fileType = fileType == null ? FileTypeEnum.JSON : fileType;
                controller.openTab(fileType.getDesc(), file);
            }
        });
    }
}
