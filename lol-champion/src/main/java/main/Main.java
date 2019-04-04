package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main extends Application {

    private static String applicationPath;
    private static DBManager dbManager;

    public static void main(String[] args) {
        findApplicationPath();

        dbManager = new DBManager();

        launch(args);
    }

    private static void findApplicationPath() {
        String findPath = "";
        try {
            findPath = (new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getParentFile().getPath();
            if(!findPath.endsWith("/")) {
                findPath += "/";
            }
            findPath += "lol-champion/";
            File pathDir = new File(findPath);
            if(!pathDir.exists()) {
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
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        primaryStage.setTitle("Champion Info");
        // primaryStage.getIcons().add(new Image("file:icon.png")); set icon, need to get location
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

    public static String getApplicationPath() {
        return applicationPath;
    }

    public static DBManager getDbManager() {
        return dbManager;
    }
}
