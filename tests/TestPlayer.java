package tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Before;
import org.junit.Test;

import src.Card;
import src.Deck;
import src.Player;

public class TestPlayer {
    Method checkPreferred;
    Method discardAndDraw;
    Field preferredCardAmount;
    Field toDiscard;
    Field deckCards;
    Field playerHand;

    @Before
    public void setup() throws NoSuchMethodException, SecurityException, NoSuchFieldException{
        checkPreferred = Player.class.getDeclaredMethod("checkPreferred", Card.class, int.class);
        checkPreferred.setAccessible(true);
        discardAndDraw = Player.class.getDeclaredMethod("discardAndDraw", int.class);
        discardAndDraw.setAccessible(true);
        preferredCardAmount = getPrivateField(Player.class, "preferredCardAmount");
        toDiscard = getPrivateField(Player.class, "toDiscard");
        deckCards = getPrivateField(Deck.class, "cards");
        playerHand = getPrivateField(Player.class, "hand");
    }

    public Field getPrivateField(Class class_, String name) throws NoSuchFieldException, SecurityException {
        Field field = class_.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    @Test
    public void testCheckPreferred() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Player player1 = new Player(1, null, null, new Card[]{}, null);

        int preferredCardAmount = (int) this.preferredCardAmount.get(player1);
        assertEquals(preferredCardAmount, 0);

        checkPreferred.invoke(player1, new Card(1), 4);

        preferredCardAmount = (int) this.preferredCardAmount.get(player1);
        assertEquals(preferredCardAmount, 1);

        checkPreferred.invoke(player1, new Card(2), 12);

        preferredCardAmount = (int) this.preferredCardAmount.get(player1);
        assertEquals(preferredCardAmount, 1);
        LinkedList<Integer> toDiscard = (LinkedList<Integer>) this.toDiscard.get(player1);
        assertArrayEquals(toDiscard.toArray(), new Integer[]{12});
    }

    @Test
    public void testDiscardAndDraw() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Card[] cards = new Card[6];
        for (int i = 0; i < 6; i++) {
            cards[i] = new Card(i+1);
        }
        Deck rightDeck = new Deck(0, new Card[]{});
        Deck leftDeck = new Deck(0, new Card[]{cards[5], cards[0]});
        Player player1 = new Player(1, leftDeck, rightDeck, new Card[]{cards[1], cards[2], cards[3], cards[4]}, null);
        
        discardAndDraw.invoke(player1, 0);

        // Get current values of private attributes
        int preferredCardAmount = (int) this.preferredCardAmount.get(player1);
        LinkedBlockingQueue<Card> rightDeckCards = (LinkedBlockingQueue) this.deckCards.get(rightDeck);
        LinkedBlockingQueue<Card> leftDeckCards = (LinkedBlockingQueue) this.deckCards.get(leftDeck);
        Card[] playerHand = (Card[]) this.playerHand.get(player1);

        assertEquals(preferredCardAmount, 0);
        assertArrayEquals(rightDeckCards.toArray(), new Card[]{cards[1]});
        assertArrayEquals(leftDeckCards.toArray(), new Card[]{cards[0]});
        assertArrayEquals(playerHand, new Card[]{cards[5], cards[2], cards[3], cards[4]});

        discardAndDraw.invoke(player1, 1);

        // refresh attribute values
        preferredCardAmount = (int) this.preferredCardAmount.get(player1);
        rightDeckCards = (LinkedBlockingQueue) this.deckCards.get(rightDeck);
        leftDeckCards = (LinkedBlockingQueue) this.deckCards.get(leftDeck);
        playerHand = (Card[]) this.playerHand.get(player1);

        assertEquals(preferredCardAmount, 1);
        assertArrayEquals(rightDeckCards.toArray(), new Card[]{cards[1], cards[2]});
        assertArrayEquals(leftDeckCards.toArray(), new Card[]{});
        assertArrayEquals(playerHand, new Card[]{cards[5], cards[0], cards[3], cards[4]});
    }

    @Test
    public void testIsWinningHand(){
        Card[] nonPreferredWinningHand = new Card[]{new Card(4), new Card(4), new Card(4), new Card(4)};
        Player player1 = new Player(1, null, null, nonPreferredWinningHand, null);
        assertTrue(player1.isWinningHand());

        Card[] preferredWinningHand = new Card[]{new Card(1), new Card(1), new Card(1), new Card(1)};
        player1 = new Player(1, null, null, preferredWinningHand, null);
        assertTrue(player1.isWinningHand());

        Card[] notWinningHand = new Card[]{new Card(4), new Card(4), new Card(4), new Card(1)};
        player1 = new Player(1, null, null, notWinningHand, null);
        assertFalse(player1.isWinningHand());
    }
}
