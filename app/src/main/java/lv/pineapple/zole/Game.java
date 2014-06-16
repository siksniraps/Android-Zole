package lv.pineapple.zole;


import java.util.Random;

public class Game {

    public Game() {

    }

    //decide on first player
    private int getFirstPlayer() {
        return (new Random()).nextInt(3);
    }
}

interface Player {
    public Card play();

    public boolean selectType();

    public int bury();
}