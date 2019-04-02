package sample;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Controller {
    HashMap<String, ImageView> championIcons = new HashMap<>();
    HashMap<String, Champion> championHashMap = new HashMap<>();
    ArrayList<Champion> allChampions = new ArrayList<>(); //Creates an array of champion objects, alphabetical order
    private Champion clickedChampion;
    //Initializers
    @FXML private ComboBox<String> sortComboBox;
    @FXML private TextField championSearchBar;
    @FXML private TilePane championTilePane;
    @FXML private ScrollPane championScrollPane;
    @FXML private AnchorPane mainWindow;
    @FXML private GridPane mainGridPane;
    @FXML private Label titleLabel;
    @FXML private Label sortByLabel;

    @FXML
    private void initialize() {
        Gson gson = new Gson(); //Parsing object

        JsonElement jelement = new JsonParser().parse(jsonGetRequest("http://ddragon.leagueoflegends.com/cdn/6.24.1/data/en_US/champion.json"));
        JsonObject jobject = jelement.getAsJsonObject();
        jobject = jobject.getAsJsonObject("data");

        Set<Map.Entry<String, JsonElement>> mySet = jobject.entrySet(); //https://www.physicsforums.com/threads/java-entryset-iterator.740478/
        System.out.println("mySet as 'key : value'");
        for (Map.Entry<String, JsonElement> singleItem : mySet) {
            Champion newChampion = gson.fromJson(singleItem.getValue(), Champion.class);
            allChampions.add(newChampion);
            System.out.println(singleItem.getKey() + " : " + singleItem.getValue());
        }

        //Load all images into hashmap
        championIcons = getChampionIcons();

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

        if(sortComboBox == null) return; //temporary
        sortComboBox.setItems(FXCollections.observableArrayList( //Creates a list containing each class of champion for the dropdown menu
                "Default",
                "Favorites",
                "Assassin",
                "Fighter",
                "Mage",
                "Marksman",
                "Support",
                "Tank"
        )); //Sets the Combobox options to the list of classes

        championTilePane.setHgap(4); //Spacing between champion tiles
        championTilePane.setVgap(4);
        updateTilePane("");

        championSearchBar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateTilePane(newValue); //TilePane is updated on each textfield action event
            }
        });
    }

    private void updateTilePane (String newValue) {
        championTilePane.getChildren().clear();
        for (Champion champion : allChampions
        ) {
            if (champion.getId().toLowerCase().contains(newValue.toLowerCase())) {
                Label newLabel = new Label(champion.getId());
                newLabel.setGraphic(championIcons.get(champion.getId()));
                newLabel.setContentDisplay(ContentDisplay.TOP);

                Pane newPane = new Pane();
                newPane.getChildren().add(newLabel);
                newPane.addEventHandler(MouseEvent.MOUSE_CLICKED, (event -> {
                    try {
                        clickedChampion = champion;
                        changeScreen(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));
                championTilePane.getChildren().add(newPane);
            }
        }
    }

    private HashMap<String, ImageView> getChampionIcons() {
        HashMap<String, ImageView> imageViewHashMap = new HashMap<>();

        for (Champion champion : allChampions
             ) {
            Image newImage = new Image("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/" + champion.getId() + ".png", 75, 75, true, false);
            ImageView imageView = new ImageView(newImage); //Creates the image of champion, pulled from riot website

            imageViewHashMap.put(champion.getId(), imageView);
        }

        return imageViewHashMap;
    }

    //When method is called, scene will change to individualChampion
    public void changeScreen(MouseEvent event) throws IOException {
        Parent individualChampionParent = FXMLLoader.load(getClass().getClassLoader().getResource("individualChampion.fxml"));
        Scene individualChampionScene = new Scene(individualChampionParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow(); //This gets the stage information
        window.setScene(individualChampionScene);
        window.show();
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

    public Champion getClickedChampion() {
        return clickedChampion;
    }
}