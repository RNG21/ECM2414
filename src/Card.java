package src;

public class Card {

    private final int value;

    @Override
    public String toString(){
        return String.valueOf(this.value);
    }

    public Card(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}