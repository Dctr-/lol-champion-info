package main;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

// this isnt linked
public class splashController {
    @FXML
    private Label loadingLabel;

    private void initialize() {
        // if file path for photos is found, set text to loading
        // else set text to initializing
        loadingLabel.setText("Yellow");
    }
}
