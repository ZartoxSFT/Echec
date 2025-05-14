package model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Classe représentant l'état du jeu.
 * Implémente la sérialisation.
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Piece> pieces;
    private boolean currentPlayerWhite;
    private Map<Piece, Boolean> hasMoved;
    private boolean isAIGame;
    private boolean aiIsWhite;
    private int aiDifficulty;
    
    /**
     * Constructeur de la classe GameState.
     * @param pieces La liste des pièces sur le plateau.
     * @param currentPlayerWhite true si c'est au joueur blanc de jouer, false sinon.
     * @param hasMoved Un map associant chaque pièce à un booléen indiquant si elle a déjà bougé.
     * @param isAIGame true si le jeu est en mode IA, false sinon.
     * @param aiIsWhite true si l'IA est blanche, false sinon.
     * @param aiDifficulty Le niveau de difficulté de l'IA.
     */
    public GameState(List<Piece> pieces, boolean currentPlayerWhite, Map<Piece, Boolean> hasMoved,
                    boolean isAIGame, boolean aiIsWhite, int aiDifficulty) {
        this.pieces = new ArrayList<>(pieces);
        this.currentPlayerWhite = currentPlayerWhite;
        this.hasMoved = new HashMap<>(hasMoved);
        this.isAIGame = isAIGame;
        this.aiIsWhite = aiIsWhite;
        this.aiDifficulty = aiDifficulty;
    }
    
    /**
     * Récupère la liste des pièces sur le plateau.
     * @return La liste des pièces.
     */
    public List<Piece> getPieces() {
        return pieces;
    }
    
    /**
     * Vérifie si c'est au joueur blanc de jouer.
     * @return true si c'est au joueur blanc de jouer, false sinon.
     */
    public boolean isCurrentPlayerWhite() {
        return currentPlayerWhite;
    }
    
    /**
     * Récupère le map associant chaque pièce à un booléen indiquant si elle a déjà bougé.
     * @return Le map associant chaque pièce à un booléen indiquant si elle a déjà bougé.
     */
    public Map<Piece, Boolean> getHasMoved() {
        return hasMoved;
    }
    
    /**
     * Vérifie si le jeu est en mode IA.
     * @return true si le jeu est en mode IA, false sinon.
     */
    public boolean isAIGame() {
        return isAIGame;
    }
    
    /**
     * Vérifie si l'IA est blanche.
     * @return true si l'IA est blanche, false sinon.
     */
    public boolean isAIWhite() {
        return aiIsWhite;
    }
    
    /**
     * Récupère le niveau de difficulté de l'IA.
     * @return Le niveau de difficulté de l'IA.
     */
    public int getAIDifficulty() {
        return aiDifficulty;
    }
} 