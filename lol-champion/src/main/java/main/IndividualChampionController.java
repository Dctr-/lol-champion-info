package main;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextAlignment;
import main.champion.Champion;
import main.champion.Skin;

import java.io.IOException;

public class IndividualChampionController {
    private Champion champion;
    private Scene parent;
    private Controller parentController;

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
    @FXML private Label attackDamageValue;
    @FXML private Label abilityPowerValue;
    @FXML private Label defenseValue;
    @FXML private TilePane skinsTilePane;
    @FXML private RadioButton favouriteButton;
    @FXML private ScrollPane skinsScrollPane;

    @FXML
    private void initialize() {
        backButton.setOnAction(new EventHandler<ActionEvent>() { //Sets the action for when the back button is pressed
            @Override
            public void handle(ActionEvent event) {
                try {
                    // update favourite list
                    changeScreen(event); //Changes screen back to the main champion display
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        });
        skinsTilePane.setHgap(6); //Sets a gap between the skin images
    }

    /**
     * Updates the favourite button, whether or not the champion is currently favourited and will sync settings with
     * the db.
     */
    private void favouriteButton() {
        // connect to db
        DBManager db = Main.getDbManager();

        // deal shading or not shading button
        if (champion.isFavourited()) {
            favouriteButton.setSelected(true);
        } else {
            favouriteButton.setSelected(false);
        }

        // add / remove champ to favourites on click of button
        favouriteButton.setOnAction(e -> {
            if (!champion.isFavourited()) {
                db.insertFavourite(champion);
                champion.setFavourited(true);
            } else {
                db.removeFavourite(champion);
                champion.setFavourited(false);
            }
        });
    }

    /**
     * Switches from individual champion display to screen displaying all champions
     *
     * @param event ActionEvent for the back button press
     * @throws IOException
     */
    public void changeScreen(ActionEvent event) throws IOException {
        Main.getPrimaryStage().setScene(parent);
        // swap stylesheets back to main css
        Main.getPrimaryStage().getScene().getStylesheets().removeAll();
        Main.getPrimaryStage().getScene().getStylesheets().add("style.css");
        parentController.backButtonUpdate();
    }

    // Accessors
    public void setParent(Scene parent) {
        this.parent = parent;
    }

    /**
     * Sets the champion in IndividualChampionController
     *
     * @param champion Champion object from a pane clicked in the main window
     */
    public void setChampion(Champion champion) {
        this.champion = champion;
        setData(); //Displays all the data regarding the clicked champion on the screen
        favouriteButton();
    }

    public void setParentController(Controller parentController) {
        this.parentController = parentController;
    }

    /**
     * Sets all data on individualChampion.fxml scene to data from the current Champion object
     */
    private void setData() {
        championName.setText(champion.getName() + " " + champion.getTitle()); //Concatenates the champion name with their small blurb
        championSplash.setImage(ImageManager.getImage(champion.getName() + "_splash").getImage()); //Sets the Splash Art in the top left, showing default champion image
        qAbilityLabel.setText(champion.getSpells().get(0).getName()); //Gets spell names
        wAbilityLabel.setText(champion.getSpells().get(1).getName());
        eAbilityLabel.setText(champion.getSpells().get(2).getName());
        rAbilityLabel.setText(champion.getSpells().get(3).getName());
        qImg.setImage(ImageManager.getImage("Champion_Q").getImage()); //Adds images for Q W E R
        wImg.setImage(ImageManager.getImage("Champion_W").getImage());
        eImg.setImage(ImageManager.getImage("Champion_E").getImage());
        rImg.setImage(ImageManager.getImage("Champion_R").getImage());
        attackDamageIcon.setImage(ImageManager.getImage("Attack_Damage").getImage()); //Transparent art for attack, ability power, and defense
        abilityPowerIcon.setImage(ImageManager.getImage("Ability_Power").getImage());
        defenseIcon.setImage(ImageManager.getImage("Defense").getImage());
        attackDamageValue.setText(Integer.toString(champion.getInfo().getAttack())); //Sets the values for attack, ability power, and defense
        abilityPowerValue.setText(Integer.toString(champion.getInfo().getMagic()));
        defenseValue.setText(Integer.toString(champion.getInfo().getDefense()));

        setSkins(); //Calls on method to set the skins for the champion
    }

    /**
     * Adds the current Champions skins to the tilePane
     */
    private void setSkins () {
        skinsTilePane.setPrefWidth(champion.getSkins().size() * (102 + 6) + 40); //Multiplies the number of skins by (Image Width +  TilePane Gap +  Wiggle Room) to scale the tilePane properly
        skinsTilePane.getChildren().clear(); //Clears the existing skins, to replace them with the new champions skins

        for (Skin skin : champion.getSkins()) { //Cycles through each skin in the Champion objects list of skins
            String[] skinNameSplit = skin.getName().split(" "); //Splits the skin name up based on spaces for proper formatting
            Label newLabel = new Label();
            newLabel.setTextAlignment(TextAlignment.CENTER); //Makes the text align to the middle of the label

            newLabel.setText(constructSkinName(skinNameSplit)); //Sets the labels text to a string created by the constructSkinName method

            newLabel.setGraphic(ImageManager.getImage(champion.getName() + "_" + skin.getNum())); //Sets the graphic to the champions skin graphic, based on skin name
            newLabel.setContentDisplay(ContentDisplay.TOP); //Makes the image display on the top, text below

            Pane newPane = new Pane();
            newPane.getChildren().add(newLabel); //Adds the label to a pane
            skinsTilePane.getChildren().add(newPane); //Adds the pane to the skinsTilePane
        }
    }

    /**
     * Constructs a formatted String to display below a Champion skin image
     *
     * @param skinNameSplit String array containing individual words within a champion name, in order
     * @return Formatted String with spaces and new line every two words
     */
    private String constructSkinName (String[] skinNameSplit) {
        String name = "";
        int i = 0;

        for (String subString : skinNameSplit //Cycles through each word in the champion skin name array
             ) {
            name += subString + " "; //Adds the word, followed by a space
            ++i;
            if (i%2 == 0) //Every two words, appends a new line, therefore text doesn't extend past the image width
                name += "\n";
        }

        return name; //Returns the formatted name to display below the champion
    }
}
