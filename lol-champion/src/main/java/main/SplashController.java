package main;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

// this isnt linked
public class SplashController {
    @FXML
    private Label loadingLabel;

    @FXML
    private void initialize() {
        // if file path for photos is found, set text to loading
        // else set text to initializing
        loadingLabel.setText("Yellow");
        System.out.println("yello");
    }
}
