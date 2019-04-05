package main;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class SplashPreloader extends Preloader {
    private Stage splashStage;

    public SplashPreloader() throws IOException {
    }

    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("splashScreen.fxml"));
    Scene splashScene = new Scene(root, 500, 700);

    public void start(Stage stage) throws Exception {
        // set stage, set css style, set scene and start the preloader.
        this.splashStage = stage;
        splashScene.getStylesheets().add("splashStyle.css");
        splashStage.setScene(splashScene);
        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.show();
    }

    /**
     * Using this to deal with finished loading, will hide screen on receiving a notification from main class
     *
     * @param info  update sent through main class, will be passed in 100 when done.
     */
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof ProgressNotification) {
            splashStage.hide();
        }
    }
}
