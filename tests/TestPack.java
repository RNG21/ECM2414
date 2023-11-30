package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import src.Pack;
import src.Card;
import src.exceptions.InvalidPack;
import src.exceptions.InvalidPlayerAmount;

public class TestPack {
    private final int playerAmount = 2;
    private String[] notPositive = new String[] {"-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private String[] notInteger = new String[] {"1", "2", "3", "4", "5", "six", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
    private String[] less8n= new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
    private String[] more8n = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17"};
    private String[] nullPack = new String[8*playerAmount];
    private String[] validPack = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};

    @Test
    public void testGeneratePack() throws InvalidPlayerAmount{
        assertThrows(InvalidPlayerAmount.class, () -> Pack.generatePack(0));

        Pack pack;
        pack = Pack.generatePack(playerAmount);

        assertEquals(8*playerAmount, pack.getCards().length);
    }

    @Test
    public void testValidatePack() throws InvalidPack{
        assertThrows(InvalidPack.class, () -> Pack.validatePack(notPositive, playerAmount));
        assertThrows(InvalidPack.class, () -> Pack.validatePack(notInteger, playerAmount));
        assertThrows(InvalidPack.class, () -> Pack.validatePack(less8n, playerAmount));
        assertThrows(InvalidPack.class, () -> Pack.validatePack(more8n, playerAmount));
        assertThrows(InvalidPack.class, () -> Pack.validatePack(nullPack, playerAmount));
        Card[] cards = Pack.validatePack(validPack, playerAmount);
        for (int i = 0; i < cards.length; i++) {
            assertEquals(String.valueOf(cards[i].value), validPack[i]);
        }
    }
}
