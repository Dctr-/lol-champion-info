package main;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.io.File;

// this isnt linked
public class SplashController {
    @FXML
    private Label loadingLabel;

    @FXML
    private void initialize() {
        // if file path for photos is found, set text to loading
        // else set text to initializing
        System.out.println("Here");
        String path = Main.getApplicationPath()+"images/";
        File imagesFolder = new File(path);
.
        if (!imagesFolder.exists()){
            loadingLabel.setText("Installing...");
        }
    }
}
