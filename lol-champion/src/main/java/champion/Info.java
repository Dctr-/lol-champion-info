package champion;

public class Info {
    private int attack, defense, magic, difficulty;

    public Info (int attack, int defense, int magic, int difficulty) {
        this.attack = attack;
        this.defense = defense;
        this.magic = magic;
        this.difficulty = difficulty;
    }

    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getMagic() { return magic; }
    public int getDifficulty() { return difficulty; }
}
