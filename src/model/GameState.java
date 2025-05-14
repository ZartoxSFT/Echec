package model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Piece> pieces;
    private boolean currentPlayerWhite;
    private Map<Piece, Boolean> hasMoved;
    private boolean isAIGame;
    private boolean aiIsWhite;
    private int aiDifficulty;
    
    public GameState(List<Piece> pieces, boolean currentPlayerWhite, Map<Piece, Boolean> hasMoved,
                    boolean isAIGame, boolean aiIsWhite, int aiDifficulty) {
        this.pieces = new ArrayList<>(pieces);
        this.currentPlayerWhite = currentPlayerWhite;
        this.hasMoved = new HashMap<>(hasMoved);
        this.isAIGame = isAIGame;
        this.aiIsWhite = aiIsWhite;
        this.aiDifficulty = aiDifficulty;
    }
    
    public List<Piece> getPieces() {
        return pieces;
    }
    
    public boolean isCurrentPlayerWhite() {
        return currentPlayerWhite;
    }
    
    public Map<Piece, Boolean> getHasMoved() {
        return hasMoved;
    }
    
    public boolean isAIGame() {
        return isAIGame;
    }
    
    public boolean isAIWhite() {
        return aiIsWhite;
    }
    
    public int getAIDifficulty() {
        return aiDifficulty;
    }
} 