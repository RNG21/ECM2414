package src.errors;

/**
 * Thrown when given pack file is invalid
 */
public class InvalidPack extends Exception{
    public InvalidPack(String message) {
        super(message);
    }
}
