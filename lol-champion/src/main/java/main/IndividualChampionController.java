package main;

import javafx.scene.control.*;
import javafx.scene.text.TextAlignment;
import main.champion.Champion;
import main.champion.Skin;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextAlignment;
import main.champion.Champion;
import main.champion.Skin;

import java.io.IOException;
import java.util.List;

public class IndividualChampionController {
    private Champion champion;
    private Scene parent;

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
        skinsTilePane.setHgap(6);
    }

    public void favouriteButton() {
        // connect to db
        DBManager db = Main.getDbManager();

        // deal shading or not shading button
        if (champion.isFavourited()){
            System.out.println("Favourited");
            favouriteButton.setSelected(true);
        } else { favouriteButton.setSelected(false); }

        // add / remove champ to favourites
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

    public void changeScreen(ActionEvent event) throws IOException {
        Main.getPrimaryStage().setScene(parent);

    }

    // Accessors
    public void setParent(Scene parent) {
        this.parent = parent;
    }

    public void setChampion(Champion champion) {
        this.champion = champion;
        setData();
        favouriteButton();
    }

    private void setData() { //Sets all the graphics on the javaFX scene
        championName.setText(champion.getName() + " " + champion.getTitle());
        championSplash.setImage(ImageManager.getImage(champion.getName() + "_splash").getImage());
        qAbilityLabel.setText(champion.getSpells().get(0).getName());
        wAbilityLabel.setText(champion.getSpells().get(1).getName());
        eAbilityLabel.setText(champion.getSpells().get(2).getName());
        rAbilityLabel.setText(champion.getSpells().get(3).getName());
        qImg.setImage(ImageManager.getImage("Champion_Q").getImage());
        wImg.setImage(ImageManager.getImage("Champion_W").getImage());
        eImg.setImage(ImageManager.getImage("Champion_E").getImage());
        rImg.setImage(ImageManager.getImage("Champion_R").getImage());
        attackDamageIcon.setImage(ImageManager.getImage("Attack_Damage").getImage());
        abilityPowerIcon.setImage(ImageManager.getImage("Ability_Power").getImage());
        defenseIcon.setImage(ImageManager.getImage("Defense").getImage());
        attackDamageValue.setText(Integer.toString(champion.getInfo().getAttack()));
        abilityPowerValue.setText(Integer.toString(champion.getInfo().getMagic()));
        defenseValue.setText(Integer.toString(champion.getInfo().getDefense()));

        setSkins();
    }

    private void setSkins () {
        skinsTilePane.getChildren().clear();
        skinsTilePane.setPrefRows(1);
        for (Skin skin : champion.getSkins()) {
            String[] skinNameSplit = skin.getName().split(" ");
            Label newLabel = new Label();
            newLabel.setTextAlignment(TextAlignment.CENTER);
            switch (skinNameSplit.length) {
                case 0:
                    newLabel.setText(skin.getName());
                    break;
                case 1:
                    newLabel.setText(skinNameSplit[0]);
                    break;
                case 2:
                    newLabel.setText(skinNameSplit[0] + "\n" + skinNameSplit[1]);
                    break;
                case 3:
                    newLabel.setText(skinNameSplit[0] + " " + skinNameSplit[1] + "\n" + skinNameSplit[2]);
                    break;
                default:
                    newLabel.setText(skin.getName());
                    break;
            }

            newLabel.setGraphic(ImageManager.getImage(champion.getName() + "_" + skin.getNum()));
            newLabel.setContentDisplay(ContentDisplay.TOP);

            Pane newPane = new Pane();
            newPane.getChildren().add(newLabel);
            skinsTilePane.getChildren().add(newPane);
        }
    }
}
