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
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Controller {
    HashMap<String, ImageView> championIcons = new HashMap<>();
    HashMap<String, Champion> championHashMap = new HashMap<>();
    ArrayList<Champion> allChampions = new ArrayList<>(); //Creates an array of champion objects, alphabetical order
    //Initializers
    @FXML private ComboBox<String> sortComboBox;
    @FXML private TextField championSearchBar;
    @FXML private TilePane championTilePane;
    @FXML private ScrollPane championScrollPane;
    @FXML private AnchorPane mainWindow;
    @FXML private GridPane mainGridPane;
    @FXML private Label titleLabel;
    @FXML private Label sortByLabel;

    private FXMLLoader individualChampionLoader;
    private Scene individualChampionScene;

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

        // combobox sort selection has been made, update champs
        sortComboBox.setOnAction(e -> {
            String filter = sortComboBox.getValue();
            if (filter.equals("Default")){
                updateTilePane("");
            } else { sortTilePanes(filter); }
        });
    }

    private void updateTilePane (String newValue) {
        championTilePane.getChildren().clear();
        for (Champion champion : allChampions) {
            if (champion.getId().toLowerCase().contains(newValue.toLowerCase())) {
                Pane newPane = iconDisplay(champion);

                // on mouse click change screen
                newPane.addEventHandler(MouseEvent.MOUSE_CLICKED, (event -> {
                    try {
                        changeScreen(event, champion);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));
                championTilePane.getChildren().add(newPane);
            }
        }
    }

    private void sortTilePanes(String filterSelected){ // what is newValue
        championTilePane.getChildren().clear();
        for (Champion champion : allChampions) {
            if (champion.getTags().contains(filterSelected)){
                Pane newPane = iconDisplay(champion);
                championTilePane.getChildren().add(newPane);
            }
        }
    }

    private Pane iconDisplay(Champion champion){
        Label newLabel = new Label(champion.getId());
        newLabel.setGraphic(championIcons.get(champion.getId()));
        newLabel.setContentDisplay(ContentDisplay.TOP);

        Pane newPane = new Pane();
        newPane.getChildren().add(newLabel);

        return newPane;
    }

    private HashMap<String, ImageView> getChampionIcons() {
        ExecutorService pool = Executors.newFixedThreadPool(20);
        HashMap<String, ImageView> imageViewHashMap = new HashMap<>();
        java.util.List<Callable<Pair<String, ImageView>>> tasks = new ArrayList<>();

        for (Champion champion : allChampions) {
            tasks.add(() -> {
                Image newImage = new Image("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/" + champion.getId() + ".png", 75, 75, true, false);
                ImageView imageView = new ImageView(newImage); //Creates the image of champion, pulled from riot website

                return new Pair(champion.getId(), imageView);
            });
        }

        try {
            List<Future<Pair<String, ImageView>>> results = pool.invokeAll(tasks);
            for (Future<Pair<String, ImageView>> result : results) {
                imageViewHashMap.put(result.get().getKey(), result.get().getValue());
            }
        } catch (Exception e) {
            System.out.println();
        }

        return imageViewHashMap;
    }

    //When method is called, scene will change to individualChampion
    public void changeScreen(MouseEvent event, Champion champion) throws IOException {
        if(individualChampionLoader == null) {
            individualChampionLoader = new FXMLLoader(getClass().getClassLoader().getResource("individualChampion.fxml"));
            individualChampionScene = new Scene(individualChampionLoader.load());
        }
        Main.getPrimaryStage().setScene(individualChampionScene);

        IndividualChampionController controller = individualChampionLoader.getController();
        controller.setParent(((Node) event.getSource()).getScene());
        System.out.println("Setting champion to " + champion.getId());
        controller.setChampion(champion);
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