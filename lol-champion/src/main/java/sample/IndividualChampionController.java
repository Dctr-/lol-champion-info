package sample;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class IndividualChampionController {
    private Champion champion;
    private Scene parent;

    //Initializers
    @FXML private Button backButton;
    @FXML private Label championName;
    @FXML private ImageView championSplash;

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

    //Accessers
    public void setParent(Scene parent) {
        this.parent = parent;
    }
    public void setChampion(Champion champion) {
        this.champion = champion;
        setData();
    }

    private void setData () { //Sets all the graphics on the javaFX scene
        championName.setText(champion.getId() + " " + champion.getTitle());
        championSplash.setImage(new Image("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/" + champion.getId() + "_0.jpg", 154, 280, true, false));
    }

    private ParseIndividualChampion getStats () {
        ParseIndividualChampion championStats = new ParseIndividualChampion();
        Gson gson = new Gson(); //Parsing object

        JsonElement jsonElement = new JsonParser().parse(jsonGetRequest("http://http://ddragon.leagueoflegends.com/cdn/6.24.1/data/en_US/champion/" + championName + ".json"));
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject = jsonObject.getAsJsonObject("data");

        Set<Map.Entry<String, JsonElement>> mySet = jsonObject.entrySet(); //https://www.physicsforums.com/threads/java-entryset-iterator.740478/
        for (Map.Entry<String, JsonElement> singleItem : mySet) {
            championStats = gson.fromJson(singleItem.getValue(), ParseIndividualChampion.class);
        }

        return championStats;
    }

    //From http://www.java2s.com/Tutorials/Java/Network_How_to/URL/Get_JSON_from_URL.htm
    private String jsonGetRequest(String userUrl) {
        String json = null;
        try {
            URL url = new URL(userUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            InputStream inStream = connection.getInputStream();
            json = streamToString(inStream); // input stream to string
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    //From http://www.java2s.com/Tutorials/Java/Network_How_to/URL/Get_JSON_from_URL.htm
    private String streamToString(InputStream inputStream) {
        String text = new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
        return text;
    }
}
