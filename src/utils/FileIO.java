package src.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import src.exceptions.MaxLines;

public class FileIO {
    /**
     * Writes a string to file
     * @param filename the file path to write to
     * @param content the content to write
     * @throws IOException
     */
    public static void writeToFile(String filename, String content) throws IOException{
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
        }
    }

    /**
     * Reads a file and returns each line as an element in an array
     * @param filename the file path to read from
     * @param max maximum amount of lines to read, if exceeded, throws {@code MaxLines}.
     *            Pass in a negative integer to negate this (unlimited lines)
     * @return The file with each line as an element in an array of strings
     * @throws IOException IO error when reading the file
     * @throws FileNotFoundException File not found
     * @throws MaxLines Line count exceeds {@code max} parameter
     */
    public static String[] fileToLines(String filename, int max) throws IOException, FileNotFoundException, MaxLines{
        try (FileReader fileReader = new FileReader(filename)) {
            BufferedReader br = new BufferedReader(fileReader);
        
            ArrayList<String> lines = new ArrayList<String>();
            String line;
            int count = 1;
            while ((line = br.readLine()) != null) {
                lines.add(line);

                if (max >= 0 && count++ > max) {
                    throw new MaxLines();
                }
            }
            return lines.toArray(new String[lines.size()]);
        }
    }
}
