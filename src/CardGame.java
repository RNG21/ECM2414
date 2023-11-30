package src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Iterator;

import src.exceptions.InvalidPack;
import src.exceptions.InvalidPlayerAmount;

public class CardGame{
    private static CardGame INSTANCE;
    private final GameState state = GameState.getInstance();

    public final int playerAmount;
    private final Player[] players;

    private final Deck[] decks;

    public static void main(String[] args) {
        CardGame.start();
    }

    /**
     * Only to be created with start
     * @param playerAmount Amount of players
     * @param pack Starting pack of cards
     */
    private CardGame(int playerAmount, Pack pack){
        this.playerAmount = playerAmount;
        this.players = new Player[playerAmount];
        this.decks = new Deck[playerAmount];

        Card[][][] sortedPack = CardGame.dealCards(pack);
        Card[][] playerHands = sortedPack[0];
        Card[][] decks = sortedPack[1];

        for (int i = 0 ; i < playerAmount ; i++) {
            this.decks[i] = new Deck(i+1, decks[i]);
        }

        for (int i = 0 ; i < playerAmount ; i++) {
            if (i+1 == this.decks.length) {
                this.players[i] = new Player(i+1, this.decks[i], this.decks[0], playerHands[i], state);
            } else {
                Deck rightDeck = this.decks[i+1];
                this.players[i] = new Player(i+1, this.decks[i], rightDeck, playerHands[i], state);
            }
        }
    }

    public synchronized static CardGame start(){
        if (INSTANCE != null) {
            return INSTANCE;
        }
        Scanner scanner = new Scanner(System.in);
        int playerAmount = CardGame.getPlayerAmount(scanner);
        Pack pack = CardGame.getPackPath(playerAmount, scanner);
        scanner.close();
                
        CardGame gameInstance = new CardGame(playerAmount, pack);

        for (Player player : gameInstance.players) {
            new Thread(player).start();
        }

        return gameInstance;
    }
    
    /**
     * Asks user to input player amount
     * @param scanner  
     * @return the value entered
     */
    public static int getPlayerAmount(Scanner scanner){
        int playerAmount;

        while (true) {
            System.out.println("Enter player amount:");
            String userInput = scanner.nextLine().strip();

            try{
                playerAmount = validatePlayerAmount(userInput);
            } catch (InvalidPlayerAmount e) {
                System.out.println(e.getMessage());
                continue;
            }

            break;
        }

        return playerAmount;
    }

    /**
     * Asks user to input the path for a pack file
     * @param scanner
     * @return The pack read from given path
     */
    public static Pack getPackPath(int playerAmount, Scanner scanner){
        Pack pack;

        while (true) {
            System.out.println("Enter pack path:");
            String userInput = scanner.nextLine().strip();

            try{
                pack = Pack.readPack(userInput, playerAmount);
                return pack;
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
            } catch (IOException | InvalidPack e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Validates a user input for player amount and converts it into int
     * @param userInput user input
     * @return the value with type int
     * @throws InvalidPlayerAmount Thrown when the value is less than 1 or not integer
     */
    private static int validatePlayerAmount(String userInput) throws InvalidPlayerAmount{
        int playerAmount;

        try {
            playerAmount = Integer.valueOf(userInput);
        } catch (NumberFormatException e) {
            throw new InvalidPlayerAmount("Player amount must be integer");
        }

        if (playerAmount <= 0) {
            throw new InvalidPlayerAmount("Player amount must be larger than 0");
        }

        return playerAmount;
    }

    /**
     * Takes in a pack and returns a list containing a list of player hands and a list containing decks.
     * Example: [[[player1hand], [player2hand], ...], [[deck1], [deck2], ...]]
     *
     * @param pack Pack object to deal cards from
     * @param playerAmount Amount of players
     * @throws PlayerAmountMismatch 
     * @return a list of list of player hand and list of player decks
     */
    public static Card[][][] dealCards(Pack pack){

        Iterator<Card> pack_ = pack.iterator();

        Card[][][] result = new Card[2][pack.playerAmount][4];

        for (int i = 0; i < 2; i++) {
            // when i = 0 -> insert into player hands
            // when i = 1 -> insert into decks
            for (int cardPos = 0; cardPos < 4; cardPos++) {
                for (int playerNum = 0; playerNum < pack.playerAmount; playerNum++) {
                    result[i][playerNum][cardPos] = pack_.next();
                }
            }
        }

        return result;
    }
}
