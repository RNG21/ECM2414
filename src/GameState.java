package src;

import src.exceptions.AlreadyWon;

public class GameState {
    private static GameState INSTANCE;

    private boolean isWon_ = false;
    private Player wonBy_;

    private GameState() {        
    }

    public synchronized static GameState getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new GameState();
        }
        return INSTANCE;
    }

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
