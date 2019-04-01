package sample;
import com.google.gson.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Controller {

    @FXML private void initialize () {
        //String championJSONAsString = jsonGetRequest("http://ddragon.leagueoflegends.com/cdn/6.24.1/data/en_US/champion.json");
        ArrayList<Champion> allChampions = new ArrayList<>(); //Creates an array of champion objects, alphabetical order
        Gson gson = new Gson(); //Parsing object

        JsonElement jelement = new JsonParser().parse(jsonGetRequest("http://ddragon.leagueoflegends.com/cdn/6.24.1/data/en_US/champion.json"));
        JsonObject jobject = jelement.getAsJsonObject();
        jobject = jobject.getAsJsonObject("data");

        Set<Map.Entry<String, JsonElement>> mySet = jobject.entrySet(); //https://www.physicsforums.com/threads/java-entryset-iterator.740478/
        System.out.println("mySet as 'key : value'");
        for (Map.Entry<String, JsonElement> singleItem : mySet)
        {
            Champion newChampion = gson.fromJson(singleItem.getValue(), Champion.class);
            allChampions.add(newChampion);
            System.out.println(singleItem.getKey() + " : " + singleItem.getValue());
        }

        ObservableList<String> options = FXCollections.observableArrayList( //Creates a list containing each class of champion for the dropdown menu
                "Default",
                "Favorites",
                "Assassin",
                "Fighter",
                "Mage",
                "Marksman",
                "Support",
                "Tank"
        );
        sortComboBox.setItems(options); //Sets the Combobox options to the list of classes

        championTilePane.setHgap(4); //Spacing between champion tiles
        championTilePane.setVgap(4);

        for (Champion champion : allChampions) { //Adds each champion to the
            Image newImage = new Image("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/" + champion.getId() + ".png", 75,75, true,false);
            ImageView imageView = new ImageView(newImage); //Creates the image of champion, pulled from riot website

            Label newLabel = new Label(champion.getId()); //Creates champion label to go underneath picture
            newLabel.setGraphic(imageView); //Adds image to label
            newLabel.setContentDisplay(ContentDisplay.TOP); //Puts text underneath photo

            Pane newPane = new Pane(); //Creates a pane (Can be clicked on)
            newPane.getChildren().add(newLabel); //Adds the label to the pane

            championTilePane.getChildren().add(newPane); //Adds the pane to the tilepane grid
        }
    }

    //Initializers
    @FXML private ComboBox<String> sortComboBox;
    @FXML private TextField championSearchBar;
    @FXML private TilePane championTilePane;
    @FXML private ScrollPane championScrollPane;

    //From http://www.java2s.com/Tutorials/Java/Network_How_to/URL/Get_JSON_from_URL.htm
    private String jsonGetRequest (String userUrl) {
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
