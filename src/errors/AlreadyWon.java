package src.errors;

/**
 * Thrown when another player has already declared a win
 */
public class AlreadyWon extends Exception{
    public AlreadyWon(String message) {
        super(message);
    }
}
