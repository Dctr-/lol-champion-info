package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class IndividualChampionController {
    private Champion champion;
    private Scene parent;

    //Initializers
    @FXML private Button backButton;
    @FXML
    private void initialize () {
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    changeScreen(event);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        });
    }

    public void changeScreen(ActionEvent event) throws IOException {
        Main.getPrimaryStage().setScene(parent);
    }

    public void setParent(Scene parent) {
        this.parent = parent;
    }

    public void setChampion(Champion champion) {
        this.champion = champion;
    }
}
