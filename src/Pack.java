package src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.exceptions.InvalidPack;
import src.exceptions.InvalidPlayerAmount;
import src.exceptions.MaxLines;
import src.utils.FileIO;
import src.utils.Random;

public class Pack{

    private final Card[] cards;
    public final int playerAmount;

    private Pack(Card[] cards, int playerAmount){
        this.cards = cards;
        this.playerAmount = playerAmount;
    };

    public Pack(int[] cards, int playerAmount) throws InvalidPack {
        this.cards = validatePack(cards, playerAmount);
        this.playerAmount = playerAmount;
    }

    public Iterator<Card> iterator(){
        return Arrays.stream(this.cards).iterator();
    }

    public Card[] getCards(){
        return this.cards;
    };

    /**
     * Each element on a new line
     */
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();

        for (Card element : this.cards) {
            builder.append(element.toString()).append("\n");
        }

        return builder.toString();
    };

    /**
     * Generates a pack of cards to draw from
     * @param n Generates 8n cards
     * @return The generated pack
     */
    public static Pack generatePack(int n) throws InvalidPlayerAmount{
        if (n <= 0) {
            throw new InvalidPlayerAmount("Player amount must not be less than 1");
        }

        Card[] pack = new Card[8*n];

        for (int i = 0; i < 8*n; i++) {
            pack[i] = new Card(Random.randInt(1, n+1));
        }

        return new Pack(pack, n);
    };

    /**
     * Writes this pack to a file
     * @param filename The file to write to
     */
    public void writeToFile(String filename) throws IOException{
        FileIO.writeToFile(filename, this.toString());
    };
  
    /**
     * Overloaded method, see {@link Pack#validatePack(int[], int)}
     */
    public static Card[] validatePack(String[] pack, int n) throws InvalidPack{
        // Converts string to int then pass into validatePack(int[], int)
        if (pack == null) {
            throw new InvalidPack("Pack must not be null");
        }
        
        int[] convertToInt = new int[pack.length];

        for (int i = 0; i < pack.length; i++) {
            try {
                convertToInt[i] = Integer.parseInt(pack[i]);
            } catch (NumberFormatException e) {
                throw new InvalidPack(String.format("Element %d is not integer: \"%s\"", i, pack[i]));
            }
        }

        return validatePack(convertToInt, n);
    };

    /**
     * Validates a an array containing the pack's values and converts it into Card[]
     * @param pack the pack to validate
     * @param n player amount
     * @return the converted array
     * @throws InvalidPack Pack is invalid
     */
    public static Card[] validatePack(int[] pack, int n) throws InvalidPack{
        if (pack == null) {
            throw new InvalidPack("Pack must not be null");
        }

        if (pack.length != (8*n)) {
            throw new InvalidPack(String.format(
                "Array length must be 8 times player amount (%d), is instead %d", 
                8*n, pack.length
            ));
        }

        Card[] output = new Card[pack.length];
        for (int i = 0; i < pack.length; i++) {
            if (pack[i] < 0) {
                throw new InvalidPack(String.format(
                    "Element %d is not positive integer: \"%d\"", 
                    i, pack[i]
                ));
            }

            output[i] = new Card(pack[i]);
        }
        return output;
    }

    /**
     * Reads a pack from a text file
     * @param filename The file to read from
     * @param playerAmount player amount
     * @throws IOException Error when reading file
     * @throws FileNotFoundException File not found
     * @throws InvalidPack Given pack file is invalid
     * @return Pack object
     */
    public static Pack readPack(String filename, int playerAmount) throws IOException, FileNotFoundException, InvalidPack{
        String[] stringPack;
        Card[] cards;
        try {
            stringPack = FileIO.fileToLines(filename, 8*playerAmount);
            cards = validatePack(stringPack, playerAmount);
        } catch (MaxLines e) {
            throw new InvalidPack(String.format(
                "File's line count must be 8 times player amount (%d lines), instead found >%d lines", 
                8*playerAmount, 8*playerAmount
            ));
        } catch (InvalidPack e) {
            // rethrow exception but modify the message
            // e.g. Element 0 -> Line 1, Array length -> File line count
            String message = e.getMessage();
            Pattern pattern = Pattern.compile("(Element)\\s(\\d+)(\\s.+)");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                int digit = Integer.valueOf(matcher.group(2)) + 1;
                message = "Line " + digit + matcher.group(3);
            }
            throw new InvalidPack(message.replaceAll("Array length", "File line count"), e);
        }
        
        return new Pack(cards, playerAmount);
    };

}
