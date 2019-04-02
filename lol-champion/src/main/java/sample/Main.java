package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private String s;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        primaryStage.setTitle("LOL-Champion-Info");
        primaryStage.setScene(new Scene(root, 600, 700));
        primaryStage.show();
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }
}
