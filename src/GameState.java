package src;

import src.errors.AlreadyWon;

public class GameState {
    private boolean isWon_ = false;
    private Player wonBy_;

    public boolean isWon(){
        return this.isWon_;
    }

    /**
     * @return The player who won, null if none
     */
    public Player wonBy(){
        return this.wonBy_;
    }

    /**
     * Declare a win
     * @param player the player who won
     * @throws AlreadyWon thrown when another player has already declared a win
     */
    public synchronized void declareWin(Player player) throws AlreadyWon{
        if (this.wonBy_ != null) {
            throw new AlreadyWon(String.format("Player %d has already declared a win", wonBy_.getPlayerNumber()));
        }
        this.wonBy_ = player;
        this.isWon_ = true;
    }
}
