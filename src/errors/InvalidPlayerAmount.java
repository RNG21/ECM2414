package src.errors;

/**
 * Thrown when player amount is less than 1
 */
public class InvalidPlayerAmount extends Exception{
    public InvalidPlayerAmount(String message) {
        super(message);
    }
}
