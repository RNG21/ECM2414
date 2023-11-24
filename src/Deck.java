package src;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

public class Deck {
    private final LinkedBlockingQueue<Card> cards;
    private final int deckNumber;

    public Deck(int deckNumber, Card[] cards){
        this.deckNumber = deckNumber;
        this.cards = new LinkedBlockingQueue<>(Arrays.asList(cards));
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
    public synchronized void addCard(Card card){
        this.cards.add(card);
        notify();
    }

    /**
     * Removes a card from the top of the deck
     * @return the removed card
     */
    public synchronized Card drawCard(){
        while (this.cards.isEmpty()) {
            System.out.println(this.deckNumber+"waiting");
            try { wait(); } catch (InterruptedException ignored) { return null; }
        }
        return this.cards.remove();
    }
}
