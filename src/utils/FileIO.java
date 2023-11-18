package src.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
        } catch (IOException e) { throw e; }
    }

    /**
     * Reads a file and returns each line as an element in an array
     * @param filename the file path to read from
     * @return The file as a string
     * @throws IOException
     */
    public static String[] fileToLines(String filename) throws IOException{
        try (FileReader fileReader = new FileReader(filename)) {
            BufferedReader br = new BufferedReader(fileReader);
        
            ArrayList<String> lines = new ArrayList<String>();
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            return lines.toArray(new String[lines.size()]);
            
        } catch (IOException e) { throw e; }
    }
}
