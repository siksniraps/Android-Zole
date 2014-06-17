package lv.pineapple.zole;

//Class describing a standard playing card.
// Options for cards are only available for legal Zole cards.
class Card implements Comparable<Card> {
    //Enum of card suits in the order of the strongest when trumps.
    public enum Suit {
        DIAMOND, HEART, SPADE, CLUBS
    }

    //Enum of the type of the card from weakest to strongest.
    // Points are provided for each.
    public enum Type {
        SEVEN(0), EIGHT(0), NINE(0), KING(4),
        TEN(10), ACE(11), JACK(2), QUEEN(3);

        private int points;

        private Type(int points) {
            this.points = points;
        }

        public int getPoints() {
            return points;
        }
    }

    //Card type and suit instance variables.
    private Suit suit;
    private Type type;

    //Constructor. Takes card type and suit.
    public Card(Type type, Suit suit) {
        this.suit = suit;
        this.type = type;
    }

    //Getter method for points.
    public int getPoints() {
        return type.getPoints();
    }

    //Override toString() method to show the name of the card.
    public String toString() {
        return type.toString() + " " + suit.toString();
    }

    //Compares suits. Returns true if the same suit, else false.
    public boolean compareSuit(Card otherCard) {
        return (this.suit == otherCard.suit);
    }

    //Compare the strength of two cards.
    public int compareTo(Card otherCard) {
       /* if(strength>otherCard.strength)return 1;
        else if(this.strength<otherCard.strength)return -1;
        else return -1;*/
        return 0;
    }
}


