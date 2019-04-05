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
        // else it will default back to Loading
        String path = Main.getApplicationPath() + "images/";
        File imagesFolder = new File(path);

        if (!imagesFolder.exists()) {
            loadingLabel.setText("Installing...");
        }

        // trying to download gif and play on center, but gif doesnt animate
        // TODO: 2019-04-05 animate gif rather than having it still
        String loadingPath = "https://i.imgur.com/x9IAP39.gif";
        Image load = new Image(loadingPath);
        loadingGif.setImage(load);
    }
}
