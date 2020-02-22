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

    private static int openWindowNum = 0;
    private static int windowNum = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        openWindowNum++;
        windowNum++;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResourceUtils.getResource("view/main.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("iToolBox" + (windowNum > 1 ? windowNum : ""));
        primaryStage.getIcons().add(new Image(ResourceUtils.getResourceAsStream("logo.png")));
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            if (--openWindowNum == 1) {
                windowNum = 1;
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
