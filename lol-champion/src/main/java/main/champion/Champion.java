package main.champion;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Champion implements Serializable {
    private String id, title, blurb;
    private int key, difficulty;
    private List<String> tags;
    private Info info;
    private List<Skin> skins;
    private List<Spell> spells;
    private boolean favourited;

    public Champion(String id, String title, String blurb, int key, ArrayList<String> tags, int difficulty) {
        this.id = id;
        this.title = title;
        this.blurb = blurb;
        this.key = key;
        this.tags = tags;
    }

    public boolean isFavourited() {
        return favourited;
    }

    public void setFavourited(boolean favourited) {
        this.favourited = favourited;
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

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(this);
        out.close();
        return bos.toByteArray();
    }

    public static Champion deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream in = new ObjectInputStream(bis);
        return (Champion) in.readObject();
    }
}
