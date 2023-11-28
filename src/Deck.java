package src;

import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingQueue;

import src.utils.FileIO;

public class Deck {
    private final LinkedBlockingQueue<Card> cards;
    private final int deckNumber;

    public Deck(int deckNumber, Card[] cards){
        this.deckNumber = deckNumber;
        if (cards == null) {
            this.cards = new LinkedBlockingQueue<>();
        } else {
            this.cards = new LinkedBlockingQueue<>(Arrays.asList(cards));
        }
    };

    @Override
    public String toString(){
        return this.cards.toString();
    }

    public int getDeckNumber() {
        return this.deckNumber;
    }

    public void writeToFile(String dir) throws IOException{
        FileIO.writeToFile(
            dir,
            "deck"+this.deckNumber+" contains "+this.toString()
            );
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
    public Card drawCard() throws NoSuchElementException{
        return this.cards.remove();
    }

    /**
     * Waits for deck to be populated
     * @param timeoutMillis maximum time to wait for
     * @return deck is empty or not
     */
    public synchronized boolean waitForCard(int timeoutMillis){
        if (this.cards.isEmpty()) {
            try {
                wait(timeoutMillis);
            } catch (InterruptedException ignored) {}
        }
        return this.cards.isEmpty();
    }
}
