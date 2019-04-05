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

    /**
     * Serializes the object into a byte array for storage in the DB
     *
     * @return byte array representing all the data associated with the object
     * @throws IOException if an I/O error occurs while writing stream header
     */
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(this);
        out.close();
        return bos.toByteArray();
    }

    /**
     * Deserializes a byte array into a champion object
     *
     * @param data a byte stream which was previously serialized using {@link main.champion.Champion#serialize()}
     * @return a champion object if found
     * @throws IOException if an I/O error occurs while reading stream header
     * @throws ClassNotFoundException if the byte array doesn't match a champion object
     */
    public static Champion deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream in = new ObjectInputStream(bis);
        return (Champion) in.readObject();
    }
}
