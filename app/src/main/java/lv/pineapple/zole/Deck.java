package lv.pineapple.zole;


import java.util.ArrayList;

public class Deck {
    //List containing all cards currently in the deck.
    private ArrayList<Card> cards = new ArrayList<Card>(26);

    //Constructor. Fill deck with cards and shuffle it.
    public Deck() {
        createCards();
    }

    //Populate the deck with the 26 legal Zole cards.
    public void createCards() {
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Type type : Card.Type.values()) {
                if (!((suit != Card.Suit.DIAMOND) && (type.compareTo(Card.Type.NINE) < 0))) {
                    cards.add(new Card(type, suit));
                }
            }
        }
    }

    public String toString() {
        return cards.toString();
    }

}
