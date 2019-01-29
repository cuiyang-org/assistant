package org.cuiyang.assistant;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.cuiyang.assistant.controller.MainController;
import org.cuiyang.assistant.util.ResourceUtils;

/**
 * 开发助手
 *
 * @author cy48576
 */
public class AssistantApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResourceUtils.getResource("view/main.fxml"));
        Parent root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("小助手");
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("logo.png")));
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.show();

        // 设置快捷键
        setShortcutKey(root, controller);
    }

    /**
     * 设置快捷键
     */
    private void setShortcutKey(Parent parent, MainController controller) {
        parent.setOnKeyPressed(event -> {
            if (event.isControlDown() && KeyCode.TAB.equals(event.getCode())) {
                controller.switchNextTab();
            }
        });
    }
}
