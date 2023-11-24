package src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import src.exceptions.InvalidPack;
import src.exceptions.InvalidPlayerAmount;
import src.exceptions.MaxLines;
import src.utils.FileIO;
import src.utils.Random;

public class Pack{

    private final Card[] cards;
    private final int playerAmount;

    /**
     * Pack object only to be created by class methods
     * @param cards
     */
    private Pack(Card[] cards, int playerAmount){
        this.cards = cards;
        this.playerAmount = playerAmount;
    };

    public Iterator<Card> iterator(){
        return Arrays.stream(this.cards).iterator();
    }

    public Card[] getCards(){
        return this.cards;
    };

    public int getPlayerAmount(){
        return this.playerAmount;
    }

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
     * Validates a pack read from file before converting into a Pack object
     * @param pack The pack to validate
     * @param n amount of players, pack file should have 8n lines
     * @throws InvalidPack when line isn't int or card amount isn't 8n
     * @return a Pack object
     */
    private static Pack validatePackFile(String[] pack, int n) throws InvalidPack{
        if (pack == null) {
            throw new InvalidPack("Pack must not be null");
        }

        if (pack.length == 0) {
            throw new InvalidPack("Pack must not be empty");
        }

        if (pack.length != (8*n)) {
            throw new InvalidPack(String.format(
                    "File's line count must be 8 times player amount (%d lines), instead found %d lines", 
                    8*n, pack.length
                ));
        }

        String line = "";
        int number;
        Card[] output = new Card[pack.length];

        for (int i = 0; i < pack.length; i++) {
            line = pack[i];
            
            try {
                number = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                throw new InvalidPack(String.format("Line %d is not integer: \"%s\"", i+1, line));
            }

            if (number < 0) {
                throw new InvalidPack(String.format("Line %d is not positive integer: \"%s\"", i+1, line));
            }

            output[i] = new Card(number);
        }

        return new Pack(output, n);
    };

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
        try {
            stringPack = FileIO.fileToLines(filename, 8*playerAmount);
        } catch (MaxLines e) {
            throw new InvalidPack(String.format(
                "File's line count must be 8 times player amount (%d lines), instead found >%d lines", 
                8*playerAmount, 8*playerAmount
            ));
        } 
        return validatePackFile(stringPack, playerAmount);
    };

}
