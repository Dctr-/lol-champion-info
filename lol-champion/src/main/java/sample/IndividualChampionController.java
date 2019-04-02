package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class IndividualChampionController {
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
        Parent sampleParent = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow(); //This gets the stage information
        window.getScene().setRoot(sampleParent);
    }
}
