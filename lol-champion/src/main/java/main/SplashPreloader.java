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
    static splashController mySplashController;

    public SplashPreloader() throws IOException {
    }

    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("splashScreen.fxml"));
    Scene splashScene = new Scene(root, 500, 700);


    public void start(Stage stage) throws Exception {
        // FXMLLoader loader = new FXMLLoader(getClass().getResource("splashScreen.fxml"));
        // Parent root = loader.load();
//        mySplashController = (splashController)loader.getController();
//        Scene splashScene = new Scene(root, 500, 700);

        this.splashStage = stage;

        splashScene.getStylesheets().add("splashStyle.css");
        splashStage.setScene(splashScene);
        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.show();
    }

    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof ProgressNotification) {
            splashStage.hide();
        }
    }
}
