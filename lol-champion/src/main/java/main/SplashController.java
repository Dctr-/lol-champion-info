package main;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.File;

public class SplashController {
    @FXML
    private Label loadingLabel;

    @FXML
    private void initialize() {
        // if file path for photos is found, set text to loading
        // else it will default back to Loading
        String path = Main.getApplicationPath() + "images/";
        File imagesFolder = new File(path);

        if (!imagesFolder.exists()) {
            loadingLabel.setText("Installing...");
        }
    }
}
