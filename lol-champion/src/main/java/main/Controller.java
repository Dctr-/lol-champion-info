package main;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import main.champion.Champion;
import main.champion.Skin;

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
    List<Champion> allChampions = new ArrayList<>(); //Creates an array of main.champion objects, alphabetical order
    //Initializers
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private TextField championSearchBar;
    @FXML
    private TilePane championTilePane;
    @FXML
    private ScrollPane championScrollPane;
    @FXML
    private AnchorPane mainWindow;
    @FXML
    private GridPane mainGridPane;
    @FXML
    private Label titleLabel;
    @FXML
    private Label sortByLabel;

    private FXMLLoader individualChampionLoader;
    private Scene individualChampionScene;

    @FXML
    private void initialize() {
        allChampions = getChampionData();

        loadImages();

        loadFavourites(allChampions);

        //Load all images into hashmap
        championIcons = getChampionIcons();

        sortComboBox.setItems(FXCollections.observableArrayList( //Creates a list containing each class of main.champion for the dropdown menu
                "All",
                "Favorites",
                "Assassin",
                "Fighter",
                "Mage",
                "Marksman",
                "Support",
                "Tank"
        )); //Sets the Combobox options to the list of classes

        championTilePane.setHgap(4); //Spacing between main.champion tiles
        championTilePane.setVgap(4);
        searchTilePanes("");

        championSearchBar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                searchTilePanes(newValue); //TilePane is updated on each textfield action event
            }
        });

        // combobox sort selection has been made, update champs
        sortComboBox.setOnAction(e -> {
            String filter = sortComboBox.getValue();
            if (filter.equals("All")) {
                searchTilePanes("");
            } else {
                sortTilePanes(filter);
            }
        });
    }

    private List<Champion> getChampionData() {
        Gson gson = new Gson(); //Parsing object
        List<Champion> champions = new ArrayList<>();

        JsonElement jelement = new JsonParser().parse(jsonGetRequest("http://ddragon.leagueoflegends.com/cdn/6.24.1/data/en_US/champion.json"));
        JsonObject jobject = jelement.getAsJsonObject();
        jobject = jobject.getAsJsonObject("data");

        // Pull main.champion names
        Set<Map.Entry<String, JsonElement>> mySet = jobject.entrySet();
        List<String> championNames = new ArrayList<>();
        for (Map.Entry<String, JsonElement> singleItem : mySet) {
            championNames.add(singleItem.getKey());
        }

        // only parse data if it doesn't exists in the db
        DBManager db = Main.getDbManager();


        if (db.queryChampion(championNames.get(0)) == null) {
            ExecutorService pool = Executors.newFixedThreadPool(20);
            java.util.List<Callable<Champion>> tasks = new ArrayList<>();

            // Pull main.champion info
            for (String championName : championNames) {
                tasks.add(() -> {
                    JsonElement jsonElement = new JsonParser().parse(jsonGetRequest("http://ddragon.leagueoflegends.com/cdn/6.24.1/data/en_US/champion/" + championName + ".json"));
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    jsonObject = jsonObject.getAsJsonObject("data");
                    jsonObject = jsonObject.getAsJsonObject(championName);

                    return gson.fromJson(jsonObject, Champion.class);
                });
            }

            try {
                for (Future<Champion> championFuture : pool.invokeAll(tasks)) {
                    champions.add(championFuture.get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // insert into DB
            for (Champion champion : champions) {
                db.insertChampion(champion);
            }
        } else {
            for (String championName : championNames) {
                champions.add(db.queryChampion(championName));
            }
        }
        return champions;
    }

    private void loadFavourites(List<Champion> champions) {
        DBManager db = Main.getDbManager();
        List<String> favourites = db.queryFavourites();

        for (Champion champion: champions){
            for (String favourite : favourites) {
                if (favourite.equals(champion.getName())) {
                    champion.setFavourited(true);
                }
            }
        }

    }

    private void loadImages() {
        for (Champion champion : allChampions) {
            ImageManager.queueImageDownload("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/" + champion.getName() + ".png", champion.getName() + "_icon", 75, 75);
            ImageManager.queueImageDownload("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/" + champion.getName() + "_0.jpg", champion.getName() + "_splash", 154, 280);
            for (Skin skin : champion.getSkins()) {
                ImageManager.queueImageDownload("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/" + champion.getName() + "_" + skin.getNum() + ".jpg", champion.getName() + "_" + skin.getNum(), 51, 93);
            }
        }

        ImageManager.queueImageDownload("https://i.imgur.com/DSk0MzV.jpg", "Champion_Q", 30, 30);
        ImageManager.queueImageDownload("https://i.imgur.com/N6eTOxI.jpg", "Champion_W", 30, 30);
        ImageManager.queueImageDownload("https://i.imgur.com/cJw5lB9.jpg", "Champion_E", 30, 30);
        ImageManager.queueImageDownload("https://i.imgur.com/uTHV0A6.jpg", "Champion_R", 30, 30);
        ImageManager.queueImageDownload("https://i.imgur.com/oTVnrLb.png", "Attack_Damage", 50, 50);
        ImageManager.queueImageDownload("https://i.imgur.com/ZcNgPR5.png", "Ability_Power", 50, 50);
        ImageManager.queueImageDownload("https://i.imgur.com/VmmAxmC.png", "Defense", 50, 50);

        ImageManager.startImageDownload();
    }

    private void searchTilePanes(String newValue) {
        championTilePane.getChildren().clear();
        for (Champion champion : allChampions) {
            if (champion.getName().toLowerCase().contains(newValue.toLowerCase())) {
                iconDisplay(champion);
            }
        }
    }

    private void sortTilePanes(String filterSelected) {
        championTilePane.getChildren().clear();

        for (Champion champion : allChampions) {
            // deal with favourite or normal tag
            if (filterSelected.equals("Favorites")){
                if (champion.isFavourited()){
                    iconDisplay(champion);
                }
            } else {
                if (champion.getTags().contains(filterSelected)) {
                    iconDisplay(champion);
                }
            }
        }
    }

    private void iconDisplay(Champion champion) {
        Label newLabel = new Label(champion.getName());
        newLabel.setGraphic(championIcons.get(champion.getName()));
        newLabel.setContentDisplay(ContentDisplay.TOP);

        Pane newPane = new Pane();
        newPane.getChildren().add(newLabel);

        // onclick action to every icon
        newPane.addEventHandler(MouseEvent.MOUSE_CLICKED, (event -> {
            try {
                changeScreen(event, champion);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        championTilePane.getChildren().add(newPane);
    }

    private HashMap<String, ImageView> getChampionIcons() {
        HashMap<String, ImageView> imageViewHashMap = new HashMap<>();
        for (Champion champion : allChampions) {
            imageViewHashMap.put(champion.getName(), ImageManager.getImage(champion.getName() + "_icon"));
        }
        return imageViewHashMap;
    }

    //When method is called, scene will change to individualChampion
    public void changeScreen(MouseEvent event, Champion champion) throws IOException {
        if (individualChampionLoader == null) {
            individualChampionLoader = new FXMLLoader(getClass().getClassLoader().getResource("individualChampion.fxml"));
            individualChampionScene = new Scene(individualChampionLoader.load());
        }
        Main.getPrimaryStage().setScene(individualChampionScene);

        IndividualChampionController controller = individualChampionLoader.getController();
        controller.setParent(((Node) event.getSource()).getScene());
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

    private String streamToString(InputStream inputStream) {
        return new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
    }
}