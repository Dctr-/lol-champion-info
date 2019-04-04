package main.champion;

import java.io.Serializable;

public class Skin implements Serializable {
    private String name;
    private int num;

    public Skin (String name, int num) {
        this.name = name;
        this.num = num;
    }

    public String getName() { return name; }
    public int getNum() { return num; }
}
