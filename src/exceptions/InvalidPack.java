package src.exceptions;

/**
 * Thrown when given pack file is invalid
 */
public class InvalidPack extends Exception{
    public InvalidPack(String message) {
        super(message);
    }

    public InvalidPack(String message, Throwable cause) {
        super(message, cause);
    }
}
