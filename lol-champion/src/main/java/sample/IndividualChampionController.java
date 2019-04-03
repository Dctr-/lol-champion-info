package sample;

import com.google.gson.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class IndividualChampionController {
    private Champion champion;
    private Scene parent;
    private ArrayList<Spell> spellsList;

    Gson gson = new Gson(); //Parsing object

    //Initializers
    @FXML private Button backButton;
    @FXML private Label championName;
    @FXML private ImageView championSplash;
    @FXML private Label championQAbility;

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
        getStats();
        setData();
    }

    private void setData () { //Sets all the graphics on the javaFX scene
        championName.setText(champion.getId() + " " + champion.getTitle());
        championSplash.setImage(new Image("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/" + champion.getId() + "_0.jpg", 154, 280, true, false));
        championQAbility.setText(spellsList.get(0).getName());
    }

    private void getStats () {
        JsonElement jsonElement = new JsonParser().parse(jsonGetRequest("http://ddragon.leagueoflegends.com/cdn/6.24.1/data/en_US/champion/" + champion.getId() + ".json"));
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject = jsonObject.getAsJsonObject("data");
        jsonObject = jsonObject.getAsJsonObject(champion.getId());

        this.spellsList = createSpellList(jsonObject.getAsJsonArray("spells"));
    }

    private ArrayList<Spell> createSpellList (JsonArray spellJSON) {
        ArrayList<Spell> spellList = new ArrayList<>();
        for (JsonElement spell: spellJSON
             ) {
            JsonObject object = spell.getAsJsonObject();
            spellList.add(gson.fromJson(object, Spell.class));
        }

        return spellList;
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
        return new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
    }
}
