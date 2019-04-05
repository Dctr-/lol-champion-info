package main;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

// this isnt linked
public class SplashController {
    @FXML
    private Label loadingLabel;
    @FXML
    private ImageView loadingGif;

    @FXML
    private void initialize() {
        // if file path for photos is found, set text to loading
        // else set text to initializing
        String path = Main.getApplicationPath() + "images/";
        File imagesFolder = new File(path);

        String loadingPath = "https://i.imgur.com/x9IAP39.gif";
        Image load = new Image(loadingPath);
        loadingGif.setImage(load);
        System.out.println("set");

        if (!imagesFolder.exists()) {
            loadingLabel.setText("Installing...");
        }
    }
}
