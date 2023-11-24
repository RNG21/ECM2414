package src;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;

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
    private int preferredCardAmount = 0;

    private int emptySlot;  // Records the index of the card that was most recently discarded

    public Player(int playerNumber, Deck leftDeck, Deck rightDeck, Card[] initialHand, GameState state) {
        this.playerNumber = playerNumber;
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.state = state;

        this.hand = initialHand;

        String loggerFilename = "Player"+this.playerNumber+" output.txt";
        this.logger = Logger.getLogger("Player"+this.playerNumber);
        File file = new File(loggerFilename);
        try {
            file.createNewFile();
            FileHandler fh = new FileHandler(loggerFilename);
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
        Card card = leftDeck.drawCard();
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
            logger.log(Level.INFO, 
                "Player " + this.playerNumber + 
                " discards a " + discardCard() + 
                " to deck " + this.rightDeck.getDeckNumber()
            );
            
            logger.log(Level.INFO,
                "Player " + this.playerNumber +
                " draws a " + drawCard() +
                " from deck " + this.leftDeck.getDeckNumber()
            );

            logger.log(Level.INFO,
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
        gameLoop();
    }
}
