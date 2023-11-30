package src;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import src.exceptions.AlreadyWon;
import src.utils.CustomFormatter;

public class Player implements Runnable {
    private final Logger logger;
    private final int playerNumber;

    private final GameState state;

    private final Deck leftDeck;
    private final Deck rightDeck;

    private final Card[] hand;
    private final LinkedList<Integer> toDiscard = new LinkedList<>();  // Stores indicies of cards to discard
    private int preferredCardAmount = 0;

    public Player(int playerNumber, Deck leftDeck, Deck rightDeck, Card[] initialHand, GameState state) {
        this.playerNumber = playerNumber;
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.state = state;

        this.hand = initialHand;
        for (int i = 0; i < initialHand.length; i++) {
            checkPreferred(initialHand[i], i);
        }

        this.logger = Logger.getLogger("Player"+this.playerNumber);
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
     * Sets up logger format and creates directory and files ready for logging
     * @param logger the logger to add formatting to
     * @return The logger
     */
    private Logger loggerSetup(){
        String outputFilePath = "./logs/Player"+this.playerNumber+"_output.txt";
        this.logger.setUseParentHandlers(false);  // Disable logger output to console
        try {
            Files.createDirectories(Paths.get("./logs"));

            File file = new File(outputFilePath);
            file.createNewFile();

            FileHandler fh = new FileHandler(outputFilePath);
            fh.setFormatter(new CustomFormatter());
            this.logger.addHandler(fh);
          } catch (IOException ignored) {}
        return this.logger;
    }

    /**
     * Cheks if a card is a preferred card and adds the index to toDiscard
     * @param card the card to check
     * @param i the index of the card
     * @return if the card is a preferred card
     */
    private boolean checkPreferred(Card card, int i) {
        if (card.value == this.playerNumber) {
            this.preferredCardAmount += 1;
            return true;
        } else {
            this.toDiscard.addLast(i);
            return false;
        }
    }

    /**
     * Discards a card to rightDeck and draws from leftDeck
     * @param i Index of card to discard
     * @return the drawn card
     */
    private Card discardAndDraw(int i){
        Card drawnCard;
        try {
            drawnCard = leftDeck.drawCard();
        } catch (NoSuchElementException e) {
            return null;
        }

        this.rightDeck.addCard(this.hand[i]);
        this.hand[i] = drawnCard;
        checkPreferred(drawnCard, i);
        
        return drawnCard;
    }

    /**
     * Declare and broadcast win to other players
     */
    private void declareWin(){
        try{this.state.declareWin(this);} catch (AlreadyWon ignored) {};
        this.logger.log(Level.INFO,
            "player "+this.playerNumber+" wins"
        );
    }

    public boolean isWinningHand(){
        if (this.preferredCardAmount == 0) {
            for (int i = 1; i < this.hand.length; i++) {
                if (this.hand[i-1].value != this.hand[i].value) {
                    return false;
                }
            }
            return true;
        }

        return this.preferredCardAmount == this.hand.length;
    }

    /**
     * Game loop, performs actions and logs them
     */
    public void gameLoop(){
        while (!this.state.isWon()) {
            if (isWinningHand()) {
                declareWin();
                System.out.println("player "+this.playerNumber+" wins");
                return;
            }

            while (this.leftDeck.waitForCard(500)) {
                if (this.state.isWon()) {
                    return;
                }
            }

            int discardIndex = this.toDiscard.removeFirst();
            Card discardedCard = this.hand[discardIndex];
            Card drawnCard = discardAndDraw(discardIndex);

            this.logger.log(Level.INFO, 
                "Player " + this.playerNumber + 
                " discards a " + discardedCard + 
                " to deck " + this.rightDeck.getDeckNumber()
            );

            this.logger.log(Level.INFO,
                "Player " + this.playerNumber +
                " draws a " + drawnCard + 
                " from deck " + this.leftDeck.getDeckNumber()
            );

            this.logger.log(Level.INFO,
                this.toString()
            );
        }
    }

    /**
     * Starts the player as a thread
     */
    @Override
    public void run(){
        loggerSetup();
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
        try{
            this.leftDeck.writeToFile("./logs/deck"+this.leftDeck.getDeckNumber()+"_output.txt");
        } catch (IOException ignored) {}
    }
}