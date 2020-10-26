package org.cuiyang.assistant;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.cuiyang.assistant.controller.MainController;
import org.cuiyang.assistant.util.ResourceUtils;

import static org.cuiyang.assistant.constant.SystemConstant.APP_NAME;

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
        Scene scene = new Scene(root);
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
}
