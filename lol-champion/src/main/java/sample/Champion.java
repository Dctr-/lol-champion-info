package sample;

import java.util.ArrayList;
import java.util.List;

public class Champion {
    private String id, title, blurb;
    private int key, difficulty;
    private List<String> tags;
    private Info info;
    private List<Skin> skins;
    private List<Spell> spells;

    public Champion(String id, String title, String blurb, int key, ArrayList<String> tags, int difficulty) {
        this.id = id;
        this.title = title;
        this.blurb = blurb;
        this.key = key;
        this.tags = tags;
    }

    public String getName() {
        return id;
    }

    public String getId() {
        return id;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getTitle() {
        return title;
    }

    public String getBlurb() {
        return blurb;
    }

    public int getKey() {
        return key;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public Info getInfo() {
        return info;
    }

    public List<Skin> getSkins() {
        return skins.subList(1, skins.size());
    }

    public List<Spell> getSpells() {
        return spells;
    }
}
