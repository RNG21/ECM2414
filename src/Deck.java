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
        this.cards = new LinkedBlockingQueue<>(Arrays.asList(cards));
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
     * Peeks the card on top of the deck
     * @param timeoutMillis 
     * @return the card
     */
    public synchronized Card peek(int timeoutMillis){
        if (this.cards.isEmpty()) {
            try {
                wait(timeoutMillis);
            } catch (InterruptedException ignored) {}
        }
        return this.cards.peek();
    }
}
