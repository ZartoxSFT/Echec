package model.pieces;

import model.Piece;
import model.Case;
import model.movement.DecoQueen;

/**
 * Classe représentant une Reine dans le jeu d'échecs.
 * La reine combine les mouvements de la tour et du fou.
 */
public class Queen extends Piece {
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur de la Reine.
     * @param color La couleur de la pièce (true pour blanc, false pour noir)
     * @param initialCase La case initiale de la pièce
     */
    public Queen(boolean color, Case initialCase) {
        super(new DecoQueen(null), initialCase);
        this.color = color;
        setImg();
    }

    /**
     * Initialise la stratégie de déplacement de la Reine.
     */
    @Override
    protected void initializeMovementStrategy() {
        this.movementStrategy = new DecoQueen(null);
    }

    /**
     * Définit l'image de la Reine.
     */
    @Override
    public void setImg() {
        String path = "src/img/" + (this.color ? "w_" : "b_") + "queen.png";
        java.io.File file = new java.io.File(path);
        if (!file.exists()) {
            System.err.println("Erreur : L'image " + path + " est introuvable.");
            return;
        }
        this.img = path;
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Récupère la couleur de la pièce.
     * @return La couleur de la pièce.
     */
    @Override
    public boolean getColor() {
        return this.color;
    }

    /**
     * Définit la couleur de la pièce.
     * @param color La couleur de la pièce.
     */
    @Override
    public void setColor(boolean color) {
        this.color = color;
        this.setChanged();
        this.notifyObservers();
    }
}
