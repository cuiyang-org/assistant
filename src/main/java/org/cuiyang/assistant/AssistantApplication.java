package org.cuiyang.assistant;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.cuiyang.assistant.constant.FileTypeEnum;
import org.cuiyang.assistant.controller.MainController;
import org.cuiyang.assistant.util.AlertUtils;
import org.cuiyang.assistant.util.ResourceUtils;

import java.io.File;

import static org.cuiyang.assistant.constant.SystemConstant.APP_NAME;

/**
 * 开发助手
 *
 * @author cy48576
 */
@Slf4j
public class AssistantApplication extends Application {

    private static Stage PRIMARY_STAGE;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            log.error("运行异常", throwable);
            AlertUtils.exception("运行异常", ExceptionUtils.getStackTrace(throwable));
        });

        PRIMARY_STAGE = primaryStage;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResourceUtils.getResource("view/main.fxml"));
        Parent root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        Scene scene = new Scene(root);
        setEvent(scene, controller);
        controller.init(primaryStage, scene);
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
     * 设置事件监听
     */
    private static void setEvent(Scene scene, MainController controller) {
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
