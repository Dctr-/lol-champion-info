package main;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends Application {

    private static String applicationPath;
    private static DBManager dbManager;

    public static void main(String[] args) {
        findApplicationPath();

        dbManager = new DBManager();

        LauncherImpl.launchApplication(Main.class, SplashPreloader.class, args);
    }

    private static void findApplicationPath() {
        String findPath = "";
        try {
            findPath = (new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getParentFile().getPath();
            if (!findPath.endsWith("/")) {
                findPath += "/";
            }
            findPath += "lol-champion/";
            File pathDir = new File(findPath);
            if (!pathDir.exists()) {
                Files.createDirectories(Paths.get(pathDir.toURI()));
            }
            applicationPath = findPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Scene newScene = new Scene(root, 500, 700);
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

    public static String getApplicationPath() {
        return applicationPath;
    }

    public static DBManager getDbManager() {
        return dbManager;
    }
}
