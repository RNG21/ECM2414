package src;

import java.util.concurrent.LinkedBlockingQueue;

public class Deck {
    private LinkedBlockingQueue<Card> cards = new LinkedBlockingQueue<>();
    private final int deckNumber;

    public Deck(int deckNumber){
        this.deckNumber = deckNumber;
    };

    @Override
    public String toString(){
        return this.cards.toString();
    }

    public int getDeckNumber() {
        return this.deckNumber;
    }

    /**
     * Adds a card to the bottom of the deck
     * @param card the card to add
     */
    public void addCard(Card card){
        cards.add(card);
    }

    /**
     * Removes a card from the top of the deck
     * @return the removed card
     */
    public Card drawCard(){
        return cards.remove();
    }
}
