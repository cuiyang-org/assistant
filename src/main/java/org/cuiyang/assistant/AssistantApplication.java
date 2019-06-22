package org.cuiyang.assistant;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.cuiyang.assistant.util.ResourceUtils;

/**
 * 开发助手
 *
 * @author cy48576
 */
public class AssistantApplication extends Application {

    private static int num = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResourceUtils.getResource("view/main.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("iToolBox" + (++num > 1 ? num : ""));
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("logo.png")));
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
