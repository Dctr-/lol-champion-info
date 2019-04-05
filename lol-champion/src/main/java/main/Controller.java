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
    boolean sorted = false;
    String currentSort = "All";
    List<Champion> curSorted = new ArrayList<>();

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
            currentSort = sortComboBox.getValue();
            if (currentSort.equals("All")) {
                // reset filter, need to clear list and clear box
                sorted = false;
                curSorted.clear();
                searchTilePanes("");
            } else {
                sortTilePanes(currentSort);
            }
        });
    }

    /**
     * Gets the champion data by parsing RIOTGames static JSON data
     *
     * @return List of Champion objects containing unique information about each champion
     */
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

        for (Champion champion : champions) {
            for (String favourite : favourites) {
                if (favourite.equals(champion.getName())) {
                    champion.setFavourited(true);
                }
            }
        }
    }

    /**
     * Loads all the images by adding them to a queue in ImageManager, utilizing multiple threads for improved speeds.
     * Images are stored locally once downloaded for fast lookup and load times.
     */
    private void loadImages() {
        for (Champion champion : allChampions) {
            ImageManager.queueImageDownload("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/" + champion.getName() + ".png", champion.getName() + "_icon", 75, 75);
            ImageManager.queueImageDownload("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/" + champion.getName() + "_0.jpg", champion.getName() + "_splash", 154, 280);
            for (Skin skin : champion.getSkins()) {
                ImageManager.queueImageDownload("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/" + champion.getName() + "_" + skin.getNum() + ".jpg", champion.getName() + "_" + skin.getNum(), 102, 186);
            }
        }

        ImageManager.queueImageDownload("https://i.imgur.com/V0qG0YN.png", "Champion_Q", 30, 30);
        ImageManager.queueImageDownload("https://i.imgur.com/D4F7lXv.png", "Champion_W", 30, 30);
        ImageManager.queueImageDownload("https://i.imgur.com/8e5wNZF.png", "Champion_E", 30, 30);
        ImageManager.queueImageDownload("https://i.imgur.com/yTQnTsV.png", "Champion_R", 30, 30);
        ImageManager.queueImageDownload("https://i.imgur.com/5ktSs91.png", "Attack_Damage", 50, 50);
        ImageManager.queueImageDownload("https://i.imgur.com/ZcNgPR5.png", "Ability_Power", 50, 50);
        ImageManager.queueImageDownload("https://i.imgur.com/fhodcWm.png", "Defense", 50, 50);

        ImageManager.startImageDownload();
    }

    /**
     * When the textfield championSearchBar listener registers a change, the list of champions are parsed.
     * Champions that fit the criteria are displayed.
     * Criteria includes the champion name containing the text entered, and the current status of the comboBox
     */
    private void searchTilePanes (String keyword) {
        championTilePane.getChildren().clear(); //Clears all the current panes on the tilePane.
        if (sorted){
            for (Champion champion : curSorted) { //Checks if the characters in the textfield are contained in the character name
                if (champion.getName().toLowerCase().contains(keyword.toLowerCase())) {
                    iconDisplay(champion); //If they are, the champion's icon is displayed.
                }
            }
        } else {
            for (Champion champion : allChampions) {
                if (champion.getName().toLowerCase().contains(keyword.toLowerCase())) {
                    iconDisplay(champion);
                }
            }
        }
    }

    public void sortTilePanes(String filterSelected) {
        sorted = true;
        championTilePane.getChildren().clear();

        for (Champion champion : allChampions) {
            // deal with favourite or normal tag
            if (filterSelected.equals("Favorites")) {
                if (champion.isFavourited()) {
                    iconDisplay(champion);
                    curSorted.add(champion);
                }
            } else {
                if (champion.getTags().contains(filterSelected)) {
                    iconDisplay(champion);
                    curSorted.add(champion);
                }
            }
        }
    }

     /**
     * Method to update favourites section on back button click, accessed in the champion controller. In
     * situation where user un-favorites a champion, this will ensure it updates.
     */
    public void backButtonUpdate() {
        if (currentSort.equals("Favorites")) {
            sortTilePanes(currentSort);
        }
    }

    /**
     * Adds a Pane to championTilePane for a given Champion object.  Contains: name, icon
     *
     * @param champion Champion object
     */
    private void iconDisplay(Champion champion) {
        Label newLabel = new Label(champion.getName()); //Creates a label with the champions name
        newLabel.setGraphic(championIcons.get(champion.getName())); //Sets the image to the icon gathered by ImageManager
        newLabel.setContentDisplay(ContentDisplay.TOP); //Sets the image to the top of the label, text below

        Pane newPane = new Pane(); //Add the label to a pane
        newPane.getChildren().add(newLabel);

        // onclick action to every icon
        newPane.addEventHandler(MouseEvent.MOUSE_CLICKED, (event -> { //Gives the pane an event handler for if the champion is clicked
            try {
                changeScreen(event, champion); //When the champion is clicked, changeScreen is called, the champions data is passed as well for more detailed information
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        championTilePane.getChildren().add(newPane); //Adds the champion to the tilePane
    }

    /**
     * Creates a hashmap of champion icons, their name as key, the Imageview as data.  Fast lookup for champion images
     */
    /**
     * Creates a HashMap of champion ImageView icons
     * @return HashMap with champion name as key, ImageView as content
     */
    private HashMap<String, ImageView> getChampionIcons() {
        HashMap<String, ImageView> imageViewHashMap = new HashMap<>();
        for (Champion champion : allChampions) {
            imageViewHashMap.put(champion.getName(), ImageManager.getImage(champion.getName() + "_icon"));
        }
        return imageViewHashMap;
    }

    /**
     * Changes the scene from sample.fxml to individualChampion.fxml
     *
     * @param event MouseEvent whereby the user clicked on a champions pane in the championTilePane
     * @param champion Champion object that corresponds to the champion pane clicked
     * @throws IOException
     */
    public void changeScreen(MouseEvent event, Champion champion) throws IOException {
        if (individualChampionLoader == null) {
            individualChampionLoader = new FXMLLoader(getClass().getClassLoader().getResource("individualChampion.fxml"));
            individualChampionScene = new Scene(individualChampionLoader.load());
        }

        Main.getPrimaryStage().setScene(individualChampionScene); //Changes scenes to the IndividualChampion layout
        Main.getPrimaryStage().getScene().getStylesheets().removeAll();
        Main.getPrimaryStage().getScene().getStylesheets().add("champStyle.css");

        IndividualChampionController controller = individualChampionLoader.getController(); //Changes the controller to the IndividualChampionController
        controller.setParent(((Node) event.getSource()).getScene()); //Gets the existing scene from the mouse event
        controller.setParentController(this);
        controller.setChampion(champion); //Passes the champion information to the new scene
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