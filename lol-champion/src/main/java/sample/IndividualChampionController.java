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
    private Info info;

    Gson gson = new Gson(); //Parsing object

    //Initializers
    @FXML private Button backButton;
    @FXML private Label championName;
    @FXML private ImageView championSplash;
    @FXML private Label lblAbilities;
    @FXML private ImageView qImg;
    @FXML private ImageView wImg;
    @FXML private ImageView eImg;
    @FXML private ImageView rImg;
    @FXML private Label qAbilityLabel;
    @FXML private Label wAbilityLabel;
    @FXML private Label eAbilityLabel;
    @FXML private Label rAbilityLabel;
    @FXML private ImageView attackDamageIcon;
    @FXML private ImageView abilityPowerIcon;
    @FXML private ImageView defenseIcon;

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
        qAbilityLabel.setText(spellsList.get(0).getName());
        wAbilityLabel.setText(spellsList.get(1).getName());
        eAbilityLabel.setText(spellsList.get(2).getName());
        rAbilityLabel.setText(spellsList.get(3).getName());
        qImg.setImage(new Image("https://i.imgur.com/DSk0MzV.jpg", 30,30,true,false));
        wImg.setImage(new Image("https://i.imgur.com/N6eTOxI.jpg", 30,30,true,false));
        eImg.setImage(new Image("https://i.imgur.com/cJw5lB9.jpg", 30,30,true,false));
        rImg.setImage(new Image("https://i.imgur.com/uTHV0A6.jpg", 30,30,true,false));
        attackDamageIcon.setImage(new Image("https://i.imgur.com/oTVnrLb.png", 50,50,true,false));
        abilityPowerIcon.setImage(new Image("https://i.imgur.com/ZcNgPR5.png", 30,50,true,false));
        defenseIcon.setImage(new Image("https://i.imgur.com/VmmAxmC.png", 50,50,true,false));
    }

    private void getStats () {
        JsonElement jsonElement = new JsonParser().parse(jsonGetRequest("http://ddragon.leagueoflegends.com/cdn/6.24.1/data/en_US/champion/" + champion.getId() + ".json"));
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject = jsonObject.getAsJsonObject("data");
        jsonObject = jsonObject.getAsJsonObject(champion.getId());

        JsonArray spellJson = jsonObject.getAsJsonArray("spells");
        JsonObject infoJson = jsonObject.getAsJsonObject("info");

        this.info = gson.fromJson(infoJson, Info.class);
        this.spellsList = createSpellList(spellJson);
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

    /* Attempting to download ability image on first call
    private void getAbilityIcons() {
        String findPath = "";
        try {
            findPath = (new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getParentFile().getPath();
            if(!findPath.endsWith("/")) {
                findPath += "/";
            }
            findPath += "lol-champion/images/";
            System.out.println(findPath);
            File pathDir = new File(findPath);
            if(!pathDir.exists()) {
                Files.createDirectories(Paths.get(pathDir.toURI()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String path = findPath;
        String[][] addresses = new String[][]{
                {"https://i.imgur.com/DSk0MzV.jpg","https://i.imgur.com/N6eTOxI.jpg","https://i.imgur.com/cJw5lB9.jpg","https://i.imgur.com/uTHV0A6.jpg"},
                {"Q","W","E","R"}
        };
        for (int i = 0; i < 4; i++) {
            ImageView imageView;
            File abilityIcon = new File(path + addresses[1][i] + ".jpg");
            if (!abilityIcon.exists()) {
                Image newImage = new Image(addresses[0][i], 30,30,true,false);
                imageView = new ImageView(newImage);
                File imageFile = new File(path + addresses[1][i] + ".jpg");
                if(!imageFile.getParentFile().exists()) {
                    imageFile.getParentFile().mkdirs();
                }
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(newImage, null), "jpg", imageFile);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
            else
                imageView = new ImageView(new Image(abilityIcon.toURI().toString()));
        }
    }
    */

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
