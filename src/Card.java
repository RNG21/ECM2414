package src;

public class Card {

    public final int value;

    @Override
    public String toString(){
        return String.valueOf(this.value);
    }

    public Card(int value) {
        this.value = value;
    }
}