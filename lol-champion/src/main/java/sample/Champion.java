package sample;

import java.util.ArrayList;

public class Champion {
    private String id, title, blurb;
    private int key, difficulty;
    private ArrayList<String> tags = new ArrayList<>();

    public Champion(String id, String title, String blurb, int key, ArrayList<String> tags, int difficulty) {
        this.id = id;
        this.title = title;
        this.blurb = blurb;
        this.key = key;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

}
