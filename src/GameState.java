package src;

import src.exceptions.AlreadyWon;

public class GameState {
    private boolean isWon_ = false;
    private Player wonBy_;

    public boolean isWon(){
        return this.isWon_;
    }

    public Player wonBy(){
        return this.wonBy_;
    }
    
    public synchronized void declareWin(Player player) throws AlreadyWon{
        if (this.wonBy_ != null) {
            throw new AlreadyWon(String.format("Player %d has already declared a win", wonBy_.getPlayerNumber()));
        }
        this.wonBy_ = player;
        this.isWon_ = true;
    }
}
