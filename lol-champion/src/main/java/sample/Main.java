package sample;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
    public static void main(String[] args) {
        LauncherImpl.launchApplication(Main.class, SplashPreloader.class, args);
    }

    private static Stage primaryStage;
    private void setPrimaryStage(Stage stage) {
        Main.primaryStage = stage;
    }

    static public Stage getPrimaryStage() {
        return Main.primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setPrimaryStage(primaryStage);
        // load first controller, heavy lifting
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(100)); // send finished loading ping to preloader

        primaryStage.setTitle("Champion Info");
        Scene newScene = new Scene(root, 600, 700);
        newScene.getStylesheets().add("style.css");
        primaryStage.setScene(newScene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

}
