package src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import src.errors.InvalidPack;
import src.errors.InvalidPlayerAmount;
import src.utils.FileIO;

public class Pack{

    private final int[] contents;

    public Pack(int[] contents) throws InvalidPack{
        if (contents.length == 0) {throw new InvalidPack("Pack must not be empty");}
        this.contents = contents;
    };

    public int[] getContents(){
        return this.contents;
    };

    /**
     * Each element on a new line
     */
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();

        for (int element : this.contents) {
            stringBuilder.append(element).append("\n");
        }

        return stringBuilder.toString();
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

        Random rand = new Random();
        int[] pack = new int[8*n];

        for (int i = 0; i < 8*n; i++) {
            pack[i] = rand.nextInt(n);
        }

        try {
            return new Pack(pack);
        } catch (InvalidPack e) {
            // Will never reach here but I have to do this to keep compiler happy
            return null;
        }
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
        ArrayList<Integer> output = new ArrayList<>();

        if (pack.length == 0) {
            throw new InvalidPack("Pack must not be empty");
        }

        if (pack.length != (8*n)) {
            throw new InvalidPack("Card amount must be 8 times player amount");
        }

        String line = "";
        try {
            for (int i = 0; i < pack.length; i++) {
                line = pack[i];
                output.add(Integer.parseInt(line));
            }
        } catch (NumberFormatException e) {
            throw new InvalidPack("Line is not integer: "+line);
        }

        return new Pack(output.stream().mapToInt(Integer::intValue).toArray());
    };

    /**
     * Reads a pack from a text file
     * @param filename The file to read from
     * @param playerAmount player amount
     * @throws IOException Error when reading file
     * @throws InvalidPack Given pack file is invalid
     * @return Pack object
     */
    public static Pack readPack(String filename, int playerAmount) throws IOException, InvalidPack{
        String[] stringPack = FileIO.fileToLines(filename);
        return validatePackFile(stringPack, playerAmount);
    };

}
