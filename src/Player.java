package src;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import src.exceptions.AlreadyWon;
import src.utils.CustomFormatter;
import src.utils.Random;

public class Player implements Runnable {
    private final Logger logger;
    private final int playerNumber;

    private final GameState state;

    private final Deck leftDeck;
    private final Deck rightDeck;

    private final Card[] hand;
    private int preferredCardAmount = 0;  // Amount of preferred card in hand

    private int emptySlot;  // Records the index of the card that was most recently discarded

    public Player(int playerNumber, Deck leftDeck, Deck rightDeck, Card[] initialHand, GameState state) {
        this.playerNumber = playerNumber;
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.state = state;
        this.hand = initialHand;

        String outputFilePath = "./logs/Player"+this.playerNumber+"_output.txt";
        this.logger = Logger.getLogger("Player"+this.playerNumber);
        logger.setUseParentHandlers(false);  // Disable output to console
        try {
            Files.createDirectories(Paths.get("./logs"));

            File file = new File(outputFilePath);
            file.createNewFile();

            FileHandler fh = new FileHandler(outputFilePath);
            fh.setFormatter(new CustomFormatter());
            logger.addHandler(fh);

          } catch (IOException ignored) {}
    }

    @Override
    public String toString(){
        return "player " + this.playerNumber + " current hand is " + Arrays.toString(this.hand);
    }

    public int getPlayerNumber(){
        return this.playerNumber;
    }

    public Card[] getHand(){
        return this.hand;
    }

    /**
     * Draws a card from leftDeck, must call {@link Player#discardCard} before 
     * @return The drawn card
     */
    private Card drawCard(){
        Card card;
        try {
            card = leftDeck.drawCard();
        } catch (NoSuchElementException e) {
            return null;
        }

        /*
         * Insert preferred card into hand[preferredCardAmount].
         * If occupied, move non-preferred card to hand[emptySlot].
         * Example player4:
         * preferredCardAmount = 1
         * 4 -> [4, 3, 6, null]
         * [4, 4, 6, 3]
         * preferredCardAmount = 2
         * 
         * Cards with indices >=preferredCardAmount -> discards
         * range of indices to discard constrained by preferredCardAmount in discardCards()
         */
        if (card.getValue() == this.playerNumber) {
            Card temp = this.hand[this.preferredCardAmount];
            if (temp == null) {
                this.hand[this.emptySlot] = card;
            } else {
                this.hand[this.emptySlot] = temp;
                this.hand[this.preferredCardAmount] = card;
            }
            this.preferredCardAmount += 1;
        } else {
            this.hand[this.emptySlot] = card;
        }
        
        return card;
    }

    /**
     * Discards a random non preferred card to rightDeck
     * @return The discarded card
     */
    private Card discardCard(){
        // Constrain range of indicies to avoid discarding preferred cards
        int randomIndex = Random.randInt(this.preferredCardAmount, this.hand.length);
        Card card = this.hand[randomIndex];

        this.rightDeck.addCard(card);
        this.hand[randomIndex] = null;
        this.emptySlot = randomIndex;

        return card;
    }

    /**
     * Declare and broadcast win to other players
     */
    private void declareWin(){
        try{this.state.declareWin(this);} catch (AlreadyWon ignored) {}
        this.logger.log(Level.INFO,
            "player "+this.playerNumber+" wins"
        );
    }

    /**
     * Checks if current hand is winning hand
     * @return 
     */
    private boolean isWinningHand(){
        return this.preferredCardAmount == this.hand.length;
    }

    /**
     * Game loop, performs actions and logs them
     */
    public void gameLoop(){
        while (!this.state.isWon()) {
            this.logger.log(Level.INFO, 
                "Player " + this.playerNumber + 
                " discards a " + discardCard() + 
                " to deck " + this.rightDeck.getDeckNumber()
            );
            
            Card card;
            while (true) {
                card = drawCard();
                if (this.state.isWon()) {
                    return;
                } else if (card instanceof Card) {
                    break;
                }
            }

            this.logger.log(Level.INFO,
                "Player " + this.playerNumber +
                " draws a " + card +
                " from deck " + this.leftDeck.getDeckNumber()
            );

            this.logger.log(Level.INFO,
                this.toString()
            );

            if (isWinningHand()) {
                declareWin();
                System.out.println("player "+this.playerNumber+" wins");
            }
        }
    }

    /**
     * Starts the player as a thread
     */
    @Override
    public void run(){
        this.logger.log(Level.INFO, 
            "Player "+this.playerNumber+" initial hand "+Arrays.toString(this.hand)
        );
        gameLoop();
        if (state.wonBy() != this) {
            this.logger.log(Level.INFO, 
                "player "+state.wonBy().playerNumber+
                " has informed player "+this.playerNumber+
                " that player "+state.wonBy().playerNumber+" has won"
            );
        }
        this.logger.log(Level.INFO, 
            "player "+this.playerNumber+" exits\n"+
            "player "+this.playerNumber+" final hand: "+Arrays.toString(this.hand)
        );
    }
}
