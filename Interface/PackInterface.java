package Interface;

public interface PackInterface {

    /**
     * Generates a pack of cards to draw from
     * @param n Generates 8n cards
     * @param min minimum face value of individual cards
     * @param max maximum face value of individual cards
     * @return The generated pack as an array
     */
    int[] generatePack(int n, int min, int max);

    /**
     * Writes a pack to file
     * @param filename The file to write to
     * @param pack The pack to write
     */
    void writeToFile(String filename, int[] pack);

    /**
     * Reads a pack from a text file
     * @param filename The file to read from
     * @return The pack
     */
    int[] readPack(String filename);

    /**
     * Validates a pack
     * @param pack The pack file to validate
     * @return Boolean indicating if it is valid
     */
    boolean validatePack(String pack);
}
