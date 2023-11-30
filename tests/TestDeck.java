package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import src.Card;
import src.Deck;

public class TestDeck {
    @Test
    public void testAddAndRemoveCard(){
        Deck deck = new Deck(0, null);
        Card addedCard = new Card(0);
        deck.addCard(addedCard);
        deck.addCard(new Card(123));
        assertEquals(deck.toString(), "[0, 123]");
        Card card = deck.drawCard();
        assertEquals(card, addedCard);
        assertEquals(deck.toString(), "[123]");
        assertThrows(NullPointerException.class, () -> deck.addCard(null));
    }

    public boolean fail = false;
    public void test(Deck deck) {
        this.fail = false;
        int waitTIme = 1000;

        long startTime = System.nanoTime();
        deck.waitForCard(waitTIme);
        long endTime = System.nanoTime();

        long timeTaken = (endTime - startTime);
        long tolerance = waitTIme*990000;  // Convert milliseconds to nanoseconds but allow for margin
        if (timeTaken >= tolerance) {
            this.fail = true;  // Exceptions thrown in thread does not fail the test
        }
    }

    @Test
    public void testWaitNotify() throws InterruptedException{
        Deck deck = new Deck(0, null);

        Thread t1 = new Thread(() -> test(deck));
        t1.start();
        t1.join();
        assertTrue(this.fail);

        Thread t2 = new Thread(() -> test(deck));
        new Thread(() -> deck.addCard(new Card(0))).start();
        t2.start();
        t2.join();
        if (this.fail) {
            fail("consumer not woken up by producer");
        }
    }
}
